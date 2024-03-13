package com.project.durumoongsil.teutoo.chat.repository.custom;

import com.project.durumoongsil.teutoo.chat.domain.Chat;
import com.project.durumoongsil.teutoo.chat.dto.query.ChatPreviewQueryDto;

import java.util.List;
import java.util.Optional;

public interface ChatCustomRepository {
    Chat findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    Long findSenderIdByRoomIdAndMemberEmail(String roomId, String memberEmail);
    Optional<Chat> findByRoomIdWithAMemberAndBMember(String roomId);

    List<ChatPreviewQueryDto> findChatMembersByEmailAndName(String memberEmail, String name);
}
