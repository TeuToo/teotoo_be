package com.project.durumoongsil.teutoo.chat.domain;


import com.project.durumoongsil.teutoo.common.BaseTimeEntity;
import com.project.durumoongsil.teutoo.member.domain.Member;
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

    private String programName;

    private String programSchedule;

    private Boolean programConfirm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Builder
    public ChatMsg(MsgType msgType, String textContent, String imgPath, String imgName, String programName,
                   String programSchedule, Boolean programConfirm, Member sender, Chat chat) {
        this.msgType = msgType;
        this.textContent = textContent;
        this.imgPath = imgPath;
        this.imgName = imgName;
        this.programName = programName;
        this.programSchedule = programSchedule;
        this.programConfirm = programConfirm;
        this.sender = sender;
        this.chat = chat;
    }
}
