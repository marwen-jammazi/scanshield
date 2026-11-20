package com.scanshield.backend.ml;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class MlClientTest {

    private HttpServer server;
    private final AtomicInteger calls = new AtomicInteger();
    private volatile int status = 200;
    private volatile String body = "{\"scam_probability\":0.87}";

    @BeforeEach
    void startFakeService() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/predict", exchange -> {
            calls.incrementAndGet();
            exchange.getRequestBody().readAllBytes();
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        });
        server.start();
    }

    @AfterEach
    void stopFakeService() {
        server.stop(0);
    }

    private MlClient client(boolean enabled) {
        return new MlClient(enabled, "http://127.0.0.1:" + server.getAddress().getPort(), 1000, 30);
    }

    @Test
    void returnsTheProbabilityFromTheService() {
        assertThat(client(true).scamProbability("hi")).contains(0.87);
    }

    @Test
    void disabledClientNeverCallsTheService() {
        assertThat(client(false).scamProbability("hi")).isEmpty();
        assertThat(calls.get()).isZero();
    }

    @Test
    void serverErrorReturnsEmptyAndPausesFurtherCalls() {
        status = 503;
        body = "{}";
        MlClient client = client(true);

        assertThat(client.scamProbability("hi")).isEmpty();
        assertThat(client.scamProbability("hi")).isEmpty();

        assertThat(calls.get()).isEqualTo(1);
    }

    @Test
    void clientErrorSkipsThatMessageWithoutPausing() {
        status = 422;
        body = "{}";
        MlClient client = client(true);
        assertThat(client.scamProbability("hi")).isEmpty();

        status = 200;
        body = "{\"scam_probability\":0.87}";
        assertThat(client.scamProbability("hi")).contains(0.87);
        assertThat(calls.get()).isEqualTo(2);
    }

    @Test
    void outOfRangeProbabilityIsIgnored() {
        body = "{\"scam_probability\":1.7}";
        assertThat(client(true).scamProbability("hi")).isEmpty();
    }

    @Test
    void unreachableServiceReturnsEmptyWithoutThrowing() throws IOException {
        int freePort;
        try (ServerSocket socket = new ServerSocket(0)) {
            freePort = socket.getLocalPort();
        }
        MlClient client = new MlClient(true, "http://127.0.0.1:" + freePort, 1000, 30);

        Optional<Double> result = client.scamProbability("hi");

        assertThat(result).isEmpty();
    }
}