/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public class FirmwareUpdateMessageDataContainer implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 8346802666043737757L;

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
