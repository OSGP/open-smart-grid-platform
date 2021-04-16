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

public class ProfileEntry implements Serializable {

  private static final long serialVersionUID = 991045734132231709L;

  private final List<ProfileEntryValue> profileEntryValues;

  public ProfileEntry(final List<ProfileEntryValue> profileEntryValues) {
    super();
    this.profileEntryValues = profileEntryValues;
  }

  public List<ProfileEntryValue> getProfileEntryValues() {
    return this.profileEntryValues;
  }
}
