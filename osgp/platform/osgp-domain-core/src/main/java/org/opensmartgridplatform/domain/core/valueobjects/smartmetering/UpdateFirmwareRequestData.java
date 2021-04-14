/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class UpdateFirmwareRequestData implements ActionRequest {

  private static final long serialVersionUID = 1537858643381805500L;

  private final List<FirmwareVersion> firmwareVersions;

  public UpdateFirmwareRequestData(final List<FirmwareVersion> firmwareVersions) {
    this.firmwareVersions = new ArrayList<>(firmwareVersions);
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed
  }

  public List<FirmwareVersion> getFirmwareVersions() {
    return new ArrayList<>(this.firmwareVersions);
  }

  /**
   * Returns a map of version information by firmware module type.
   *
   * <p>If the list of firmware versions as returned by {@link #getFirmwareVersions()} contains
   * multiple versions for the same module type, the last version will be reflected in the value
   * from this map.
   *
   * @return a map of version by module type
   */
  public Map<FirmwareModuleType, String> getVersionByModuleType() {
    final Map<FirmwareModuleType, String> versionByModuleType =
        new EnumMap<>(FirmwareModuleType.class);
    for (final FirmwareVersion firmwareVersion : this.firmwareVersions) {
      versionByModuleType.put(
          firmwareVersion.getFirmwareModuleType(), firmwareVersion.getVersion());
    }
    return versionByModuleType;
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.UPDATE_FIRMWARE;
  }
}
