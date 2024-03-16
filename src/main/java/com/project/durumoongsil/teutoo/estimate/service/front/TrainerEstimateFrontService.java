package com.project.durumoongsil.teutoo.estimate.service.front;

import com.project.durumoongsil.teutoo.common.RestResult;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import com.project.durumoongsil.teutoo.estimate.dto.trainer.*;
import com.project.durumoongsil.teutoo.estimate.dto.user.PageUserEstimateDto;
import com.project.durumoongsil.teutoo.estimate.service.TrainerEstimateService;
import com.project.durumoongsil.teutoo.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerEstimateFrontService {

    private final ModelMapper modelMapper;
    private final TrainerEstimateService estimateService;
    private final FileService fileService;
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
     *  트레이너입장에서 사용자 견적서 No-offset
     */
    public RestResult searchAllEstimateResult(Long cursorId, int size) {
        List<Estimate> allUserEstimate = estimateService.searchAllUserEstimate(cursorId, size);
        List<PageUserEstimateDto> userEstimateDtoList = allUserEstimate.stream()
                .map(estimate -> new PageUserEstimateDto(estimate.getPrice(), estimate.getMember().getName(), fileService.getImgUrl("member_profile", estimate.getMember().getProfileImageName())))
                .toList();
        return new RestResult(userEstimateDtoList);
    }

    /**
     * 트레이너 견적서 단건조회
     */
    public RestResult searchEstimateResult(Long estimateId) {
        TrainerEstimate trainerEstimate = estimateService.searchTrainerEstimate(estimateId);
        return new RestResult(SearchTrainerEstimateDto.fromEntity(trainerEstimate, SearchEstimateProgramDto.fromEntity(trainerEstimate)));
    }

    public RestResult updateEstimateResult(Long estimateId, UpdateTrainerEstimateDto updateEstimateDto) {
        TrainerEstimate updatedTrainerEstimate = estimateService.updateTrainerEstimate(estimateId,updateEstimateDto);
        //RestResult restResult = searchEstimateResult(updatedTrainerEstimate.getId()); 수정완료 띄울거라면 사용
        return new RestResult("견적서 수정 완료");
    }

    public RestResult deleteEstimateResult(Long estimateId) {
        estimateService.deleteTrainerEstimate(estimateId);
        return new RestResult("견적서 삭제 완료");
    }
}
