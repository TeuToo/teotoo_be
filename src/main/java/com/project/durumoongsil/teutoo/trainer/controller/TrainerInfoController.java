package com.project.durumoongsil.teutoo.trainer.controller;

import com.project.durumoongsil.teutoo.trainer.dto.TrainerUpdateInfoDto;
import com.project.durumoongsil.teutoo.trainer.service.TrainerInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "트레이너 소개 페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/trainer/info")
public class TrainerInfoController {

    private final TrainerInfoService trainerInfoService;

    @GetMapping("{trainerId}")
    public void getTrainerIntro(@PathVariable Long trainerId) {

    }


    @PostMapping("{trainerId}")
    @Operation(summary = "트레이너 소개 페이지 등록 API", description = "트레이너 소개 페이지에 데이터 갱신 하기 위한 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "트레이너 소개 페이지 갱신 성공"),
    })
    public void saveTrainerIntro(@PathVariable Long trainerId,
                                 @Valid TrainerUpdateInfoDto trainerUpdateInfoDto) {
        // 갱신하려는 trainer가 아직 credential 확인 전이라고 감안하고, trainer id를 받고 갱신.
        trainerInfoService.saveOrUpdate(trainerId, trainerUpdateInfoDto);
    }
}
