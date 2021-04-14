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

public class GetDataSystemIdentifierDto extends SystemIdentifierDto implements Serializable {

  private static final long serialVersionUID = -1981029545705567105L;

  private List<MeasurementDto> measurements;
  private List<ProfileDto> profiles;

  public GetDataSystemIdentifierDto(
      final int id, final String systemType, final List<MeasurementDto> measurements) {
    this(id, systemType, measurements, new ArrayList<>());
  }

  public GetDataSystemIdentifierDto(
      final int id,
      final String systemType,
      final List<MeasurementDto> measurements,
      final List<ProfileDto> profiles) {
    super(id, systemType);
    this.measurements = new ArrayList<>(measurements);
    this.profiles = new ArrayList<>(profiles);
  }

  public List<MeasurementDto> getMeasurements() {
    return Collections.unmodifiableList(this.measurements);
  }

  public List<ProfileDto> getProfiles() {
    return Collections.unmodifiableList(this.profiles);
  }
}
