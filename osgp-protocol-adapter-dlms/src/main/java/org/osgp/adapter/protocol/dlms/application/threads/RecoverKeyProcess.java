/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.threads;

import java.io.IOException;
import java.net.InetAddress;

import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.osgp.adapter.protocol.dlms.application.services.DomainHelperService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.exceptions.RecoverKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        this.initDevice();
        if (!this.device.hasNewSecurityKey()) {
            return;
        }

        if (this.canConnect()) {
            this.promoteInvalidKey();
        }
    }

    private void initDevice() {
        try {
            this.device = this.domainHelperService.findDlmsDevice(this.deviceIdentification, this.ipAddress);
        } catch (final ProtocolAdapterException e) {
            // Thread can not recover from these exceptions.
            throw new RecoverKeyException(e.getMessage(), e);
        }

        if (this.device == null) {
            throw new IllegalArgumentException("Device " + this.deviceIdentification + " not found.");
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
        ClientConnection connection = null;
        try {
            connection = this.createConnection();
            return true;
        } catch (final Exception e) {
            LOGGER.warn("Connection exception: {}", e.getMessage(), e);
            return false;
        } finally {
            if (connection != null) {
                connection.close();
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
     */
    private ClientConnection createConnection() throws IOException {
        final byte[] authenticationKey = Hex.decode(this.getSecurityKey(SecurityKeyType.E_METER_AUTHENTICATION)
                .getKey());
        final byte[] encryptionKey = Hex.decode(this.getSecurityKey(SecurityKeyType.E_METER_ENCRYPTION).getKey());

        final TcpConnectionBuilder tcpConnectionBuilder = new TcpConnectionBuilder(InetAddress.getByName(this.device
                .getIpAddress())).useGmacAuthentication(authenticationKey, encryptionKey)
                .enableEncryption(encryptionKey).responseTimeout(this.responseTimeout)
                .logicalDeviceAddress(this.logicalDeviceAddress).clientAccessPoint(this.clientAccessPoint);

        final Integer challengeLength = this.device.getChallengeLength();
        if (challengeLength != null) {
            tcpConnectionBuilder.challengeLength(challengeLength);
        }

        return tcpConnectionBuilder.buildLnConnection();
    }

    private SecurityKey getSecurityKey(final SecurityKeyType securityKeyType) {
        SecurityKey key = this.device.getNewSecurityKey(securityKeyType);
        if (key == null) {
            key = this.device.getValidSecurityKey(securityKeyType);
        }
        return key;
    }
}
