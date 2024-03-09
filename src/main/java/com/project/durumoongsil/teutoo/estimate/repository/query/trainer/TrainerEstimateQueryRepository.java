package com.project.durumoongsil.teutoo.estimate.repository.query.trainer;

import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;

public interface TrainerEstimateQueryRepository {

    TrainerEstimate findByPtProgramIdWithFetch(Long trainerEstimateId);
}
