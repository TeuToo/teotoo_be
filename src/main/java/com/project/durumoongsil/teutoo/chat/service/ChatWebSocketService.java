package com.project.durumoongsil.teutoo.chat.service;

import com.project.durumoongsil.teutoo.chat.constants.MsgAction;
import com.project.durumoongsil.teutoo.chat.domain.Chat;
import com.project.durumoongsil.teutoo.chat.domain.ChatMsg;
import com.project.durumoongsil.teutoo.chat.constants.MsgType;
import com.project.durumoongsil.teutoo.chat.dto.request.ChatReadReqDto;
import com.project.durumoongsil.teutoo.chat.dto.request.ChatSendTextMsgDto;
import com.project.durumoongsil.teutoo.chat.dto.response.ChatMsgResDTO;
import com.project.durumoongsil.teutoo.chat.dto.response.ChatReadResDto;
import com.project.durumoongsil.teutoo.chat.repository.ChatMsgRepository;
import com.project.durumoongsil.teutoo.chat.repository.ChatRepository;
import com.project.durumoongsil.teutoo.common.domain.FilePath;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.ChatNotFoundException;
import com.project.durumoongsil.teutoo.exception.InvalidActionException;
import com.project.durumoongsil.teutoo.exception.UnauthorizedActionException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketService {

    private final ChatRepository chatRepository;
    private final ChatMsgRepository chatMsgRepository;
    private final FileService fileService;
    private final SecurityService securityService;


    /**
     * 메시지를 저장하고, 저장된 메시지 정보를 바탕으로 ChatMessageDTO를 생성하여 반환합니다.
     *
     * @param roomId 채팅방 ID
     * @param sendTextMsgDto 메시지 정보를 담고 있는 DTO
     * @return 텍스트 관련 메시지 DTO
     * @throws ChatNotFoundException UnauthorizedActionException
     */
    @Transactional
    public ChatMsgResDTO saveMsgAndReturnChatTextMsg(String roomId, ChatSendTextMsgDto sendTextMsgDto) {
        Chat chat = this.getChatByRoomId(roomId);

        Member sender = this.getMemberFromChat(chat);

        ChatMsg chatMsg = ChatMsg.builder()
                .msgType(MsgType.TEXT)
                .textContent(sendTextMsgDto.getContent())
                .sender(sender)
                .chat(chat)
                .build();

        ChatMsg savedChatMsg = chatMsgRepository.save(chatMsg);

        // sender의 message index 업데이트
        this.updateSenderMsgIdx(chat, sender.getId(), savedChatMsg.getId());

        return ChatMsgResDTO.builder()
                .msgAction(MsgAction.SEND)
                .contentType(savedChatMsg.getMsgType())
                .msgIdx(savedChatMsg.getId())
                .content(savedChatMsg.getTextContent())
                .createdAt(savedChatMsg.getCreatedAt())
                .senderId(sender.getId())
                .build();
    }

    private Chat getChatByRoomId(String roomId) {
        return chatRepository.findByRoomIdWithAMemberAndBMember(roomId)
                .orElseThrow(() -> new ChatNotFoundException("해당 채팅방을 찾을 수 없습니다."));
    }


    private Member getMemberFromChat(Chat chat) {

        // 사용자 이메일 획득
        String senderEmail = securityService.getLoginedUserEmail();

        if (chat.getAMember().getEmail().equals(senderEmail))
            return chat.getAMember();
        else if (chat.getBMember().getEmail().equals(senderEmail))
            return chat.getBMember();

        // 해당 채팅방에 해당 하는 사용자가 아니라면..
        throw new UnauthorizedActionException("해당 채팅방의 사용자가 아닙니다.");
    }

    private void updateSenderMsgIdx(Chat chat, Long memberId, Long updatedMsgIdx) {
        Member aMember = chat.getAMember();

        if (aMember.getId() == memberId) {
            chat.updateAMsgIdx(updatedMsgIdx);
        } else {
            chat.updateBMsgIdx(updatedMsgIdx);
        }
    }


    /**
     * 채팅방에서, sender가 보낸 메시지를 읽고 난 후, reader가 서버에 해당 메시지 id 값을 통하여
     * 메시지 index를 갱신 하기 위한 메소드입니다.
     *
     * @param roomId 채팅방 ID
     * @param chatReadReqDto 읽은 사용자의 갱신할 메시지 index
     * @return sender id와 sender 메시지 index, receiver id와 receiver 메시지 index 를 포함한 DTO
     * @throws InvalidActionException
     */
    @Transactional
    public ChatReadResDto readMsgAndReturnChatReadResponse(String roomId, ChatReadReqDto chatReadReqDto) {
        Chat chat = getChatByRoomId(roomId);
        Member receiver = getMemberFromChat(chat);
        updateReceiverMsgIdx(chat, receiver.getId(), chatReadReqDto.getReadIdx());

        return buildChatReadResDto(chat, receiver);
    }

    private void updateReceiverMsgIdx(Chat chat, Long memberId, Long updatedMsgIdx) {

        if (isAMember(chat, memberId)) {
            validateAndUpdateMsgIdx(chat, updatedMsgIdx, chat.getAMsgIdx(), chat.getBMsgIdx());
        } else {
            validateAndUpdateMsgIdx(chat, updatedMsgIdx, chat.getBMsgIdx(), chat.getAMsgIdx());
        }
    }

    private boolean isAMember(Chat chat, Long memberId) {
        return chat.getAMember().getId().equals(memberId);
    }

    private void validateAndUpdateMsgIdx(Chat chat, Long updatedMsgIdx, Long currentMsgIdx, Long otherMsgIdx) {
        if (updatedMsgIdx < currentMsgIdx || updatedMsgIdx > otherMsgIdx) {
            throw new InvalidActionException("유효하지 않은 MsgIdx 갱신 요청 입니다.");
        }
        if (isAMember(chat, updatedMsgIdx)) {
            chat.updateAMsgIdx(updatedMsgIdx);
        } else {
            chat.updateBMsgIdx(updatedMsgIdx);
        }
    }

    private ChatReadResDto buildChatReadResDto(Chat chat, Member receiver) {
        Long receiverId = receiver.getId();
        Long receiverIdx = isAMember(chat, receiverId) ? chat.getAMsgIdx() : chat.getBMsgIdx();
        Long senderId = isAMember(chat, receiverId) ? chat.getBMember().getId() : chat.getAMember().getId();
        Long senderIdx = isAMember(chat, receiverId) ? chat.getBMsgIdx() : chat.getAMsgIdx();

        return ChatReadResDto.builder()
                .receiverId(receiverId)
                .receiverIdx(receiverIdx)
                .senderId(senderId)
                .senderIdx(senderIdx)
                .build();
    }

    /**
     * 채팅방에서, sender가 상대방에게 이미지를 보내기 위해, 이미지를 저장 후, 이미지 메시지 데이터를 반환하기 위한 메소드입니다.
     * @param roomId 채팅방 ID
     * @param chatImgMsgList 저장할 이미지
     * @return 버킷 url을 포함한 이미지 관련 메시지 DTO
     */
    @Transactional
    public List<ChatMsgResDTO> saveChatImgList(String roomId, List<MultipartFile> chatImgMsgList) {
        Chat chat = getChatByRoomId(roomId);
        Member sender = getMemberFromChat(chat);


      /*  1. S3에 이미지 저장
        2. Chat msg 저장 및 sender msg index 갱신
        3. Chat msg response dto로 반환*/
        return chatImgMsgList.stream()
                .map(chatImg -> saveChatImgAndGetResponse(chat, sender, chatImg))
                .toList();
    }

    private ChatMsgResDTO saveChatImgAndGetResponse(Chat chat, Member sender, MultipartFile chatImg) {
        String savedImgName = fileService.saveImg(FilePath.CHAT_IMG.getPath(), chatImg);
        ChatMsg chatMsg = createChatMsg(chat, sender, savedImgName);
        ChatMsg savedChatMsg = chatMsgRepository.save(chatMsg);
        this.updateSenderMsgIdx(chat, sender.getId(), savedChatMsg.getId());

        String imgUrl = fileService.getImgUrl(FilePath.CHAT_IMG.getPath(), savedImgName);
        return createChatMsgResDTO(savedChatMsg, sender, imgUrl);
    }

    private ChatMsg createChatMsg(Chat chat, Member sender, String savedImgName) {
        return ChatMsg.builder()
                .chat(chat)
                .sender(sender)
                .msgType(MsgType.IMG)
                .imgPath(FilePath.CHAT_IMG.getPath())
                .imgName(savedImgName)
                .build();
    }

    private ChatMsgResDTO createChatMsgResDTO(ChatMsg savedChatMsg, Member sender, String imgUrl) {
        return ChatMsgResDTO.builder()
                .msgIdx(savedChatMsg.getId())
                .senderId(sender.getId())
                .contentType(MsgType.IMG)
                .content(imgUrl)
                .createdAt(savedChatMsg.getCreatedAt())
                .build();
    }
}
