package backend.datn.service;

import backend.datn.entity.Device;

public interface DeviceService {
    Device getByDeviceId(String id);
    String sendDeviceCommand(Long userId, String command);
    boolean linkDeviceToAccount(Long userId, String device);
}
