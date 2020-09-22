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
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecurityKeyService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsDeviceAssociation;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.RecoverKeyException;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RecoverKeyProcess implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecoverKeyProcess.class);

    private final DomainHelperService domainHelperService;

    private final int responseTimeout;

    private final int logicalDeviceAddress;

    private final int clientId;

    private String deviceIdentification;

    private DlmsDevice device;

    private String ipAddress;

    @Autowired
    @Qualifier("secretManagementService")
    private SecurityKeyService securityKeyService;


    public RecoverKeyProcess(final DomainHelperService domainHelperService,
            final int responseTimeout, final int logicalDeviceAddress,
            final DlmsDeviceAssociation deviceAssociation) {
        this.domainHelperService = domainHelperService;
        this.responseTimeout = responseTimeout;
        this.logicalDeviceAddress = logicalDeviceAddress;
        this.clientId = deviceAssociation.getClientId();
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
            this.findDevice();
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception", e);
        }

        if (securityKeyService.isActivated(this.deviceIdentification, SecurityKeyType.E_METER_AUTHENTICATION)) {
            return;
        }

       if (this.canConnect()) {
           try {
               this.securityKeyService.activateNewKey(this.deviceIdentification,
                       SecurityKeyType.E_METER_ENCRYPTION);
           }
           catch(ProtocolAdapterException e) {
               throw new RecoverKeyException(e.getMessage(), e);
           }
           try {
               this.securityKeyService.activateNewKey(this.deviceIdentification,
                       SecurityKeyType.E_METER_AUTHENTICATION);
           }
           catch(ProtocolAdapterException e) {
               throw new RecoverKeyException(e.getMessage(), e);
           }
       }
    }

    private void findDevice() throws OsgpException {
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

    /**
     * Create a connection with the device.
     *
     * @return The connection.
     * @throws IOException
     *             When there are problems in connecting to or communicating
     *             with the device.
     */
    private DlmsConnection createConnection() throws IOException, FunctionalException {

        byte[][] keys = this.securityKeyService.getKeys(this.deviceIdentification,
                new SecurityKeyType[]{ SecurityKeyType.E_METER_AUTHENTICATION, SecurityKeyType.E_METER_ENCRYPTION });

        final byte[] authenticationKey = Hex.decode(keys[0]);
        final byte[] encryptionKey = Hex.decode(keys[1]);

        final SecuritySuite securitySuite = SecuritySuite.builder().setAuthenticationKey(authenticationKey)
                .setAuthenticationMechanism(AuthenticationMechanism.HLS5_GMAC)
                .setGlobalUnicastEncryptionKey(encryptionKey).setEncryptionMechanism(EncryptionMechanism.AES_GCM_128)
                .build();

        final TcpConnectionBuilder tcpConnectionBuilder = new TcpConnectionBuilder(
                InetAddress.getByName(this.device.getIpAddress())).setSecuritySuite(securitySuite)
                        .setResponseTimeout(this.responseTimeout).setLogicalDeviceId(this.logicalDeviceAddress)
                        .setClientId(this.clientId);

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

}
