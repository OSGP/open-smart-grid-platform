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
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataRequestDto;

public class GetDataDeviceRequest extends DeviceRequest {

  private GetDataRequestDto getDataRequest;

  public GetDataDeviceRequest(
      final Builder deviceRequestBuilder, final GetDataRequestDto getDataRequest) {
    super(deviceRequestBuilder);
    this.getDataRequest = getDataRequest;
  }

  public GetDataRequestDto getDataRequest() {
    return this.getDataRequest;
  }
}
