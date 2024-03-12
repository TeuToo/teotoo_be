package com.project.durumoongsil.teutoo.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.durumoongsil.teutoo.chat.dto.response.ChatActivationResDTO;
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

    private ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

    @GetMapping("/activation")
    @Operation(summary = "대화방 데이터 조회", description = "채팅을 하기전 상대방과의 대화방 관련 데이터를 얻기 위한 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대화방 데이터 조회 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청")
    })
    public ChatActivationResDTO getChatActivation(@RequestParam("receiverId")Long receiverId) {
        return chatService.getActivationChat(receiverId);
    }

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
