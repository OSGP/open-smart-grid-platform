// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Instant;
import java.util.Objects;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

/** DeviceFirmwareFile entity class */
@Entity
public class DeviceFirmwareFile extends AbstractEntity implements Comparable<DeviceFirmwareFile> {

  private static final long serialVersionUID = 5003530514434626119L;

  @Column(nullable = false)
  private Instant installationDate;

  @Column() private String installedBy;

  @ManyToOne(optional = false)
  @JoinColumn(name = "firmware_file_id")
  private FirmwareFile firmwareFile;

  @ManyToOne(optional = false)
  @JoinColumn(name = "device_id")
  private Device device;

  protected DeviceFirmwareFile() {
    // Default constructor for hibernate
  }

  public DeviceFirmwareFile(
      final Device device,
      final FirmwareFile firmwareFile,
      final Instant installationDate,
      final String installedBy) {
    this.device = device;
    this.firmwareFile = firmwareFile;
    this.installationDate = installationDate;
    this.installedBy = installedBy;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DeviceFirmwareFile)) {
      return false;
    }
    final DeviceFirmwareFile other = (DeviceFirmwareFile) obj;
    return Objects.equals(this.device, other.device)
        && Objects.equals(this.firmwareFile, other.firmwareFile)
        && Objects.equals(this.installationDate, other.installationDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.device, this.firmwareFile, this.installationDate);
  }

  @Override
  public int compareTo(final DeviceFirmwareFile o) {
    /*
     * Probably usually not necessary, since device firmware files will be
     * looked at in the context of a single device. Compare the IDs of the
     * device just in case.
     */
    final int compareDevice = Long.compare(this.device.getId(), o.device.getId());
    if (compareDevice != 0) {
      return compareDevice;
    }
    return this.installationDate.compareTo(o.installationDate);
  }

  public Instant getInstallationDate() {
    return this.installationDate;
  }

  public FirmwareFile getFirmwareFile() {
    return this.firmwareFile;
  }

  public Device getDevice() {
    return this.device;
  }

  public String getInstalledBy() {
    return this.installedBy;
  }

  public void setInstalledBy(final String installedBy) {
    this.installedBy = installedBy;
  }
}
