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
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class GetPowerQualityProfileRequestDataDto implements ActionRequestDto {

  private final String profileType;
  private final Date beginDate;
  private final Date endDate;
  private final ArrayList<CaptureObjectDefinitionDto> selectedValues = new ArrayList<>();

  private static final long serialVersionUID = -2483665562035897062L;

  public GetPowerQualityProfileRequestDataDto(
      final String profileType,
      final Date beginDate,
      final Date endDate,
      final List<CaptureObjectDefinitionDto> selectedValues) {
    this.profileType = profileType;
    this.beginDate = beginDate;
    this.endDate = endDate;
    if (selectedValues != null) {
      this.selectedValues.addAll(selectedValues);
    }
  }

  @Override
  public String toString() {
    return String.format(
        "%s[profileType=%s, begin=%tF %<tT.%<tL %<tZ, end=%tF %<tT.%<tL %<tZ, selected=%s]",
        GetPowerQualityProfileRequestDataDto.class.getSimpleName(),
        this.profileType,
        this.beginDate,
        this.endDate,
        this.selectedValues.isEmpty() ? "all capture objects" : this.selectedValues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.profileType, this.beginDate, this.endDate, this.selectedValues);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof GetPowerQualityProfileRequestDataDto)) {
      return false;
    }
    final GetPowerQualityProfileRequestDataDto other = (GetPowerQualityProfileRequestDataDto) obj;
    return Objects.equals(this.profileType, other.profileType)
        && Objects.equals(this.beginDate, other.beginDate)
        && Objects.equals(this.endDate, other.endDate)
        && Objects.equals(this.selectedValues, other.selectedValues);
  }

  public String getProfileType() {
    return this.profileType;
  }

  public Date getBeginDate() {
    return this.beginDate;
  }

  public Date getEndDate() {
    return this.endDate;
  }

  public List<CaptureObjectDefinitionDto> getSelectedValues() {
    return this.selectedValues;
  }
}
