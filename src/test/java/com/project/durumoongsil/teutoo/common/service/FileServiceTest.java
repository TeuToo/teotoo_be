package com.project.durumoongsil.teutoo.common.service;

import com.project.durumoongsil.teutoo.common.domain.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileServiceTest {

    @Autowired
    FileService fileService;

    @Test
    @DisplayName("S3 버킷 파일 저장 테스트")
    public void saveFileInS3BucketTest() throws IOException {

        MultipartFile testImgFile = getTestImgFile();
        String path = "test/trainerInfo";

        // 파일 저장
        File file = fileService.saveImgToDB(path, testImgFile);

        String imgUrl = fileService.getImgUrl(path, file.getFileName());

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<byte[]> imgResponse = restTemplate.exchange(imgUrl, HttpMethod.GET, entity, byte[].class);

        // 저장 되었는지 확인
        assertEquals(imgResponse.getStatusCode(), HttpStatus.OK);

        // 파일 삭제
        fileService.deleteImg(path, file.getFileName());

        imgUrl = fileService.getImgUrl(path, file.getFileName());

        try {
            // 파일 삭제 확인
            ResponseEntity<String> errResponse = restTemplate.exchange(imgUrl, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals(e.getStatusCode(), HttpStatus.FORBIDDEN);
        }
    }

    public MultipartFile getTestImgFile() throws IOException {
        String filePath = "trainerInfo/google_img.png";
        Resource resource = new ClassPathResource(filePath);
        java.io.File file = resource.getFile();

        CustomMultipartFile multipartFile = new CustomMultipartFile(file.toPath());

        return multipartFile;
    }

}