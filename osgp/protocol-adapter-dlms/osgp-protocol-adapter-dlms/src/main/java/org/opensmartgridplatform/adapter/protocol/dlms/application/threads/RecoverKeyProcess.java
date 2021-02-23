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
import java.util.Arrays;

import org.openmuc.jdlms.DlmsConnection;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsDeviceAssociation;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.Hls5Connector;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.RecoverKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecoverKeyProcess implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecoverKeyProcess.class);

    private final DomainHelperService domainHelperService;

    private final int responseTimeout;

    private final int logicalDeviceAddress;

    private final int clientId;

    private String deviceIdentification;

    private String ipAddress;

    private final Hls5Connector hls5Connector;

    private final SecretManagementService secretManagementService;

    public RecoverKeyProcess(final DomainHelperService domainHelperService, final int responseTimeout,
            final int logicalDeviceAddress, final DlmsDeviceAssociation deviceAssociation,
            final Hls5Connector hls5Connector, SecretManagementService secretManagementService) {
        this.domainHelperService = domainHelperService;
        this.responseTimeout = responseTimeout;
        this.logicalDeviceAddress = logicalDeviceAddress;
        this.clientId = deviceAssociation.getClientId();
        this.hls5Connector = hls5Connector;
        this.secretManagementService = secretManagementService;
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

        DlmsDevice device = this.findDevice();
        if (!this.secretManagementService.hasNewSecretOfType(this.deviceIdentification, E_METER_AUTHENTICATION)) {
            LOGGER.error(
                    "Could not recover keys: device has no new authorisation key registered in secret-mgmt module");
            return;
        }
        if (!this.canConnectUsingNewKeys(device)) {
            LOGGER.error("Could not recover keys: could not connect to device using new keys");
            //shouldn't we try to connect using 'old' keys? or send key change to device again?
            return;
        }

        try {
            this.secretManagementService.activateNewKeys(this.deviceIdentification,
                    Arrays.asList(E_METER_ENCRYPTION, E_METER_AUTHENTICATION));
        } catch (Exception e) {
            throw new RecoverKeyException(e);
        }
    }

    private DlmsDevice findDevice() {
        try {
            return this.domainHelperService.findDlmsDevice(this.deviceIdentification, this.ipAddress);
        } catch (final Exception e) {
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

    private boolean canConnectUsingNewKeys(DlmsDevice device) {
        DlmsConnection connection = null;
        try {
            connection = this.hls5Connector.connectUnchecked(device, null, this.secretManagementService::getNewKey);
            return connection != null;
        } catch (Exception exc) {
            LOGGER.error("Connection exception: {}", exc.getMessage(), exc);
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
}
