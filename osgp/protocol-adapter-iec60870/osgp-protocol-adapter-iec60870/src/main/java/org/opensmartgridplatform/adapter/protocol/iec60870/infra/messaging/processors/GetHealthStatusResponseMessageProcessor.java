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
import org.opensmartgridplatform.dto.da.GetHealthStatusResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for processing get health status response messages.
 */
public class GetHealthStatusResponseMessageProcessor extends BaseResponseEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetHealthStatusResponseMessageProcessor.class);

    private static final String HEALTH_STATUS_OK = "OK";

    public GetHealthStatusResponseMessageProcessor(final MessageMetadata messageMetadata,
            final ResponseMessageSender responseMessageSender) {
        super(messageMetadata, responseMessageSender);
    }

    @Override
    public void newASdu(final ASdu incomingAsdu) {
        LOGGER.info("Received the following ASDU for GetHealthStatus: {}", incomingAsdu);

        if (incomingAsdu.getTypeIdentification() == TypeId.C_IC_NA_1) {
            this.getHealthStatusResponse();
        } else {
            LOGGER.info(
                    "Ignore this ASDU for now, handling will be implemented by a later user story. The ASDU contains scaled values.");
        }
    }

    private void getHealthStatusResponse() {
        LOGGER.info("In de response-methode");

        final GetHealthStatusResponseDto response = new GetHealthStatusResponseDto(HEALTH_STATUS_OK);

        this.sendResponse(response);
    }

}
