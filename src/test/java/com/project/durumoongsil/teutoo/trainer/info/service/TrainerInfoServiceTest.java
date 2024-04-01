package com.project.durumoongsil.teutoo.trainer.info.service;

import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.TrainerInfoNotFoundException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.trainer.info.domain.CareerImg;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.info.dto.request.TrainerListReqDto;
import com.project.durumoongsil.teutoo.trainer.info.dto.request.TrainerUpdateInfoDto;
import com.project.durumoongsil.teutoo.trainer.info.dto.response.TrainerInfoResDto;
import com.project.durumoongsil.teutoo.trainer.info.dto.response.TrainerSummaryResDto;
import com.project.durumoongsil.teutoo.trainer.info.repository.CareerImgRepository;
import com.project.durumoongsil.teutoo.trainer.info.repository.TrainerInfoRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtProgramResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.service.PtProgramService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerInfoServiceTest {

    @InjectMocks
    TrainerInfoService trainerInfoService;

    @Mock
    TrainerInfoRepository trainerInfoRepository;

    @Mock
    CareerImgRepository careerImgRepository;

    @Mock
    FileService fileService;

    @Mock
    PtProgramService ptProgramService;

    @Test
    @DisplayName("트레이너 소개 API 등록/갱신 테스트 - trainerInfo 등록 안된 상태")
    public void trainerInfoSaveOrUpdateTest() throws IOException {

        Member testMember = Member.builder()
                .id(1L)
                .name("aaaa")
                .email("aaaa@gmail.com")
                .password("1234")
                .address("good")
                .role(Role.TRAINER)
                .build();

        List<MultipartFile> imgList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            byte[] inputArray = "Test String".getBytes();
            MockMultipartFile mockMultipartFile = new MockMultipartFile("tempFileName" + i,inputArray);
            imgList.add(mockMultipartFile);
        }

        TrainerUpdateInfoDto testUpdateInfoDto = TrainerUpdateInfoDto.builder()
                        .simpleIntro("안녕하세요...")
                        .gymName("GYM")
                        .introContent("안녕하세요 안녕하세요 안녕하세요 ")
                        .careerImgList(imgList)
                        .build();

        when(trainerInfoRepository.findMemberByIdWithTrainerInfo(testMember.getEmail())).thenReturn(Optional.of(testMember));

        ArgumentCaptor<TrainerInfo> trainerInfoArgumentCaptor = ArgumentCaptor.forClass(TrainerInfo.class);

        trainerInfoService.saveOrUpdate(testMember.getEmail(), testUpdateInfoDto);


        verify(trainerInfoRepository).save(trainerInfoArgumentCaptor.capture());

        // trainerInfo 테스트
        TrainerInfo trainerInfo = trainerInfoArgumentCaptor.getValue();
        assertEquals(trainerInfo.getGymName(), testUpdateInfoDto.getGymName());
        assertEquals(trainerInfo.getIntroContent(), testUpdateInfoDto.getIntroContent());
        assertEquals(trainerInfo.getSimpleIntro(), testUpdateInfoDto.getSimpleIntro());
        assertEquals(trainerInfo.getMember(), testMember);

        // 자격사항 이미지 저장 테스트
        verify(fileService, times(10)).saveImgToDB(eq("trainer_info"), any(MultipartFile.class));
    }


    @Test
    @DisplayName("트레이너 소개 API 조회 테스트 - DTO 유효성 테스트")
    public void checkTrainerInfoResDtoValidationTest() {
        Member testMember = Member.builder()
                .id(1L)
                .name("aaaa")
                .email("aaaa@gmail.com")
                .password("1234")
                .address("good")
                .role(Role.TRAINER)
                .build();

        TrainerInfo testTrainerInfo = TrainerInfo.builder()
                        .id(1L)
                        .simpleIntro("안녕하세요...")
                        .gymName("GYM")
                        .introContent("안녕하세요 안녕하세요 안녕하세요")
                        .build();

        testMember.setTrainerInfo(testTrainerInfo);
        when(trainerInfoRepository.findMemberByIdWithTrainerInfo(1L)).thenReturn(Optional.of(testMember));

        // 이미지 임의 저장
        List<CareerImg> mockCareerImgList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String filePath = "path" + i;
            String fileName = "name" + i;

            CareerImg careerImg = new CareerImg(testTrainerInfo, new File(filePath, fileName));
            mockCareerImgList.add(careerImg);

            when(fileService.getImgUrl(filePath, fileName)).thenReturn("url" + i);
        }

        // pt 프로그램 이미지 임의 저장
        List<PtProgramResDto> mockPtProgramImgList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PtProgramResDto ptProgramResDto =PtProgramResDto.builder()
                    .ptProgramId((long) i + 1)
                    .price(9999)
                    .title("goood")
                    .content("good")
                    .build();
            mockPtProgramImgList.add(ptProgramResDto);
        }

        when(careerImgRepository.findByTrainerIdWithFile(1L)).thenReturn(mockCareerImgList);
        when(ptProgramService.getPtProgramListFromMember(testMember)).thenReturn(mockPtProgramImgList);

        // 호출
        TrainerInfoResDto trainerInfoResDto = trainerInfoService.getInfo(1L);

        // DTO 테스트
        assertEquals(testMember.getName(), trainerInfoResDto.getTrainerName());
        assertEquals(testMember.getAddress(), trainerInfoResDto.getTrainerAddress());

        assertEquals(testTrainerInfo.getGymName(), trainerInfoResDto.getGymName());
        assertEquals(testTrainerInfo.getSimpleIntro(), trainerInfoResDto.getSimpleIntro());
        assertEquals(testTrainerInfo.getGymName(), trainerInfoResDto.getGymName());
        assertEquals(testTrainerInfo.getIntroContent(), trainerInfoResDto.getIntroContent());

        for (int i = 0; i < 3; i++) {
            ImgResDto imgResDto = trainerInfoResDto.getCareerImgList().get(i);
            assertEquals("url" + i, imgResDto.getImgUrl());
            assertEquals("name" + i, imgResDto.getImgName());
        }

        for (int i = 0; i < 3; i++) {
            PtProgramResDto ptProgramResDto = trainerInfoResDto.getPtProgramResDtoList().get(i);
            assertEquals(ptProgramResDto.getPtProgramId(), i+1);
            assertEquals(ptProgramResDto.getPrice(), 9999);
        }
    }

    @Test
    @DisplayName("트레이너 소개 API 조회 테스트 - trainerInfo 등록안됨")
    public void checkErrorGetTrainerInfo() {
        Member testMember = Member.builder()
                .id(1L)
                .name("aaaa")
                .email("aaaa@gmail.com")
                .password("1234")
                .address("good")
                .role(Role.TRAINER)
                .build();

        when(trainerInfoRepository.findMemberByIdWithTrainerInfo(1L)).thenReturn(Optional.of(testMember));

        assertThrows(TrainerInfoNotFoundException.class, () -> trainerInfoService.getInfo(1L));
    }

    // 삭제될 이미지 존재시 테스트 케이스 작성해야함..


    @Test
    @DisplayName("트레이너 목록 API 조회 테스트")
    public void checkGetTrainerList() {
        String[] names = {"가가가", "김김김", "이이이", "박박박", "한한한"};
        String[] addresses = {"서울특별시", "경기도", "충청도", "강원도", "전라도"};

        List<TrainerInfo> testTrainerInfoList = new ArrayList<>();

        for (int i = 0; i < names.length; i++) {
            Member testMember = Member.builder()
                    .id((long) (i+1))
                    .name(names[i])
                    .address(addresses[i])
                    .role(Role.TRAINER)
                    .profileImageName("adsdadadsa")
                    .profileImagePath("sadadasdsadsadadsa good")
                    .build();

            TrainerInfo testTrainerInfo = TrainerInfo.builder()
                    .id((long) (i+1))
                    .simpleIntro("안녕하세요...")
                    .gymName("GYM"+i)
                    .introContent("안녕하세요 안녕하세요 안녕하세요")
                    .member(testMember)
                    .build();

            testTrainerInfoList.add(testTrainerInfo);
        }

        TrainerListReqDto trainerListReqDtoMock = mock(TrainerListReqDto.class);

        Page<TrainerInfo> mockTrainerInfoPage = new PageImpl<>(testTrainerInfoList);

        when(trainerInfoRepository.findBySearchCondition(trainerListReqDtoMock)).thenReturn(mockTrainerInfoPage);

        List<TrainerSummaryResDto> trainerSummaryResDtoList = trainerInfoService.getTrainerList(trainerListReqDtoMock).getContent();

        // return 된 값 크기 검사
        assertEquals(trainerSummaryResDtoList.size(), 5);

        // fileService 호출
        verify(fileService, times(5)).getImgUrl(anyString(), anyString());

        // return 된 값 유효 검사
        for (int i = 0; i < 5; i++) {
            // return 된 값
            TrainerSummaryResDto trainerSummaryResDto = trainerSummaryResDtoList.get(i);

            // input 값
            TrainerInfo testTrainerInfo = testTrainerInfoList.get(i);

            // 검사
            assertEquals(trainerSummaryResDto.getTrainerName(), testTrainerInfo.getMember().getName());
            assertEquals(trainerSummaryResDto.getGymName(), testTrainerInfo.getGymName());
            assertEquals(trainerSummaryResDto.getSimpleIntro(), testTrainerInfo.getSimpleIntro());
        }
    }

}