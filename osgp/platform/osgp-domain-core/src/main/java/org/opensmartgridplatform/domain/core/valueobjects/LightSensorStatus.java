/**
 * Copyright 2020 Alliander N.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

public class LightSensorStatus implements Serializable {

  private static final long serialVersionUID = -6385082207732463078L;

  private final boolean on;

  public LightSensorStatus(final boolean on) {
    this.on = on;
  }

  public boolean isOn() {
    return this.on;
  }
}
