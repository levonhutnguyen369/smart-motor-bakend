package backend.datn.service;

import backend.datn.entity.Alert;

import java.util.List;

public interface AlertService {

    Alert save(
            String deviceId,
            String type,
            Double lat,
            Double lng
    );

    List<Alert> getAll();

    List<Alert> getByUserId(Long id);
}
