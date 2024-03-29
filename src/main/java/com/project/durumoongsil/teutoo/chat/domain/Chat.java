package com.project.durumoongsil.teutoo.chat.domain;

import com.project.durumoongsil.teutoo.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "a_member_id")
    private Member aMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b_member_id")
    private Member bMember;

    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    private List<ChatMsg> chatMsgList;

    private Long aMsgIdx;

    private Long bMsgIdx;

    public void updateAMsgIdx(Long aMsgIdx) {
        this.aMsgIdx = aMsgIdx;
    }

    public void updateBMsgIdx(Long bMsgIdx) {
        this.bMsgIdx = bMsgIdx;
    }

    @Builder
    public Chat(String roomId, Member aMember, Member bMember, List<ChatMsg> chatMsgList, Long aMsgIdx, Long bMsgIdx) {
        this.roomId = roomId;
        this.aMember = aMember;
        this.bMember = bMember;
        this.chatMsgList = chatMsgList;
        this.aMsgIdx = aMsgIdx;
        this.bMsgIdx = bMsgIdx;
    }
}
