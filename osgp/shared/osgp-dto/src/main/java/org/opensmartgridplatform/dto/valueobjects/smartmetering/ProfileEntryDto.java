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

public class ProfileEntryDto implements Serializable {

  private static final long serialVersionUID = 2123390296585369209L;

  private final List<ProfileEntryValueDto> profileEntryValues;

  public ProfileEntryDto(final List<ProfileEntryValueDto> profileEntryValues) {
    super();
    this.profileEntryValues = profileEntryValues;
  }

  public List<ProfileEntryValueDto> getProfileEntryValues() {
    return this.profileEntryValues;
  }
}
