/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class RecoverKeyProcess implements Runnable {

  private final DomainHelperService domainHelperService;

  private final int responseTimeout;

  private final int logicalDeviceAddress;

  private final int clientId;

  @Setter private String deviceIdentification;

  private DlmsDevice device;

  @Setter private String ipAddress;

  @Setter private MessageMetadata messageMetadata;

  @Autowired private SecretManagementService secretManagementService;

  public RecoverKeyProcess(
      final DomainHelperService domainHelperService,
      final int responseTimeout,
      final int logicalDeviceAddress,
      final DlmsDeviceAssociation deviceAssociation) {
    this.domainHelperService = domainHelperService;
    this.responseTimeout = responseTimeout;
    this.logicalDeviceAddress = logicalDeviceAddress;
    this.clientId = deviceAssociation.getClientId();
  }

  @Override
  public void run() {
    this.checkState();

    log.info(
        "[{}] Attempting key recovery for device {}",
        this.messageMetadata.getCorrelationUid(),
        this.deviceIdentification);

    try {
      this.findDevice();
    } catch (final Exception e) {
      log.error("[{}] Could not find device", this.messageMetadata.getCorrelationUid(), e);
      // why try to find device if you don't do anything with the result?!?
      // shouldn't we throw an exception here?
    }

    if (!this.secretManagementService.hasNewSecretOfType(
        this.messageMetadata, this.deviceIdentification, E_METER_AUTHENTICATION)) {
      log.warn(
          "[{}] Could not recover keys: device has no new authorisation key registered in secret-mgmt module",
          this.messageMetadata.getCorrelationUid());
      return;
    }

    if (this.canConnectUsingNewKeys()) {
      final List<SecurityKeyType> keyTypesToActivate =
          Arrays.asList(E_METER_ENCRYPTION, E_METER_AUTHENTICATION);
      try {
        this.secretManagementService.activateNewKeys(
            this.messageMetadata, this.deviceIdentification, keyTypesToActivate);
      } catch (final Exception e) {
        throw new RecoverKeyException(e);
      }
    } else {
      log.warn(
          "[{}] Could not recover keys: could not connect to device using new keys",
          this.messageMetadata.getCorrelationUid());
      // shouldn't we try to connect using 'old' keys? or send key change to device again?
    }
  }

  private void findDevice() throws OsgpException {
    try {
      this.device =
          this.domainHelperService.findDlmsDevice(this.deviceIdentification, this.ipAddress);
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
      log.warn("Connection exception: {}", e.getMessage(), e);
      return false;
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (final IOException e) {
          log.warn("Connection exception: {}", e.getMessage(), e);
        }
      }
    }
  }

  /**
   * Create a connection with the device.
   *
   * @return The connection.
   * @throws IOException When there are problems in connecting to or communicating with the device.
   */
  private DlmsConnection createConnectionUsingNewKeys() throws IOException, FunctionalException {
    final Map<SecurityKeyType, byte[]> keys =
        this.secretManagementService.getNewKeys(
            this.messageMetadata,
            this.deviceIdentification,
            Arrays.asList(E_METER_AUTHENTICATION, E_METER_ENCRYPTION));
    final byte[] authenticationKey = Hex.decode(keys.get(E_METER_AUTHENTICATION));
    final byte[] encryptionKey = Hex.decode(keys.get(E_METER_ENCRYPTION));

    final SecuritySuite securitySuite =
        SecuritySuite.builder()
            .setAuthenticationKey(authenticationKey)
            .setAuthenticationMechanism(AuthenticationMechanism.HLS5_GMAC)
            .setGlobalUnicastEncryptionKey(encryptionKey)
            .setEncryptionMechanism(EncryptionMechanism.AES_GCM_128)
            .build();

    final TcpConnectionBuilder tcpConnectionBuilder =
        new TcpConnectionBuilder(InetAddress.getByName(this.device.getIpAddress()))
            .setSecuritySuite(securitySuite)
            .setResponseTimeout(this.responseTimeout)
            .setLogicalDeviceId(this.logicalDeviceAddress)
            .setClientId(this.clientId);

    final Integer challengeLength = this.device.getChallengeLength();

    try {
      if (challengeLength != null) {
        tcpConnectionBuilder.setChallengeLength(challengeLength);
      }
    } catch (final IllegalArgumentException e) {
      log.error("Exception occurred: Invalid key format");
      throw new FunctionalException(
          FunctionalExceptionType.INVALID_DLMS_KEY_FORMAT, ComponentType.PROTOCOL_DLMS, e);
    }

    return tcpConnectionBuilder.build();
  }
}
