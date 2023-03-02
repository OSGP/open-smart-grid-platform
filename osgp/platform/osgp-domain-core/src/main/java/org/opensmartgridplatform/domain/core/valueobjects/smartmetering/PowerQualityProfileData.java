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
import java.util.List;

public class PowerQualityProfileData extends ActionResponse implements Serializable {

  private static final long serialVersionUID = -156966569210717657L;

  private final ObisCodeValues logicalName;
  private final List<CaptureObject> captureObjects;
  private final List<ProfileEntry> profileEntries;
  private final List<ProfileType> profileTypes;

  public PowerQualityProfileData(
      final ObisCodeValues logicalName,
      final List<CaptureObject> captureObjects,
      final List<ProfileEntry> profileEntries,
      final List<ProfileType> profileTypes) {
    super();
    this.logicalName = logicalName;
    this.captureObjects = captureObjects;
    this.profileEntries = profileEntries;
    this.profileTypes = profileTypes;
  }

  public ObisCodeValues getLogicalName() {
    return this.logicalName;
  }

  public List<CaptureObject> getCaptureObjects() {
    return this.captureObjects;
  }

  public List<ProfileEntry> getProfileEntries() {
    return this.profileEntries;
  }

  public List<ProfileType> getProfileTypes() {
    return this.profileTypes;
  }
}
