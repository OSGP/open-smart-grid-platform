/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.microgrids.valueobjects;

import java.util.ArrayList;
import java.util.List;

public class SetDataSystemIdentifier extends SystemIdentifier {

  private static final long serialVersionUID = 9130054367163068097L;

  private final List<SetPoint> setPoints;
  private final List<Profile> profiles;

  public SetDataSystemIdentifier(
      final int id,
      final String systemType,
      final List<SetPoint> setPoints,
      final List<Profile> profiles) {
    super(id, systemType);
    this.setPoints = new ArrayList<>(setPoints);
    this.profiles = new ArrayList<>(profiles);
  }

  public List<SetPoint> getSetPoints() {
    return new ArrayList<>(this.setPoints);
  }

  public List<Profile> getProfiles() {
    return new ArrayList<>(this.profiles);
  }
}
