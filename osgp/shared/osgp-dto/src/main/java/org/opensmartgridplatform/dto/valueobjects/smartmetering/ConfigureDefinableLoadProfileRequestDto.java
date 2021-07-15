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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ConfigureDefinableLoadProfileRequestDto implements ActionRequestDto {

  private final String deviceIdentification;
  private final ObisCodeValuesDto obisCode;
  private final Date beginDate;
  private final Date endDate;
  private final List<CaptureObjectDefinitionDto> selectedValues;

  private static final long serialVersionUID = -2483665562035897062L;

  public ConfigureDefinableLoadProfileRequestDto(
      final String deviceIdentification,
      final ObisCodeValuesDto obisCode,
      final Date beginDate,
      final Date endDate,
      final List<CaptureObjectDefinitionDto> selectedValues) {

    this.deviceIdentification = deviceIdentification;
    this.obisCode = obisCode;
    this.beginDate = beginDate;
    this.endDate = endDate;
    this.selectedValues = selectedValues;
  }

  public ConfigureDefinableLoadProfileRequestDto(
      final String deviceIdentification,
      final ObisCodeValuesDto obisCode,
      final Date beginDate,
      final Date endDate) {
    this(deviceIdentification, obisCode, beginDate, endDate, Collections.emptyList());
  }

  @Override
  public String toString() {
    return String.format(
        "%s[device=%s, obisCode=%s, begin=%tF %<tT.%<tL %<tZ, end=%tF %<tT.%<tL %<tZ, selected=%s]",
        ConfigureDefinableLoadProfileRequestDto.class.getSimpleName(),
        this.deviceIdentification,
        this.obisCode,
        this.beginDate,
        this.endDate,
        this.selectedValues.isEmpty() ? "all capture objects" : this.selectedValues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        this.deviceIdentification,
        this.obisCode,
        this.beginDate,
        this.endDate,
        this.selectedValues);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ConfigureDefinableLoadProfileRequestDto)) {
      return false;
    }
    final ConfigureDefinableLoadProfileRequestDto other =
        (ConfigureDefinableLoadProfileRequestDto) obj;

    return Objects.equals(this.deviceIdentification, other.deviceIdentification)
        && Objects.equals(this.obisCode, other.obisCode)
        && Objects.equals(this.beginDate, other.beginDate)
        && Objects.equals(this.endDate, other.endDate)
        && Objects.equals(this.selectedValues, other.selectedValues);
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public Date getBeginDate() {
    return new Date(this.beginDate.getTime());
  }

  public Date getEndDate() {
    return new Date(this.endDate.getTime());
  }

  public List<CaptureObjectDefinitionDto> getSelectedValues() {
    return new ArrayList<>(this.selectedValues);
  }
}
