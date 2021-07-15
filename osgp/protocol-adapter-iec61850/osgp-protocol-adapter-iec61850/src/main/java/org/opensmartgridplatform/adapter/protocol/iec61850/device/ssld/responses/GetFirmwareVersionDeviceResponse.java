/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses;

import java.util.List;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponse;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;

public class GetFirmwareVersionDeviceResponse extends DeviceResponse {

  private final List<FirmwareVersionDto> firmwareVersions;

  public GetFirmwareVersionDeviceResponse(
      final DeviceRequest deviceRequest, final List<FirmwareVersionDto> firmwareVersions) {
    super(
        deviceRequest.getOrganisationIdentification(),
        deviceRequest.getDeviceIdentification(),
        deviceRequest.getCorrelationUid(),
        deviceRequest.getMessagePriority());
    this.firmwareVersions = firmwareVersions;
  }

  public List<FirmwareVersionDto> getFirmwareVersions() {
    return this.firmwareVersions;
  }
}
