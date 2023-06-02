//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class PowerQualityProfileData extends ActionResponse implements Serializable {

  private static final long serialVersionUID = -156966569210717657L;

  private final ObisCodeValues logicalName;
  private final List<CaptureObject> captureObjects;
  private final List<ProfileEntry> profileEntries;
  private final ProfileType profileType;

  public PowerQualityProfileData(
      final ObisCodeValues logicalName,
      final List<CaptureObject> captureObjects,
      final List<ProfileEntry> profileEntries,
      final ProfileType profileType) {
    super();
    this.logicalName = logicalName;
    this.captureObjects = captureObjects;
    this.profileEntries = profileEntries;
    this.profileType = profileType;
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

  public ProfileType getProfileType() {
    return this.profileType;
  }
}
