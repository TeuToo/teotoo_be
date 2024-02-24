package com.project.durumoongsil.teutoo.trainer.repository;

import com.project.durumoongsil.teutoo.member.domain.Member;

import java.util.Optional;

public interface TrainerInfoCustomRepository {

    Optional<Member> findMemberByIdWithTrainerInfo(Long memberId);
}
