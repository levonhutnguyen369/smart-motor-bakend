package backend.datn.service;


import backend.datn.entity.Device;
import backend.datn.entity.User;
import backend.datn.repository.DeviceRepository;
import backend.datn.repository.TelemetryRepository;
import backend.datn.entity.Telemetry;
import backend.datn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelemetryServiceImpl implements TelemetryService {

    private final TelemetryRepository repository;
    private final DeviceService deviceService;

    @Override
    public Telemetry save(
            String deviceId,
            Double latitude,
            Double longitude
    ) {

        Telemetry telemetry =
                Telemetry.builder()
                        .deviceId(deviceId)
                        .latitude(latitude)
                        .longitude(longitude)
                        .createdAt(LocalDateTime.now())
                        .build();

        return repository.save(telemetry);
    }

    @Override
    public List<Telemetry> findByDevice(
            Long userId
    ) {
        Device device = deviceService.getByUserId(userId);
        return repository
                .findByDeviceIdOrderByCreatedAtDesc(
                        device.getDeviceId()
                );
    }

    @Override
    public Telemetry latest(
            Long userId
    ) {
        Device device = deviceService.getByUserId(userId);
        List<Telemetry> list =
                repository.findByDeviceIdOrderByCreatedAtDesc(
                        device.getDeviceId()
                );

        return list.isEmpty()
                ? null
                : list.get(0);
    }

    @Override
    public List<Telemetry> tripHistory(
            Long userId,
            String startTime,
            String endTime
    ) {
        Device device = deviceService.getByUserId(userId);

        if (startTime == null || endTime == null) {
            return repository.findByDeviceIdOrderByCreatedAtDesc(
                    device.getDeviceId()
            );
        }

        LocalDateTime start =
                LocalDateTime.parse(startTime);

        LocalDateTime end =
                LocalDateTime.parse(endTime);

        return repository
                .findByDeviceIdAndCreatedAtBetweenOrderByCreatedAtAsc(
                        device.getDeviceId(),
                        start,
                        end
                );
    }
}
