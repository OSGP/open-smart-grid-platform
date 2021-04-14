/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ProfileEntryValueDto implements Serializable {

  private static final long serialVersionUID = 2123390296585369209L;

  private final Serializable value;

  public ProfileEntryValueDto(final Serializable value) {
    this.value = value;
  }

  public Serializable getValue() {
    return this.value;
  }
}
