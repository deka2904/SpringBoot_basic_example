package com.mysit.sbb.user;

import com.mysit.sbb.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class TempPasswordMail {
    private final JavaMailSender javaMailSender;

    private String ePw;

    public MimeMessage createMessage(String to)
            throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("sbb 임시 비밀번호");

        String msgg = "";
        msgg += "<div style='margin:100px;'>";
        msgg +=
                "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>임시 비밀번호입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += ePw + "</strong><div><br/> ";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("deka2904@naver.com", "관리자"));

        return message;
    }

    public void sendSimpleMessage(String to, String pw) {
        this.ePw = pw;
        MimeMessage message;
        try {
            message = createMessage(to);
        } catch (UnsupportedEncodingException | MessagingException e) {
            e.printStackTrace();
            throw new EmailException("이메일 생성 에러");
        }
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            throw new EmailException("이메일 전송 에러");
        }
    }
}
