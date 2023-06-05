// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.device;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SimulatorDeviceRegister {
  private final Map<Long, Map<String, Long>> logicalIdByPortByDeviceIdentification =
      new HashMap<>();

  private final Integer maxLogicalIds;

  public SimulatorDeviceRegister(
      @Value("${simulator.max.logicalids.per.port:100}") final Integer maxLogicalIds) {
    this.maxLogicalIds = maxLogicalIds;
  }

  public Long getLogicalId(final Long port, final String deviceIdentification) {
    if (port == null || deviceIdentification == null) {
      return 1L;
    }
    this.logicalIdByPortByDeviceIdentification.putIfAbsent(port, new HashMap<>());
    final Long logicalId =
        this.logicalIdByPortByDeviceIdentification.get(port).get(deviceIdentification);
    if (logicalId != null) {
      log.info(
          "Found existing logicalDeviceId: {}, device: {}, port: {}",
          logicalId,
          deviceIdentification,
          port);
      return logicalId;
    }
    final Optional<Long> lastLogicalId =
        this.logicalIdByPortByDeviceIdentification.get(port).values().stream().max(Long::compareTo);

    final Long newLogicalId = lastLogicalId.orElse(0L) + 1;
    if (newLogicalId > this.maxLogicalIds) {
      throw new RuntimeException(
          "Reached maximun number of logicalDevices for test-suite, increase number of logical device simulator");
    }
    this.logicalIdByPortByDeviceIdentification.get(port).put(deviceIdentification, newLogicalId);

    log.info(
        "Registered new getLogicalId: {}, device: {}, port: {}",
        newLogicalId,
        deviceIdentification,
        port);
    return newLogicalId;
  }
}
