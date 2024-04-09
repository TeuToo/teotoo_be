package com.project.durumoongsil.teutoo.chat.repository.custom;

import com.project.durumoongsil.teutoo.chat.domain.QChat;
import com.project.durumoongsil.teutoo.chat.domain.QChatMsg;
import com.project.durumoongsil.teutoo.chat.dto.query.ChatMsgQueryDto;
import com.project.durumoongsil.teutoo.chat.dto.query.QChatMsgQueryDto;
import com.project.durumoongsil.teutoo.member.domain.QMember;
import com.project.durumoongsil.teutoo.trainer.info.domain.QTrainerInfo;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtReservation;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    QMember qTrainer = new QMember("trainer");
    QChatMsg qChatMsg = QChatMsg.chatMsg;
    QPtReservation qPtReservation = QPtReservation.ptReservation;
    QPtProgram qPtProgram = QPtProgram.ptProgram;
    QTrainerInfo qTrainerInfo = QTrainerInfo.trainerInfo;


    @Override
    public List<ChatMsgQueryDto> findBySenderIdAndReceiverId(Long senderId, Long receiverId, Long currentOldestMsgIdx, int size) {

        // ID를 기준으로, 작다면 a member, 크면 b member 로 지정.
        Long aMemberId = Math.min(senderId, receiverId);
        Long bMemberId = Math.max(senderId, receiverId);

        return queryFactory
                .select(new QChatMsgQueryDto(
                        qChatMsg.id,
                        qChatMsg.sender.id,
                        qChatMsg.sender.name,
                        qChatMsg.msgType,
                        qChatMsg.createdAt,
                        qChatMsg.textContent,
                        qChatMsg.imgPath,
                        qChatMsg.imgName,
                        qPtProgram.id,
                        qPtProgram.title,
                        qChatMsg.ptProgramPrice,
                        qChatMsg.gymAddress,
                        qPtReservation.startDateTime,
                        qPtReservation.endDateTime,
                        qPtReservation.status,
                        qMember.id,
                        qMember.name,
                        qTrainer.id,
                        qTrainer.name,
                        qPtReservation.id,
                        qChatMsg.ptProgramName
                ))
                .from(qChatMsg)
                .innerJoin(qChatMsg.chat, qChat)
                .leftJoin(qChatMsg.ptReservation, qPtReservation)
                .leftJoin(qPtReservation.ptProgram, qPtProgram)
                .leftJoin(qPtProgram.trainerInfo, qTrainerInfo)
                .leftJoin(qTrainerInfo.member, qTrainer)
                .leftJoin(qPtReservation.member, qMember)
                .where(
                        qChat.aMember.id.eq(aMemberId),
                        qChat.bMember.id.eq(bMemberId),
                        msgIdxLessThan(currentOldestMsgIdx)
                )
                .orderBy(qChatMsg.id.desc())
                .limit(size)
                .fetch();
    }

    private BooleanExpression msgIdxLessThan(Long msgIdx) {
        return (msgIdx != null) ? qChatMsg.id.lt(msgIdx) : null;
    }
}
