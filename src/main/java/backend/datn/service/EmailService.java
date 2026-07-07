package backend.datn.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Configuration
public class EmailService {
//    private final JavaMailSender mailSender;
//
//    public void send(String to, String subject, String content) {
//
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//
//            message.setTo(to);
//            message.setSubject(subject);
//            message.setText(content);
//
//            mailSender.send(message);
//
//            System.out.println("===== SEND MAIL SUCCESS =====");
//
//        } catch (Exception e) {
//            System.out.println("===== SEND MAIL ERROR =====");
//            e.printStackTrace();
//            throw e;
//        }
//    }
    @Value("${brevo.api-key}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();

    public void send(String to, String subject, String content) {

        Map<String, Object> body = new HashMap<>();

        body.put("sender", Map.of(
                "name", "Smart Motor",
                "email", "levonhutnguyen369@gmail.com"
        ));

        body.put("to", List.of(
                Map.of("email", to)
        ));

        body.put("subject", subject);

        body.put("textContent", content);

        restClient.post()
                .uri("https://api.brevo.com/v3/smtp/email")
                .header("accept", "application/json")
                .header("api-key", apiKey)
                .header("content-type", "application/json")
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
