package com.project.durumoongsil.teutoo.chat.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ChatMemberIdsQueryDto {

    private Long aMemberId;
    private Long bMemberId;

    @QueryProjection
    public ChatMemberIdsQueryDto(Long aMemberId, Long bMemberId) {
        this.aMemberId = aMemberId;
        this.bMemberId = bMemberId;
    }
}
