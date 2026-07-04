package backend.datn.mqtt;

import backend.datn.dto.AlertPayload;
import backend.datn.dto.BatteryPayload;
import backend.datn.dto.SimBalanceResponse;
import backend.datn.dto.TelemetryPayload;
import backend.datn.entity.Device;
import backend.datn.entity.Status;
import backend.datn.repository.DeviceRepository;
import backend.datn.service.AlertService;
import backend.datn.service.TelemetryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MqttMessageHandler {

    private final ObjectMapper mapper;

    private final TelemetryService telemetryService;

    private final AlertService alertService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    // import cai log vo nua
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private DeviceRepository deviceRepository;

    public MqttMessageHandler(
            ObjectMapper mapper,
            TelemetryService telemetryService,
            AlertService alertService, MqttPublisherService mqttPublisherService, SimpMessagingTemplate messagingTemplate)
    {
        this.messagingTemplate = messagingTemplate;
        System.out.println("MqttMessageHandler CREATED");

        this.mapper = mapper;
        this.telemetryService = telemetryService;
        this.alertService = alertService;
    }

    @ServiceActivator(
            inputChannel = "mqttInputChannel"
    )
    public void receive(Message<?> message) throws Exception {

        String topic =
                (String) message.getHeaders()
                        .get("mqtt_receivedTopic");

        String payload =
                (String) message.getPayload();

        System.out.println("RECEIVED MQTT");
        System.out.println(payload);

        assert topic != null;
        if(topic.startsWith("bike/telemetry/"))
        {
            TelemetryPayload dto =
                    mapper.readValue(
                            payload,
                            TelemetryPayload.class
                    );
            if (deviceRepository.findByDeviceId(dto.getDeviceId()).isEmpty()) {
                System.out.println("Device not found: " + dto.getDeviceId());
                deviceRepository.save(
                        backend.datn.entity.Device.builder()
                                .deviceId(dto.getDeviceId())
                                .name("ESP32 " + dto.getDeviceId())
                                .batteryVoltage(0.0)
                                .batteryPercent(0)
                                .antiTheftEnabled(false)
                                .status(Status.INACTIVE) // hoặc Status.INACTIVE tùy enum của bạn
                                .lastSeen(LocalDateTime.now())
                                .user(null)
                                .build()
                );
            }

            telemetryService.save(
                    dto.getDeviceId(),
                    dto.getLat(),
                    dto.getLng()
            );
        }

        if(topic.startsWith("bike/alert/"))
        {
            AlertPayload dto =
                    mapper.readValue(
                            payload,
                            AlertPayload.class
                    );

            alertService.save(
                    dto.getDeviceId(),
                    dto.getEvent(),
                    dto.getLat(),
                    dto.getLng()
            );
        }
        if (topic.startsWith("bike/battery/")) {

            String deviceId = topic.substring("bike/battery/".length());

            Device device = deviceRepository.findByDeviceId(deviceId).orElse(null);

            if (device == null) {
                System.out.println("Device not found: " + deviceId);
                return;
            }

            String[] parts = payload.split(",");

            BatteryPayload dto =
                    mapper.readValue(
                            payload,
                            BatteryPayload.class
                    );

            if (dto != null) {
                try {
                    device.setBatteryVoltage(dto.getVoltage());
                    device.setBatteryPercent(dto.getPercent());

                    deviceRepository.save(device);

                    System.out.println("Updated battery for " + deviceId);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (topic.startsWith("bike/balance/response/")) {
            System.out.println("MATCH");
        }

        if (topic.startsWith("bike/balance/response/")) {

            String deviceId = topic.substring("bike/balance/response/".length()).trim();

            Device device = deviceRepository.findByDeviceId(deviceId)
                    .orElse(null);

            if (device == null) {
                log.warn("Device not found: {}", deviceId);
                return;
            }

            SimBalanceResponse response =
                    objectMapper.readValue(payload, SimBalanceResponse.class);

            messagingTemplate.convertAndSend(
                    "/topic/balance/" + deviceId,
                    response
            );

            log.info("Balance response sent to WebSocket: {}", deviceId);
        }


    }
}