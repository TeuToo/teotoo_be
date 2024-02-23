package com.project.durumoongsil.teutoo.login.controller;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TokenResponse {

    private String token;
}