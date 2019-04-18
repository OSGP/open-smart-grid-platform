/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.MeasurementReportMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Iec60870MeasurementReportMessageSender implements MeasurementReportMessageSender {

    @Autowired
    DeviceResponseMessageSender deviceResponseMessageSender;

    @Override
    public void send(final MeasurementReportDto measurementReportDto) {
        final ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withDataObject(measurementReportDto).build();
        this.deviceResponseMessageSender.send(responseMessage);
    }
}
