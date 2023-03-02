/*
 * Copyright 2023 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class ProfileType implements Serializable {

  private final List<ProfileTypeValue> profileTypeValues;

  public ProfileType(final List<ProfileTypeValue> profileTypeValues) {
    super();
    this.profileTypeValues = profileTypeValues;
  }

  public List<ProfileTypeValue> getProfileTypeValues() {
    return this.profileTypeValues;
  }
}
