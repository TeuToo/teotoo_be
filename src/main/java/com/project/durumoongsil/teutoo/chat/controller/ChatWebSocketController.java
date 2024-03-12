package com.project.durumoongsil.teutoo.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.durumoongsil.teutoo.chat.dto.request.ChatReadReqDto;
import com.project.durumoongsil.teutoo.chat.dto.request.ChatSendTextMsgDto;
import com.project.durumoongsil.teutoo.chat.dto.response.ChatMsgResDTO;
import com.project.durumoongsil.teutoo.chat.dto.response.ChatReadResDto;
import com.project.durumoongsil.teutoo.chat.service.ChatWebSocketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "채팅 중 채팅 관련 API")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate template;
    private final ChatWebSocketService chatWebSocketService;

    private ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

    @Operation(summary = "채팅 중 이미지 메시지 전송", description = "채팅 중 상대방 에게 이미지 메시지 전송하기 위한 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "저장 완료 및 웹소켓에 구독 중인 채팅방에 이미지 메시지 전송"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청")
    })
    @PostMapping("/{roomId}/img")
    public void saveChatImg(@PathVariable String roomId, List<MultipartFile> chatImgMsgList) {
        List<ChatMsgResDTO> chatMsgResDTOList = chatWebSocketService.saveChatImgList(roomId, chatImgMsgList);
        sendMessageToTopic(roomId, chatMsgResDTOList);
    }

    @MessageMapping("/chat/{roomId}/text")
    public void sendMessage(@DestinationVariable String roomId, ChatSendTextMsgDto sendTextMsgDto) {
        ChatMsgResDTO chatMsgResDTO = chatWebSocketService.saveMsgAndReturnChatTextMsg(roomId, sendTextMsgDto);
        sendMessageToTopic(roomId, chatMsgResDTO);
    }

    @MessageMapping("/chat/{roomId}/read")
    public void readMsg(@DestinationVariable String roomId, ChatReadReqDto chatReadReqDto) {
        ChatReadResDto chatReadResDto = chatWebSocketService.readMsgAndReturnChatReadResponse(roomId, chatReadReqDto);
        sendMessageToTopic(roomId, chatReadResDto);
    }

    private void sendMessageToTopic(String roomId, Object message) {
        try {
            template.convertAndSend("/topic/message/" + roomId, om.writeValueAsBytes(message));
        } catch (JsonProcessingException e) {
            // 익셉션 제어 필요..
            throw new RuntimeException(e);
        }
    }
}
