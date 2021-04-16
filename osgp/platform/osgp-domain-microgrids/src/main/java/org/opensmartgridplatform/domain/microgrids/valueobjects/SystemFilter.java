/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.microgrids.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SystemFilter extends SystemIdentifier implements Serializable {

  private static final long serialVersionUID = 2069822566541617223L;

  private final List<MeasurementFilter> measurementFilters;
  private final List<ProfileFilter> profileFilters;
  private final boolean all;

  public SystemFilter(
      final int id,
      final String systemType,
      final List<MeasurementFilter> measurementFilters,
      final List<ProfileFilter> profileFilters,
      final boolean all) {
    super(id, systemType);
    this.measurementFilters = new ArrayList<>(measurementFilters);
    this.profileFilters = new ArrayList<>(profileFilters);
    this.all = all;
  }

  public List<MeasurementFilter> getMeasurementFilters() {
    return new ArrayList<>(this.measurementFilters);
  }

  public List<ProfileFilter> getProfileFilters() {
    return new ArrayList<>(this.profileFilters);
  }

  public boolean isAll() {
    return this.all;
  }
}
