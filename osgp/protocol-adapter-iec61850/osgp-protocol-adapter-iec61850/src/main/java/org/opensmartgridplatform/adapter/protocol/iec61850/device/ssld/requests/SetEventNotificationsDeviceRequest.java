// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationMessageDataContainerDto;

public class SetEventNotificationsDeviceRequest extends DeviceRequest {

  private EventNotificationMessageDataContainerDto eventNotificationsContainer;

  public SetEventNotificationsDeviceRequest(
      final Builder deviceRequestBuilder,
      final EventNotificationMessageDataContainerDto eventNotificationsContainer) {
    super(deviceRequestBuilder);
    this.eventNotificationsContainer = eventNotificationsContainer;
  }

  public EventNotificationMessageDataContainerDto getEventNotificationsContainer() {
    return this.eventNotificationsContainer;
  }
}
