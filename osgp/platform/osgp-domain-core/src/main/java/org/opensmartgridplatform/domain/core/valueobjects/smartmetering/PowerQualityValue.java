/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class PowerQualityValue implements Serializable {

  private static final long serialVersionUID = 991045734132231709L;

  private final Serializable value;

  public PowerQualityValue(final Serializable value) {
    this.value = value;
  }

  public Serializable getValue() {
    return this.value;
  }
}
