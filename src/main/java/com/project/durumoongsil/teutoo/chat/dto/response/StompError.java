package com.project.durumoongsil.teutoo.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StompError {
    private String code;
    private String message;
}
