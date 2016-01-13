/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import org.openmuc.jdlms.LnClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.SynchronizeTimeCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeRequest;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "dlmsAdhocService")
public class AdhocService extends DlmsApplicationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdhocService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DlmsConnectionFactory dlmsConnectionFactory;

    @Autowired
    private SynchronizeTimeCommandExecutor synchronizeTimeCommandExecutor;

    // === REQUEST Synchronize Time DATA ===

    public void synchronizeTime(final DlmsDeviceMessageMetadata messageMetadata,
            final SynchronizeTimeRequest synchronizeTimeRequest, final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "synchronizeTime");

        LnClientConnection conn = null;
        try {

            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);

            conn = this.dlmsConnectionFactory.getConnection(device);

            this.synchronizeTimeCommandExecutor.execute(conn, null);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during synchronizeTime", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
