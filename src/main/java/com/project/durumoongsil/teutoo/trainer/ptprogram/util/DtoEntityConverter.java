package com.project.durumoongsil.teutoo.trainer.ptprogram.util;

import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramManageResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramRegDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramResDto;

import java.util.List;

public class DtoEntityConverter {

    public PtProgram toPtProgram(PtProgramRegDto ptProgramRegDto, TrainerInfo trainerInfo) {
        return PtProgram.builder()
                .content(ptProgramRegDto.getContent())
                .price(ptProgramRegDto.getPrice())
                .ptCnt(ptProgramRegDto.getPtCnt())
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
                .ptCnt(ptProgram.getPtCnt())
                .ptProgramImgList(imgResDtoList)
                .build();
    }

}
