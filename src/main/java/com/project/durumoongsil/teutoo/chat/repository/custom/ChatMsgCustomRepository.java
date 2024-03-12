package com.project.durumoongsil.teutoo.chat.repository.custom;

import com.project.durumoongsil.teutoo.chat.dto.query.ChatMsgQueryDTO;

import java.util.List;

public interface ChatMsgCustomRepository {

    List<ChatMsgQueryDTO> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
