package com.project.durumoongsil.teutoo.trainer.ptprogram.service;

import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.security.service.SecurityService;
import com.project.durumoongsil.teutoo.trainer.info.domain.CareerImg;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.info.repository.TrainerInfoRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtImg;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.TrainerProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramRegDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtImgRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.TrainerProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PtProgramService {

    private final SecurityService securityService;
    private final TrainerInfoRepository trainerInfoRepository;
    private final TrainerProgramRepository trainerProgramRepository;
    private final PtImgRepository ptImgRepository;
    private final FileService fileService;

    @Transactional
    public void register(PtProgramRegDto ptProgramRegDto) {

        // security에서, member를 얻고, member의 trainer_info와 맵핑을 시켜주어야 한다..
        String userEmail = securityService.getLoginedUserEmail();

        // member 의 유무 파악과 trainer info id만 되므로, 이 부분은 개선필요!!
        Member member = trainerInfoRepository.findMemberByIdWithTrainerInfo(userEmail)
                .orElseThrow(() -> new NotFoundUserException("사용자를 찾을 수 없습니다."));

        TrainerInfo trainerInfo = member.getTrainerInfo();

        // 익셉션 핸들링 필요
        if (trainerInfo == null)
            throw new RuntimeException();

        TrainerProgram trainerProgram = TrainerProgram.builder()
                .content(ptProgramRegDto.getContent())
                .price(ptProgramRegDto.getPrice())
                .ptCnt(ptProgramRegDto.getPtCnt())
                .title(ptProgramRegDto.getTitle())
                .trainerInfo(trainerInfo)
                .build();

        trainerProgramRepository.save(trainerProgram);

        // pt 프로그램 이미지 저장
        for (MultipartFile file : ptProgramRegDto.getProgramImgList()) {
            try {
                File savedFile = fileService.saveImgToDB("pt_program", file);
                PtImg careerImg = new PtImg(trainerProgram, savedFile);
                ptImgRepository.save(careerImg);
            } catch (IOException e) {
                // 익셉션 핸들링 제어 필요
                throw new RuntimeException("자격사항 이미지 저장에 실패 하였습니다. 다시 시도 해주세요.");
            }
        }
    }

}
