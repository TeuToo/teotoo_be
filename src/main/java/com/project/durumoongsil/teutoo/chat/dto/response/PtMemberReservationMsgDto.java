package com.project.durumoongsil.teutoo.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PtMemberReservationMsgDto {

    private String ptProgramName;

    private int ptProgramPrice;

    private String gymAddress;
}
