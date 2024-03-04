package com.project.durumoongsil.teutoo.trainer.info.util;

import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.info.dto.TrainerInfoResDto;
import com.project.durumoongsil.teutoo.trainer.info.dto.TrainerSummaryResDto;
import com.project.durumoongsil.teutoo.trainer.info.dto.TrainerUpdateInfoDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramResDto;

import java.util.List;


public class TrainerInfoConverter {

    public TrainerInfo toTrainerInfo(TrainerUpdateInfoDto trainerUpdateInfoDto, Member member) {
        return TrainerInfo.builder()
                .introContent(trainerUpdateInfoDto.getIntroContent())
                .gymName(trainerUpdateInfoDto.getGymName())
                .simpleIntro(trainerUpdateInfoDto.getSimpleIntro())
                .member(member)
                .build();
    }

    public TrainerInfoResDto toTrainerInfoResDto(TrainerInfo trainerInfo, Member member, ImgResDto imgResDto,
                                   List<ImgResDto> careerImgList, List<PtProgramResDto> ptProgramResDtoList) {
        return TrainerInfoResDto.builder()
                .trainerInfoId(trainerInfo.getId())
                .trainerAddress(member.getAddress())
                .trainerName(member.getName())
                .imgResDto(imgResDto)
                .gymName(trainerInfo.getGymName())
                .simpleIntro(trainerInfo.getSimpleIntro())
                .introContent(trainerInfo.getIntroContent())
                .careerImgList(careerImgList)
                .ptProgramResDtoList(ptProgramResDtoList)
                .build();
    }

    public TrainerSummaryResDto toTrainerSummaryResDto(TrainerInfo trainerInfo, Member member, ImgResDto imgResDto) {
        return TrainerSummaryResDto.builder()
                .trainerInfoId(trainerInfo.getId())
                .trainerName(member.getName())
                .reviewScore(trainerInfo.getReviewScore())
                .reviewCnt(trainerInfo.getReviewCnt())
                .simpleIntro(trainerInfo.getSimpleIntro())
                .gymName(trainerInfo.getGymName())
                .imgResDto(imgResDto)
                .build();
    }
}
