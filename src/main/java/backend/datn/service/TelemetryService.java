package backend.datn.service;

import backend.datn.entity.Telemetry;

import java.util.List;


public interface TelemetryService {

    Telemetry save(
            String deviceId,
            Double latitude,
            Double longitude
    );

    List<Telemetry> findByDevice(
            Long userId
    );

    Telemetry latest(
            Long userId
    );
}
