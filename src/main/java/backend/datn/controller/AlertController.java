package backend.datn.controller;

import backend.datn.entity.Alert;
import backend.datn.service.AlertService;

import backend.datn.service.JwtService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService service;
    private final JwtService jwtService;

    @GetMapping
    public List<Alert> getAll() {
        return service.getAll();
    }

    @GetMapping("/device")
    public List<Alert> getByDevice(
            @RequestHeader("Authorization") String bearerToken)
    {

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7);
        }
        Long userId = jwtService.getUserIdFromToken(bearerToken);
        return service.getByUserId(userId);
    }
}
