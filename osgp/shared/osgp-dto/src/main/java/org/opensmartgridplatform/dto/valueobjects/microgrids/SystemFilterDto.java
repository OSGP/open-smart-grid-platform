// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
