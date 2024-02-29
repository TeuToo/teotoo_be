package com.project.durumoongsil.teutoo.trainer.ptprogram.service;


import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import com.project.durumoongsil.teutoo.security.service.SecurityService;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.info.repository.TrainerInfoRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtImg;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramRegDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtImgRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtProgramRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class PtProgramServiceIntegrationTest {

    @Autowired
    PtProgramService ptProgramService;

    @Autowired
    TrainerInfoRepository trainerInfoRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PtProgramRepository ptProgramRepository;

    @MockBean
    SecurityService securityService;

    @MockBean
    FileService fileService;

    @MockBean
    PtImgRepository ptImgRepository;

    @Test
    @DisplayName("PT 프로그램 등록 테스트")
    @Transactional
    public void registerPtProgramTest() throws IOException {
        when(securityService.getLoginedUserEmail()).thenReturn("aaaa@gmail.com");

        when(fileService.saveImgToDB(eq("pt_program"), any(MultipartFile.class)))
                .thenReturn(mock(File.class));

        Member testMember = Member.builder()
                .id(1L)
                .name("aaaa")
                .email("aaaa@gmail.com")
                .password("1234")
                .address("good")
                .role(Role.TRAINER)
                .build();

        TrainerInfo testTrainerInfo = TrainerInfo.builder()
                .simpleIntro("안녕하세요...")
                .gymName("GYM")
                .introContent("안녕하세요 안녕하세요 안녕하세요")
                .member(testMember)
                .build();

        Member savedMember = memberRepository.save(testMember);
        TrainerInfo savedTrainerInfo = trainerInfoRepository.save(testTrainerInfo);

        List<MultipartFile> mockImgList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            byte[] inputArray = "Test String".getBytes();
            MockMultipartFile mockMultipartFile = new MockMultipartFile("tempFileName" + i,inputArray);
            mockImgList.add(mockMultipartFile);
        }

        PtProgramRegDto ptProgramRegDto = new PtProgramRegDto();
        ptProgramRegDto.setTitle("nice");
        ptProgramRegDto.setContent("good");
        ptProgramRegDto.setPrice(1000);
        ptProgramRegDto.setPtCnt(500);
        ptProgramRegDto.setProgramImgList(mockImgList);

        ptProgramService.register(ptProgramRegDto);

        // DB에 저장된 값 유효 검사
        PtProgram savedPtProgram = ptProgramRepository.findAll().get(0);

        assertEquals(savedPtProgram.getPrice(), ptProgramRegDto.getPrice());
        assertEquals(savedPtProgram.getPtCnt(), ptProgramRegDto.getPtCnt());
        assertEquals(savedPtProgram.getTitle(), ptProgramRegDto.getTitle());
        assertEquals(savedPtProgram.getContent(), ptProgramRegDto.getContent());
        assertEquals(savedPtProgram.getTrainerInfo().getId(), savedTrainerInfo.getId());

        verify(ptImgRepository, times(10)).save(any(PtImg.class));
    }

}