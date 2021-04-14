/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
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
