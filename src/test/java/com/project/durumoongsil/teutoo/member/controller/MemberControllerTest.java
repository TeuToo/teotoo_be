package com.project.durumoongsil.teutoo.member.controller;

import com.project.durumoongsil.teutoo.member.service.MemberService;
import com.project.durumoongsil.teutoo.security.jwt.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//@ActiveProfiles("test")
@WebMvcTest
class MemberControllerTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    MemberService memberService;


//    @Test
//    @DisplayName("회원가입 테스트 성공 메서드")
//    void validation_성공_회원가입성공() throws Exception {
//        String memberJoinDtoJson = "{\"name\":\"변주환\",\"password\":\"1111\",\"email\":\"test@test.com\"}";
//
//        mockMvc.perform(post("/join")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(memberJoinDtoJson))
//                .andExpect(status().isOk())
//                .andExpect(content().string("회원가입 성공"));
//    }
//
//
//    @Test
//    @DisplayName("회원가입 테스트 실패 메서드")
//    void validation_실패_회원가입실패() throws Exception {
//        String invalidMemberJoinDtoJson = "{\"name\":\"\",\"password\":\"\",\"email\":\"invalid-email\"}";
//
//        mockMvc.perform(post("/join")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(invalidMemberJoinDtoJson)
//                        .characterEncoding("UTF-8"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.name").value("이름은 필수 값 입니다."))
//                .andExpect(jsonPath("$.email").value("이메일 형식을 지켜주세요"));
//    }
}