package com.project.durumoongsil.teutoo.estimate.controller;

import com.project.durumoongsil.teutoo.common.LoginEmail;
import com.project.durumoongsil.teutoo.common.RestResult;
import com.project.durumoongsil.teutoo.estimate.dto.trainer.CreateTrainerEstimateDto;
import com.project.durumoongsil.teutoo.estimate.dto.user.UpdateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.service.front.TrainerEstimateFrontService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "트레이너 견적서 작성 ,수정, 조회, 삭제 API")
@Slf4j
@RestController
@RequestMapping("/trainer")
@RequiredArgsConstructor
public class TrainerEstimateController {

    private final TrainerEstimateFrontService frontService;


    @Operation(summary = "트레이너 견적서 작성 API", description = "트레이너 견적서 작성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 작성 성공"),
            @ApiResponse(responseCode = "409", description = "중복 견적서 작성")
    })
    @PostMapping("/estimates")
    public RestResult createEstimate(@Validated @RequestBody CreateTrainerEstimateDto createTrainerEstimateDto) {
        log.info("CreateEstimateDto = {}", createTrainerEstimateDto);
        return frontService.createEstimateResult(createTrainerEstimateDto);
    }


    @Operation(summary = "트레이너 견적서 전체 조회", description = "일반 유저 입장에서 견적서 버튼 클릭시 트레이너가 작성한 견적서 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 목록 조회 성공")
    })
    @GetMapping("/estimates")
    public RestResult getEstimates(@PageableDefault Pageable pageable, @Parameter(name = "PT 검색 주소") String ptAddress) {
        return frontService.searchAllEstimateResult(pageable, ptAddress);
    }

    @Operation(summary = "트레이너 견적서 단건 조회", description = "견적서 단건 조회(본인, 타인)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 단건 조회 성공")
    })
    @GetMapping("/estimates/{estimateId}")
    public RestResult getEstimate(@Parameter(name = "견적서 ID") @PathVariable Long estimateId) {
        return frontService.searchEstimateResult(estimateId);
    }

    @Operation(summary = "트레이너 견적서 수정", description = "자기가 작성한 견적서 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 수정 성공"),
            @ApiResponse(responseCode = "403", description = "자기가 작성한게 아닌 타인이 수정하려할때 권한 제어")
    })
    @PatchMapping("/estimates/{estimateId}")
    public RestResult updateEstimate(@Parameter(name = "견적서 ID") @PathVariable Long estimateId, UpdateEstimateDto updateEstimateDto) {
        return frontService.updateEstimateResult(estimateId, updateEstimateDto,LoginEmail.getLoginUserEmail());
    }

    @Operation(summary = "트레이너 견적서 삭제", description = "자기가 작성한 견적서 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "자기가 작성한게 아닌 타인이 삭제 하려할때 권한 제어")
    })
    @DeleteMapping("/estimates/{estimateId}")
    public RestResult deleteEstimate(@Parameter(name = "견적서 ID") @PathVariable Long estimateId) {
        return frontService.deleteEstimateResult(estimateId);
    }
}
