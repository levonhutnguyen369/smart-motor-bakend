package backend.datn.service;

import backend.datn.entity.Device;

public interface DeviceService {
    Device getByUserId(Long id);
    String sendDeviceCommand(Long userId, String command);
    boolean linkDeviceToAccount(Long userId, String device);
}
