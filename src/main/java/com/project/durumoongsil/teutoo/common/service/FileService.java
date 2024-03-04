package com.project.durumoongsil.teutoo.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.common.repository.FileRepository;
import com.project.durumoongsil.teutoo.exception.FileSaveException;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/*
    S3 bucket 에 이미지 저장, 획득, 삭제 하기 위한 Service
 */

@Service
@RequiredArgsConstructor
public class FileService {

    private final AmazonS3 amazonS3;
    private final FileRepository fileRepository;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;


    /*
         이미지 저장
         path: bucket에 저장될 디렉토리명

         return 시, 버킷에 저장 된 파일명 반환
     */
    public String saveImg(String path, MultipartFile imgFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(imgFile.getContentType());
        objectMetadata.setContentLength(imgFile.getSize());

        String savedFileName = convertUniqueName(imgFile.getOriginalFilename());
        String savedFileFullName = path + "/" + savedFileName;

        try {
            amazonS3.putObject(bucketName, savedFileFullName, imgFile.getInputStream(), objectMetadata);
        } catch (IOException e) {
            throw new FileSaveException("파일을 저장 할 수 없습니다.");
        }

        return savedFileName;
    }

    @Transactional
    public File saveImgToDB(String path, MultipartFile imgFile) {
        String savedFileName = saveImg(path, imgFile);

        // 저장 성공시, 레포지터리에 파일 정보 저장
        File file = new File(path, savedFileName);
        return fileRepository.save(file);
    }

    /*
         s3 bucket에 이미지 url 획득, 반환
         path: bucket에 저장될 디렉토리 경로
     */
    public String getImgUrl(String path, String imgFileName) {
        if (path == null || imgFileName == null)
            return null;

        String savedFileFullName = path + "/" + imgFileName;

        return amazonS3.getUrl(bucketName, savedFileFullName).toString();
    }

    public void deleteImg(String path, String imgFileName) {
        String savedFileFullName = path + "/" + imgFileName;

        amazonS3.deleteObject(bucketName, savedFileFullName);
    }

    @Transactional
    public void deleteImgListToDB(String path, List<String> imgFileNameList) {
        // DB에서 삭제
        fileRepository.deleteAllByFileName(imgFileNameList);

        // 버킷의 이미지 삭제
        for (String imgFileName : imgFileNameList) {
            this.deleteImg("trainer_info", imgFileName);
        }
    }


    private String convertUniqueName(String imgName) {
        String extension = imgName.substring(imgName.lastIndexOf("."));

        return UUID.randomUUID() + extension;
    }
}
