package com.project.durumoongsil.teutoo.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class MemberJoinDto {

    @NotBlank(message = "이름은 필수 값 입니다.")
    @Schema(description = "사용자 이름")
    private String name;

    @Email(message = "이메일 형식을 지켜주세요")
    @Schema(description = "사용자 이메일")
    private String email;

    @Schema(description = "비밀번호")
    @Min(4)
    @NotBlank
    private String password;

    @Schema(description = "주소")
    @NotBlank
    private String address;

    @Schema( description = "역할 구분 (true: 트레이너, false: 일반 사용자)")
    @NotBlank
    private Boolean sortRole;

    @Schema(description = "프로필 이미지")
    private MultipartFile profileImage;
}
