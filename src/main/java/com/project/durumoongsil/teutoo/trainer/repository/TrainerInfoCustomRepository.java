package com.project.durumoongsil.teutoo.trainer.repository;

import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.trainer.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.dto.TrainerListReqDto;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface TrainerInfoCustomRepository {

    Optional<Member> findMemberByIdWithTrainerInfo(String userEmail);

    Page<TrainerInfo> findBySearchCondition(TrainerListReqDto trainerListReqDto);
    Optional<Member> findMemberByIdWithTrainerInfo(Long id);
}
