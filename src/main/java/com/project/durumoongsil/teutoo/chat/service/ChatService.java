package com.project.durumoongsil.teutoo.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.durumoongsil.teutoo.chat.domain.*;
import com.project.durumoongsil.teutoo.chat.dto.query.ChatMsgQueryDto;
import com.project.durumoongsil.teutoo.chat.dto.query.ChatPreviewQueryDto;
import com.project.durumoongsil.teutoo.chat.dto.response.*;
import com.project.durumoongsil.teutoo.chat.repository.ChatMsgRepository;
import com.project.durumoongsil.teutoo.chat.repository.ChatRepository;
import com.project.durumoongsil.teutoo.common.domain.FilePath;
import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import com.project.durumoongsil.teutoo.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    public ChatActivationResDto getActivationChat(Long receiverId) {

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

            chatMsgList = chatMsgQueryList.stream()
                    .map(this::toChatMessageResDTO).collect(Collectors.toList());
            Collections.reverse(chatMsgList);
        }


        ChatInfo chatInfo = this.getChatInfo(chat, sender.getId(), receiver.getId());

        ImgResDto receiverImgResDto = new ImgResDto(receiver.getProfileImageName(),
                fileService.getImgUrl(FilePath.MEMBER_PROFILE.getPath(), receiver.getProfileImageName()));

        return ChatActivationResDto.builder()
                .roomId(chatInfo.getRoomId())
                .senderIdx(chatInfo.getSenderChatIdx())
                .receiverIdx(chatInfo.getReceiverChatIdx())
                .messages(chatMsgList)
                .receiverImg(receiverImgResDto)
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

    private ChatMsgResDTO toChatMessageResDTO(ChatMsgQueryDto chatMsgQueryDto) {

        ChatMsgResDTO chatMessageResDto = initializeChatMessageResponse(chatMsgQueryDto);
        this.setMessageContentBasedOnType(chatMessageResDto, chatMsgQueryDto);

        return chatMessageResDto;
    }

    private ChatMsgResDTO initializeChatMessageResponse(ChatMsgQueryDto chatMsgQueryDto) {
        ChatMsgResDTO chatMessageResponse = new ChatMsgResDTO();
        chatMessageResponse.setCreatedAt(chatMsgQueryDto.getCreatedAt());
        chatMessageResponse.setMsgIdx(chatMsgQueryDto.getMsgIdx());
        chatMessageResponse.setContentType(chatMsgQueryDto.getContentType());
        chatMessageResponse.setSenderId(chatMsgQueryDto.getSenderId());
        return chatMessageResponse;
    }

    private void setMessageContentBasedOnType(ChatMsgResDTO chatMessageResDto, ChatMsgQueryDto chatMsgQueryDto) {
        switch (chatMsgQueryDto.getContentType()) {
            case TEXT -> this.setTextMsg(chatMessageResDto, chatMsgQueryDto);
            case IMG -> this.setImgMsg(chatMessageResDto, chatMsgQueryDto);
            case RESERVATION, RESERVATION_ACCEPTED, RESERVATION_CANCELED -> this.setReservationMsg(chatMessageResDto, chatMsgQueryDto);
            case RESERVATION_REQ_MEMBER -> this.setMemberReservationRequestMsg(chatMessageResDto, chatMsgQueryDto);
            case RESERVATION_REQ_TRAINER -> this.setTrainerReservationRequestMsg(chatMessageResDto, chatMsgQueryDto);
        }
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
                        .reservationId(chatMsgQueryDto.getReservationId())
                        .trainerId(chatMsgQueryDto.getTrainerId())
                        .trainerName(chatMsgQueryDto.getTrainerName())
                        .memberId(chatMsgQueryDto.getMemberId())
                        .memberName(chatMsgQueryDto.getMemberName())
                        .programId(chatMsgQueryDto.getProgramId())
                        .programName(chatMsgQueryDto.getProgramName())
                        .status(chatMsgQueryDto.getStatus())
                        .startDateTime(chatMsgQueryDto.getStartDateTime())
                        .endDateTime(chatMsgQueryDto.getEndDateTime())
                        .memberName(chatMsgQueryDto.getSenderName())
                        .build();

        chatMsgResDTO.setContent(this.toContentJsonStr(ptReservationMsgDto));

        return chatMsgResDTO;
    }

    private ChatMsgResDTO setMemberReservationRequestMsg(ChatMsgResDTO chatMsgResDTO, ChatMsgQueryDto chatMsgQueryDto) {
        PtMemberReservationMsgDto memberReservationMsgDto = PtMemberReservationMsgDto.builder()
                .programName(chatMsgQueryDto.getMsgProgramName())
                .price(chatMsgQueryDto.getPtProgramPrice())
                .address(chatMsgQueryDto.getGymAddress())
                .build();

        chatMsgResDTO.setContent(this.toContentJsonStr(memberReservationMsgDto));

        return chatMsgResDTO;
    }

    private ChatMsgResDTO setTrainerReservationRequestMsg(ChatMsgResDTO chatMsgResDTO, ChatMsgQueryDto chatMsgQueryDto) {
        PtTrainerReservationMsgDto trainerReservationMsgDto = PtTrainerReservationMsgDto.builder()
                .price(chatMsgQueryDto.getPtProgramPrice())
                .address(chatMsgQueryDto.getGymAddress())
                .build();

        chatMsgResDTO.setContent(this.toContentJsonStr(trainerReservationMsgDto));

        return chatMsgResDTO;
    }

    private String toContentJsonStr(Object content) {
        try {
            return om.writeValueAsString(content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
                .map(chatPreviewQueryDto -> toChatPreviewResDto(chatPreviewQueryDto, memberId))
                .toList();
    }

    private ChatPreviewResDto toChatPreviewResDto(ChatPreviewQueryDto chatPreviewQueryDto, Long memberId) {
        ChatPreviewResDto chatPreviewResDto = new ChatPreviewResDto();

        // 상대방이 BMember 라면,
        boolean isOtherUserBMember = (Objects.equals(chatPreviewQueryDto.getAMemberId(), memberId));

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

