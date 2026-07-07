package backend.datn.controller;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.AuthResponse;
import backend.datn.dto.LoginRequest;
import backend.datn.dto.RegisterRequest;
import backend.datn.entity.Status;
import backend.datn.entity.User;
import backend.datn.repository.UserRepository;
import backend.datn.service.EmailService;
import backend.datn.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã tồn tại!");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String otpCode = String.valueOf((int) (Math.random() * 9000) + 1000);
        user.setOtpCode(otpCode);
        try {
            emailService.send(
                    user.getEmail(),
                    "Hệ thống smart motor",
                    "Bạn đã đăng kí tài khoản thành công. Mã OTP: " + user.getOtpCode()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
        user.setStatus(Status.PENDING);

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otpCode) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (user.getOtpCode().equals(otpCode)) {
            user.setStatus(Status.ACTIVE);
            user.setOtpCode(null); // Xóa mã OTP sau khi xác thực thành công
            userRepository.save(user);
            return ResponseEntity.ok("Xác thực OTP thành công!");
        } else {
            return ResponseEntity.badRequest().body("Mã OTP không hợp lệ!");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest request) {
        // Spring Security sẽ tự động kiểm tra email/password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        if (user.getStatus() != Status.ACTIVE) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Tài khoản chưa được xác thực!", null));
        }
        String jwtToken = jwtService.generateToken(user);

        return ResponseEntity.ok().body(new ApiResponse<>(true, "", jwtToken));
    }
}
