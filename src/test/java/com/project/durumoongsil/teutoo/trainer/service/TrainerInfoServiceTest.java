package com.project.durumoongsil.teutoo.trainer.service;

import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.trainer.domain.CareerImg;
import com.project.durumoongsil.teutoo.trainer.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.dto.TrainerUpdateInfoDto;
import com.project.durumoongsil.teutoo.trainer.repository.CareerImgRepository;
import com.project.durumoongsil.teutoo.trainer.repository.TrainerInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class TrainerInfoServiceTest {

    @Autowired
    TrainerInfoService trainerInfoService;

    @MockBean
    TrainerInfoRepository trainerInfoRepository;

    @MockBean
    CareerImgRepository careerImgRepository;

    @MockBean
    FileService fileService;

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
                        .careerImages(imgList)
                        .build();


        when(trainerInfoRepository.findMemberByIdWithTrainerInfo(1L)).thenReturn(Optional.of(testMember));

        ArgumentCaptor<TrainerInfo> trainerInfoArgumentCaptor = ArgumentCaptor.forClass(TrainerInfo.class);

        trainerInfoService.saveOrUpdate(1L, testUpdateInfoDto);

        verify(trainerInfoRepository).save(trainerInfoArgumentCaptor.capture());

        // trainerInfo 테스트
        TrainerInfo trainerInfo = trainerInfoArgumentCaptor.getValue();
        assertEquals(trainerInfo.getGymName(), testUpdateInfoDto.getGymName());
        assertEquals(trainerInfo.getIntroContent(), testUpdateInfoDto.getIntroContent());
        assertEquals(trainerInfo.getSimpleIntro(), testUpdateInfoDto.getSimpleIntro());
        assertEquals(trainerInfo.getMember(), testMember);

        // 자격사항 이미지 저장 테스트
        verify(fileService, times(10)).saveImg(eq("trainer_info"), any(MultipartFile.class));
    }
}