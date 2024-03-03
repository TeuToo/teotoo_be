package com.project.durumoongsil.teutoo.trainer.ptprogram.service;

import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.security.service.SecurityService;
import com.project.durumoongsil.teutoo.trainer.info.repository.TrainerInfoRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtImg;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramRegDto;
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

    @Test
    @DisplayName("PT 프로그램 업데이트 테스트")
    public void updatePtProgramTest() {

        String memberEmail = "aaa@aaa.com";

        List<String> delPtImgList = List.of("a.png", "b.png", "c.png");

        PtProgramUpdateDto ptProgramUpdateDto = new PtProgramUpdateDto();
        ptProgramUpdateDto.setProgramId(1L);
        ptProgramUpdateDto.setTitle("nice");
        ptProgramUpdateDto.setContent("nice");
        ptProgramUpdateDto.setPrice(99);
        ptProgramUpdateDto.setPtCnt(99);
        ptProgramUpdateDto.setDelPtImgList(delPtImgList);
        ptProgramUpdateDto.setAddPtImgList(new ArrayList<>());

        PtProgram ptProgramMock = mock(PtProgram.class);

        when(ptProgramMock.getId()).thenReturn(1L);
        when(securityService.getLoginedUserEmail()).thenReturn(memberEmail);

        when(ptProgramRepository.findByPtProgramByIdAndMemberEmail(ptProgramUpdateDto.getProgramId(), memberEmail))
                .thenReturn(Optional.ofNullable(ptProgramMock));

        List<PtImg> ptImgList = new ArrayList<>();

        String[] fileName = {"a.png", "b.png", "c.png"};
        for (int i = 0; i < 3; i++) {
            ptImgList.add(new PtImg(null, new File("a", fileName[i])));
        }

        when(ptImgRepository
                .findAllByProgramIdAndImgNameListWithFile(ptProgramUpdateDto.getProgramId(),
                delPtImgList)).thenReturn(ptImgList);


        // 호출
        ptProgramService.update(ptProgramUpdateDto);


        // PtProgram 수정
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

        // 삭제 이미지
        ArgumentCaptor<List<Long>> delImgIdListCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<String>> savedDelImgListCaptor = ArgumentCaptor.forClass(List.class);

        verify(ptImgRepository).deleteAllById(delImgIdListCaptor.capture());
        verify(fileService).deleteImgListToDB(eq("pt_program"), savedDelImgListCaptor.capture());

        assertEquals(delImgIdListCaptor.getValue().size(), 3);
        assertEquals(savedDelImgListCaptor.getValue().size(), 3);
    }


}