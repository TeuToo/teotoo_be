package com.project.durumoongsil.teutoo.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "회원 수정 Dto")
public class MemberUpdateDto {

    @Schema(description = "주소")
    @NotBlank
    private String address;

    @Schema(description = "프로필 이미지")
    private MultipartFile profileImage;

    @Schema(description = "비밀번호")
    private String password;

    @Schema( description = "역할 구분 (true: 트레이너, false: 일반 사용자)")
    private Boolean role;
}
