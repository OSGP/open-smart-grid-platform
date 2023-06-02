//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
