// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.ResumeScheduleMessageDataContainerDto;

public class ResumeScheduleDeviceRequest extends DeviceRequest {

  private ResumeScheduleMessageDataContainerDto resumeScheduleContainer;

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
