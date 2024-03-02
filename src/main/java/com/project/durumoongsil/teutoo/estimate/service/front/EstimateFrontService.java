package com.project.durumoongsil.teutoo.estimate.service.front;

import com.project.durumoongsil.teutoo.common.RestResult;
import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import com.project.durumoongsil.teutoo.estimate.dto.CreateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.dto.EstimateSearchDto;
import com.project.durumoongsil.teutoo.estimate.dto.UpdateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.service.EstimateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
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

    public RestResult searchEstimateResult(Long estimateId) {
        modelMapper.typeMap(Estimate.class,EstimateSearchDto.class).addMappings(mapper-> {
            mapper.map(estimate -> estimate.getMember().getName(), EstimateSearchDto::setName);
        });

        Estimate estimate = estimateService.searchEstimate(estimateId);
        return new RestResult(modelMapper.map(estimate, EstimateSearchDto.class));
    }

    public RestResult updateEstimateResult(Long estimateId, UpdateEstimateDto updateEstimateDto, String currentLoginId) {
        modelMapper.typeMap(Estimate.class,EstimateSearchDto.class).addMappings(mapper-> {
            mapper.map(estimate -> estimate.getMember().getName(), EstimateSearchDto::setName);
        });

        Estimate estimate = estimateService.updateEstimate(estimateId, updateEstimateDto, currentLoginId);
        return new RestResult(modelMapper.map(estimate,EstimateSearchDto.class));
    }

    public RestResult deleteEstimateResult(Long estimateId, String currentLoginId) {
        estimateService.deleteEstimate(estimateId, currentLoginId);
        return new RestResult("견적서 삭제 완료");
    }
}
