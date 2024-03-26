package com.project.durumoongsil.teutoo.trainer.ptprogram.controller;

import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtTrainerScheduleResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.service.PtProgramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "PT 프로그램 스케쥴 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("schedule")
public class PtScheduleController {

    private final PtProgramService ptProgramService;

    @Operation(summary = "트레이너 PT 일정 조회", description = "트레이너의 PT 프로그램 스케쥴 조회를 위한 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "트레이너의 PT 프로그램 스케쥴 조회 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청")
    })
    @GetMapping("/trainer/me")
    public List<PtTrainerScheduleResDto> getPtProgramOverviewForScheduling() {
        return ptProgramService.getPtProgramTrainerScheduleList();
    }
}
