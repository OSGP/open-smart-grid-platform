/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.starter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.simulator.protocol.dlms.database.DatabaseHelper;
import org.opensmartgridplatform.simulator.protocol.dlms.server.DeviceServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;

/** Utility for starting multiple device simulators using a single configuration file. */
@SpringBootApplication
public class Starter {
  private static final Logger LOGGER = LoggerFactory.getLogger(Starter.class);

  private final DatabaseHelper databaseHelper;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  public Starter(
      final DatabaseHelper databaseHelper, final ApplicationArguments applicationArguments)
      throws IOException {
    this.databaseHelper = databaseHelper;
    this.run(applicationArguments.getNonOptionArgs());
  }

  public static void main(final String[] args) {
    SpringApplication.run(StarterConfig.class, args);
  }

  private void run(final List<String> args) throws IOException {
    Assert.isTrue(
        args.size() == 2,
        "Expected 2 arguments: <path to configuration file> <action: insert/start/insertAndStart/delete");
    final String path = args.get(0);
    final String action = args.get(1);
    LOGGER.info("Configuration file: {}, action: {}", path, action);

    for (final SimulatorConfiguration simulatorConfiguration :
        this.parseSimulatorConfigurations(path)) {
      this.performAction(simulatorConfiguration, action);
    }
  }

  private void performAction(
      final SimulatorConfiguration simulatorConfiguration, final String action)
      throws JsonProcessingException {
    switch (action) {
      case "insert":
        this.databaseHelper.insertDevices(simulatorConfiguration);
        break;
      case "start":
        this.startSimulator(simulatorConfiguration);
        break;
      case "insertAndStart":
        this.databaseHelper.insertDevices(simulatorConfiguration);
        this.startSimulator(simulatorConfiguration);
        break;
      case "delete":
        this.databaseHelper.deleteDevices(simulatorConfiguration);
        break;
      default:
        throw new IllegalArgumentException("Unknown action: " + action);
    }
  }

  private void startSimulator(final SimulatorConfiguration simulatorConfiguration)
      throws JsonProcessingException {
    System.setProperty("user.timezone", "UTC");
    System.setProperty(
        "spring.application.json", this.toJson(simulatorConfiguration.getConfiguration()));
    System.setProperty(
        "spring.autoconfigure.exclude",
        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration");
    DeviceServer.main(
        "--spring.profiles.active="
            + org.opensmartgridplatform.simulator.protocol.dlms.starter.Starter.formatActiveProfiles(
                simulatorConfiguration.getProfiles()));
  }

  private static String formatActiveProfiles(final String[] profiles) {
    return String.join(",", profiles);
  }

  private List<SimulatorConfiguration> parseSimulatorConfigurations(final String path)
      throws IOException {
    return this.fromJson(path, SimulatorConfigurationList.class).getSimulators();
  }

  private <T> T fromJson(final String path, final Class<T> targetClass) throws IOException {
    return this.objectMapper.readValue(new File(path), targetClass);
  }

  private String toJson(final Map<String, Object> value) throws JsonProcessingException {
    return this.objectMapper.writeValueAsString(value);
  }
}
