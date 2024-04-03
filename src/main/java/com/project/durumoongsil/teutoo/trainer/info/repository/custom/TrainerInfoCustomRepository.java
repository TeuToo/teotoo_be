package com.project.durumoongsil.teutoo.trainer.info.repository.custom;

import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.info.dto.request.TrainerListReqDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.OptionalLong;

public interface TrainerInfoCustomRepository {

    Optional<Member> findMemberByIdWithTrainerInfo(String userEmail);

    Page<TrainerInfo> findBySearchCondition(PageRequest pageRequest, String search);
    Optional<Member> findMemberByIdWithTrainerInfo(Long id);
    OptionalLong findTrainerInfoIdByMemberEmail(String email);
}
