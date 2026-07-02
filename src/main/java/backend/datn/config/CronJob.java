package backend.datn.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CronJob {

    // Chạy mỗi 14 phút
    @Scheduled(fixedRate = 840000)
    public void keepServerAwake() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            // Thay URL dưới đây bằng domain thực tế của bạn trên Render
            String url = "https://smart-motor-bakend-1.onrender.com";
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("Self-ping success: " + response);
        } catch (Exception e) {
            System.err.println("Self-ping error: " + e.getMessage());
        }
    }
}
