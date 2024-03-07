// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import java.time.Instant;
import java.util.Objects;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaSettings;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class LightMeasurementDevice extends Device {

  private static final long serialVersionUID = 3318500857714946908L;

  /** Device type indicator for LMD */
  public static final String LMD_TYPE = "LMD";

  @Column private String description;

  @Column private String code;

  @Column private String color;

  @Column private Short digitalInput;

  @Column private Instant lastCommunicationTime;

  public LightMeasurementDevice() {
    // Default constructor.
  }

  public LightMeasurementDevice(final String deviceIdentification) {
    super(deviceIdentification);
  }

  public LightMeasurementDevice(
      final String deviceIdentification,
      final String alias,
      final Address containerAddress,
      final GpsCoordinates gpsCoordinates,
      final CdmaSettings cdmaSettings) {
    super(deviceIdentification, alias, containerAddress, gpsCoordinates, cdmaSettings);
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public String getCode() {
    return this.code;
  }

  public void setCode(final String code) {
    this.code = code;
  }

  public String getColor() {
    return this.color;
  }

  public void setColor(final String color) {
    this.color = color;
  }

  public Short getDigitalInput() {
    return this.digitalInput;
  }

  public void setDigitalInput(final Short digitalInput) {
    this.digitalInput = digitalInput;
  }

  public Instant getLastCommunicationTime() {
    return this.lastCommunicationTime;
  }

  public void setLastCommunicationTime(final Instant lastCommunicationTime) {
    this.lastCommunicationTime = lastCommunicationTime;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final LightMeasurementDevice device = (LightMeasurementDevice) o;
    return Objects.equals(this.deviceIdentification, device.deviceIdentification);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.deviceIdentification);
  }
}
