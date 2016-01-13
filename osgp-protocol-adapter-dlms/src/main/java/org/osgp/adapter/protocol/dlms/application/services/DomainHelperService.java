/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Service(value = "dlmsDomainHelperService")
public class DomainHelperService {

    private static final ComponentType COMPONENT_TYPE = ComponentType.PROTOCOL_DLMS;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    /**
     * Use {@link #findDlmsDevice(DlmsDeviceMessageMetadata)} instead, as this
     * will also set the IP address.
     * <p>
     * If this method turns out to be called from a location where
     * {@link DlmsDeviceMessageMetadata} is not available, check if the IP
     * address needs to be provided in another way.
     */
    @Deprecated
    public DlmsDevice findDlmsDevice(final String deviceIdentification) throws FunctionalException {
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
        if (dlmsDevice == null) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE,
                    new ProtocolAdapterException("Unable to communicate with unknown device: " + deviceIdentification));
        }
        return dlmsDevice;
    }

    public DlmsDevice findDlmsDevice(final DlmsDeviceMessageMetadata messageMetadata) throws FunctionalException {
        final String deviceIdentification = messageMetadata.getDeviceIdentification();
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
        if (dlmsDevice == null) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE,
                    new ProtocolAdapterException("Unable to communicate with unknown device: " + deviceIdentification));
        }
        dlmsDevice.setIpAddress(messageMetadata.getIpAddress());
        return dlmsDevice;
    }
}
