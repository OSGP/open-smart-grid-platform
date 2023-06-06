// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public class GpsCoordinatesDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -8621653416475365264L;

  private Float latitude;
  private Float longitude;

  public GpsCoordinatesDto(final Float latitude, final Float longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public Float getLatitude() {
    return this.latitude;
  }

  public Float getLongitude() {
    return this.longitude;
  }
}
