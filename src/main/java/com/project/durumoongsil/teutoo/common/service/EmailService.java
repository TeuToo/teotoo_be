package com.project.durumoongsil.teutoo.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService{

    private final JavaMailSender javaMailSender;

    public void sendMail(String email, String tempPassword) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        try{
            simpleMailMessage.setTo(email);
            // 2. 메일 제목 설정
            simpleMailMessage.setSubject("임시 비밀 번호 발급");
            // 3. 메일 내용 설정
            simpleMailMessage.setText("임시 비밀 번호 : " + tempPassword);
            // 4. 메일 전송
            javaMailSender.send(simpleMailMessage);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
