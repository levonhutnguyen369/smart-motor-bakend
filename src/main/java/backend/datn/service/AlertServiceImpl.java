package backend.datn.service;


import backend.datn.entity.Alert;
import backend.datn.entity.Device;
import backend.datn.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository repository;
    private final DeviceService deviceService;

    @Override
    public Alert save(
            String deviceId,
            String event,
            Double lat,
            Double lng
    ) {

        Alert alert =
                Alert.builder()
                        .deviceId(deviceId)
                        .event(event)
                        .latitude(lat)
                        .longitude(lng)
                        .createdAt(LocalDateTime.now())
                        .build();

        return repository.save(alert);
    }

    @Override
    public List<Alert> getAll() {
        return repository.findAll();
    }

    @Override
    public List<Alert> getByUserId(Long id) {
        Device device = deviceService.getByUserId(id);
        return repository.findAlertByDeviceId(device.getDeviceId());
    }
}
