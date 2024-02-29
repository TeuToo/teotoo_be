package com.project.durumoongsil.teutoo.trainer.ptprogram.service;

import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.security.service.SecurityService;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.info.repository.TrainerInfoRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtImg;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramRegDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramUpdateDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtImgRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PtProgramService {

    private final SecurityService securityService;
    private final TrainerInfoRepository trainerInfoRepository;
    private final PtProgramRepository ptProgramRepository;
    private final PtImgRepository ptImgRepository;
    private final FileService fileService;

    @Transactional
    public void register(PtProgramRegDto ptProgramRegDto) {

        String userEmail = securityService.getLoginedUserEmail();

        // trainer info id 만 조회,
        Long trainerInfoId = trainerInfoRepository.findTrainerInfoIdByMemberEmail(userEmail)
                .orElseThrow(() -> new NotFoundUserException("트레이너 소개 등록 정보를 찾을 수 없습니다."));

        TrainerInfo trainerInfo = TrainerInfo.builder()
                .id(trainerInfoId)
                .build();

        PtProgram ptProgram = PtProgram.builder()
                .content(ptProgramRegDto.getContent())
                .price(ptProgramRegDto.getPrice())
                .ptCnt(ptProgramRegDto.getPtCnt())
                .title(ptProgramRegDto.getTitle())
                .trainerInfo(trainerInfo)
                .build();

        ptProgramRepository.save(ptProgram);

        // pt 프로그램 이미지 저장
        for (MultipartFile file : ptProgramRegDto.getProgramImgList()) {
            try {
                File savedFile = fileService.saveImgToDB("pt_program", file);
                PtImg careerImg = new PtImg(ptProgram, savedFile);
                ptImgRepository.save(careerImg);
            } catch (IOException e) {
                // 익셉션 핸들링 제어 필요
                throw new RuntimeException("자격사항 이미지 저장에 실패 하였습니다. 다시 시도 해주세요.");
            }
        }
    }

    @Transactional
    public void update(PtProgramUpdateDto ptProgramUpdateDto) {
        String userEmail = securityService.getLoginedUserEmail();

        PtProgram ptProgram = ptProgramRepository.findByPtProgramByIdAndMemberEmail(ptProgramUpdateDto.getProgramId(), userEmail)
                .orElseThrow(() -> new NotFoundUserException("트레이너 프로그램 등록 정보를 찾을 수 없습니다."));

        // PtProgram 수정
        ptProgram.updatePrice(ptProgramUpdateDto.getPrice());
        ptProgram.updatePtCnt(ptProgramUpdateDto.getPtCnt());
        ptProgram.updateTitle(ptProgramUpdateDto.getTitle());
        ptProgram.updateContent(ptProgramUpdateDto.getContent());

        // 사용자가 삭제한 이미지가 존재한다면,
        if (!ptProgramUpdateDto.getDeletedImgList().isEmpty()) {
            List<String> delImgList = ptProgramUpdateDto.getDeletedImgList();

            // pt program id하고, 삭제될 이미지를 통해, PtImg 조회 (사용자의 이메일로 우선 조회했기 때문에, 본인만 가능)
            List<PtImg> ptImgList = ptImgRepository.findAllByProgramIdAndImgNameList(ptProgram.getId(), delImgList);
            List<Long> delImgIdList = ptImgList.stream().map(PtImg::getId).toList();
            ptImgRepository.deleteAllById(delImgIdList);

            List<String> filterDelImgList = ptImgList.stream().map(ptImg -> ptImg.getFile().getFileName()).toList();

            fileService.deleteImgListToDB("pt_program", filterDelImgList);
        }

        // 자격사항 이미지 저장
        for (MultipartFile file : ptProgramUpdateDto.getAddProgramImgList()) {
            // 익셉션 핸들링 제어 필요
            try {
                File savedFile = fileService.saveImgToDB("pt_program", file);
                PtImg ptImg = new PtImg(ptProgram, savedFile);
                ptImgRepository.save(ptImg);
            } catch (IOException e) {
                throw new RuntimeException("자격사항 이미지 저장에 실패 하였습니다. 다시 시도 해주세요.");
            }
        }
    }



}
