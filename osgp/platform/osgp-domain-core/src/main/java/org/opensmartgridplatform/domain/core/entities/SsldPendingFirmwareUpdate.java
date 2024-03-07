// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
public class SsldPendingFirmwareUpdate extends AbstractEntity {

  private static final long serialVersionUID = 1L;

  @Column(nullable = false)
  private String deviceIdentification;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private FirmwareModuleType firmwareModuleType;

  @Column(nullable = false)
  private String firmwareVersion;

  @Column(nullable = false)
  private String organisationIdentification;

  @Column private String correlationUid;

  public SsldPendingFirmwareUpdate() {
    // Default constructor.
  }

  public SsldPendingFirmwareUpdate(
      final String deviceIdentification,
      final FirmwareModuleType firmwareModuleType,
      final String firmwareVersion,
      final String organisationIdentification,
      final String correlationUid) {
    this.deviceIdentification = deviceIdentification;
    this.firmwareModuleType = firmwareModuleType;
    this.firmwareVersion = firmwareVersion;
    this.organisationIdentification = organisationIdentification;
    this.correlationUid = correlationUid;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public FirmwareModuleType getFirmwareModuleType() {
    return this.firmwareModuleType;
  }

  public String getFirmwareVersion() {
    return this.firmwareVersion;
  }

  public String getOrganisationIdentification() {
    return this.organisationIdentification;
  }

  public String getCorrelationUid() {
    return this.correlationUid;
  }

  @Override
  public String toString() {
    return "SsldPendingFirmwareUpdate [deviceIdentification="
        + this.deviceIdentification
        + ", firmwareModuleType="
        + this.firmwareModuleType
        + ", firmwareVersion="
        + this.firmwareVersion
        + ", organisationIdentification="
        + this.organisationIdentification
        + ", correlationUid="
        + this.correlationUid
        + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.deviceIdentification, this.firmwareModuleType, this.firmwareVersion);
  }

  @Override
  public boolean equals(final Object that) {
    if (this == that) {
      return true;
    }
    if (!(that instanceof SsldPendingFirmwareUpdate)) {
      return false;
    }
    final SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate = (SsldPendingFirmwareUpdate) that;
    return Objects.equals(this.deviceIdentification, ssldPendingFirmwareUpdate.deviceIdentification)
        && Objects.equals(this.firmwareModuleType, ssldPendingFirmwareUpdate.firmwareModuleType)
        && Objects.equals(this.firmwareVersion, ssldPendingFirmwareUpdate.firmwareVersion);
  }
}
