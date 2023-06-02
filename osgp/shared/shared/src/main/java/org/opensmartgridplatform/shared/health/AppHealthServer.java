//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.health;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.annotation.PostConstruct;
import org.opensmartgridplatform.shared.config.AppHealthEnabledCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

@Service
@Conditional(AppHealthEnabledCondition.class)
public class AppHealthServer {
  private static final Logger LOGGER = LoggerFactory.getLogger(AppHealthServer.class);
  private static final int HTTP_RESPONSE_OK = 200;
  private static final int HTTP_RESPONSE_NOT_OK = 503;

  @Value("${healthcheck.port}")
  private Integer port;

  private final List<HealthCheck> healthChecks;
  private HttpServer server;

  public AppHealthServer(final List<HealthCheck> healthChecks) {
    this.healthChecks = healthChecks;
  }

  @PostConstruct
  public void start() throws IOException {
    this.createHttpServer();
    LOGGER.debug("AppHealth server created.");

    this.newDaemonThread(this.server::start).start();
    LOGGER.info(
        "AppHealth server started on http://localhost:{}/health with {} checks.",
        this.port,
        this.healthChecks.size());
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

  private void createHttpServer() throws IOException {
    this.server = HttpServer.create(new InetSocketAddress(this.port), 0);
    this.server.createContext("/health", this::handle);
  }

  private Thread newDaemonThread(final Runnable runnable) {
    final Thread thread = new Thread(runnable);
    thread.setDaemon(true);
    return thread;
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
