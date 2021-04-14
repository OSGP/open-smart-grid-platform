/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

public class FirmwareUpdateMessageDataContainer implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -1767816115878168922L;

  private final FirmwareModuleData firmwareModuleData;
  private final String firmwareUrl;

  public FirmwareUpdateMessageDataContainer(
      final FirmwareModuleData firmwareModuleData, final String firmwareUrl) {
    this.firmwareModuleData = firmwareModuleData;
    this.firmwareUrl = firmwareUrl;
  }

  public FirmwareModuleData getFirmwareModuleData() {
    return this.firmwareModuleData;
  }

  public String getFirmwareUrl() {
    return this.firmwareUrl;
  }
}
