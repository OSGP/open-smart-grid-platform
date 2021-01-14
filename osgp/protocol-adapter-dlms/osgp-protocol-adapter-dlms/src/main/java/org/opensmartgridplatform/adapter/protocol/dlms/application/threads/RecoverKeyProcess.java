/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.threads;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_AUTHENTICATION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_ENCRYPTION;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.AuthenticationMechanism;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.SecuritySuite;
import org.openmuc.jdlms.SecuritySuite.EncryptionMechanism;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
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
    private SecretManagementService secretManagementService;

    public RecoverKeyProcess(final DomainHelperService domainHelperService, final int responseTimeout,
            final int logicalDeviceAddress, final DlmsDeviceAssociation deviceAssociation) {
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
            LOGGER.error("Could not find device", e);
            //why try to find device if you don't do anything with the result?!?
            //shouldn't we throw an exception here?
        }

        if (!this.secretManagementService.hasNewSecretOfType(this.deviceIdentification, E_METER_AUTHENTICATION)) {
            LOGGER.warn("Could not recover keys: device has no new authorisation key registered in secret-mgmt module");
            return;
        }

        if (this.canConnectUsingNewKeys()) {
            List<SecurityKeyType> keyTypesToActivate=Arrays.asList(E_METER_ENCRYPTION,E_METER_AUTHENTICATION);
            try {
                this.secretManagementService.activateNewKeys(this.deviceIdentification, keyTypesToActivate);
            } catch (Exception e) {
                throw new RecoverKeyException(e);
            }
        } else {
            LOGGER.warn("Could not recover keys: could not connect to device using new keys");
            //shouldn't we try to connect using 'old' keys? or send key change to device again?
        }
    }

    private void findDevice() throws OsgpException {
        try {
            this.device = this.domainHelperService.findDlmsDevice(this.deviceIdentification, this.ipAddress);
        } catch (final ProtocolAdapterException e) { // Thread can not recover from these exceptions.
            throw new RecoverKeyException(e);
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

    private boolean canConnectUsingNewKeys() {
        DlmsConnection connection = null;
        try {
            connection = this.createConnectionUsingNewKeys();
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
     *
     * @throws IOException
     *         When there are problems in connecting to or communicating
     *         with the device.
     */
    private DlmsConnection createConnectionUsingNewKeys() throws IOException, FunctionalException {
        Map<SecurityKeyType, byte[]> keys = this.secretManagementService
                .getNewKeys(this.deviceIdentification, Arrays.asList(E_METER_AUTHENTICATION, E_METER_ENCRYPTION));
        final byte[] authenticationKey = Hex.decode(keys.get(E_METER_AUTHENTICATION));
        final byte[] encryptionKey = Hex.decode(keys.get(E_METER_ENCRYPTION));

        final SecuritySuite securitySuite = SecuritySuite.builder().setAuthenticationKey(authenticationKey)
                                                         .setAuthenticationMechanism(AuthenticationMechanism.HLS5_GMAC)
                                                         .setGlobalUnicastEncryptionKey(encryptionKey)
                                                         .setEncryptionMechanism(EncryptionMechanism.AES_GCM_128)
                                                         .build();

        final TcpConnectionBuilder tcpConnectionBuilder = new TcpConnectionBuilder(
                InetAddress.getByName(this.device.getIpAddress())).setSecuritySuite(securitySuite)
                                                                  .setResponseTimeout(this.responseTimeout)
                                                                  .setLogicalDeviceId(this.logicalDeviceAddress)
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
