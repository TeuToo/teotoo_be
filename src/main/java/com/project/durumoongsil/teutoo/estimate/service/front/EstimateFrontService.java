package com.project.durumoongsil.teutoo.estimate.service.front;

import com.project.durumoongsil.teutoo.common.RestResult;
import com.project.durumoongsil.teutoo.estimate.dto.CreateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.dto.UpdateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.service.EstimateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstimateFrontService {

    private final EstimateService estimateService;
    private final ModelMapper modelMapper;

    public RestResult createEstimateResult(CreateEstimateDto createEstimateDto, String loginUserEmail) {
        estimateService.createEstimate(createEstimateDto,loginUserEmail);
        return new RestResult("견적서 작성 성공");
    }

    public Object searchEstimateResult(Long createEstimateDto, String currentLoginId) {
        return null;
    }

    public Object deleteEstimateResult(Long createEstimateDto, String currentLoginId) {
        return null;
    }

    public Object updateEstimateResult(Long estimateId, UpdateEstimateDto updateEstimateDto, String currentLoginId) {
        return null;
    }
}
