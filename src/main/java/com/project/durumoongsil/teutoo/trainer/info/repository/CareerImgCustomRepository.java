package com.project.durumoongsil.teutoo.trainer.info.repository;

import com.project.durumoongsil.teutoo.trainer.info.domain.CareerImg;

import java.util.List;

public interface CareerImgCustomRepository {

    List<CareerImg> findByTrainerIdWithFile(Long trainerId);
}
