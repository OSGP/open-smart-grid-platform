// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulatorstarter.protocol.dlms.starter;

import static java.util.Arrays.asList;

import com.fasterxml.jackson.annotation.JsonRawValue;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.simulator.protocol.dlms.util.LogicalDeviceIdsConverter;

public class SimulatorConfiguration {
  private String[] profiles;

  @JsonRawValue private Map<String, Object> configuration;
  private int[] logicalDeviceIds;

  private SimulatorConfiguration() {
    // No-args constructor, required for JSON deserialization.
  }

  public SimulatorConfiguration(final Map<String, Object> configuration) {
    this.configuration = configuration;
  }

  public String[] getProfiles() {
    return this.profiles;
  }

  public Map<String, Object> getConfiguration() {
    return this.configuration;
  }

  public String deviceIdentificationForIndex(final int index) {
    final String strPort = String.valueOf(this.getPort());
    return "SMLT"
        + strPort
        + StringUtils.leftPad(String.valueOf(index), 13 - strPort.length(), '0');
  }

  public int[] getLogicalDeviceIds() {
    if (this.logicalDeviceIds == null) {
      this.logicalDeviceIds =
          LogicalDeviceIdsConverter.convert((String) this.configuration.get("logicalDeviceIds"));
    }
    return this.logicalDeviceIds;
  }

  public int getPort() {
    return (int) this.configuration.get("port");
  }

  public boolean isForSmr5() {
    return asList(this.profiles).contains("smr5");
  }
}
