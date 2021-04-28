/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class LightSensorStatusDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private final LightSensorStatusTypeDto status;

  public LightSensorStatusDto(final LightSensorStatusTypeDto status) {
    this.status = status;
  }

  public LightSensorStatusTypeDto getStatus() {
    return this.status;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof LightSensorStatusDto)) {
      return false;
    }
    final LightSensorStatusDto other = (LightSensorStatusDto) obj;
    return this.status == other.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.status);
  }

  @Override
  public String toString() {
    return "LightSensorStatusDto [status=" + this.status + "]";
  }
}
