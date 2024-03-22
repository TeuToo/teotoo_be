package com.project.durumoongsil.teutoo.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.durumoongsil.teutoo.chat.domain.*;
import com.project.durumoongsil.teutoo.chat.dto.query.ChatMsgQueryDto;
import com.project.durumoongsil.teutoo.chat.dto.query.ChatPreviewQueryDto;
import com.project.durumoongsil.teutoo.chat.dto.response.ChatActivationResDTO;
import com.project.durumoongsil.teutoo.chat.dto.response.ChatMsgResDTO;
import com.project.durumoongsil.teutoo.chat.dto.response.ChatPreviewResDto;
import com.project.durumoongsil.teutoo.chat.dto.response.PtReservationMsgDto;
import com.project.durumoongsil.teutoo.chat.repository.ChatMsgRepository;
import com.project.durumoongsil.teutoo.chat.repository.ChatRepository;
import com.project.durumoongsil.teutoo.common.domain.FilePath;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import com.project.durumoongsil.teutoo.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMsgRepository chatMsgRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;
    private final SecurityService securityService;
    private final ObjectMapper om;


    /**
     * 사용자 A와 사용자 B의 아이디를 얻고, 채팅방 번호와 최근 채팅 메시지를 반환
     * @param receiverId 대화를 활성화 할 상대방의 id
     * @return 활성화 될 room id, 메세지 내역과 자신과 상대방의 메시지 index를 포함한 DTO
     */
    @Transactional
    public ChatActivationResDTO getActivationChat(Long receiverId) {

        String userEmail = securityService.getLoginedUserEmail();

        Member sender = memberRepository.findMemberByEmail(userEmail)
                .orElseThrow(() -> new NotFoundUserException("해당 사용자를 찾을 수 없습니다."));
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundUserException("해당 사용자를 찾을 수 없습니다."));

        Chat chat = chatRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId());

        List<ChatMsgResDTO> chatMsgList = new ArrayList<>();
        // 만약, 채팅방이 존재하지 않는다면,
        if (chat == null) {
            chat = this.saveChat(sender, receiver);
        } else {
            // 존재하면, 최근 채팅목록 불러옴,
            List<ChatMsgQueryDto> chatMsgQueryList =
                    chatMsgRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId());

            chatMsgList = chatMsgQueryList.stream().map(this::toChatMessageResDTO).toList();
        }

        ChatInfo chatInfo = this.getChatInfo(chat, sender.getId(), receiver.getId());

        return ChatActivationResDTO.builder()
                .roomId(chatInfo.getRoomId())
                .senderIdx(chatInfo.getSenderChatIdx())
                .receiverIdx(chatInfo.getReceiverChatIdx())
                .messages(chatMsgList)
                .build();
    }

    private ChatInfo getChatInfo(Chat chat, Long senderId, Long receiverId) {

        Long senderMsgIdx = (chat.getAMember().getId() == senderId) ? chat.getAMsgIdx() : chat.getBMsgIdx();
        Long receiverMsgIdx = (chat.getAMember().getId() != senderId) ? chat.getAMsgIdx() : chat.getBMsgIdx();

        return ChatInfo.builder()
                .roomId(chat.getRoomId())
                .senderId(senderId)
                .senderChatIdx(senderMsgIdx)
                .receiverId(receiverId)
                .receiverChatIdx(receiverMsgIdx)
                .build();
    }

    private Chat saveChat(Member sender, Member receiver) {

        // id 값이 작은 member가 a member, 크면 b member
        Member aMember = (sender.getId() < receiver.getId()) ? sender : receiver;
        Member bMember = (sender.getId() < receiver.getId()) ? receiver : sender;

        Chat chat = Chat.builder()
                .roomId(this.generateRandomRoomId())
                .aMember(aMember)
                .aMsgIdx(0L)
                .bMember(bMember)
                .bMsgIdx(0L)
                .build();

        return chatRepository.save(chat);
    }

    private String generateRandomRoomId() {
        return UUID.randomUUID().toString();
    }

    private ChatMsgResDTO toChatMessageResDTO(ChatMsgQueryDto chatMsgQueryDTO) {

        ChatMsgResDTO chatMessageResDTO = new ChatMsgResDTO();
        chatMessageResDTO.setCreatedAt(chatMsgQueryDTO.getCreatedAt());
        chatMessageResDTO.setMsgIdx(chatMsgQueryDTO.getMsgIdx());
        chatMessageResDTO.setContentType(chatMsgQueryDTO.getContentType());
        chatMessageResDTO.setSenderId(chatMsgQueryDTO.getSenderId());

        switch (chatMsgQueryDTO.getContentType()) {
            case TEXT -> this.setTextMsg(chatMessageResDTO, chatMsgQueryDTO);
            case IMG -> this.setImgMsg(chatMessageResDTO, chatMsgQueryDTO);
            case RESERVATION -> this.setReservationMsg(chatMessageResDTO, chatMsgQueryDTO);
        }

        return chatMessageResDTO;
    }
    
    private ChatMsgResDTO setTextMsg(ChatMsgResDTO chatMsgResDTO, ChatMsgQueryDto chatMsgQueryDto) {
        chatMsgResDTO.setContent(chatMsgQueryDto.getTextContent());

        return chatMsgResDTO;
    }

    private ChatMsgResDTO setImgMsg(ChatMsgResDTO chatMsgResDTO, ChatMsgQueryDto chatMsgQueryDto) {
        chatMsgResDTO.setContent(fileService
                .getImgUrl(chatMsgQueryDto.getImgPath(), chatMsgQueryDto.getImgName()));

        return chatMsgResDTO;
    }

    private ChatMsgResDTO setReservationMsg(ChatMsgResDTO chatMsgResDTO, ChatMsgQueryDto chatMsgQueryDto) {

        PtReservationMsgDto ptReservationMsgDto = PtReservationMsgDto.builder()
                        .programId(chatMsgQueryDto.getProgramId())
                        .programName(chatMsgQueryDto.getProgramName())
                        .status(chatMsgQueryDto.getStatus())
                        .startDateTime(chatMsgQueryDto.getStartDateTime())
                        .endDateTime(chatMsgQueryDto.getEndDateTime())
                        .memberName(chatMsgQueryDto.getSenderName())
                        .build();

        try {
            chatMsgResDTO.setContent(om.writeValueAsString(ptReservationMsgDto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return chatMsgResDTO;
    }


    /**
     * 사용자의 대화목록을 반환하기 위한 메소드
     * @param searchName 검색할 상대방의 이름
     * @return 상대방의 id, 이름, 프로필이미지, 안읽은 채팅개수, 가장 최근의 메시지 DTO를 포함한 DTO
     */
    @Transactional
    public List<ChatPreviewResDto> getChatPreviewList(String searchName) {
        String userEmail = securityService.getLoginedUserEmail();

        Long memberId = memberRepository.findIdByEmail(userEmail)
                .orElseThrow(() -> new NotFoundUserException("해당 회원을 찾을 수 없습니다."));

        List<ChatPreviewQueryDto> previewQueryDtoList = chatRepository.findChatMembersByEmailAndName(userEmail, searchName);

        return previewQueryDtoList.stream()
                .map(chatPreviewQueryDto -> mapToChatPreviewResDto(chatPreviewQueryDto, memberId))
                .toList();
    }

    private ChatPreviewResDto mapToChatPreviewResDto(ChatPreviewQueryDto chatPreviewQueryDto, Long memberId) {
        ChatPreviewResDto chatPreviewResDto = new ChatPreviewResDto();

        // 상대방이 BMember 라면,
        boolean isOtherUserBMember = (chatPreviewQueryDto.getAMemberId() == memberId);

        this.setChatPreviewResDto(chatPreviewResDto, chatPreviewQueryDto, isOtherUserBMember);
        this.setUnReadChatCnt(chatPreviewResDto, chatPreviewQueryDto, isOtherUserBMember);
        this.setLatestChat(chatPreviewResDto, chatPreviewQueryDto);

        return chatPreviewResDto;
    }

    private void setChatPreviewResDto(ChatPreviewResDto chatPreviewResDto, ChatPreviewQueryDto chatPreviewQueryDto, boolean isOtherUserBMember) {

        if (isOtherUserBMember) {
            chatPreviewResDto.setMemberId(chatPreviewQueryDto.getBMemberId());
            chatPreviewResDto.setName(chatPreviewQueryDto.getBMemberName());
            chatPreviewResDto.setProfileImgUrl(fileService.getImgUrl(FilePath.MEMBER_PROFILE.getPath(), chatPreviewQueryDto.getBMemberImgName()));
        } else {
            chatPreviewResDto.setMemberId(chatPreviewQueryDto.getAMemberId());
            chatPreviewResDto.setName(chatPreviewQueryDto.getAMemberName());
            chatPreviewResDto.setProfileImgUrl(fileService.getImgUrl(FilePath.MEMBER_PROFILE.getPath(), chatPreviewQueryDto.getAMemberImgName()));
        }
    }

    private void setUnReadChatCnt(ChatPreviewResDto chatPreviewResDto, ChatPreviewQueryDto chatPreviewQueryDto, boolean isOtherUserBMember) {

        // 본인이 A member 일 때, 안읽은 메시지 개수
        long unReadMsgCntFromBMember = chatPreviewQueryDto.getAMemberChatIdx() - chatPreviewQueryDto.getBMemberChatIdx();
        // 본인이 B member 일 때, 안읽은 메시지 개수
        long unReadMsgCntFromAMember = chatPreviewQueryDto.getBMemberChatIdx() - chatPreviewQueryDto.getAMemberChatIdx();

        long unReadMsgCnt = isOtherUserBMember ? unReadMsgCntFromBMember : unReadMsgCntFromAMember;
        // 0보다 작으면, 0으로 초기화
        chatPreviewResDto.setUnReadChatCnt(Math.max(unReadMsgCnt, 0));
    }

    private void setLatestChat(ChatPreviewResDto chatPreviewResDto, ChatPreviewQueryDto chatPreviewQueryDto) {
        ChatMsgResDTO latestChatMsgResDTO = this.toChatMessageResDTO(chatPreviewQueryDto.getChatMsgQueryDTO());
        chatPreviewResDto.setLatestChat(latestChatMsgResDTO);
    }
}

