package com.project.durumoongsil.teutoo.trainer.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrainerListReqDto {

    private int page = 0;
    private int size = 10;
    private String sort = "alpha";
    private String direction = "desc";
    String searchTrainer;
    String searchGym;
    String searchLocation;
}
