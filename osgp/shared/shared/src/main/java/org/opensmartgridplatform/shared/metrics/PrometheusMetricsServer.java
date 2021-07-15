/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.metrics;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Component that exposes custom metrics, created using Micrometer, for Prometheus. Based on the
 * example code <a href=https://micrometer.io/docs/registry/prometheus#_configuring>here</a>.
 *
 * <p>How to use:
 *
 * <ol>
 *   <li>In your configuration, include these Spring beans:
 *       <pre>
 * {@literal @}Bean
 * public PrometheusMeterRegistry prometheusMeterRegistry() {
 *     return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
 * }
 *
 * {@literal @}Bean
 * public PrometheusMetricsServer prometheusMetricsServer(PrometheusMeterRegistry prometheusMeterRegistry) {
 *     return new PrometheusMetricsServer(prometheusMeterRegistry);
 * }</pre>
 *   <li>In the application properties, specify port and path to use to publish the metrics, and the
 *       name of the component:
 *       <pre>
 * metrics.prometheus.port=9101
 * metrics.prometheus.path=/metrics
 * metrics.componentname=smart-meter-integration</pre>
 *   <li>Start writing metrics, using the meter registry bean configured in step 1. For example:
 *       <pre>
 * metrics.counter("some.counter", "tag1, "tagvalue1").increment();</pre>
 *       See <a href=https://micrometer.io/docs/concepts#_meters>the Micrometer documentation</a>
 *       for more.
 * </ol>
 */
// Suppress warnings, since HttpServer and HttpExchange are "suitable for use outside of the JDK
// implementation itself".
@SuppressWarnings({"squid:S1191", "restriction"})
public class PrometheusMetricsServer {
  private static final Logger LOGGER = LoggerFactory.getLogger(PrometheusMetricsServer.class);

  @Value("${metrics.prometheus.port}")
  private Integer port;

  @Value("${metrics.prometheus.path}")
  private String path;

  @Value("${metrics.componentname}")
  private String componentName;

  private final PrometheusMeterRegistry prometheusMeterRegistry;
  private HttpServer server;

  @Autowired
  public PrometheusMetricsServer(final PrometheusMeterRegistry prometheusMeterRegistry) {
    this.prometheusMeterRegistry = prometheusMeterRegistry;
  }

  @PostConstruct
  public void start() throws IOException {
    requireNonNull(this.port, "Port not set");
    requireNonNull(this.path, "Path not set");
    requireNonNull(this.componentName, "Component name not set");

    this.prometheusMeterRegistry.config().commonTags("component", this.componentName);
    this.createHttpServer();
    LOGGER.debug("Prometheus metrics server created.");

    this.newDaemonThread(this.server::start).start();
    LOGGER.info("Prometheus metrics server started on port {} and path {}.", this.port, this.path);
  }

  private Thread newDaemonThread(final Runnable runnable) {
    final Thread thread = new Thread(runnable);
    thread.setDaemon(true);
    return thread;
  }

  private void createHttpServer() throws IOException {
    this.server = HttpServer.create(new InetSocketAddress(this.port), 0);
    this.server.createContext(this.path, this::handle);
  }

  private void handle(final HttpExchange httpExchange) throws IOException {
    final byte[] metricsAsBytes = this.prometheusMeterRegistry.scrape().getBytes(UTF_8);
    httpExchange.sendResponseHeaders(200, metricsAsBytes.length);
    try (final OutputStream out = httpExchange.getResponseBody()) {
      out.write(metricsAsBytes);
    }
  }

  @PreDestroy
  public void stop() {
    this.server.stop(1);
  }
}
