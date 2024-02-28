package com.project.durumoongsil.teutoo.trainer.ptprogram.repository;

import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.TrainerProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerProgramRepository extends JpaRepository<TrainerProgram, Long> {
}
