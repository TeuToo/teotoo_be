package com.project.durumoongsil.teutoo.chat.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMemberReservationReqDto {
    private int price;
    private long programId;
    private String address;
}
