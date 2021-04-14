/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleMessageDataContainerDto;

public class SetScheduleDeviceRequest extends DeviceRequest {

  private final ScheduleMessageDataContainerDto scheduleMessageDataContainer;
  private final RelayTypeDto relayType;

  public SetScheduleDeviceRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final int messagePriority,
      final ScheduleMessageDataContainerDto scheduleMessageDataContainer,
      final RelayTypeDto relayType) {
    super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);
    this.scheduleMessageDataContainer = scheduleMessageDataContainer;
    this.relayType = relayType;
  }

  public SetScheduleDeviceRequest(
      final Builder deviceRequestBuilder,
      final ScheduleMessageDataContainerDto scheduleMessageDataContainer,
      final RelayTypeDto relayType) {
    super(deviceRequestBuilder);
    this.scheduleMessageDataContainer = scheduleMessageDataContainer;
    this.relayType = relayType;
  }

  public ScheduleMessageDataContainerDto getScheduleMessageDataContainer() {
    return this.scheduleMessageDataContainer;
  }

  public RelayTypeDto getRelayType() {
    return this.relayType;
  }
}
