package backend.datn.dto;

import lombok.Data;

@Data
public class UpdatePhoneCallRequest {
    private String phoneNumber;
    private String command;
}
