// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GpsCoordinates implements Serializable {

  private static final long serialVersionUID = 454785458685030204L;

  @Column private Float latitude;
  @Column private Float longitude;

  protected GpsCoordinates() {
    // Default constructor for hibernate
  }

  public GpsCoordinates(final Float latitude, final Float longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public Float getLatitude() {
    return this.latitude;
  }

  public Float getLongitude() {
    return this.longitude;
  }

  @Override
  public String toString() {
    return "GpsCoordinates [latitude=" + this.latitude + ", longitude=" + this.longitude + "]";
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof GpsCoordinates)) {
      return false;
    }

    final GpsCoordinates other = (GpsCoordinates) obj;
    return Objects.equals(this.latitude, other.latitude)
        && Objects.equals(this.longitude, other.longitude);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.latitude, this.longitude);
  }
}
