package backend.datn.dto;

import lombok.Data;

@Data
public class BatteryPayload {

    private String deviceId;

    private double voltage;

    private int percent;

    private String status;

    private double adc;

    private double adcVoltage;

    private long timestamp;

    // getter setter
}
