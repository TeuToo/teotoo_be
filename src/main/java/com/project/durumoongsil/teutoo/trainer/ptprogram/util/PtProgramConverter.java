package com.project.durumoongsil.teutoo.trainer.ptprogram.util;

import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtProgramManageResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.request.PtProgramRegDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtProgramResDto;

import java.util.List;

public class PtProgramConverter {

    public PtProgram toPtProgram(PtProgramRegDto ptProgramRegDto, TrainerInfo trainerInfo) {
        return PtProgram.builder()
                .content(ptProgramRegDto.getContent())
                .availableStartTime(ptProgramRegDto.getAvailableStartTime())
                .availableEndTime(ptProgramRegDto.getAvailableEndTime())
                .price(ptProgramRegDto.getPrice())
                .title(ptProgramRegDto.getTitle())
                .trainerInfo(trainerInfo)
                .build();
    }

    public PtProgramManageResDto toPtProgramManageResDto(List<PtProgramResDto> ptProgramResDtoList, Member member, ImgResDto imgResDto) {
        return PtProgramManageResDto.builder()
                .trainerName(member.getName())
                .trainerImg(imgResDto)
                .ptProgramResList(ptProgramResDtoList)
                .build();
    }

    public PtProgramResDto toPtProgramResDto(PtProgram ptProgram, List<ImgResDto> imgResDtoList) {
        return PtProgramResDto.builder()
                .ptProgramId(ptProgram.getId())
                .title(ptProgram.getTitle())
                .content(ptProgram.getContent())
                .price(ptProgram.getPrice())
                .availableStartTime(ptProgram.getAvailableStartTime())
                .availableEndTime(ptProgram.getAvailableEndTime())
                .ptProgramImgList(imgResDtoList)
                .build();
    }

}
