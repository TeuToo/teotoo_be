package com.project.durumoongsil.teutoo.estimate.repository.query.user;

import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EstimateQueryRepository {

    Estimate findEstimateWithMemberName(Long estimateId);

    List<Estimate> findEstimateAfterCursor(Long cursorId, int size);

    Page<Estimate> pageUserEstimateWithPtAddress(Pageable pageable, String ptAddress);

}
