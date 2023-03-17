/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.shared.health;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.annotation.PostConstruct;
import org.opensmartgridplatform.shared.config.AppHealthEnabledCondition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

@Service
@Conditional(AppHealthEnabledCondition.class)
public class AppHealthServer {
  private static final int HTTP_RESPONSE_OK = 200;
  private static final int HTTP_RESPONSE_NOT_OK = 503;

  @Value("${actuator.port}")
  private Integer port;

  private final List<HealthCheck> healthChecks;

  public AppHealthServer(final List<HealthCheck> healthChecks) {
    this.healthChecks = healthChecks;
  }

  @PostConstruct
  public void start() throws IOException {
    final var httpServer = HttpServer.create(new InetSocketAddress(this.port), 0);
    httpServer.createContext("/health", this::handle);
  }

  private void handle(final HttpExchange httpExchange) throws IOException {
    final var errors =
        this.healthChecks.stream()
            .map(HealthCheck::isHealthy)
            .filter(healthResponse -> !healthResponse.isOk())
            .map(HealthResponse::getMessage)
            .toList();
    if (errors.isEmpty()) {
      this.respond(httpExchange, HTTP_RESPONSE_OK, "OK");
    } else {
      this.respond(httpExchange, HTTP_RESPONSE_NOT_OK, String.join(", ", errors));
    }
  }

  private void respond(
      final HttpExchange httpExchange, final int responseCode, final String message)
      throws IOException {
    final var bytes = message.getBytes(StandardCharsets.UTF_8);
    httpExchange.sendResponseHeaders(responseCode, bytes.length);
    try (final var out = httpExchange.getResponseBody()) {
      out.write(bytes);
    }
  }
}
