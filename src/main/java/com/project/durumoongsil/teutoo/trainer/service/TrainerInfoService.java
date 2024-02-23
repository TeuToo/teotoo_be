package com.project.durumoongsil.teutoo.trainer.service;

import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import com.project.durumoongsil.teutoo.trainer.domain.CareerImg;
import com.project.durumoongsil.teutoo.trainer.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.dto.TrainerUpdateInfoDto;
import com.project.durumoongsil.teutoo.trainer.repository.CareerImgRepository;
import com.project.durumoongsil.teutoo.trainer.repository.TrainerInfoCustomRepositoryImpl;
import com.project.durumoongsil.teutoo.trainer.repository.TrainerInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TrainerInfoService {

    private final TrainerInfoRepository trainerInfoRepository;
    private final CareerImgRepository careerImgRepository;
    private final FileService fileService;

    // 트레이너 소개 페이지 등록 dto -> dao
    @Transactional
    public void saveOrUpdate(Long trainerId, TrainerUpdateInfoDto trainerUpdateInfoDto) {

        Member member = trainerInfoRepository.findMemberByIdWithTrainerInfo(trainerId)
                .orElseThrow(() -> new NotFoundUserException("사용자를 찾을 수 없습니다."));

        TrainerInfo trainerInfo = member.getTrainerInfo();
        if (trainerInfo == null) {
            // trainerInfo 가 없을 때,
            trainerInfo = TrainerInfo.builder()
                    .introContent(trainerUpdateInfoDto.getIntroContent())
                    .gymName(trainerUpdateInfoDto.getGymName())
                    .simpleIntro(trainerUpdateInfoDto.getSimpleIntro())
                    .member(member)
                    .build();
            // 저장
            trainerInfoRepository.save(trainerInfo);
        } else {
            // trainerInfo 가 존재한다면, dirty checking 업데이트
            trainerInfo.updateSimpleIntro(trainerUpdateInfoDto.getSimpleIntro());
            trainerInfo.updateGymName(trainerUpdateInfoDto.getGymName());
            trainerInfo.updateIntroContent(trainerUpdateInfoDto.getIntroContent());
        }

        // 자격사항 이미지 저장
        for (MultipartFile file : trainerUpdateInfoDto.getCareerImages()) {
            File savedFile = null;
            try {
                savedFile = fileService.saveImg("trainer_info", file);
            } catch (IOException e) {
                throw new RuntimeException("자격사항 이미지 저장에 실패 하였습니다. 다시 시도 해주세요.");
            }
            CareerImg careerImg = new CareerImg(trainerInfo, savedFile);
            careerImgRepository.save(careerImg);
        }
    }
}
