package backend.datn.service;


import backend.datn.entity.Alert;
import backend.datn.entity.Device;
import backend.datn.entity.User;
import backend.datn.repository.DeviceRepository;
import backend.datn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepository;
    private final MqttClient mqttClient;
    private final UserRepository userRepository;

    public Device getByDeviceId(String id) {
        Optional<Device> device = deviceRepository.findByDeviceId(id);
        if (device.isPresent()) {
            return device.get();
        } else {
            throw new RuntimeException("Device not found with id: " + id);
        }
    }

    @Override
    public String sendDeviceCommand(Long userId, String command) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        String deviceId = user.getDevice().getDeviceId();
        Device device = getByDeviceId(deviceId);
        if (device == null) {
            throw new RuntimeException("Device not found with id: " + deviceId);
        }
        try {
            mqttClient.publish(
                    "bike/command/" + deviceId,
                    command.getBytes(),
                    0,
                    false
            );
            return "Command sent successfully";
        } catch (MqttException e) {
            throw new RuntimeException("Failed to publish MQTT message", e);
        }
    }

    @Override
    public boolean linkDeviceToAccount(Long userId, String deviceId) {
        Optional<Device> existingDevice = deviceRepository.findByDeviceId(deviceId);
        Optional<User> existingUser = userRepository.findById(userId);

        if (existingDevice.isPresent() && existingUser.isPresent()) {
            Device device = existingDevice.get();
            device.setUser(existingUser.get());
            deviceRepository.save(device);
            return true;
        }
        return false;
    }

}
