//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.ResumeScheduleMessageDataContainerDto;

public class ResumeScheduleDeviceRequest extends DeviceRequest {

  private final ResumeScheduleMessageDataContainerDto resumeScheduleContainer;

  public ResumeScheduleDeviceRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final int messagePriority,
      final ResumeScheduleMessageDataContainerDto resumeScheduleContainer) {
    super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);

    this.resumeScheduleContainer = resumeScheduleContainer;
  }

  public ResumeScheduleDeviceRequest(
      final Builder deviceRequestBuilder,
      final ResumeScheduleMessageDataContainerDto resumeScheduleContainer) {
    super(deviceRequestBuilder);
    this.resumeScheduleContainer = resumeScheduleContainer;
  }

  public ResumeScheduleMessageDataContainerDto getResumeScheduleContainer() {
    return this.resumeScheduleContainer;
  }
}
