/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class FirmwareVersion implements Serializable {

  private static final long serialVersionUID = 892449074530829565L;

  private final FirmwareModuleType firmwareModuleType;
  private final String version;

  public FirmwareVersion(final FirmwareModuleType firmwareModuleType, final String version) {
    this.firmwareModuleType = firmwareModuleType;
    this.version = version;
  }

  @Override
  public String toString() {
    return String.format("[%s => %s]", this.firmwareModuleType, this.version);
  }

  public FirmwareModuleType getFirmwareModuleType() {
    return this.firmwareModuleType;
  }

  public String getVersion() {
    return this.version;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.firmwareModuleType, this.version);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof FirmwareVersion)) {
      return false;
    }
    final FirmwareVersion other = (FirmwareVersion) obj;
    return this.firmwareModuleType == other.firmwareModuleType
        && Objects.equals(this.version, other.version);
  }
}
