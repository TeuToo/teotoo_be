package com.project.durumoongsil.teutoo.trainer.ptprogram.controller;

import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramManageResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramRegDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramUpdateDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.service.PtProgramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "트레이너 PT 프로그램 관리/조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("trainer/program")
public class PtProgramController {

    private final PtProgramService ptProgramService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "트레이너 PT 프로그램 저장", description = "트레이너 PT 프로그램을 저장 하기 위한 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PT 프로그램 저장 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청")
    })
    public void saveProgram(@Valid PtProgramRegDto ptProgramRegDto) {
        ptProgramService.register(ptProgramRegDto);
    }

    @PutMapping(value = "{programId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "트레이너 PT 프로그램 업데이트", description = "트레이너 PT 프로그램을 업데이트 하기 위한 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PT 프로그램 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청")
    })
    public void updateProgram(@PathVariable Long programId, @Valid PtProgramUpdateDto ptProgramUpdateDto) {
        ptProgramService.update(programId, ptProgramUpdateDto);
    }

    @Operation(summary = "트레이너 PT 관리 페이지 조회", description = "트레이너 PT 프로그램 관리 페이지를 위한 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "트레이너 PT 프로그램 리스트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청")
    })
    @GetMapping("me")
    public PtProgramManageResDto getProgramListForManagement() {
        return ptProgramService.getPtProgramListForManagement();
    }

    @Operation(summary = "트레이너 PT 프로그램 삭제", description = "트레이너 PT 프로그램 삭제를 위한 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "트레이너 PT 프로그램 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청")
    })
    @DeleteMapping("{programId}")
    public void deleteProgram(@PathVariable Long programId) {
        ptProgramService.deletePtProgram(programId);
    }

}
