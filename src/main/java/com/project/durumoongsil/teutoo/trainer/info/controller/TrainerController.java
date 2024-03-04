package com.project.durumoongsil.teutoo.trainer.info.controller;

import com.project.durumoongsil.teutoo.security.service.SecurityService;
import com.project.durumoongsil.teutoo.trainer.info.dto.TrainerInfoResDto;
import com.project.durumoongsil.teutoo.trainer.info.dto.TrainerListReqDto;
import com.project.durumoongsil.teutoo.trainer.info.dto.TrainerSummaryResDto;
import com.project.durumoongsil.teutoo.trainer.info.dto.TrainerUpdateInfoDto;
import com.project.durumoongsil.teutoo.trainer.info.service.TrainerInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "트레이너 소개 관리/조회 페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("trainer")
public class TrainerController {

    private final TrainerInfoService trainerInfoService;
    private final SecurityService securityService;

    @GetMapping("info/{trainerId}")
    @Operation(summary = "트레이너 소개 페이지 조회 API", description = "트레이너 소개 페이지 데이터를 얻기 위한 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "트레이너 소개 페이지 조회 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청")
    })
    public TrainerInfoResDto getTrainerIntro(@PathVariable Long trainerId) {
        return trainerInfoService.getInfo(trainerId);
    }


    @PostMapping(value = "info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "트레이너 소개 페이지 등록/업데이트 API", description = "트레이너 소개 페이지에 데이터 갱신/업데이트 하기 위한 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "트레이너 소개 페이지 갱신 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청")
    })
    public String saveTrainerIntro(@Valid TrainerUpdateInfoDto trainerUpdateInfoDto) {

        String userEmail = securityService.getLoginedUserEmail();

        // 갱신하려는 trainer가 아직 credential 확인 전이라고 감안하고, trainer id를 받고 갱신.
        trainerInfoService.saveOrUpdate(userEmail, trainerUpdateInfoDto);

        return "트레이너 소개 데이터 갱신 성공";
    }

    @GetMapping("list")
    @Operation(summary = "트레이너 목록 페이지 조회 API", description = "트레이너 찾기 페이지에 데이터 조회 하기 위한 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "트레이너 목록 조회 성공")
    })
    public Page<TrainerSummaryResDto> getTrainerList(@ParameterObject @ModelAttribute TrainerListReqDto trainerListReqDto) {
        return trainerInfoService.getTrainerList(trainerListReqDto);
    }
}
