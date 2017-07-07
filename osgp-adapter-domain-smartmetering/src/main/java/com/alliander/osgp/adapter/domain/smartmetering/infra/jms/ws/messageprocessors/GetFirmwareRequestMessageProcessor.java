/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.services.ConfigurationService;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceRequestMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetFirmwareVersion;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;

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
        super(DeviceFunction.GET_FIRMWARE_VERSION);
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
