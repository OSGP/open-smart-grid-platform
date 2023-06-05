// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationDto;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;

public interface LightMeasurementService {
  /**
   * Send a measurement report.
   *
   * @param lightSensorStatusDto The {@link LightSensorStatusDto} instance to send.
   * @param responseMetadata The {@link ResponseMetadata} instance.
   */
  void sendSensorStatus(
      LightSensorStatusDto lightSensorStatusDto, ResponseMetadata responseMetadata);

  /**
   * Send a notification about a light measurement device event.
   *
   * @param eventNotificationDto The {@link EventNotificationDto} instance to send.
   * @param responseMetadata The {@link ResponseMetadata} instance.
   */
  void sendEventNotification(
      EventNotificationDto eventNotificationDto, ResponseMetadata responseMetadata);
}
