/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
