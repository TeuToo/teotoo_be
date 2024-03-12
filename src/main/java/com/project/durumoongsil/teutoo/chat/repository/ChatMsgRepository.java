package com.project.durumoongsil.teutoo.chat.repository;

import com.project.durumoongsil.teutoo.chat.domain.ChatMsg;
import com.project.durumoongsil.teutoo.chat.repository.custom.ChatMsgCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMsgRepository extends JpaRepository<ChatMsg, Long>, ChatMsgCustomRepository {
}
