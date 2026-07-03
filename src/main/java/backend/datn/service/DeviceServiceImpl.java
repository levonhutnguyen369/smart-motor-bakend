package backend.datn.service;


import backend.datn.dto.UpdatePhoneCallRequest;
import backend.datn.entity.Alert;
import backend.datn.entity.Device;
import backend.datn.entity.User;
import backend.datn.repository.DeviceRepository;
import backend.datn.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepository;
    private final MqttClient mqttClient;
    private final UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Device getByUserId(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        String deviceId = user.getDevice().getDeviceId();
        Optional<Device> device = deviceRepository.findByDeviceId(deviceId);
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
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + deviceId));
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
    public String sendDeviceCommandToUpdatePhoneCall(Long userId, UpdatePhoneCallRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        String deviceId = user.getDevice().getDeviceId();
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + deviceId));
        if (device == null) {
            throw new RuntimeException("Device not found with id: " + deviceId);
        }
        try {
            String payload = objectMapper.writeValueAsString(request);

            mqttClient.publish(
                    "bike/command/" + deviceId,
                    payload.getBytes(StandardCharsets.UTF_8),
                    0,
                    false
            );

            return "Command sent successfully";
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize request", e);
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
