//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulatorstarter.protocol.dlms.starter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  public Starter(final ApplicationArguments applicationArguments) throws IOException {
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
    if ("start".equals(action)) {
      this.startSimulator(simulatorConfiguration);
    } else {
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
            + org.opensmartgridplatform.simulatorstarter.protocol.dlms.starter.Starter
                .formatActiveProfiles(simulatorConfiguration.getProfiles()));
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
