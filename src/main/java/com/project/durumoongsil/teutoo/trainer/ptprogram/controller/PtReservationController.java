package com.project.durumoongsil.teutoo.trainer.ptprogram.controller;

import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.request.PtAcceptReqDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.request.PtReservationReqDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtAcceptResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtReservationResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.service.PtReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "트레이너 PT 프로그램 예약 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("trainer/program/reservation")
public class PtReservationController {

    private final PtReservationService ptReservationService;

    @Operation(summary = "트레이너 PT 예약", description = "프로그램 예약 페이지에서 예약하기 위한 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로그램 예약 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이미 예약된 시간대에 예약시 발생하는 에러")
    })
    @PostMapping
    public PtReservationResDto reservePtProgram(@RequestBody PtReservationReqDto ptReservationReqDto) {
        return ptReservationService.reserve(ptReservationReqDto);
    }

    @Operation(summary = "트레이너 PT 예약확인", description = "프로그램 예약 확인 처리를 위한 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로그램 예약 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 사용자에 대한 에러")
    })
    @PostMapping("/accept")
    public PtAcceptResDto acceptReservationPtProgram(@RequestBody PtAcceptReqDto ptAcceptReqDto) {
        return ptReservationService.accept(ptAcceptReqDto);
    }
}
