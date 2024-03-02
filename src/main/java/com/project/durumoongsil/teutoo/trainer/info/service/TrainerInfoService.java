package com.project.durumoongsil.teutoo.trainer.info.service;

import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.trainer.info.domain.CareerImg;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.info.dto.*;
import com.project.durumoongsil.teutoo.trainer.info.repository.CareerImgRepository;
import com.project.durumoongsil.teutoo.trainer.info.repository.TrainerInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public void saveOrUpdate(String userEmail, TrainerUpdateInfoDto trainerUpdateInfoDto) {

        Member member = trainerInfoRepository.findMemberByIdWithTrainerInfo(userEmail)
                .orElseThrow(() -> new NotFoundUserException("해당 트레이너를 찾을 수 없습니다."));

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


            // 사용자가 삭제한 이미지가 존재한다면,
            if (!trainerUpdateInfoDto.getDeletedImgList().isEmpty()) {
                // 삭제 될 CareerImg 조회
                // 이 부분 수정해야함
                List<CareerImg> delCareerImgList = careerImgRepository.findByFileNameWithCareerImg(trainerInfo.getId(), trainerUpdateInfoDto.getDeletedImgList());

                // 삭제될 CareerImg id 획득
                List<Long> delCareerImgIdList = delCareerImgList.stream().map(CareerImg::getId).toList();
                List<String> delFileNameList = delCareerImgList.stream().map(careerImg -> careerImg.getFile().getFileName()).toList();

                careerImgRepository.deleteAllById(delCareerImgIdList);
                fileService.deleteImgListToDB("trainer_info", delFileNameList);
            }
        }

        // 자격사항 이미지 저장
        for (MultipartFile file : trainerUpdateInfoDto.getCareerImgList()) {
            // 익셉션 핸들링 제어 필요
            try {
                File savedFile = fileService.saveImgToDB("trainer_info", file);
                CareerImg careerImg = new CareerImg(trainerInfo, savedFile);
                careerImgRepository.save(careerImg);
            } catch (IOException e) {
                throw new RuntimeException("자격사항 이미지 저장에 실패 하였습니다. 다시 시도 해주세요.");
            }
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

        List<ImgResDto> careerImgList = new ArrayList<>();

        // 자격사항 이미지 불러옴
        for (CareerImg careerImg : careerImgRepository.findByTrainerIdWithFile(trainerId)) {
            String imgName = careerImg.getFile().getFileName();
            String imgUrl = fileService.getImgUrl(careerImg.getFile().getFilePath(), careerImg.getFile().getFileName());

            careerImgList.add(new ImgResDto(imgName, imgUrl));
        }

        // 트레이너 프로필 이미지
        String trainerImgUrl = null;
        if (member.getProfileImageName() != null && member.getProfileOriginalImageName() != null)
            trainerImgUrl = fileService.getImgUrl(member.getProfileImageName(), member.getProfileOriginalImageName());

        return TrainerInfoResDto.builder()
                .trainerInfoId(trainerInfo.getId())
                .trainerAddress(member.getAddress())
                .trainerName(member.getName())
                .trainerImgUrl(trainerImgUrl)
                .gymName(trainerInfo.getGymName())
                .simpleIntro(trainerInfo.getSimpleIntro())
                .introContent(trainerInfo.getIntroContent())
                .careerImgList(careerImgList)
                .build();
    }

    public Page<TrainerSummaryResDto> getTrainerList(TrainerListReqDto TrainerListReqDto) {
        Page<TrainerInfo> trainerInfoPage = trainerInfoRepository.findBySearchCondition(TrainerListReqDto);

        return trainerInfoPage.map(trainerInfo -> {
            Member member = trainerInfo.getMember();

            return TrainerSummaryResDto.builder()
                    .trainerInfoId(trainerInfo.getId())
                    .trainerName(member.getName())
                    .reviewScore(trainerInfo.getReviewScore())
                    .reviewCnt(trainerInfo.getReviewCnt())
                    .simpleIntro(trainerInfo.getSimpleIntro())
                    .gymName(trainerInfo.getGymName())
                    .imgResDto(new ImgResDto(
                            member.getProfileOriginalImageName(),
                            fileService.getImgUrl(
                                    member.getProfileImageName(), member.getProfileOriginalImageName())
                            )
                    )
                    .build();
        });
    }
}
