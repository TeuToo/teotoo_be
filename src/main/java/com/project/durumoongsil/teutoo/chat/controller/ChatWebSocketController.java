package com.project.durumoongsil.teutoo.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.durumoongsil.teutoo.chat.constants.ChatActivationType;
import com.project.durumoongsil.teutoo.chat.constants.ChatErrorCode;
import com.project.durumoongsil.teutoo.chat.dto.request.*;
import com.project.durumoongsil.teutoo.chat.dto.response.*;
import com.project.durumoongsil.teutoo.chat.service.ChatService;
import com.project.durumoongsil.teutoo.chat.service.ChatWebSocketService;
import com.project.durumoongsil.teutoo.exception.ChatNotFoundException;
import com.project.durumoongsil.teutoo.exception.InvalidActionException;
import com.project.durumoongsil.teutoo.exception.UnauthorizedActionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Tag(name = "채팅 중 채팅 관련 API")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final SimpMessagingTemplate template;
    private final ChatWebSocketService chatWebSocketService;
    private final ChatService chatService;

    private final ObjectMapper om;

    @Operation(summary = "채팅 중 이미지 메시지 전송", description = "채팅 중 상대방 에게 이미지 메시지 전송하기 위한 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "저장 완료 및 웹소켓에 구독 중인 채팅방에 이미지 메시지 전송"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청")
    })
    @PostMapping("/{roomId}/img")
    public void saveChatImg(@PathVariable String roomId, List<MultipartFile> chatImgMsgList) {
        List<ChatMsgResDTO> chatMsgResDTOList = chatWebSocketService.saveChatImgList(roomId, chatImgMsgList);
        this.sendMessageToTopic(roomId, chatMsgResDTOList);
    }

    @PostMapping("/activation/{receiverId}")
    @Operation(summary = "대화방 데이터 조회", description = "채팅을 하기전 상대방과의 대화방 관련 데이터를 얻기 위한 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대화방 데이터 조회 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청")
    })
    public ChatActivationResDto getChatActivation(@PathVariable("receiverId")Long receiverId, @RequestBody ChatActivationReqDto chatActivationReqDto) {

        ChatActivationResDto chatActivationResDto = chatService.getActivationChat(receiverId);

        ChatActivationType requestType = chatActivationReqDto.getActivationType();
        if (requestType != ChatActivationType.INFO) {
            ChatMsgResDTO chatMsgResDTO = null;

            if (requestType == ChatActivationType.MEMBER_RESERVE) {
                // 회원이 예약 요청시,
                chatMsgResDTO = chatWebSocketService
                        .saveAndReturnReservationRequestFromMember(chatActivationResDto.getRoomId(), chatActivationReqDto.getMemberReservationDto());
            } else if (requestType == ChatActivationType.TRAINER_RESERVE) {
                // 트레이너가 예약 요청시,
                chatMsgResDTO = chatWebSocketService
                        .saveAndReturnReservationRequestFromTrainer(chatActivationResDto.getRoomId(), chatActivationReqDto.getTrainerReservationDto());
            }

            // 메시지 추가해서 반환해줌,
            chatActivationResDto.getMessages().add(chatMsgResDTO);

            // 채팅방에 메시지 전송
            this.sendMessageToTopic(chatActivationResDto.getRoomId(), chatMsgResDTO);
        }

        return chatActivationResDto;
    }

    @MessageMapping("/chat/{roomId}/text")
    public void sendMessage(@DestinationVariable String roomId, ChatSendTextMsgDto sendTextMsgDto) {
        ChatMsgResDTO chatMsgResDTO = chatWebSocketService.saveMsgAndReturnChatTextMsg(roomId, sendTextMsgDto);
        this.sendMessageToTopic(roomId, chatMsgResDTO);
    }

    @MessageMapping("/chat/{roomId}/read")
    public void readMsg(@DestinationVariable String roomId, ChatReadReqDto chatReadReqDto) {

        ChatReadResDto chatReadResDto = chatWebSocketService.readMsgAndReturnChatReadResponse(roomId, chatReadReqDto);
        this.sendMessageToTopic(roomId, chatReadResDto);
    }

    @MessageMapping("/chat/{roomId}/reservation")
    public void reservationMsg(@DestinationVariable String roomId, ChatReservationReqDto chatReservationReqDto) {
        ChatMsgResDTO chatMsgResDTO = chatWebSocketService.saveAndReturnReservationMsg(roomId, chatReservationReqDto);

        this.sendMessageToTopic(roomId, chatMsgResDTO);
    }

    @MessageMapping("/chat/{roomId}/reservation/accept")
    public void reservationAcceptMsg(@DestinationVariable String roomId, ChatReservationAcceptDto chatReservationAcceptDto) {
        ChatMsgResDTO chatMsgResDTO = chatWebSocketService.saveAndReturnReservationAcceptMsg(roomId, chatReservationAcceptDto);

        this.sendMessageToTopic(roomId, chatMsgResDTO);
    }

    private void sendMessageToTopic(String roomId, Object message) {
        try {
            template.convertAndSend("/topic/message/" + roomId, om.writeValueAsBytes(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("전송 실패");
        }
    }

    @MessageExceptionHandler(RuntimeException.class)
    @SendToUser(destinations="/topic/error", broadcast=false)
    public StompError handleException(RuntimeException ex, Principal principal){
        log.info("exception occurred about {}, name: {}", ex.getMessage(), principal.getName());

        return this.createErrorFromException(ex);
    }

    private StompError createErrorFromException(RuntimeException ex) {
        // exception 종류에 따라 error code 획득
        ChatErrorCode errorCode = getErrorCode(ex);
        // exception 종류에 따라 error message 획득
        String errorMsg = getErrorMessage(ex, errorCode);

        return new StompError(errorCode.getCode(), errorMsg);
    }

    private ChatErrorCode getErrorCode(RuntimeException ex) {
        if (ex instanceof ChatNotFoundException) {
            return ChatErrorCode.CHAT_NOT_FOUND;
        } else if (ex instanceof UnauthorizedActionException) {
            return ChatErrorCode.UNAUTHORIZATION_ACTION;
        } else if (ex instanceof InvalidActionException) {
            return ChatErrorCode.INVALID_ACTION;
        }
        return ChatErrorCode.DEFAULT_ERROR;
    }

    private String getErrorMessage(RuntimeException ex, ChatErrorCode errorCode) {
        String errorMsg = ex.getMessage();
        if (errorMsg == null) {
            errorMsg = errorCode.getMessage();
        }
        return errorMsg;
    }
}
