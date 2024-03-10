package com.project.durumoongsil.teutoo.estimate.repository.query.trainer;

import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;

import java.util.List;

public interface TrainerEstimateQueryRepository {

    TrainerEstimate findByPtProgramIdWithFetch(Long trainerEstimateId);

    List<TrainerEstimate> findTrainerEstimateAfterCursor(Long cursorId, int size);
}
