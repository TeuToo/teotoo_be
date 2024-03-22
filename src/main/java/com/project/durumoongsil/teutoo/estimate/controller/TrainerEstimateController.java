package com.project.durumoongsil.teutoo.estimate.controller;

import com.project.durumoongsil.teutoo.common.LoginEmail;
import com.project.durumoongsil.teutoo.common.RestEstimateResult;
import com.project.durumoongsil.teutoo.common.RestResult;
import com.project.durumoongsil.teutoo.estimate.dto.trainer.CreateTrainerEstimateDto;
import com.project.durumoongsil.teutoo.estimate.dto.trainer.UpdateTrainerEstimateDto;
import com.project.durumoongsil.teutoo.estimate.service.front.TrainerEstimateFrontService;
import com.project.durumoongsil.teutoo.login.dto.LoginDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "트레이너 견적서 작성 ,수정, 조회, 삭제 API")
@RestController
@RequestMapping("trainer/estimates")
@RequiredArgsConstructor
public class TrainerEstimateController {

    private final TrainerEstimateFrontService frontService;


    @Operation(summary = "트레이너 견적서 작성 시 프로그램, 이름 정보 API", description = "트레이너 견적서 작성 기본 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 프로그램 조회 성공")
    })
    @GetMapping("/programs")
    public RestResult getPtPrograms() {
        return frontService.getTrainerPtPrograms(LoginEmail.getLoginUserEmail());
    }

    @Operation(summary = "트레이너 견적서 작성 API", description = "트레이너 견적서 작성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 작성 성공"),
            @ApiResponse(responseCode = "409", description = "중복 견적서 작성")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestResult createEstimate(@Validated CreateTrainerEstimateDto createTrainerEstimateDto) {
        return frontService.createEstimateResult(createTrainerEstimateDto);
    }

    @Operation(summary = "유저 견적서 전체 조회", description = "트레이너 입장에서 일반 유저가 작성한 견적서의 전체 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "파라미터 오류")
    })
    @GetMapping
    public RestEstimateResult getEstimates(@PageableDefault Pageable pageable, String ptAddress) {
        return frontService.searchAllEstimateResult(pageable, ptAddress);
    }



    @Operation(summary = "트레이너 견적서 단건 조회", description = "견적서 단건 조회(본인, 타인)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 단건 조회 성공")
    })
    @GetMapping("{estimateId}")
    public RestResult getEstimate(@PathVariable Long estimateId) {
        return frontService.searchEstimateResult(estimateId);
    }

    @Operation(summary = "트레이너 견적서 수정", description = "자기가 작성한 견적서 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 수정 성공"),
            @ApiResponse(responseCode = "403", description = "자기가 작성한게 아닌 타인이 수정하려할때 권한 제어")
    })
    @PatchMapping(value = "{estimateId}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RestResult updateEstimate(@PathVariable Long estimateId, @Validated UpdateTrainerEstimateDto updateTrainerEstimateDto) {
        return frontService.updateEstimateResult(estimateId, updateTrainerEstimateDto);
    }

    @Operation(summary = "트레이너 견적서 삭제", description = "자기가 작성한 견적서 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "견적서 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "자기가 작성한게 아닌 타인이 삭제 하려할때 권한 제어")
    })
    @DeleteMapping("{estimateId}")
    public RestResult deleteEstimate(@PathVariable Long estimateId) {
        return frontService.deleteEstimateResult(estimateId);
    }
}
