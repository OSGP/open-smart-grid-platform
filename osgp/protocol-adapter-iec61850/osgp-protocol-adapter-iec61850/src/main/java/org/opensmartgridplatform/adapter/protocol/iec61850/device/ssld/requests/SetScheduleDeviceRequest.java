/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleDto;

public class SetScheduleDeviceRequest extends DeviceRequest {

  private ScheduleDto schedule;
  private RelayTypeDto relayType;

  public SetScheduleDeviceRequest(
      final Builder deviceRequestBuilder,
      final ScheduleDto schedule,
      final RelayTypeDto relayType) {
    super(deviceRequestBuilder);
    this.schedule = schedule;
    this.relayType = relayType;
  }

  public RelayTypeDto getRelayType() {
    return this.relayType;
  }

  public ScheduleDto getSchedule() {
    return this.schedule;
  }
}
