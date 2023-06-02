//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.MeasurementReportingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Iec60870MeasurementReportingService implements MeasurementReportingService {

  private static final String MESSAGE_TYPE = "GET_MEASUREMENT_REPORT";

  @Autowired DeviceResponseMessageSender deviceResponseMessageSender;

  @Override
  public void send(
      final MeasurementReportDto measurementReportDto, final ResponseMetadata responseMetadata) {
    final ProtocolResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(
                responseMetadata
                    .asMessageMetadata()
                    .builder()
                    .withMessageType(MESSAGE_TYPE)
                    .withDomain(responseMetadata.getDomainInfo().getDomain())
                    .withDomainVersion(responseMetadata.getDomainInfo().getDomainVersion())
                    .build())
            .dataObject(measurementReportDto)
            .result(ResponseMessageResultType.OK)
            .build();
    this.deviceResponseMessageSender.send(responseMessage);
  }
}
