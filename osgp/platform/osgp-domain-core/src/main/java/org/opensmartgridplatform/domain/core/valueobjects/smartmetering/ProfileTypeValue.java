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

public class ProfileTypeValue implements Serializable {

  private final Serializable value;

  public ProfileTypeValue(final Serializable value) {
    this.value = value;
  }

  public Serializable getValue() {
    return this.value;
  }
}
