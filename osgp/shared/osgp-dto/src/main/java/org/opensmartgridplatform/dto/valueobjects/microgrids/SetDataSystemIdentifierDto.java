/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetDataSystemIdentifierDto extends SystemIdentifierDto {

  private static final long serialVersionUID = 1491574329325798488L;

  private List<SetPointDto> setPoints;
  private List<ProfileDto> profiles;

  public SetDataSystemIdentifierDto(
      final int id,
      final String systemType,
      final List<SetPointDto> setPoints,
      final List<ProfileDto> profiles) {
    super(id, systemType);
    this.setPoints = new ArrayList<>(setPoints);
    this.profiles = new ArrayList<>(profiles);
  }

  public List<SetPointDto> getSetPoints() {
    return Collections.unmodifiableList(this.setPoints);
  }

  public List<ProfileDto> getProfiles() {
    return Collections.unmodifiableList(this.profiles);
  }
}
