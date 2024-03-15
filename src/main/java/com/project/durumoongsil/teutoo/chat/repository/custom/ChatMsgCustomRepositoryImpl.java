package com.project.durumoongsil.teutoo.chat.repository.custom;

import com.project.durumoongsil.teutoo.chat.domain.QChat;
import com.project.durumoongsil.teutoo.chat.domain.QChatMsg;
import com.project.durumoongsil.teutoo.chat.dto.query.ChatMsgQueryDto;
import com.project.durumoongsil.teutoo.chat.dto.query.QChatMsgQueryDto;
import com.project.durumoongsil.teutoo.member.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatMsgCustomRepositoryImpl implements ChatMsgCustomRepository{

    private final JPAQueryFactory queryFactory;

    QChat qChat = QChat.chat;
    QMember qMember = QMember.member;
    QChatMsg qChatMsg = QChatMsg.chatMsg;


    @Override
    public List<ChatMsgQueryDto> findBySenderIdAndReceiverId(Long senderId, Long receiverId) {

        // ID를 기준으로, 작다면 a member, 크면 b member 로 지정.
        Long aMemberId = Math.min(senderId, receiverId);
        Long bMemberId = Math.max(senderId, receiverId);

        return queryFactory
                .select(new QChatMsgQueryDto(
                        qChatMsg.id,
                        qChatMsg.sender.id,
                        qChatMsg.msgType,
                        qChatMsg.createdAt,
                        qChatMsg.textContent,
                        qChatMsg.imgPath,
                        qChatMsg.imgName,
                        qChatMsg.programName,
                        qChatMsg.programSchedule,
                        qChatMsg.programConfirm
                ))
                .from(qChatMsg)
                .join(qChatMsg.chat, qChat)
                .where(
                        qChat.aMember.id.eq(aMemberId),
                        qChat.bMember.id.eq(bMemberId)
                )
                .orderBy(qChatMsg.id.desc())
                .limit(10)
                .fetch();
    }
}
