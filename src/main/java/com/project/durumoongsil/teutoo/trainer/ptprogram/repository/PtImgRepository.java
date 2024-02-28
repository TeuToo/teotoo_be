package com.project.durumoongsil.teutoo.trainer.ptprogram.repository;

import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PtImgRepository extends JpaRepository<PtImg, Long> {
}
