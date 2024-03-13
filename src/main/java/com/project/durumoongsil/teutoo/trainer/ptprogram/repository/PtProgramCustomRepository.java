package com.project.durumoongsil.teutoo.trainer.ptprogram.repository;

import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;

import java.util.List;
import java.util.Optional;

public interface PtProgramCustomRepository {
    List<PtProgram> findByMemberEmailWithPtImg(String email);
    Optional<PtProgram> findByIdAndMemberEmailWithPtImgAndFile(Long ptProgramId, String email);
}
