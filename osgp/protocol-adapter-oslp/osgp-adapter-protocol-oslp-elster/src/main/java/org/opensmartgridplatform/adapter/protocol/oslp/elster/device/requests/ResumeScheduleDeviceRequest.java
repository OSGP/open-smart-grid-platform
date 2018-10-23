/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.ResumeScheduleMessageDataContainerDto;

public class ResumeScheduleDeviceRequest extends DeviceRequest {

    private final ResumeScheduleMessageDataContainerDto resumeScheduleContainer;

    public ResumeScheduleDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final int messagePriority,
            final ResumeScheduleMessageDataContainerDto resumeScheduleContainer) {
        super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);

        this.resumeScheduleContainer = resumeScheduleContainer;
    }

    public ResumeScheduleDeviceRequest(final Builder deviceRequestBuilder,
            final ResumeScheduleMessageDataContainerDto resumeScheduleContainer) {
        super(deviceRequestBuilder);
        this.resumeScheduleContainer = resumeScheduleContainer;
    }

    public ResumeScheduleMessageDataContainerDto getResumeScheduleContainer() {
        return this.resumeScheduleContainer;
    }
}
