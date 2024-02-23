package com.project.durumoongsil.teutoo.trainer.repository;

import com.project.durumoongsil.teutoo.trainer.domain.CareerImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerImgRepository extends JpaRepository<CareerImg, Long> {

}
