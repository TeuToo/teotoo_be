package com.project.durumoongsil.teutoo.estimate.repository.query.user;

import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EstimateQueryRepository {

    Estimate findEstimateWithMemberName(Long estimateId);

    Page<Estimate> findAllWithPtAddressPage(Pageable pageable, String ptAddress);
}
