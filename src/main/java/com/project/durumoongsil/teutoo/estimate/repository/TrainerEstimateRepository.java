package com.project.durumoongsil.teutoo.estimate.repository;

import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import com.project.durumoongsil.teutoo.estimate.repository.query.trainer.TrainerEstimateQueryRepository;
import com.project.durumoongsil.teutoo.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerEstimateRepository extends JpaRepository<TrainerEstimate, Long>, TrainerEstimateQueryRepository {
    int countTrainerEstimateByMember(Member member);
}
