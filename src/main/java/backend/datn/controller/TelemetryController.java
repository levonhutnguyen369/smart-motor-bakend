package backend.datn.controller;

import backend.datn.entity.Telemetry;
import backend.datn.entity.User;
import backend.datn.service.DeviceService;
import backend.datn.service.JwtService;
import backend.datn.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService service;
    private final JwtService jwtService;

    @GetMapping("/history")
    public List<Telemetry> history(
            @RequestHeader("Authorization") String bearerToken
    ) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7); // Chỉ lấy chuỗi token đứng sau
        }
        Long userId = jwtService.getUserIdFromToken(bearerToken);


        return service.findByDevice(
                userId
        );
    }

    @GetMapping("/latest")
    public Telemetry latest(
            @RequestHeader("Authorization") String bearerToken
    ) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7); // Chỉ lấy chuỗi token đứng sau
        }
        Long userId = jwtService.getUserIdFromToken(bearerToken);
        return service.latest(
                userId
        );
    }

    @GetMapping("/trip-history")
    public List<Telemetry> tripHistory(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7); // Chỉ lấy chuỗi token đứng sau
        }
        Long userId = jwtService.getUserIdFromToken(bearerToken);
        return service.tripHistory(
                userId, startTime, endTime
        );
    }
}
