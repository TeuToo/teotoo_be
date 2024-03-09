package com.project.durumoongsil.teutoo.estimate.repository;

import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerEstimateRepository extends JpaRepository<TrainerEstimate, Long> {
}
