/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.starter;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.simulator.protocol.dlms.starter.SimulatorConfiguration;
import org.springframework.core.io.ClassPathResource;

public class SimulatorConfigurationTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void returnsLogicalDeviceIds() throws Exception {
    final SimulatorConfiguration simulatorConfiguration =
        this.parseJson("SimulatorConfiguration.json");
    assertThat(simulatorConfiguration.getLogicalDeviceIds()).containsExactly(124, 125, 126);
  }

  private SimulatorConfiguration parseJson(final String path) throws IOException {
    final File file = new ClassPathResource(path).getFile();
    return this.objectMapper.readValue(file, SimulatorConfiguration.class);
  }

  @Test
  public void returnsDeviceIdentificationForDeviceAndPort() {
    this.assertDeviceIdentificationForPortAndIndex(9999, 1, "SMLT9999000000001");
    this.assertDeviceIdentificationForPortAndIndex(9999, 12, "SMLT9999000000012");
    this.assertDeviceIdentificationForPortAndIndex(9999, 123, "SMLT9999000000123");
    this.assertDeviceIdentificationForPortAndIndex(9999, 1234, "SMLT9999000001234");
    this.assertDeviceIdentificationForPortAndIndex(9999, 12345, "SMLT9999000012345");
    this.assertDeviceIdentificationForPortAndIndex(9999, 123456, "SMLT9999000123456");
    this.assertDeviceIdentificationForPortAndIndex(99999, 1, "SMLT9999900000001");
    this.assertDeviceIdentificationForPortAndIndex(99999, 12, "SMLT9999900000012");
    this.assertDeviceIdentificationForPortAndIndex(99999, 123, "SMLT9999900000123");
    this.assertDeviceIdentificationForPortAndIndex(99999, 1234, "SMLT9999900001234");
    this.assertDeviceIdentificationForPortAndIndex(99999, 12345, "SMLT9999900012345");
    this.assertDeviceIdentificationForPortAndIndex(99999, 123456, "SMLT9999900123456");
  }

  private AbstractCharSequenceAssert<?, String> assertDeviceIdentificationForPortAndIndex(
      final int port, final int index, final String expected) {
    return Assertions.assertThat(
            this.configurationForPort(port).deviceIdentificationForIndex(index))
        .isEqualTo(expected);
  }

  private SimulatorConfiguration configurationForPort(final int port) {
    final Map<String, Object> configuration = new HashMap<>();
    configuration.put("port", port);
    return new SimulatorConfiguration(configuration);
  }

  @Test
  public void returnsIfProfileIsForSmr5() throws Exception {
    assertThat(this.parseJson("SimulatorConfiguration.json").isForSmr5()).isFalse();
    assertThat(this.parseJson("SimulatorConfiguration2.json").isForSmr5()).isTrue();
  }
}
