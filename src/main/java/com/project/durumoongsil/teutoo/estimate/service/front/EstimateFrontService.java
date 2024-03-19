package com.project.durumoongsil.teutoo.estimate.service.front;

import com.project.durumoongsil.teutoo.common.RestResult;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import com.project.durumoongsil.teutoo.estimate.dto.trainer.PagedTrainerEstimateDto;
import com.project.durumoongsil.teutoo.estimate.dto.user.CreateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.dto.user.EstimatePageDto;
import com.project.durumoongsil.teutoo.estimate.dto.user.EstimateSearchDto;
import com.project.durumoongsil.teutoo.estimate.dto.user.UpdateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.service.EstimateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class EstimateFrontService {

    private final EstimateService estimateService;
    private final FileService fileService;
    private final ModelMapper modelMapper;

    public RestResult createEstimateResult(CreateEstimateDto createEstimateDto, String loginUserEmail) {
        estimateService.createEstimate(createEstimateDto,loginUserEmail);
        return new RestResult("견적서 작성 성공");
    }


    /**
     * 견적서 단건 조회
     */
    public RestResult searchEstimateResult(Long estimateId) {
        EntityToSearchDto();
        Estimate estimate = estimateService.searchEstimate(estimateId);
        return new RestResult(modelMapper.map(estimate, EstimateSearchDto.class));
    }

    public RestResult updateEstimateResult(Long estimateId, UpdateEstimateDto updateEstimateDto, String currentLoginId) {
        EntityToSearchDto();
        Estimate estimate = estimateService.updateEstimate(estimateId, updateEstimateDto, currentLoginId);
        return new RestResult(modelMapper.map(estimate,EstimateSearchDto.class));
    }

    public RestResult deleteEstimateResult(Long estimateId, String currentLoginId) {
        estimateService.deleteEstimate(estimateId, currentLoginId);
        return new RestResult("견적서 삭제 완료");
    }

    private void EntityToSearchDto() {
        modelMapper.typeMap(Estimate.class,EstimateSearchDto.class).addMappings(mapper-> {
            mapper.map(estimate -> estimate.getMember().getName(), EstimateSearchDto::setName);
        });
    }

    private PagedTrainerEstimateDto convertToDto(TrainerEstimate trainerEstimate) {
        return PagedTrainerEstimateDto.builder()
                .estimateId(trainerEstimate.getId())
                .name(trainerEstimate.getMember().getName())
                .price(trainerEstimate.getPrice())
                .profileImagePath(fileService.getImgUrl("member_profile", trainerEstimate.getMember().getProfileImageName()))
                .build();
    }

    /**
     * 유저 입장에서는 트레이너 견적서 목록이 no-offset 으로 나와야함
     */
    public RestResult searchAllTrainerEstimatesResult(Long cursorId, int size) {
        List<TrainerEstimate> allTrainerEstimates = estimateService.searchAllTrainerEstimates(cursorId, size);
        List<PagedTrainerEstimateDto> noOffsetTrainerSearchDto = allTrainerEstimates.stream()
                .map(trainerEstimate -> new PagedTrainerEstimateDto(trainerEstimate.getId(), trainerEstimate.getPrice(),
                        trainerEstimate.getMember().getName(), fileService.getImgUrl("member_profile", trainerEstimate.getMember().getProfileImageName())))
                .toList();
        return new RestResult(noOffsetTrainerSearchDto);
    }
}
