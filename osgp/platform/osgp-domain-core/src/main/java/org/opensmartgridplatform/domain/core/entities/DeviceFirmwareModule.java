// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

/**
 * DeviceFirmwareModule entity class holds the module version of a firmware module known to be
 * installed on a device.
 */
@Entity
public class DeviceFirmwareModule implements Comparable<DeviceFirmwareModule>, Serializable {

  private static final long serialVersionUID = -2836788496333802810L;

  @Embeddable
  public static class DeviceFirmwareModuleId implements Serializable {

    private static final long serialVersionUID = -9004715010956854043L;

    @Column private Long deviceId;

    @Column private Long firmwareModuleId;

    protected DeviceFirmwareModuleId() {
      // No-argument constructor, not to be used by application code.
    }

    public DeviceFirmwareModuleId(final Long deviceId, final Long firmwareModuleId) {
      this.deviceId = deviceId;
      this.firmwareModuleId = firmwareModuleId;
    }

    public Long getDeviceId() {
      return this.deviceId;
    }

    public Long getFirmwareModuleId() {
      return this.firmwareModuleId;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof DeviceFirmwareModuleId)) {
        return false;
      }
      final DeviceFirmwareModuleId other = (DeviceFirmwareModuleId) obj;
      return Objects.equals(this.deviceId, other.deviceId)
          && Objects.equals(this.firmwareModuleId, other.firmwareModuleId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.deviceId, this.firmwareModuleId);
    }

    @Override
    public String toString() {
      return String.format("[device=%s, module=%s]", this.deviceId, this.firmwareModuleId);
    }
  }

  @EmbeddedId private DeviceFirmwareModuleId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "device_id")
  @MapsId("deviceId")
  private Device device;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "firmware_module_id")
  @MapsId("firmwareModuleId")
  private FirmwareModule firmwareModule;

  @Column(nullable = false)
  private String moduleVersion;

  protected DeviceFirmwareModule() {
    // No-argument constructor, not to be used by application code.
  }

  public DeviceFirmwareModule(
      final Device device, final FirmwareModule firmwareModule, final String moduleVersion) {
    this.device = device;
    this.firmwareModule = firmwareModule;
    this.id = new DeviceFirmwareModuleId(device.getId(), firmwareModule.getId());
    this.moduleVersion = moduleVersion;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DeviceFirmwareModule)) {
      return false;
    }
    final DeviceFirmwareModule other = (DeviceFirmwareModule) obj;
    return Objects.equals(this.device, other.device)
        && Objects.equals(this.firmwareModule, other.firmwareModule);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.device, this.firmwareModule);
  }

  @Override
  public int compareTo(final DeviceFirmwareModule o) {
    final int compareDevice =
        this.device.getDeviceIdentification().compareTo(o.device.getDeviceIdentification());
    if (compareDevice != 0) {
      return compareDevice;
    }
    final int compareFirmwareModule = this.firmwareModule.compareTo(o.firmwareModule);
    if (compareFirmwareModule != 0) {
      return compareFirmwareModule;
    }
    return this.moduleVersion.compareTo(o.moduleVersion);
  }

  @Override
  public String toString() {
    return String.format(
        "DeviceFirmwareModule[device=%s, module=%s, version=%s]",
        this.device.getDeviceIdentification(),
        this.firmwareModule.getDescription(),
        this.moduleVersion);
  }

  public void prepareForRemoval() {
    this.device = null;
    this.firmwareModule = null;
  }

  public DeviceFirmwareModuleId getId() {
    return this.id;
  }

  public Device getDevice() {
    return this.device;
  }

  public FirmwareModule getFirmwareModule() {
    return this.firmwareModule;
  }

  public String getModuleVersion() {
    return this.moduleVersion;
  }

  public void setModuleVersion(final String moduleVersion) {
    this.moduleVersion = moduleVersion;
  }
}
