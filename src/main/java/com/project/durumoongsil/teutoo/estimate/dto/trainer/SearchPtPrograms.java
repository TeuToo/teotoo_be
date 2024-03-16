package com.project.durumoongsil.teutoo.estimate.dto.trainer;

import com.project.durumoongsil.teutoo.member.domain.Member;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SearchPtPrograms {
    private String name;
    private List<PtProgramDto> programs;


    public SearchPtPrograms(Member member) {
        this.name = member.getName();
        this.programs = member.getTrainerInfo().getPtProgramList().stream()
                .map(ptProgram -> new PtProgramDto(ptProgram.getId(), ptProgram.getTitle()))
                .collect(Collectors.toList());
    }
}
