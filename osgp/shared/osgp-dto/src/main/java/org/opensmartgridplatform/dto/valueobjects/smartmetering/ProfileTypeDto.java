/*
 * Copyright 2023 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class ProfileTypeDto implements Serializable {

  private static final long serialVersionUID = 2123390296585369209L;

  private final List<ProfileTypeValueDto> profileTypeValues;

  public ProfileTypeDto(final List<ProfileTypeValueDto> profileTypeValues) {
    super();
    this.profileTypeValues = profileTypeValues;
  }

  public List<ProfileTypeValueDto> getProfileTypeValues() {
    return this.profileTypeValues;
  }
}
