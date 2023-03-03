/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class PowerQualityProfileDataDto implements Serializable {

  private static final long serialVersionUID = 5222890224967684849L;

  private final ObisCodeValuesDto logicalName;
  private final List<CaptureObjectDto> captureObjects;
  private final List<ProfileEntryDto> profileEntries;
  private final ProfileTypeDto profileType;

  public PowerQualityProfileDataDto(
      final ObisCodeValuesDto logicalName,
      final List<CaptureObjectDto> captureObjects,
      final List<ProfileEntryDto> profileEntries,
      final ProfileTypeDto profileType) {
    super();
    this.logicalName = logicalName;
    this.captureObjects = captureObjects;
    this.profileEntries = profileEntries;
    this.profileType = profileType;
  }

  public ObisCodeValuesDto getLogicalName() {
    return this.logicalName;
  }

  public List<CaptureObjectDto> getCaptureObjects() {
    return this.captureObjects;
  }

  public List<ProfileEntryDto> getProfileEntries() {
    return this.profileEntries;
  }

  public ProfileTypeDto getProfileType() {
    return this.profileType;
  }
}
