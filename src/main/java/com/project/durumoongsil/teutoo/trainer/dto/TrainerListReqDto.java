package com.project.durumoongsil.teutoo.trainer.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "트레이너 목록 요청 Dto")
@Getter
@Setter
@ToString
public class TrainerListReqDto {

    @Parameter(description = "요청 페이지", example = "0", required = true)
    private int page = 0;
    @Parameter(description = "요청 개수: 페이지네이션은 (page * size, page * size + size] 까지 조회로 이루어지고, page >= 0, size >= 1", example = "10", required = true)
    private int size = 10;
    @Parameter(description = "alpha: 알파벳 or 한글 순, review: 후기점수 순", example = "alpha or review", required = true)
    private String sort = "alpha";
    @Parameter(description = "asc: 오름차순, desc: 내림차순", example = "asc or desc", required = true)
    private String direction = "desc";
    @Parameter(description = "검색 할 트레이너 이름", example = "김김김")
    String searchTrainer;
    @Parameter(description = "검색 할 헬스장 이름", example = "Good GYM")
    String searchGym;
    @Parameter(description = "검색 할 지역이름, 서버에서는 start with로 조회됨", example = "서울특별시 강서구")
    String searchLocation;
}
