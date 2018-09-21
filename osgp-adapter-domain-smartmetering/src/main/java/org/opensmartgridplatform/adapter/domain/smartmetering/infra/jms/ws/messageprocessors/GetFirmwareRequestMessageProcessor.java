/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetFirmwareVersion;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class for processing common get firmware request messages
 */
@Component
public class GetFirmwareRequestMessageProcessor extends WebServiceRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFirmwareRequestMessageProcessor.class);

    @Autowired
    @Qualifier("domainSmartMeteringConfigurationService")
    private ConfigurationService configurationService;

    public GetFirmwareRequestMessageProcessor() {
        super(MessageType.GET_FIRMWARE_VERSION);
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata, final Object dataObject)
            throws FunctionalException {

        /*
         * Ignore the dataObject, which should be a GetFirmwareVersion, since it
         * contains nothing useful for the ConfigurationService to handle the
         * request.
         */
        if (dataObject != null && !(dataObject instanceof GetFirmwareVersion)) {
            LOGGER.warn("dataObject was ignored because GetFirmwareVersion does not hold interesting data"
                    + " for further processing, however dataObject was a " + dataObject.getClass().getName());
        }

        this.configurationService.requestFirmwareVersion(deviceMessageMetadata);
    }

}
