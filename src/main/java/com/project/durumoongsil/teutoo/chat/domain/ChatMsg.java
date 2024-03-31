package com.project.durumoongsil.teutoo.chat.domain;


import com.project.durumoongsil.teutoo.chat.constants.MsgType;
import com.project.durumoongsil.teutoo.common.BaseTimeEntity;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtReservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMsg extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MsgType msgType;

    private String textContent;

    private String imgPath;

    private String imgName;

    private String ptProgramName;

    private Integer ptProgramPrice;

    private String gymAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private PtReservation ptReservation;

    @Builder
    public ChatMsg(MsgType msgType, String textContent, String imgPath, String imgName, int ptProgramPrice, String ptProgramName,
                   String gymAddress, Member sender, Chat chat, PtReservation ptReservation) {
        this.msgType = msgType;
        this.textContent = textContent;
        this.imgPath = imgPath;
        this.imgName = imgName;
        this.ptProgramPrice = ptProgramPrice;
        this.ptProgramName = ptProgramName;
        this.gymAddress = gymAddress;
        this.sender = sender;
        this.chat = chat;
        this.ptReservation = ptReservation;
    }
}
