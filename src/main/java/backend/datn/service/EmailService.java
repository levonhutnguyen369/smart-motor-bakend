package backend.datn.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Configuration
public class EmailService {
    private final JavaMailSender mailSender;

    public void send(String to, String subject, String content) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);

            System.out.println("===== SEND MAIL SUCCESS =====");

        } catch (Exception e) {
            System.out.println("===== SEND MAIL ERROR =====");
            e.printStackTrace();
            throw e;
        }
    }
}
