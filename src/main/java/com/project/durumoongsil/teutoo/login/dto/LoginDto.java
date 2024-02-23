package com.project.durumoongsil.teutoo.login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 2, max = 100)
    private String password;
}
