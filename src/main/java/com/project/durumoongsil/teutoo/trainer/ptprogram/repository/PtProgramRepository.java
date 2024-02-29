package com.project.durumoongsil.teutoo.trainer.ptprogram.repository;

import com.project.durumoongsil.teutoo.trainer.info.repository.TrainerInfoCustomRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PtProgramRepository extends JpaRepository<PtProgram, Long>, TrainerInfoCustomRepository {
}
