package backend.datn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ping")
@RequiredArgsConstructor
public class PingController {

    private final JdbcTemplate jdbcTemplate;
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
}
