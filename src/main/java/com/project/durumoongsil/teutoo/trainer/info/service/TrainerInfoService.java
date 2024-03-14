package com.project.durumoongsil.teutoo.trainer.info.service;

import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.common.domain.FilePath;
import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.trainer.info.domain.CareerImg;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.info.dto.*;
import com.project.durumoongsil.teutoo.trainer.info.repository.CareerImgRepository;
import com.project.durumoongsil.teutoo.trainer.info.repository.TrainerInfoRepository;
import com.project.durumoongsil.teutoo.trainer.info.util.TrainerInfoConverter;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtProgramResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.service.PtProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerInfoService {

    private final TrainerInfoRepository trainerInfoRepository;
    private final CareerImgRepository careerImgRepository;
    private final FileService fileService;
    private final PtProgramService ptProgramService;
    private final TrainerInfoConverter converter = new TrainerInfoConverter();

    // 트레이너 소개 페이지 등록 및 갱신
    @Transactional
    public void saveOrUpdate(String memberEmail, TrainerUpdateInfoDto trainerUpdateInfoDto) {

        Member member = trainerInfoRepository.findMemberByIdWithTrainerInfo(memberEmail)
                .orElseThrow(() -> new NotFoundUserException("해당 트레이너를 찾을 수 없습니다."));

        TrainerInfo trainerInfo = member.getTrainerInfo();
        if (trainerInfo == null) {
            // trainerInfo 가 없을 때,
            trainerInfo = converter.toTrainerInfo(trainerUpdateInfoDto, member);
            // 저장
            trainerInfoRepository.save(trainerInfo);
        } else {
            // trainerInfo 가 존재한다면, dirty checking 업데이트
            trainerInfo.updateSimpleIntro(trainerUpdateInfoDto.getSimpleIntro());
            trainerInfo.updateGymName(trainerUpdateInfoDto.getGymName());
            trainerInfo.updateIntroContent(trainerUpdateInfoDto.getIntroContent());


            // 사용자가 삭제한 이미지가 존재한다면,
            if (trainerUpdateInfoDto.getDeletedImgList() != null) {
                // 삭제 될 CareerImg 조회
                // 이 부분 수정해야함
                List<CareerImg> delCareerImgList = careerImgRepository.findByFileNameWithCareerImg(trainerInfo.getId(), trainerUpdateInfoDto.getDeletedImgList());

                // 삭제될 CareerImg id 획득
                List<Long> delCareerImgIdList = delCareerImgList.stream().map(CareerImg::getId).toList();
                List<String> delFileNameList = delCareerImgList.stream().map(careerImg -> careerImg.getFile().getFileName()).toList();

                careerImgRepository.deleteAllById(delCareerImgIdList);
                fileService.deleteImgListToDB(FilePath.TRAINER_INFO.getPath(), delFileNameList);
            }
        }

        if (trainerUpdateInfoDto.getCareerImgList() != null) {
            // 자격사항 이미지 저장
            for (MultipartFile file : trainerUpdateInfoDto.getCareerImgList()) {

                File savedFile = fileService.saveImgToDB(FilePath.TRAINER_INFO.getPath(), file);
                CareerImg careerImg = new CareerImg(trainerInfo, savedFile);
                careerImgRepository.save(careerImg);
            }
        }
    }

    // 트레이너 소개 페이지 데이터 조회
    public TrainerInfoResDto getInfo(Long trainerId) {
        Member member = trainerInfoRepository.findMemberByIdWithTrainerInfo(trainerId)
                .orElseThrow(() -> new NotFoundUserException("사용자를 찾을 수 없습니다."));

        return this.getTrainerInfoDto(member);
    }

    // 트레이너 관리 페이지 데이터 조회
    public TrainerInfoResDto getInfoForManagement(String memberEmail) {
        Member member = trainerInfoRepository.findMemberByIdWithTrainerInfo(memberEmail)
                .orElseThrow(() -> new NotFoundUserException("해당 트레이너를 찾을 수 없습니다."));

        return this.getTrainerInfoDto(member);
    }

    private TrainerInfoResDto getTrainerInfoDto(Member member) {
        // 만약, 트레이너 소개 페이지가 등록되어있지 않다면,
        TrainerInfo trainerInfo = member.getTrainerInfo();
        if (trainerInfo == null) {
            throw new NotFoundUserException("해당 사용자의 소개 데이터를 찾을 수 없습니다.");
        }

        List<ImgResDto> careerImgList = new ArrayList<>();

        // 자격사항 이미지 불러옴
        for (CareerImg careerImg : careerImgRepository.findByTrainerIdWithFile(trainerInfo.getId())) {
            String imgName = careerImg.getFile().getFileName();
            String imgUrl = fileService.getImgUrl(careerImg.getFile().getFilePath(), careerImg.getFile().getFileName());

            careerImgList.add(ImgResDto.create(imgName, imgUrl));
        }

        // 트레이너 프로필 이미지
        String trainerImgUrl = fileService.getImgUrl(FilePath.MEMBER_PROFILE.getPath(), member.getProfileImageName());

        ImgResDto imgResDto = ImgResDto.create(member.getProfileImageName(), trainerImgUrl);

        // 트레이너 PT 프로그램 리스트
        List<PtProgramResDto> ptProgramResDtoList = ptProgramService.getPtProgramListFromMember(member);

        return converter.toTrainerInfoResDto(trainerInfo, member, imgResDto, careerImgList, ptProgramResDtoList);
    }



    // 페이지네이션 방식으로, 트레이너 목록 조회
    public Page<TrainerSummaryResDto> getTrainerList(TrainerListReqDto TrainerListReqDto) {
        Page<TrainerInfo> trainerInfoPage = trainerInfoRepository.findBySearchCondition(TrainerListReqDto);

        return trainerInfoPage.map(trainerInfo -> {
            Member member = trainerInfo.getMember();

            ImgResDto imgResDto = ImgResDto.create(
                    member.getProfileOriginalImageName(),
                    fileService
                            .getImgUrl(FilePath.MEMBER_PROFILE.getPath(), member.getProfileImageName())
            );

            return converter.toTrainerSummaryResDto(trainerInfo, member, imgResDto);
        });
    }
}
