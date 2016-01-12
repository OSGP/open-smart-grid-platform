/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnitQuery;
import org.osgp.adapter.protocol.dlms.application.mapping.InstallationMapper;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.SmartMeteringDevice;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javax.naming.OperationNotSupportedException;
import org.openmuc.jdlms.LnClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.GetScalerUnitCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

@Service(value = "dlmsInstallationService")
public class InstallationService extends DlmsApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstallationService.class);

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private InstallationMapper installationMapper;

    @Autowired
    private GetScalerUnitCommandExecutor getScalerUnitCommandExecutor;

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DlmsConnectionFactory dlmsConnectionFactory;

    // === ADD METER ===

    public void addMeter(final DlmsDeviceMessageMetadata messageMetadata, final SmartMeteringDevice smartMeteringDevice,
            final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "addMeter");

        try {
            final DlmsDevice dlmsDevice = this.installationMapper.map(smartMeteringDevice, DlmsDevice.class);

            this.dlmsDeviceRepository.save(dlmsDevice);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during addMeter", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

    /**
     * Function to set or update scaler and unit for a E-meter, so that these
     * values can be used to convert values to standardized units required by
     * the platform.
     * 
     * @param messageMetadata
     *            the device we want to query
     * @throws FunctionalException
     * @throws IOException
     * @throws OperationNotSupportedException
     * @throws TimeoutException
     * @throws ProtocolAdapterException
     */
    public void getAndStoreScalerUnitForEmeter(final DlmsDeviceMessageMetadata messageMetadata)
            throws FunctionalException, IOException, OperationNotSupportedException, TimeoutException,
            ProtocolAdapterException {
        this.logStart(LOGGER, messageMetadata, "getAndStoreScalerUnitForEmeter");

        LnClientConnection conn = null;
        try {

            final DlmsDevice device = this.domainHelperService
                    .findDlmsDevice(messageMetadata.getDeviceIdentification());

            conn = this.dlmsConnectionFactory.getConnection(device);

            final ScalerUnit response = getScalerUnitCommandExecutor.execute(conn, new ScalerUnitQuery());

            device.setScaler(response.getScaler());
            device.setDlmsUnit(response.getDlmsUnit());

            dlmsDeviceRepository.save(device);

            /*
             * TODO call this function for example from addMeter or on demand or
             * both in order to set or update scaler and unit mbus device
             * administration not yet present in protocol layer
             */

        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
