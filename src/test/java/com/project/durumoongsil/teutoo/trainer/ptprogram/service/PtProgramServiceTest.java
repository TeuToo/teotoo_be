package com.project.durumoongsil.teutoo.trainer.ptprogram.service;

import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.security.service.SecurityService;
import com.project.durumoongsil.teutoo.trainer.info.repository.TrainerInfoRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramRegDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    @Test
    @DisplayName("PT 프로그램 등록 테스트")
    public void registerPtProgramTest() throws IOException {

        List<MultipartFile> imgList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            byte[] inputArray = "Test String".getBytes();
            MockMultipartFile mockMultipartFile = new MockMultipartFile("tempFileName" + i,inputArray);
            imgList.add(mockMultipartFile);
        }

        PtProgramRegDto ptProgramRegDto = new PtProgramRegDto();
        ptProgramRegDto.setPrice(99999);
        ptProgramRegDto.setPtCnt(9999);
        ptProgramRegDto.setContent("abc");
        ptProgramRegDto.setTitle("abc");
        ptProgramRegDto.setAddPtImgList(imgList);

        when(securityService.getLoginedUserEmail()).thenReturn("aaa@aaa.com");
        when(trainerInfoRepository.findTrainerInfoIdByMemberEmail("aaa@aaa.com"))
                .thenReturn(OptionalLong.of(1L));

        ArgumentCaptor<PtProgram> ptProgramArgumentCaptor = ArgumentCaptor.forClass(PtProgram.class);

        ptProgramService.register(ptProgramRegDto);
        verify(ptProgramRepository).save(ptProgramArgumentCaptor.capture());

        PtProgram savePtProgram = ptProgramArgumentCaptor.getValue();
        assertEquals(savePtProgram.getPrice(), ptProgramRegDto.getPrice());
        assertEquals(savePtProgram.getPtCnt(), ptProgramRegDto.getPtCnt());
        assertEquals(savePtProgram.getTitle(), ptProgramRegDto.getTitle());
        assertEquals(savePtProgram.getContent(), ptProgramRegDto.getContent());
        assertEquals(savePtProgram.getTrainerInfo().getId(), 1L);

        verify(fileService, times(5)).saveImgToDB(eq("pt_program"), any(MultipartFile.class));
    }


}