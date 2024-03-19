package com.project.durumoongsil.teutoo.estimate.repository.query.trainer;

import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TrainerEstimateQueryRepository {

    TrainerEstimate findByPtProgramIdWithFetch(Long trainerEstimateId);

    Optional<TrainerEstimate> findByEstimateIdWithMember(Long estimateId);
}
