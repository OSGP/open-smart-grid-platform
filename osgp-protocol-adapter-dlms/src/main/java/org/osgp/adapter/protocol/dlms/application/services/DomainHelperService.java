/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders.SessionProvider;
import org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders.SessionProviderService;
import org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders.exceptions.SessionProviderException;
import org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders.exceptions.SessionProviderUnsupportedException;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.osgp.adapter.protocol.dlms.infra.ws.JasperWirelessSmsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Service(value = "dlmsDomainHelperService")
public class DomainHelperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainHelperService.class);

    private static final ComponentType COMPONENT_TYPE = ComponentType.PROTOCOL_DLMS;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private SessionProviderService sessionProviderService;

    @Autowired
    private JasperWirelessSmsClient jasperWirelessSmsClient;

    @Autowired
    private int jasperGetSessionRetries;

    @Autowired
    private int jasperGetSessionSleepBetweenRetries;

    /**
     * Use {@link #findDlmsDevice(DlmsDeviceMessageMetadata)} instead, as this
     * will also set the IP address.
     * <p>
     * If this method turns out to be called from a location where
     * {@link DlmsDeviceMessageMetadata} is not available, check if the IP
     * address needs to be provided in another way.
     *
     * @deprecated
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

    public DlmsDevice findDlmsDevice(final DlmsDeviceMessageMetadata messageMetadata) throws FunctionalException,
    ProtocolAdapterException {
        final String deviceIdentification = messageMetadata.getDeviceIdentification();
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
        if (dlmsDevice == null) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE,
                    new ProtocolAdapterException("Unable to communicate with unknown device: " + deviceIdentification));
        }

        dlmsDevice.setIpAddress(this.getDeviceIpAddress(dlmsDevice, messageMetadata.getIpAddress()));

        return dlmsDevice;
    }

    private String getDeviceIpAddress(final DlmsDevice dlmsDevice, final String messageMetaDataIpAddress)
            throws ProtocolAdapterException {

        if (dlmsDevice.isIpAddressIsStatic()) {
            return messageMetaDataIpAddress;
        } else {
            return this.getDeviceIpAddressFromSessionProvider(dlmsDevice);
        }
    }

    private String getDeviceIpAddressFromSessionProvider(final DlmsDevice dlmsDevice) throws ProtocolAdapterException {

        final SessionProvider sessionProvider = this.sessionProviderService.getSessionProvider(dlmsDevice
                .getCommunicationProvider());
        String deviceIpAddress = null;
        try {
            deviceIpAddress = sessionProvider.getIpAddress(dlmsDevice.getIccId());

            // If the result is null then the meter is not in session (not
            // awake).
            // So wake up the meter and start polling for the session
            if (deviceIpAddress == null) {
                this.jasperWirelessSmsClient.sendWakeUpSMS(dlmsDevice.getIccId());
                for (int i = 0; i < this.jasperGetSessionRetries; i++) {
                    Thread.sleep(this.jasperGetSessionSleepBetweenRetries);
                    deviceIpAddress = sessionProvider.getIpAddress(dlmsDevice.getIccId());

                    if (deviceIpAddress != null) {
                        return deviceIpAddress;
                    }
                }
            }
        } catch (final InterruptedException e) {
            throw new ProtocolAdapterException(
                    "Interrupted while sleeping before calling the sessionProvider.getIpAddress", e);
        } catch (SessionProviderUnsupportedException | SessionProviderException e) {
            throw new ProtocolAdapterException("", e);
        }
        if (deviceIpAddress == null || "".equals(deviceIpAddress)) {
            throw new ProtocolAdapterException("Session provider: " + dlmsDevice.getCommunicationProvider()
                    + " did not return an IP address for device: " + dlmsDevice.getDeviceIdentification()
                    + " and iccId: " + dlmsDevice.getIccId());
        } else {
            return deviceIpAddress;
        }
    }
}
