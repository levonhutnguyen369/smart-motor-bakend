package backend.datn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ping")
@RequiredArgsConstructor
public class PingController {

    private final JdbcTemplate jdbcTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    private Environment env;
    @GetMapping
    public String ping() {
        try {
            // Chạy một query cực nhẹ để giữ Neon Database không bị ngủ
            jdbcTemplate.execute("SELECT 1");
            return "ping ping pong pong - Backend & Database are awake!";
        } catch (Exception e) {
            return "Ping failed: " + e.getMessage();
        }
    }

    @GetMapping("/test/ws")
    public String test() {

        messagingTemplate.convertAndSend(
                "/topic/balance/device001",
                "Hello WebSocket");

        return "OK";
    }



    @GetMapping("/test-env")
    public String test1() {
        return "MAIL_HOST = " + env.getProperty("MAIL_HOST");
    }
}
