/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;

public class FirmwareVersionGasResponse extends ActionResponse {

  private static final long serialVersionUID = -5750461161369562141L;

  private final FirmwareVersion firmwareVersion;

  public FirmwareVersionGasResponse(final FirmwareVersion firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
  }

  public FirmwareVersion getFirmwareVersion() {
    return this.firmwareVersion;
  }
}
