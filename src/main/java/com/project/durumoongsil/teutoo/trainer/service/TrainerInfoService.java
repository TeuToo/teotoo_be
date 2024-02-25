package com.project.durumoongsil.teutoo.trainer.service;

import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.trainer.domain.CareerImg;
import com.project.durumoongsil.teutoo.trainer.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.dto.TrainerInfoResDto;
import com.project.durumoongsil.teutoo.trainer.dto.TrainerUpdateInfoDto;
import com.project.durumoongsil.teutoo.trainer.repository.CareerImgRepository;
import com.project.durumoongsil.teutoo.trainer.repository.TrainerInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerInfoService {

    private final TrainerInfoRepository trainerInfoRepository;
    private final CareerImgRepository careerImgRepository;
    private final FileService fileService;

    // 트레이너 소개 페이지 등록 및 갱신
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
                savedFile = fileService.saveImgToDB("trainer_info", file);
            } catch (IOException e) {
                throw new RuntimeException("자격사항 이미지 저장에 실패 하였습니다. 다시 시도 해주세요.");
            }
            CareerImg careerImg = new CareerImg(trainerInfo, savedFile);
            careerImgRepository.save(careerImg);
        }
    }

    // 트레이너 소개 페이지 데이터 조회
    @Transactional(readOnly = true)
    public TrainerInfoResDto getInfo(Long trainerId) {
        Member member = trainerInfoRepository.findMemberByIdWithTrainerInfo(trainerId)
                .orElseThrow(() -> new NotFoundUserException("사용자를 찾을 수 없습니다."));

        // 만약, 트레이너 소개 페이지가 등록되어있지 않다면,
        TrainerInfo trainerInfo = member.getTrainerInfo();
        if (trainerInfo == null) {
            throw new NotFoundUserException("해당 사용자의 소개 데이터를 찾을 수 없습니다.");
        }

        List<String> careerImgUrls = new ArrayList<>();

        for (CareerImg careerImg : careerImgRepository.findByTrainerIdWithFile(trainerId)) {
            String imgUrl = fileService.getImgUrl(careerImg.getFile().getFilePath(), careerImg.getFile().getFileName());
            careerImgUrls.add(imgUrl);
        }

        String trainerImgUrl = null;
        if (member.getProfile_image_path() != null && member.getProfile_image_name() != null)
            trainerImgUrl = fileService.getImgUrl(member.getProfile_image_path(), member.getProfile_image_name());

        TrainerInfoResDto trainerInfoResDto = TrainerInfoResDto.builder()
                .trainerAddress(member.getAddress())
                .trainerName(member.getName())
                .trainerImgUrl(trainerImgUrl)
                .gymName(trainerInfo.getGymName())
                .simpleIntro(trainerInfo.getSimpleIntro())
                .introContent(trainerInfo.getIntroContent())
                .careerImgUrls(careerImgUrls)
                .build();

        return trainerInfoResDto;
    }
}
