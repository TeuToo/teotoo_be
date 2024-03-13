package com.project.durumoongsil.teutoo.trainer.ptprogram.service;

import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import com.project.durumoongsil.teutoo.security.service.SecurityService;
import com.project.durumoongsil.teutoo.trainer.info.repository.TrainerInfoRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtImg;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramManageResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramRegDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramUpdateDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtImgRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtProgramRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PtProgramServiceTest {

    @InjectMocks
    PtProgramService ptProgramService;

    @Mock
    SecurityService securityService;
    @Mock
    TrainerInfoRepository trainerInfoRepository;
    @Mock
    PtProgramRepository ptProgramRepository;
    @Mock
    PtImgRepository ptImgRepository;
    @Mock
    FileService fileService;
    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("PT 프로그램 등록 테스트")
    public void registerPtProgramTest() throws IOException {

        List<MultipartFile> imgList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            byte[] inputArray = "Test String".getBytes();
            MockMultipartFile mockMultipartFile = new MockMultipartFile("tempFileName" + i,inputArray);
            imgList.add(mockMultipartFile);
        }

        PtProgramRegDto ptProgramRegDto = getTestPtProgramRegDto(imgList);

        when(securityService.getLoginedUserEmail()).thenReturn("aaa@aaa.com");
        when(trainerInfoRepository.findTrainerInfoIdByMemberEmail("aaa@aaa.com"))
                .thenReturn(OptionalLong.of(1L));

        // 테스트 실행
        ptProgramService.register(ptProgramRegDto);

        ArgumentCaptor<PtProgram> ptProgramArgumentCaptor = ArgumentCaptor.forClass(PtProgram.class);
        // 검증
        verify(ptProgramRepository).save(ptProgramArgumentCaptor.capture());
        verifyPtProgram(ptProgramArgumentCaptor.getValue(), ptProgramRegDto);
        verify(fileService, times(imgList.size())).saveImgToDB(eq("pt_program"), any(MultipartFile.class));
    }

    private PtProgramRegDto getTestPtProgramRegDto(List<MultipartFile> addImgList) {
        PtProgramRegDto ptProgramRegDto = new PtProgramRegDto();
        ptProgramRegDto.setPrice(99999);
        ptProgramRegDto.setPtCnt(9999);
        ptProgramRegDto.setContent("abc");
        ptProgramRegDto.setTitle("abc");
        ptProgramRegDto.setAddPtImgList(addImgList);

        return ptProgramRegDto;
    }

    private void verifyPtProgram(PtProgram savePtProgram, PtProgramRegDto ptProgramRegDto) {
        assertEquals(savePtProgram.getPrice(), ptProgramRegDto.getPrice());
        assertEquals(savePtProgram.getPtCnt(), ptProgramRegDto.getPtCnt());
        assertEquals(savePtProgram.getTitle(), ptProgramRegDto.getTitle());
        assertEquals(savePtProgram.getContent(), ptProgramRegDto.getContent());
        assertEquals(savePtProgram.getTrainerInfo().getId(), 1L);
    }



    @Test
    @DisplayName("PT 프로그램 업데이트 테스트")
    public void updatePtProgramTest() {

        String memberEmail = "aaa@aaa.com";
        Long reqTrainerId = 1L;

        // 삭제 될 이미지 리스트
        List<String> delPtImgList = List.of("a.png", "b.png", "c.png");

        PtProgramUpdateDto ptProgramUpdateDto = getPtProgramUpdateDto(delPtImgList);

        PtProgram ptProgramMock = mock(PtProgram.class);

        when(securityService.getLoginedUserEmail()).thenReturn(memberEmail);
        when(ptProgramRepository.findByIdAndMemberEmailWithPtImgAndFile(reqTrainerId, memberEmail))
                .thenReturn(Optional.of(ptProgramMock));

        // 이미지 리스트 설정
        List<PtImg> ptImgList = getTestPtImgList(reqTrainerId, delPtImgList);
        when(ptProgramMock.getPtImgList()).thenReturn(ptImgList);

        // 테스트 실행
        ptProgramService.update(1L, ptProgramUpdateDto);

        // 검증
        verifyPtProgramUpdate(ptProgramMock, ptProgramUpdateDto);
        verifyImgDeletion(ptImgList, ptProgramUpdateDto.getDelPtImgList());
    }

    private PtProgramUpdateDto getPtProgramUpdateDto(List<String> delPtImgList) {
        PtProgramUpdateDto ptProgramUpdateDto = new PtProgramUpdateDto();
        ptProgramUpdateDto.setTitle("nice");
        ptProgramUpdateDto.setContent("nice");
        ptProgramUpdateDto.setPrice(99);
        ptProgramUpdateDto.setPtCnt(99);
        ptProgramUpdateDto.setDelPtImgList(delPtImgList);
        ptProgramUpdateDto.setAddPtImgList(new ArrayList<>());;

        return ptProgramUpdateDto;
    }

    private List<PtImg> getTestPtImgList(Long programId, List<String> imgNames) {
        List<PtImg> imgList = new ArrayList<>();
        for (String imgName : imgNames) {
            imgList.add(new PtImg(null, new File(null, imgName)));
        }
        return imgList;
    }

    private void verifyPtProgramUpdate(PtProgram ptProgramMock, PtProgramUpdateDto ptProgramUpdateDto) {
        ArgumentCaptor<Integer> priceCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> ptCntCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

        verify(ptProgramMock).updatePrice(priceCaptor.capture());
        verify(ptProgramMock).updatePtCnt(ptCntCaptor.capture());
        verify(ptProgramMock).updateTitle(titleCaptor.capture());
        verify(ptProgramMock).updateContent(contentCaptor.capture());

        assertEquals(ptCntCaptor.getValue(), ptProgramUpdateDto.getPtCnt());
        assertEquals(priceCaptor.getValue(), ptProgramUpdateDto.getPrice());
        assertEquals(titleCaptor.getValue(), ptProgramUpdateDto.getTitle());
        assertEquals(contentCaptor.getValue(), ptProgramUpdateDto.getContent());
    }

    private void verifyImgDeletion(List<PtImg> ptImgList, List<String> delPtImgList) {
        ArgumentCaptor<List<Long>> delImgIdListCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<String>> savedDelImgListCaptor = ArgumentCaptor.forClass(List.class);

        verify(ptImgRepository).deleteAllById(delImgIdListCaptor.capture());
        verify(fileService).deleteImgListToDB(eq("pt_program"), savedDelImgListCaptor.capture());

        assertEquals(delImgIdListCaptor.getValue().size(), delPtImgList.size());
        assertEquals(savedDelImgListCaptor.getValue().size(), delPtImgList.size());
    }

    @Test
    @DisplayName("PT 프로그램 관리 페이지 데이터 조회")
    public void getPtProgramListForManagementTest() {
        String memberEmail = "aaa@aaa.com";

        when(securityService.getLoginedUserEmail()).thenReturn(memberEmail);
        Member testMember = getTestMember();

        when(memberRepository.findMemberByEmail(memberEmail)).thenReturn(Optional.of(testMember));
        List<PtProgram> testPtProgramList = getTestPtProgramList();

        when(ptProgramRepository.findByMemberEmailWithPtImg(memberEmail)).thenReturn(testPtProgramList);

        when(fileService.getImgUrl(anyString(), anyString())).thenReturn("testUrl");

        PtProgramManageResDto ptProgramManageResDto = ptProgramService.getPtProgramListForManagement();

        assertEquals(ptProgramManageResDto.getTrainerName(), testMember.getName());
        List<PtProgramResDto> ptProgramResDtoList = ptProgramManageResDto.getPtProgramResList();
        for (int i = 1; i <= 3; i++) {
            PtProgramResDto ptProgramResDto = ptProgramResDtoList.get(i-1);
            assertEquals(ptProgramResDto.getTitle(), "good" + i);
            assertEquals(ptProgramResDto.getContent(), "good" + i);
            assertEquals(ptProgramResDto.getPtProgramId(), i);
            assertEquals(ptProgramResDto.getPrice(), i);
            assertEquals(ptProgramResDto.getPtCnt(), i);

            List<ImgResDto> imgResDtoList = ptProgramResDto.getPtProgramImgList();
            String[] nameStr = {"a", "b", "c"};
            for (int j = 1; j <= 3; j++) {
                ImgResDto imgResDto = imgResDtoList.get(j - 1);
                assertEquals(imgResDto.getImgName(), nameStr[j-1] + j);
            }
        }
    }

    private Member getTestMember() {
         return Member.builder()
                .id(1L)
                .name("aaaa")
                .email("aaaa@gmail.com")
                .password("1234")
                .address("good")
                .role(Role.TRAINER)
                .build();
    }

    private List<PtProgram> getTestPtProgramList() {
        List<PtProgram> testPtProgramList = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            PtProgram testPtProgram = PtProgram.builder()
                    .title("good" + i)
                    .content("good" + i)
                    .price((int) i)
                    .ptCnt((int) i)
                    .build();
            ReflectionTestUtils.setField(testPtProgram, "id", (long) i);

            List<PtImg> ptImgList = new ArrayList<>();

            String[] nameStr = {"a", "b", "c"};
            for (int j = 1; j <= 3; j++) {
                File file = new File(nameStr[j-1] + j, nameStr[j-1] + j);
                PtImg ptImg = new PtImg(testPtProgram, file);
                ptImgList.add(ptImg);
            }
            ReflectionTestUtils.setField(testPtProgram, "ptImgList", ptImgList);

            testPtProgramList.add(testPtProgram);
        }

        return testPtProgramList;
    }



}