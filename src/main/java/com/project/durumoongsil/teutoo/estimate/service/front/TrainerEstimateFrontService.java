package com.project.durumoongsil.teutoo.estimate.service.front;

import com.project.durumoongsil.teutoo.common.RestResult;
import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import com.project.durumoongsil.teutoo.estimate.dto.trainer.CreateTrainerEstimateDto;
import com.project.durumoongsil.teutoo.estimate.dto.trainer.SearchEstimateProgramDto;
import com.project.durumoongsil.teutoo.estimate.dto.trainer.SearchPtPrograms;
import com.project.durumoongsil.teutoo.estimate.dto.trainer.SearchTrainerEstimateDto;
import com.project.durumoongsil.teutoo.estimate.dto.user.UpdateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.service.TrainerEstimateService;
import com.project.durumoongsil.teutoo.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerEstimateFrontService {

    private final ModelMapper modelMapper;
    private final TrainerEstimateService estimateService;
    public RestResult getTrainerPtPrograms(String currentLoginId) {
        List<Member> ptProgramsAndName = estimateService.getPtProgramsAndName(currentLoginId);
        List<SearchPtPrograms> list = ptProgramsAndName.stream()
                .map(SearchPtPrograms::new)
                .toList();
        return new RestResult(list);
    }

    public RestResult createEstimateResult(CreateTrainerEstimateDto createEstimateDto) {
        estimateService.createTrainerEstimate(createEstimateDto);
        return new RestResult("견적서 작성 완료");
    }

    /**
     *  사용자입장에서 트레이너 견적서 No-offset
     */
    public RestResult searchAllEstimateResult(Pageable pageable, String ptAddress) {
        return null;
    }

    /**
     * 트레이너 견적서 단건조회
     */
    public RestResult searchEstimateResult(Long estimateId) {
        TrainerEstimate trainerEstimate = estimateService.searchTrainerEstimate(estimateId);
        SearchEstimateProgramDto searchEstimateProgramDto = new SearchEstimateProgramDto(trainerEstimate.getPtProgram().getId(), trainerEstimate.getPtProgram().getTitle());
        SearchTrainerEstimateDto searchTrainerEstimateDto = new SearchTrainerEstimateDto(trainerEstimate.getPrice(), trainerEstimate.getPtCenterAddress(),searchEstimateProgramDto,trainerEstimate.getMember().getName());
        return new RestResult(searchTrainerEstimateDto);
    }

    public RestResult updateEstimateResult(Long estimateId, UpdateEstimateDto updateEstimateDto) {
        estimateService.updateTrainerEstimate(estimateId,updateEstimateDto);
        return new RestResult(null);
    }

    public RestResult deleteEstimateResult(Long estimateId) {
        estimateService.deleteTrainerEstimate(estimateId);
        return new RestResult("견적서 삭제 완료");
    }
}
