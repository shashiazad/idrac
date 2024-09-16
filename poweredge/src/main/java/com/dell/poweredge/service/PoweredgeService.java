package com.dell.poweredge.service;

import com.dell.poweredge.model.ResponseDTO;
import com.dell.poweredge.model.TelemetryData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PoweredgeService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @Value("${corpaggregator.url}")
    String corpAggregatorUrl;

    @Scheduled(fixedRate = 3000)
    public String sendTelemetryData() {
        TelemetryData data = generateRandomTelemetryData();
        int ofcDigit = random.nextInt(8) + 1;
        data.setNode("PowerEdge Server " + ofcDigit);
        String str = "";
        try {
            ResponseEntity<ResponseDTO> response = restTemplate.postForEntity(corpAggregatorUrl, data, ResponseDTO.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                str = "Telemetry data sent successfully: " + response.getBody().getMessage();
            } else {
                str =  "Failed to send telemetry data. Status code: " + response.getStatusCode();
            }
        } catch (RestClientException e) {
            System.err.println("Error while sending telemetry data: " + e.getMessage());
            e.printStackTrace();
        }
        return str;
    }

    public TelemetryData generateRandomTelemetryData() {
        TelemetryData data = new TelemetryData();
        data.setTemperature(20 + random.nextDouble() * 10);
        data.setNetworkSpeed(100 + random.nextDouble() * 50);
        data.setDiskUtilization(random.nextDouble() * 100);
        data.setCpuUtilization(random.nextDouble() * 100);
        data.setTimestamp(LocalDateTime.now());
        return data;
    }
}
