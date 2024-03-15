package com.project.durumoongsil.teutoo.chat.repository.custom;

import com.project.durumoongsil.teutoo.chat.dto.query.ChatMsgQueryDto;

import java.util.List;

public interface ChatMsgCustomRepository {

    List<ChatMsgQueryDto> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
