// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

/**
 * DeviceCurrentFirmwareModuleVersion entity class is a view, which holds a firmware module
 * description and firmware module version for a device.
 */
@Entity
@IdClass(DeviceFirmwareModuleVersionId.class)
public class DeviceCurrentFirmwareModuleVersion implements Serializable {

  private static final long serialVersionUID = -5820728527642412085L;

  @Id
  @Column(nullable = false, updatable = false)
  private Long deviceId;

  @Id
  @Column(nullable = false, updatable = false)
  private String moduleDescription;

  @Id
  @Column(nullable = false, updatable = false)
  private String moduleVersion;

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DeviceCurrentFirmwareModuleVersion)) {
      return false;
    }
    final DeviceCurrentFirmwareModuleVersion other = (DeviceCurrentFirmwareModuleVersion) obj;
    return this.deviceId.equals(other.deviceId)
        && this.moduleDescription.equals(other.moduleDescription)
        && this.moduleVersion.equals(other.moduleVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.deviceId + this.moduleDescription + this.moduleVersion);
  }

  @Override
  public String toString() {
    return String.format(
        "DeviceId[%s], ModuleDescription[%s], ModuleVersion[%s]",
        this.deviceId, this.moduleDescription, this.moduleVersion);
  }

  public final Long getDeviceId() {
    return this.deviceId;
  }

  public String getModuleDescription() {
    return this.moduleDescription;
  }

  public String getModuleVersion() {
    return this.moduleVersion;
  }
}
