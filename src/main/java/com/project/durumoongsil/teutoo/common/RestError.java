package com.project.durumoongsil.teutoo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class RestError {
    private String code;
    private String message;


}
