/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefinableLoadProfileConfigurationDto implements ActionRequestDto {

  private static final long serialVersionUID = -4722989892412090306L;

  private final List<CaptureObjectDefinitionDto> captureObjects = new ArrayList<>();
  private final Long capturePeriod;

  public DefinableLoadProfileConfigurationDto(
      final List<CaptureObjectDefinitionDto> captureObjects, final Long capturePeriod) {
    if (captureObjects != null) {
      this.captureObjects.addAll(captureObjects);
    }
    this.capturePeriod = capturePeriod;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("DefinableLoadProfile[");
    if (this.hasCaptureObjects()) {
      sb.append("captureObjects=").append(this.captureObjects);
    }
    if (this.hasCapturePeriod()) {
      sb.append("capturePeriod=").append(this.capturePeriod);
    }
    return sb.append(']').toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.captureObjects, this.capturePeriod);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DefinableLoadProfileConfigurationDto)) {
      return false;
    }
    final DefinableLoadProfileConfigurationDto other = (DefinableLoadProfileConfigurationDto) obj;
    return Objects.equals(this.captureObjects, other.captureObjects)
        && Objects.equals(this.capturePeriod, other.capturePeriod);
  }

  public boolean hasCaptureObjects() {
    return !this.captureObjects.isEmpty();
  }

  public List<CaptureObjectDefinitionDto> getCaptureObjects() {
    return new ArrayList<>(this.captureObjects);
  }

  public boolean hasCapturePeriod() {
    return this.capturePeriod != null;
  }

  public Long getCapturePeriod() {
    return this.capturePeriod;
  }
}
