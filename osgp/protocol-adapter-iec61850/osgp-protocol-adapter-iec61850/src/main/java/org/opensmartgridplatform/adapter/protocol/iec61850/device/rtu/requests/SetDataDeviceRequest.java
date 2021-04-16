/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.requests;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetDataRequestDto;

public class SetDataDeviceRequest extends DeviceRequest {

  private SetDataRequestDto setDataRequest;

  public SetDataDeviceRequest(
      final Builder deviceRequestBuilder, final SetDataRequestDto setDataRequest) {
    super(deviceRequestBuilder);
    this.setDataRequest = setDataRequest;
  }

  public SetDataRequestDto getSetDataRequest() {
    return this.setDataRequest;
  }
}
