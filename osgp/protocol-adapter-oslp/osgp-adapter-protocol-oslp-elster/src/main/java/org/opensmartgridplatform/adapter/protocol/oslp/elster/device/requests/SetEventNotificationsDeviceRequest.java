// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationMessageDataContainerDto;

public class SetEventNotificationsDeviceRequest extends DeviceRequest {

  private final EventNotificationMessageDataContainerDto eventNotificationsContainer;

  public SetEventNotificationsDeviceRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final int messagePriority,
      final EventNotificationMessageDataContainerDto eventNotificationsContainer) {
    super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);
    this.eventNotificationsContainer = eventNotificationsContainer;
  }

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
