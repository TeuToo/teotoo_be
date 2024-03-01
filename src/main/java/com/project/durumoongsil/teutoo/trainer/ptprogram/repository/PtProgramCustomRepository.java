package com.project.durumoongsil.teutoo.trainer.ptprogram.repository;

import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;

import java.util.Optional;

public interface PtProgramCustomRepository {

    Optional<PtProgram> findByPtProgramByIdAndMemberEmail(Long ptProgramId, String email);
}
