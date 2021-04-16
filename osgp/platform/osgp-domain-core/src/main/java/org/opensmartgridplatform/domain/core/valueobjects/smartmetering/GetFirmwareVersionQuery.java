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

public class GetFirmwareVersionQuery implements Serializable {

  private static final long serialVersionUID = -217306438695457044L;

  private final boolean mbusDevice;

  public GetFirmwareVersionQuery() {
    this(false);
  }

  public GetFirmwareVersionQuery(final boolean mbusDevice) {
    this.mbusDevice = mbusDevice;
  }

  public boolean isMbusDevice() {
    return this.mbusDevice;
  }
}
