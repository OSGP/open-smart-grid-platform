/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class GetPowerQualityProfileRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -6134172239685084920L;

  private final String profileType;
  private final Date beginDate;
  private final Date endDate;
  private final List<CaptureObjectDefinition> selectedValues = new ArrayList<>();

  public GetPowerQualityProfileRequestData(
      final String profileType,
      final Date beginDate,
      final Date endDate,
      final List<CaptureObjectDefinition> selectedValues) {
    this.profileType = profileType;
    this.beginDate = new Date(beginDate.getTime());
    this.endDate = new Date(endDate.getTime());
    if (selectedValues != null) {
      this.selectedValues.addAll(selectedValues);
    }
  }

  public GetPowerQualityProfileRequestData(
      final String profileType, final Date beginDate, final Date endDate) {
    this(profileType, beginDate, endDate, Collections.emptyList());
  }

  public String getProfileType() {
    return this.profileType;
  }

  public Date getBeginDate() {
    return new Date(this.beginDate.getTime());
  }

  public Date getEndDate() {
    return new Date(this.endDate.getTime());
  }

  public List<CaptureObjectDefinition> getSelectedValues() {
    return new ArrayList<>(this.selectedValues);
  }

  @Override
  public void validate() throws FunctionalException {
    // not needed here
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.GET_PROFILE_GENERIC_DATA;
  }

  @Override
  public String toString() {
    return String.format(
        "%s[profileType=%s, begin=%tF %<tT.%<tL %<tZ, end=%tF %<tT.%<tL %<tZ, selected=%s]",
        GetPowerQualityProfileRequestData.class.getSimpleName(),
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
    if (!(obj instanceof GetPowerQualityProfileRequestData)) {
      return false;
    }
    final GetPowerQualityProfileRequestData other = (GetPowerQualityProfileRequestData) obj;
    return Objects.equals(this.profileType, other.profileType)
        && Objects.equals(this.beginDate, other.beginDate)
        && Objects.equals(this.endDate, other.endDate)
        && Objects.equals(this.selectedValues, other.selectedValues);
  }
}
