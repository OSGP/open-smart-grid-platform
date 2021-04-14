/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SystemFilterDto extends SystemIdentifierDto implements Serializable {

  private static final long serialVersionUID = 972589625016827390L;

  private List<MeasurementFilterDto> measurementFilters;
  private List<ProfileFilterDto> profileFilters;
  private boolean all;

  public SystemFilterDto(
      final int id,
      final String systemType,
      final List<MeasurementFilterDto> measurementFilters,
      final boolean all) {
    this(id, systemType, measurementFilters, new ArrayList<>(), all);
  }

  public SystemFilterDto(
      final int id,
      final String systemType,
      final List<MeasurementFilterDto> measurementFilters,
      final List<ProfileFilterDto> profileFilters,
      final boolean all) {
    super(id, systemType);
    this.measurementFilters = new ArrayList<>(measurementFilters);
    this.profileFilters = new ArrayList<>(profileFilters);
    this.all = all;
  }

  public List<MeasurementFilterDto> getMeasurementFilters() {
    return Collections.unmodifiableList(this.measurementFilters);
  }

  public List<ProfileFilterDto> getProfileFilters() {
    return Collections.unmodifiableList(this.profileFilters);
  }

  public boolean isAll() {
    return this.all;
  }
}
