package com.scanshield.backend.ml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Asks the ML service how likely a message is to be a scam.
 * Never throws: when the service is disabled, slow or down, it returns an empty result
 * so scanning keeps working with the rule engine alone.
 */
@Component
public class MlClient {

    private static final Logger log = LoggerFactory.getLogger(MlClient.class);

    public record PredictRequest(String message) {}

    public record PredictResponse(double scam_probability) {}

    private final boolean enabled;
    private final RestClient client;
    private final long cooldownNanos;
    private final long cooldownSeconds;
    private volatile long pausedUntil = System.nanoTime() - 1;

    public MlClient(
            @Value("${scamshield.ml.enabled:false}") boolean enabled,
            @Value("${scamshield.ml.url:http://localhost:8000}") String baseUrl,
            @Value("${scamshield.ml.timeout-ms:1500}") long timeoutMs,
            @Value("${scamshield.ml.cooldown-seconds:30}") long cooldownSeconds) {
        this.enabled = enabled;
        this.cooldownSeconds = cooldownSeconds;
        this.cooldownNanos = TimeUnit.SECONDS.toNanos(cooldownSeconds);

        HttpClient http = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(timeoutMs))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(http);
        factory.setReadTimeout(Duration.ofMillis(timeoutMs));
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    public Optional<Double> scamProbability(String message) {
        if (!enabled || System.nanoTime() - pausedUntil < 0) {
            return Optional.empty();
        }
        try {
            PredictResponse response = client.post()
                    .uri("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PredictRequest(message))
                    .retrieve()
                    .body(PredictResponse.class);

            if (response == null || response.scam_probability() < 0 || response.scam_probability() > 1) {
                pause("invalid response");
                return Optional.empty();
            }
            return Optional.of(response.scam_probability());
        } catch (HttpClientErrorException e) {
            // The service rejected this one message: skip it, but keep using the service
            return Optional.empty();
        } catch (RuntimeException e) {
            pause(e.getClass().getSimpleName());
            return Optional.empty();
        }
    }

    private void pause(String reason) {
        pausedUntil = System.nanoTime() + cooldownNanos;
        log.warn("ML service unavailable ({}), scanning without it for {} s", reason, cooldownSeconds);
    }
}