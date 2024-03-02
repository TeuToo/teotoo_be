package com.project.durumoongsil.teutoo.estimate;

import com.project.durumoongsil.teutoo.common.LoginEmail;
import com.project.durumoongsil.teutoo.common.RestResult;
import com.project.durumoongsil.teutoo.estimate.dto.CreateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.dto.UpdateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.service.front.EstimateFrontService;
import com.project.durumoongsil.teutoo.security.util.SecurityUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EstimateController {

    private final EstimateFrontService estimateFrontService;

    @PostMapping("/estimates")
    public RestResult createEstimate(@Validated CreateEstimateDto createEstimateDto) {
        log.info("CreateEstimateDto = {}", createEstimateDto);
        return estimateFrontService.createEstimateResult(createEstimateDto, LoginEmail.getLoginUserEmail());
    }

    @GetMapping("/estimates/{estimateId}")
    public RestResult getEstimate(@PathVariable Long estimateId) {
        return estimateFrontService.searchEstimateResult(estimateId);
    }

    @PatchMapping("/estimates/{estimateId}")
    public RestResult updateEstimate(@PathVariable Long estimateId, UpdateEstimateDto updateEstimateDto) {
        return estimateFrontService.updateEstimateResult(estimateId, updateEstimateDto,LoginEmail.getLoginUserEmail());
    }

    @DeleteMapping("/estimates/{estimateId}")
    public RestResult deleteEstimate(@PathVariable Long estimateId) {
        return estimateFrontService.deleteEstimateResult(estimateId,LoginEmail.getLoginUserEmail());
    }
}
