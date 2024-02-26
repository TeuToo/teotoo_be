package com.project.durumoongsil.teutoo.trainer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.durumoongsil.teutoo.trainer.dto.TrainerUpdateInfoDto;
import com.project.durumoongsil.teutoo.trainer.service.TrainerInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class TrainerInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    TrainerInfoService trainerInfoService;

    ObjectMapper om = new ObjectMapper();

    @Test
    @DisplayName("트레이너 소개 페이지 등록/갱신API - request시에 TrainerUpdateInfoDto valid 정상 테스트")
    public void saveTrainerIntroDtoValidationTest() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/trainer/info/{trainerId}", 1)
                                .param("gymName", "goooood gym")
                                .param("introContent", "goooooooood")
                                .param("simpleIntro", "niceeeeeeeeeeee")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("트레이너 소개 페이지 등록/갱신API - request시에 TrainerUpdateInfoDto valid 에러 테스트")
    public  void saveTrainerIntroDtoValidationErrTest() throws Exception {

        String[] formKey = {"gymName", "introContent", "simpleIntro"};
        String[] formValue = {"goooood gym", "goooooood", "niceeeeeeeee"};

        for(int i = 0; i < (1 << 3) - 1; i++) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

            for(int j = 0; j < 3; j++) {
                if ((i & (1 << j)) >= 1)
                    map.add(formKey[j], formValue[j]);
            }
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post("/trainer/info/{trainerId}", 1)
                                    .params(map)
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }
    }
}