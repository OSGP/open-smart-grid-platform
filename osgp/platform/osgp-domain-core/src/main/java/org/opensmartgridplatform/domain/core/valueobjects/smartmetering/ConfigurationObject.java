//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class ConfigurationObject implements Serializable {

  private static final long serialVersionUID = 2955060885937669868L;

  private final GprsOperationModeType gprsOperationMode;

  private final ConfigurationFlags configurationFlags;

  public ConfigurationObject(
      final GprsOperationModeType gprsOperationMode, final ConfigurationFlags configurationFlags) {
    this.gprsOperationMode = gprsOperationMode;
    this.configurationFlags = configurationFlags;
  }

  public GprsOperationModeType getGprsOperationMode() {
    return this.gprsOperationMode;
  }

  public ConfigurationFlags getConfigurationFlags() {
    return this.configurationFlags;
  }
}
