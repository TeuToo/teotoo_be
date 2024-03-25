package com.project.durumoongsil.teutoo.estimate.repository.query.user;

import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;

import java.util.List;

public interface EstimateQueryRepository {

    Estimate findEstimateWithMemberName(Long estimateId);

    List<Estimate> pageUserEstimateWithPtAddress(Long courseId, int size, String ptAddress);

    List<TrainerEstimate> findTrainerEstimateNoOffset(Long cursorId, int size);

    Long getMyEstimateId(String email);
}
