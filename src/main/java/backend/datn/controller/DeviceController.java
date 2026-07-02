package backend.datn.controller;


import backend.datn.dto.ApiResponse;
import backend.datn.entity.Device;
import backend.datn.entity.User;
import backend.datn.service.DeviceService;
import backend.datn.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final JwtService jwtService;

    @PostMapping("/link")
    public ResponseEntity<ApiResponse<?>> linkDevice(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam String deviceId)
    {

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7); // Chỉ lấy chuỗi token đứng sau
        }
        Long userId = jwtService.getUserIdFromToken(bearerToken);
        boolean linkDevice = deviceService.linkDeviceToAccount(userId, deviceId);
        ApiResponse<?> response = new ApiResponse<>();
        if (linkDevice) {
            response.setSuccess(true);
            response.setMessage("Device linked successfully");
            return ResponseEntity.ok(response);
        } else {
            response.setSuccess(false);
            response.setMessage("Failed to link device");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{deviceId}")
    public Device getDeviceById(@PathVariable String deviceId) {
        return deviceService.getByDeviceId(deviceId);
    }

    @PostMapping("/command")
    public ResponseEntity<ApiResponse<String>> sendDeviceCommand(
            @RequestHeader("Authorization") String token,
            @RequestBody String command) {
        Long userId = jwtService.getUserIdFromToken(token);
        String result = deviceService.sendDeviceCommand(userId, command);

        ApiResponse<String> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Command sent successfully");
        response.setData(result);

        return ResponseEntity.ok(response);
    }
}
