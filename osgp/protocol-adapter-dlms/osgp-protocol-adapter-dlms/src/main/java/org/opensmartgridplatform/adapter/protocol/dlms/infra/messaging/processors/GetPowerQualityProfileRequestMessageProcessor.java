// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing Power Quality Profile request messages. */
@Component
public class GetPowerQualityProfileRequestMessageProcessor extends DeviceRequestMessageProcessor {

  @Autowired private MonitoringService monitoringService;

  public GetPowerQualityProfileRequestMessageProcessor() {
    super(MessageType.GET_PROFILE_GENERIC_DATA);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.assertRequestObjectType(GetPowerQualityProfileRequestDataDto.class, requestObject);

    final GetPowerQualityProfileRequestDataDto getPowerQualityProfileRequestDataDto =
        (GetPowerQualityProfileRequestDataDto) requestObject;

    return this.monitoringService.requestPowerQualityProfile(
        conn, device, getPowerQualityProfileRequestDataDto, messageMetadata);
  }
}
