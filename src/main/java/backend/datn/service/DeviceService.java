package backend.datn.service;

import backend.datn.dto.UpdatePhoneCallRequest;
import backend.datn.entity.Device;

public interface DeviceService {
    Device getByUserId(Long id);
    String sendDeviceCommand(Long userId, String command);

    String sendDeviceCommandToUpdatePhoneCall(Long userId, UpdatePhoneCallRequest request);
    boolean linkDeviceToAccount(Long userId, String device);
}
