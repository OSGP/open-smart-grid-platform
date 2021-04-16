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

public class GetDataSystemIdentifier extends SystemIdentifier {

  private static final long serialVersionUID = -6201476739756810987L;

  private final List<Measurement> measurements;
  private final List<Profile> profiles;

  public GetDataSystemIdentifier(
      final int id, final String systemType, final List<Measurement> measurements) {
    this(id, systemType, measurements, new ArrayList<Profile>());
  }

  public GetDataSystemIdentifier(
      final int id,
      final String systemType,
      final List<Measurement> measurements,
      final List<Profile> profiles) {
    super(id, systemType);
    this.measurements = new ArrayList<>(measurements);
    this.profiles = new ArrayList<>(profiles);
  }

  public List<Measurement> getMeasurements() {
    return new ArrayList<>(this.measurements);
  }

  public List<Profile> getProfiles() {
    return new ArrayList<>(this.profiles);
  }
}
