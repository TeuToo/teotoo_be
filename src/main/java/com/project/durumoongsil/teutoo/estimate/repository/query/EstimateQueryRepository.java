package com.project.durumoongsil.teutoo.estimate.repository.query;

import com.project.durumoongsil.teutoo.estimate.domain.Estimate;

public interface EstimateQueryRepository {

    Estimate findEstimateWithMemberName(Long estimateId);
}
