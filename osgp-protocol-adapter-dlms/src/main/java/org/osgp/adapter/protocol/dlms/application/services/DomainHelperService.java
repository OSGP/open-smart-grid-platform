/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.KeyGenerator;

import org.apache.commons.codec.binary.Hex;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.jasper.infra.ws.JasperWirelessSmsClient;
import org.osgp.adapter.protocol.jasper.sessionproviders.SessionProvider;
import org.osgp.adapter.protocol.jasper.sessionproviders.SessionProviderService;
import org.osgp.adapter.protocol.jasper.sessionproviders.exceptions.SessionProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.infra.jms.MessageMetadata;

@Service(value = "dlmsDomainHelperService")
public class DomainHelperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainHelperService.class);

    private static final ComponentType COMPONENT_TYPE = ComponentType.PROTOCOL_DLMS;

    public static final int AES_GMC_128_KEY_SIZE = 128;

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
     * This method can be used to find an mBusDevice. For other devices, use
     * {@link #findDlmsDevice(MessageMetadata)} instead, as this will also set
     * the IP address.
     */
    public DlmsDevice findDlmsDevice(final String deviceIdentification) throws FunctionalException {
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
        if (dlmsDevice == null) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE,
                    new ProtocolAdapterException("Unable to communicate with unknown device: " + deviceIdentification));
        }
        return dlmsDevice;
    }

    public DlmsDevice findDlmsDevice(final MessageMetadata messageMetadata)
            throws ProtocolAdapterException, FunctionalException {
        return this.findDlmsDevice(messageMetadata.getDeviceIdentification(), messageMetadata.getIpAddress());
    }

    public DlmsDevice findDlmsDevice(final String deviceIdentification, final String ipAddress)
            throws ProtocolAdapterException, FunctionalException {
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
        if (dlmsDevice == null) {
            final String errorMessage = String.format("Unable to communicate with unknown device: %s",
                    deviceIdentification);
            LOGGER.error(errorMessage);

            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, ComponentType.PROTOCOL_DLMS);
        }

        if (dlmsDevice.isIpAddressIsStatic()) {
            dlmsDevice.setIpAddress(ipAddress);
        } else {
            dlmsDevice.setIpAddress(this.getDeviceIpAddressFromSessionProvider(dlmsDevice));
        }
        return dlmsDevice;
    }

    /**
     * Store new key
     *
     * CAUTION: only call when a successful connection with the device has been
     * made, and you are sure any existing new key data is NOT VALID.
     *
     * @param device
     *            Device
     * @param key
     *            Key data
     * @param securityKeyType
     *            Type of key
     * @return Saved device
     */
    public DlmsDevice storeNewKey(final DlmsDevice device, final byte[] key, final SecurityKeyType securityKeyType) {
        // If a new key exists, delete this key.
        final SecurityKey existingKey = device.getNewSecurityKey(securityKeyType);
        if (existingKey != null) {
            device.getSecurityKeys().remove(existingKey);
        }

        device.addSecurityKey(new SecurityKey(device, securityKeyType, Hex.encodeHexString(key), null, null));
        return this.dlmsDeviceRepository.save(device);
    }

    /**
     * Store new key state
     *
     * @param device
     *            Device
     * @param securityKeyTypel
     *            Type of key
     * @return Saved device
     */
    public DlmsDevice storeNewKeyState(final DlmsDevice device, final SecurityKeyType securityKeyType) {
        final Date now = new Date();
        if (device.getValidSecurityKey(securityKeyType) != null) {
            device.getValidSecurityKey(securityKeyType).setValidTo(now);
        }

        device.getNewSecurityKey(securityKeyType).setValidFrom(now);
        return this.dlmsDeviceRepository.save(device);
    }

    public final byte[] generateKey() {
        try {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(AES_GMC_128_KEY_SIZE);
            return keyGenerator.generateKey().getEncoded();
        } catch (final NoSuchAlgorithmException e) {
            throw new AssertionError("Expected AES algorithm to be available for key generation.", e);
        }
    }

    private String getDeviceIpAddressFromSessionProvider(final DlmsDevice dlmsDevice)
            throws ProtocolAdapterException, FunctionalException {

        final SessionProvider sessionProvider = this.sessionProviderService
                .getSessionProvider(dlmsDevice.getCommunicationProvider());
        String deviceIpAddress = null;
        try {
            deviceIpAddress = sessionProvider.getIpAddress(dlmsDevice.getIccId());
            if (deviceIpAddress != null) {
                return deviceIpAddress;
            }

            // If the result is null then the meter is not in session (not
            // awake).
            // So wake up the meter and start polling for the session
            this.jasperWirelessSmsClient.sendWakeUpSMS(dlmsDevice.getIccId());
            deviceIpAddress = this.pollForSession(sessionProvider, dlmsDevice);

        } catch (final SessionProviderException e) {
            LOGGER.error("IccId is probably not supported in this session provider", e);
            throw new FunctionalException(FunctionalExceptionType.INVALID_ICCID, ComponentType.PROTOCOL_DLMS, e);
        }
        if ((deviceIpAddress == null) || "".equals(deviceIpAddress)) {
            throw new ProtocolAdapterException("Session provider: " + dlmsDevice.getCommunicationProvider()
                    + " did not return an IP address for device: " + dlmsDevice.getDeviceIdentification()
                    + "and iccId: " + dlmsDevice.getIccId());

        }
        return deviceIpAddress;
    }

    private String pollForSession(final SessionProvider sessionProvider, final DlmsDevice dlmsDevice)
            throws ProtocolAdapterException, FunctionalException {

        String deviceIpAddress = null;
        try {
            for (int i = 0; i < this.jasperGetSessionRetries; i++) {
                Thread.sleep(this.jasperGetSessionSleepBetweenRetries);
                deviceIpAddress = sessionProvider.getIpAddress(dlmsDevice.getIccId());
                if (deviceIpAddress != null) {
                    return deviceIpAddress;
                }
            }
        } catch (final InterruptedException e) {
            throw new ProtocolAdapterException(
                    "Interrupted while sleeping before calling the sessionProvider.getIpAddress", e);
        } catch (final SessionProviderException e) {
            throw new ProtocolAdapterException("", e);
        }
        return deviceIpAddress;
    }
}
