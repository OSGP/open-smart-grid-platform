/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.distributionautomation.asduhandlers;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.distributionautomation.DistributionAutomationClientAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.LogItemFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ASDU Handler for ASDUs with type identification C_IC_NA_1:.
 * <ul>
 * <li>Interrogation Command</li>
 * </ul>
 */
@Component
public class InterrogationAsduHandler extends DistributionAutomationClientAsduHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterrogationAsduHandler.class);

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private ResponseMetadataFactory responseMetadataFactory;

    @Autowired
    private LogItemFactory logItemFactory;

    public InterrogationAsduHandler() {
        super(ASduType.C_IC_NA_1);
    }

    @Override
    public void handleAsdu(final ASdu asdu, final ResponseMetadata responseMetadata) {
        LOGGER.info("Received interrogation command {}.", asdu);
        final ResponseMetadata newResponseMetadata = this.responseMetadataFactory
                .createWithNewCorrelationUid(responseMetadata);

        // Only log item for now
        final LogItem logItem = this.logItemFactory.create(asdu, newResponseMetadata, true);
        this.loggingService.log(logItem);
    }
}
