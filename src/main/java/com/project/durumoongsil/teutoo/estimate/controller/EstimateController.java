package com.project.durumoongsil.teutoo.estimate.controller;

import com.project.durumoongsil.teutoo.common.LoginEmail;
import com.project.durumoongsil.teutoo.common.RestEstimateResult;
import com.project.durumoongsil.teutoo.common.RestResult;
import com.project.durumoongsil.teutoo.estimate.dto.user.CreateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.dto.user.UpdateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.service.front.EstimateFrontService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@Tag(name = "견적서 작성 ,수정, 조회, 삭제 API")
@Slf4j
@RestController
@RequestMapping("user/estimates")
@RequiredArgsConstructor
public class EstimateController {

    private final EstimateFrontService estimateFrontService;

    @Operation(summary = "견적서 작성 API", description = "일반 유저 견적서 작성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 작성 성공"),
            @ApiResponse(responseCode = "409", description = "중복 견적서 작성")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestResult createEstimate(@Validated CreateEstimateDto createEstimateDto) {
        return estimateFrontService.createEstimateResult(createEstimateDto, LoginEmail.getLoginUserEmail());
    }


    @Operation(summary = "트레이너 견적서 전체 조회", description = "유저는 트레이너의 견적서 목록이 보여진다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 목록 조회 성공")
    })
    @GetMapping
    public RestEstimateResult getEstimates(@RequestParam Long cursorId, @RequestParam int size) {
        return estimateFrontService.searchAllTrainerEstimatesResult(cursorId, size);
    }

    @Operation(summary = "견적서 단건 조회", description = "견적서 단건 조회(본인, 타인)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 단건 조회 성공")
    })
    @GetMapping("{estimateId}")
    public RestResult getEstimate(@PathVariable Long estimateId) {
        return estimateFrontService.searchEstimateResult(estimateId);
    }

    @Operation(summary = "견적서 수정", description = "자기가 작성한 견적서 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 수정 성공"),
            @ApiResponse(responseCode = "403", description = "자기가 작성한게 아닌 타인이 수정하려할때 권한 제어")
    })
    @PatchMapping(value = "{estimateId}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RestResult updateEstimate(@PathVariable Long estimateId, @Validated UpdateEstimateDto updateEstimateDto) {
        return estimateFrontService.updateEstimateResult(estimateId, updateEstimateDto,LoginEmail.getLoginUserEmail());
    }

    @Operation(summary = "견적서 삭제", description = "자기가 작성한 견적서 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "자기가 작성한게 아닌 타인이 삭제 하려할때 권한 제어")
    })
    @DeleteMapping("{estimateId}")
    public RestResult deleteEstimate( @PathVariable Long estimateId) {
        return estimateFrontService.deleteEstimateResult(estimateId,LoginEmail.getLoginUserEmail());
    }
}
