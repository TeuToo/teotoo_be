package com.project.durumoongsil.teutoo.trainer.info.repository;

import com.project.durumoongsil.teutoo.trainer.info.domain.CareerImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerImgRepository extends JpaRepository<CareerImg, Long>, CareerImgCustomRepository{
}
