package com.project.durumoongsil.teutoo.chat.controller;

import com.project.durumoongsil.teutoo.chat.dto.response.ChatActivationResDto;
import com.project.durumoongsil.teutoo.chat.dto.response.ChatPreviewResDto;
import com.project.durumoongsil.teutoo.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "채팅 관련 API")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;


    @GetMapping("/list")
    @Operation(summary = "대화목록 조회", description = "대화 목록을 얻기 위한 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대화방 데이터 조회 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청")
    })
    public List<ChatPreviewResDto> getChatList(@RequestParam(value = "searchName", defaultValue = "", required = false) String searchName) {
        return chatService.getChatPreviewList(searchName);
    }
}
