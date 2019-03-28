/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.BaseResponseEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.services.DeviceMessageLoggingService;
import org.opensmartgridplatform.dto.da.GetHealthStatusResponseDto;
import org.opensmartgridplatform.shared.domain.services.CorrelationProviderIdServiceV2;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for processing get health status response messages.
 *
 * Note: the class does not process an actual "get health status" response. It
 * contains a first example of processing incoming ASDUs.
 */
public class GetHealthStatusResponseEventListener extends BaseResponseEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetHealthStatusResponseEventListener.class);

    private static final String HEALTH_STATUS_OK = "OK";

    private String responseMessagesRepresentation;

    public GetHealthStatusResponseEventListener(final MessageMetadata messageMetadata,
            final ResponseMessageSender responseMessageSender,
            final DeviceMessageLoggingService deviceMessageLoggingService,
            final CorrelationProviderIdServiceV2 correlationIdProviderService) {
        super(messageMetadata, responseMessageSender, deviceMessageLoggingService, correlationIdProviderService);
    }

    @Override
    public void newASdu(final ASdu receivedAsdu) {
        LOGGER.info("Received the following ASDU for GetHealthStatus: {}", receivedAsdu);
        this.saveReceivedMessage(receivedAsdu);

        if (receivedAsdu.getTypeIdentification() == TypeId.C_IC_NA_1) {
            this.sendGetHealthStatusResponse();
        } else {
            LOGGER.info(
                    "Don't process this ASDU for now. Handling will be implemented by a later user story. The ASDU contains scaled values.");
        }
    }

    private void saveReceivedMessage(final ASdu receivedAsdu) {
        LOGGER.info("In saveReceivedMessage");

        if (receivedAsdu.getTypeIdentification().equals(TypeId.C_IC_NA_1)) {
            this.responseMessagesRepresentation = "getHealthStatusResponse:";
        }

        this.responseMessagesRepresentation += System.lineSeparator() + System.lineSeparator() + receivedAsdu;

        if (receivedAsdu.getTypeIdentification().equals(TypeId.M_ME_NB_1)) {
            // This is the last ASDU for the interrogation command, log
            // the message in the DB.
            this.getDeviceMessageLoggingService().logMessage(this.getMessageMetadata(), true, true,
                    this.responseMessagesRepresentation, 0);
        }
    }

    private void sendGetHealthStatusResponse() {
        LOGGER.info("Send getHealthStatusResponse");

        final GetHealthStatusResponseDto response = new GetHealthStatusResponseDto(HEALTH_STATUS_OK);

        this.sendResponse(response);
    }

}
