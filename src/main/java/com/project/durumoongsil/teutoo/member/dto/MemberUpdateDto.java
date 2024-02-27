package com.project.durumoongsil.teutoo.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@Schema(description = "회원 수정 Dto")
public class MemberUpdateDto {

    @Schema(description = "주소")
    @NotBlank
    private String address;

    @Schema(description = "프로필 이미지")
    private MultipartFile profileImage;


    @Builder
    public MemberUpdateDto(String address) {
        this.address = address;
    }
}
