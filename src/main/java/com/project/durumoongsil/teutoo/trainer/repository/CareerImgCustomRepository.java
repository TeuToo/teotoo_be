package com.project.durumoongsil.teutoo.trainer.repository;

import com.project.durumoongsil.teutoo.trainer.domain.CareerImg;

import java.util.List;

public interface CareerImgCustomRepository {

    List<CareerImg> findByTrainerIdWithFile(Long trainerId);
}
