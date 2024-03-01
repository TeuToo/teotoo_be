package com.project.durumoongsil.teutoo.trainer.info.repository;

import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerInfoRepository extends JpaRepository<TrainerInfo, Long>, TrainerInfoCustomRepository {
}
