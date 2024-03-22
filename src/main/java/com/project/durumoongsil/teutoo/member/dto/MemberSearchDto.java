package com.project.durumoongsil.teutoo.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberSearchDto {
    private Long memberId;
    @NotBlank(message = "이름은 필수 값 입니다.")
    @Schema(description = "사용자 이름")
    private String name;
    @Schema(description = "주소")
    @NotBlank
    private String address;
    private String profileImagePath;
    private String profileImageName;
    private String email;
    private String setRole;
}
