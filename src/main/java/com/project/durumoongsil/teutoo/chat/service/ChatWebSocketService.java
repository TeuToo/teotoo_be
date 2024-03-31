package com.project.durumoongsil.teutoo.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.durumoongsil.teutoo.chat.constants.MsgAction;
import com.project.durumoongsil.teutoo.chat.domain.Chat;
import com.project.durumoongsil.teutoo.chat.domain.ChatMsg;
import com.project.durumoongsil.teutoo.chat.constants.MsgType;
import com.project.durumoongsil.teutoo.chat.dto.request.*;
import com.project.durumoongsil.teutoo.chat.dto.response.*;
import com.project.durumoongsil.teutoo.chat.repository.ChatMsgRepository;
import com.project.durumoongsil.teutoo.chat.repository.ChatRepository;
import com.project.durumoongsil.teutoo.common.domain.FilePath;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.*;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.security.service.SecurityService;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtReservation;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtProgramRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketService {

    private final ChatRepository chatRepository;
    private final ChatMsgRepository chatMsgRepository;
    private final FileService fileService;
    private final SecurityService securityService;
    private final PtReservationRepository ptReservationRepository;
    private final PtProgramRepository ptProgramRepository;

    private ObjectMapper om = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


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

    private Member getOtherMemberFromChat(Chat chat) {

        String senderEmail = securityService.getLoginedUserEmail();

        if (chat.getAMember().getEmail().equals(senderEmail))
            return chat.getBMember();
        else if (chat.getBMember().getEmail().equals(senderEmail))
            return chat.getAMember();

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
        Member reader = getMemberFromChat(chat);
        this.updateReceiverMsgIdx(chat, reader.getId(), chatReadReqDto.getReadIdx());

        return buildChatReadResDto(chat, reader);
    }

    private void updateReceiverMsgIdx(Chat chat, Long readerId, Long updatedMsgIdx) {

        if (isAMember(chat, readerId)) {
            validateAndUpdateMsgIdx(chat, readerId, updatedMsgIdx, chat.getAMsgIdx(), chat.getBMsgIdx());
        } else {
            validateAndUpdateMsgIdx(chat, readerId, updatedMsgIdx, chat.getBMsgIdx(), chat.getAMsgIdx());
        }
    }

    private boolean isAMember(Chat chat, Long memberId) {
        return chat.getAMember().getId().equals(memberId);
    }

    private void validateAndUpdateMsgIdx(Chat chat, Long readerId, Long updatedMsgIdx, Long currentMsgIdx, Long otherMsgIdx) {
        if (updatedMsgIdx < currentMsgIdx || updatedMsgIdx > otherMsgIdx) {
            throw new InvalidActionException("유효하지 않은 MsgIdx 갱신 요청 입니다.");
        }

        if (isAMember(chat, readerId)) {
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


    /**
     * 회원으로 부터 예약 메시지를 저장하고, 이를 해당 채팅방에 예약 메시지를 전송하기 위한 메소드입니다.
     * @param roomId 채팅방 ID
     * @param chatReservationReqDto 예약을 식별 할 수 있는 예약 ID
     * @return 예약 관련 메시지를 포함한 예약 관련 메시지 DTO
     */
    @Transactional
    public ChatMsgResDTO saveAndReturnReservationMsg(String roomId, ChatReservationReqDto chatReservationReqDto) {
        Chat chat = this.getChatByRoomId(roomId);
        Member member = this.getMemberFromChat(chat);
        Member trainer = this.getOtherMemberFromChat(chat);

        PtReservation ptReservation = ptReservationRepository.findByIdWithMemberAndPtProgram(chatReservationReqDto.getReservationId())
                .orElseThrow(() -> new PtReservationNotFoundException("PT 예약 정보를 찾을 수 없습니다."));

        if (!isReservationOwner(ptReservation, member)) {
            throw new UnauthorizedActionException();
        }

        // DB에 chat 메시지 저장
        ChatMsg savedChatMsg = this.saveReservationChatMsg(chat, member, ptReservation);

        ChatMsgResDTO chatMsgResDTO = this.createReservationChatMsgResDTO(savedChatMsg, ptReservation, member, trainer);

        return chatMsgResDTO;
    }

    private boolean isReservationOwner(PtReservation ptReservation, Member member) {
        return Objects.equals(ptReservation.getMember().getId(), member.getId());
    }

    private PtReservationMsgDto createReservationMsgDto(PtReservation ptReservation, PtProgram ptProgram,
                                                        Member member, Member trainer) {

        return PtReservationMsgDto.builder()
                .reservationId(ptReservation.getId())
                .programId(ptProgram.getId())
                .programName(ptProgram.getTitle())
                .status(ptReservation.getStatus())
                .startDateTime(ptReservation.getStartDateTime())
                .endDateTime(ptReservation.getEndDateTime())
                .memberId(member.getId())
                .memberName(member.getName())
                .trainerId(trainer.getId())
                .trainerName(trainer.getName())
                .build();
    }

    private ChatMsg saveReservationChatMsg(Chat chat, Member sender, PtReservation ptReservation) {

        ChatMsg chatMsg = ChatMsg.builder()
                .chat(chat)
                .sender(sender)
                .msgType(MsgType.RESERVATION)
                .ptReservation(ptReservation)
                .build();

        return chatMsgRepository.save(chatMsg);
    }

    private ChatMsgResDTO createReservationChatMsgResDTO(ChatMsg savedChatMsg, PtReservation ptReservation, Member member, Member trainer) {
        PtReservationMsgDto ptReservationMsgDto = this.createReservationMsgDto(ptReservation, ptReservation.getPtProgram(), member, trainer);
        String content = this.toPtReservationMsgDtoJsonStr(ptReservationMsgDto);

        return ChatMsgResDTO.builder()
                .msgIdx(savedChatMsg.getId())
                .msgAction(MsgAction.SEND)
                .contentType(MsgType.RESERVATION)
                .senderId(member.getId())
                .createdAt(savedChatMsg.getCreatedAt())
                .content(content)
                .build();
    }

    private String toPtReservationMsgDtoJsonStr(PtReservationMsgDto ptReservationMsgDto) {
        try {
            return om.writeValueAsString(ptReservationMsgDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 트레이너로 부터 예약 확인 메시지를 저장하고,이를 해당 채팅방에 예약 확인 메시지를 전송하기 위한 메소드입니다.
     * @param roomId 채팅방 ID
     * @param reservationAcceptDto 예약을 식별 할 수 있는 예약 ID
     * @return 예약 관련 메시지를 포함한 예약 확인 관련 메시지 DTO
     */
    @Transactional
    public ChatMsgResDTO saveAndReturnReservationAcceptMsg(String roomId, ChatReservationAcceptDto reservationAcceptDto) {
        Chat chat = this.getChatByRoomId(roomId);
        Member trainer = this.getMemberFromChat(chat);
        Member member = this.getOtherMemberFromChat(chat);

        PtReservation ptReservation = ptReservationRepository.findByIdWithPtProgramAndTrainerInfo(reservationAcceptDto.getReservationId())
                .orElseThrow(() -> new PtReservationNotFoundException("PT 예약 정보를 찾을 수 없습니다."));

        if (!isProgramOwner(ptReservation.getPtProgram(), trainer)) {
            throw new UnauthorizedActionException();
        }

        ChatMsg savedChatMsg = this.saveReservationAcceptChatMsg(chat, trainer, ptReservation);

        ChatMsgResDTO chatMsgResDTO = this.createReservationAcceptChatMsgResDTO(savedChatMsg, ptReservation, member, trainer);

        return chatMsgResDTO;
    }

    private boolean isProgramOwner(PtProgram ptProgram, Member trainer) {
        return Objects.equals(ptProgram.getTrainerInfo().getId(), trainer.getId());
    }

    private ChatMsg saveReservationAcceptChatMsg(Chat chat, Member sender, PtReservation ptReservation) {
        ChatMsg chatMsg = ChatMsg.builder()
                .chat(chat)
                .sender(sender)
                .msgType(MsgType.RESERVATION_ACCEPTED)
                .ptReservation(ptReservation)
                .build();

        return chatMsgRepository.save(chatMsg);
    }

    private ChatMsgResDTO createReservationAcceptChatMsgResDTO(ChatMsg savedChatMsg, PtReservation ptReservation, Member member, Member trainer) {
        PtReservationMsgDto ptReservationMsgDto = this.createReservationMsgDto(ptReservation, ptReservation.getPtProgram(), member, trainer);
        String content = this.toPtReservationMsgDtoJsonStr(ptReservationMsgDto);

        return ChatMsgResDTO.builder()
                .msgIdx(savedChatMsg.getId())
                .msgAction(MsgAction.SEND)
                .contentType(MsgType.RESERVATION_ACCEPTED)
                .senderId(trainer.getId())
                .createdAt(savedChatMsg.getCreatedAt())
                .content(content)
                .build();
    }


    /**
     * 사용자가 트레이너의 견적서를 보고, 예약 하기 위해 예약 요청 메시지를 처리하기 위한 메소드입니다.
     */
    @Transactional
    public ChatMsgResDTO saveAndReturnReservationRequestFromMember(String roomId, ChatMemberReservationReqDto memberReservationReqDto) {
        Chat chat = this.getChatByRoomId(roomId);
        Member member = this.getMemberFromChat(chat);
        Member trainer = this.getOtherMemberFromChat(chat);

        if (!isPtProgramValid(trainer, memberReservationReqDto.getPtProgramId())) {
            throw new InvalidActionException("잘못된 예약 요청입니다.");
        }

        PtProgram ptProgram = ptProgramRepository.findById(memberReservationReqDto.getPtProgramId())
                .orElseThrow(PtProgramNotFoundException::new);

        ChatMsg savedChatMsg = this.saveMemberReservationChatMsg(chat, member, ptProgram, memberReservationReqDto);

        PtMemberReservationMsgDto ptMemberReservationMsgDto = this
                .createPtMemberReservationMsgDto(ptProgram, savedChatMsg);

        String reservationMsgContent = this.toPtMemberReservationMsgDtoJsonStr(ptMemberReservationMsgDto);

        return ChatMsgResDTO.builder()
                .msgIdx(savedChatMsg.getId())
                .msgAction(MsgAction.SEND)
                .content(reservationMsgContent)
                .contentType(MsgType.RESERVATION_REQ_MEMBER)
                .senderId(member.getId())
                .createdAt(savedChatMsg.getCreatedAt())
                .build();
    }

    private boolean isPtProgramValid(Member trainer, long reservationProgramId) {
        Long savedTrainerId = ptProgramRepository.findTrainerIdById(reservationProgramId)
                .orElseThrow(PtProgramNotFoundException::new);

        return Objects.equals(trainer.getId(), savedTrainerId);
    }

    private ChatMsg saveMemberReservationChatMsg(Chat chat, Member member, PtProgram ptProgram,
                                                        ChatMemberReservationReqDto memberReservationReqDto) {
        ChatMsg chatMsg = ChatMsg.builder()
                .chat(chat)
                .sender(member)
                .msgType(MsgType.RESERVATION_REQ_MEMBER)
                .gymAddress(memberReservationReqDto.getAddress())
                .ptProgramPrice(memberReservationReqDto.getPrice())
                .ptProgramName(ptProgram.getTitle())
                .build();

        return chatMsgRepository.save(chatMsg);
    }

    private PtMemberReservationMsgDto createPtMemberReservationMsgDto(PtProgram ptProgram, ChatMsg chatMsg) {
        return PtMemberReservationMsgDto.builder()
                .ptProgramName(ptProgram.getTitle())
                .gymAddress(chatMsg.getGymAddress())
                .ptProgramPrice(chatMsg.getPtProgramPrice())
                .build();
    }

    private String toPtMemberReservationMsgDtoJsonStr(PtMemberReservationMsgDto ptMemberReservationMsgDto) {
        try {
            return om.writeValueAsString(ptMemberReservationMsgDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * 트레이너가 사용자의 견적서를 보고, 예약 하기 위해 예약 요청 메시지를 처리하기 위한 메소드입니다.
     */
    @Transactional
    public ChatMsgResDTO saveAndReturnReservationRequestFromTrainer(String roomId, ChatTrainerReservationReqDto trainerReservationReqDto) {

        Chat chat = this.getChatByRoomId(roomId);
        Member trainer = this.getMemberFromChat(chat);

        ChatMsg chatMsg = this.saveTrainerReservationChatMsg(chat, trainer, trainerReservationReqDto);

        PtTrainerReservationMsgDto ptTrainerReservationMsgDto = PtTrainerReservationMsgDto.builder()
                .price(chatMsg.getPtProgramPrice())
                .address(chatMsg.getGymAddress())
                .build();

        String reservationMsgContent = this.toPtTrainerReservationMsgDtoJsonStr(ptTrainerReservationMsgDto);

        return ChatMsgResDTO.builder()
                .msgIdx(chatMsg.getId())
                .msgAction(MsgAction.SEND)
                .content(reservationMsgContent)
                .contentType(MsgType.RESERVATION_REQ_TRAINER)
                .senderId(trainer.getId())
                .createdAt(chatMsg.getCreatedAt())
                .build();
    }

    private String toPtTrainerReservationMsgDtoJsonStr(PtTrainerReservationMsgDto ptTrainerReservationMsgDto) {
        try {
            return om.writeValueAsString(ptTrainerReservationMsgDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ChatMsg saveTrainerReservationChatMsg(Chat chat, Member trainer,
                                                  ChatTrainerReservationReqDto trainerReservationReqDto) {
        ChatMsg chatMsg = ChatMsg.builder()
                .chat(chat)
                .sender(trainer)
                .msgType(MsgType.RESERVATION_REQ_TRAINER)
                .gymAddress(trainerReservationReqDto.getAddress())
                .ptProgramPrice(trainerReservationReqDto.getPrice())
                .build();

        return chatMsgRepository.save(chatMsg);
    }
}
