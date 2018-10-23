/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.threads;

import java.io.IOException;
import java.net.InetAddress;

import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.AuthenticationMechanism;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.SecuritySuite;
import org.openmuc.jdlms.SecuritySuite.EncryptionMechanism;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.RecoverKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public class RecoverKeyProcess implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecoverKeyProcess.class);

    private final DomainHelperService domainHelperService;

    private final DlmsDeviceRepository dlmsDeviceRepository;

    private final int responseTimeout;

    private final int logicalDeviceAddress;

    private final int clientAccessPoint;

    private String deviceIdentification;

    private DlmsDevice device;

    private String ipAddress;

    public RecoverKeyProcess(final DomainHelperService domainHelperService,
            final DlmsDeviceRepository dlmsDeviceRepository, final int responseTimeout, final int logicalDeviceAddress,
            final int clientAccessPoint) {
        this.domainHelperService = domainHelperService;
        this.dlmsDeviceRepository = dlmsDeviceRepository;
        this.responseTimeout = responseTimeout;
        this.logicalDeviceAddress = logicalDeviceAddress;
        this.clientAccessPoint = clientAccessPoint;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public void run() {
        this.checkState();

        LOGGER.info("Attempting key recovery for device {}", this.deviceIdentification);

        try {
            this.initDevice();
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception: {}", e);
        }
        if (!this.device.hasNewSecurityKey()) {
            return;
        }

        if (this.canConnect()) {
            this.promoteInvalidKey();
        }
    }

    private void initDevice() throws OsgpException {
        try {
            this.device = this.domainHelperService.findDlmsDevice(this.deviceIdentification, this.ipAddress);
        } catch (final ProtocolAdapterException e) {
            // Thread can not recover from these exceptions.
            throw new RecoverKeyException(e.getMessage(), e);
        }
    }

    private void checkState() {
        if (this.deviceIdentification == null) {
            throw new IllegalStateException("DeviceIdentification not set.");
        }
        if (this.ipAddress == null) {
            throw new IllegalStateException("IP address not set.");
        }
    }

    private boolean canConnect() {
        DlmsConnection connection = null;
        try {
            connection = this.createConnection();
            return true;
        } catch (final Exception e) {
            LOGGER.warn("Connection exception: {}", e.getMessage(), e);
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (final IOException e) {
                    LOGGER.warn("Connection exception: {}", e.getMessage(), e);
                }
            }
        }
    }

    private void promoteInvalidKey() {
        this.device.promoteInvalidKey();
        this.dlmsDeviceRepository.save(this.device);
    }

    /**
     * Create a connection with the device.
     *
     * @return The connection.
     * @throws IOException
     *             When there are problems in connecting to or communicating
     *             with the device.
     * @throws FunctionalException
     */
    private DlmsConnection createConnection() throws IOException, FunctionalException {
        final byte[] authenticationKey = Hex
                .decode(this.getSecurityKey(SecurityKeyType.E_METER_AUTHENTICATION).getKey());
        final byte[] encryptionKey = Hex.decode(this.getSecurityKey(SecurityKeyType.E_METER_ENCRYPTION).getKey());

        final SecuritySuite securitySuite = SecuritySuite.builder().setAuthenticationKey(authenticationKey)
                .setAuthenticationMechanism(AuthenticationMechanism.HLS5_GMAC)
                .setGlobalUnicastEncryptionKey(encryptionKey).setEncryptionMechanism(EncryptionMechanism.AES_GCM_128)
                .build();

        final TcpConnectionBuilder tcpConnectionBuilder = new TcpConnectionBuilder(
                InetAddress.getByName(this.device.getIpAddress())).setSecuritySuite(securitySuite)
                        .setResponseTimeout(this.responseTimeout).setLogicalDeviceId(this.logicalDeviceAddress)
                        .setClientId(this.clientAccessPoint);

        final Integer challengeLength = this.device.getChallengeLength();

        try {
            if (challengeLength != null) {
                tcpConnectionBuilder.setChallengeLength(challengeLength);
            }
        } catch (final IllegalArgumentException e) {
            LOGGER.error("Exception occurred: Invalid key format");
            throw new FunctionalException(FunctionalExceptionType.INVALID_DLMS_KEY_FORMAT, ComponentType.PROTOCOL_DLMS,
                    e);
        }

        return tcpConnectionBuilder.build();
    }

    private SecurityKey getSecurityKey(final SecurityKeyType securityKeyType) {
        SecurityKey key = this.device.getNewSecurityKey(securityKeyType);
        if (key == null) {
            key = this.device.getValidSecurityKey(securityKeyType);
        }
        return key;
    }
}
