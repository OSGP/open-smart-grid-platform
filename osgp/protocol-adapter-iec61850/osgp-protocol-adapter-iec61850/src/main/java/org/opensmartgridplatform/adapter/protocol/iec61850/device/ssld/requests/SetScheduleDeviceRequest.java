// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
