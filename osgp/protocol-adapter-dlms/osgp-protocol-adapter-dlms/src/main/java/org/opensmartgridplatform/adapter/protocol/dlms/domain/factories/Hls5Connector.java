/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_AUTHENTICATION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_ENCRYPTION;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.openmuc.jdlms.AuthenticationMechanism;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.SecuritySuite;
import org.openmuc.jdlms.SecuritySuite.EncryptionMechanism;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.threads.RecoverKeyProcessInitiator;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Hls5Connector extends SecureDlmsConnector {

  private static final Logger LOGGER = LoggerFactory.getLogger(Hls5Connector.class);

  private static final int AES_GMC_128 = 128;

  private final RecoverKeyProcessInitiator recoverKeyProcessInitiator;

  @Autowired private SecretManagementService secretManagementService;

  public Hls5Connector(
      final RecoverKeyProcessInitiator recoverKeyProcessInitiator,
      final int responseTimeout,
      final int logicalDeviceAddress,
      final DlmsDeviceAssociation deviceAssociation) {
    super(responseTimeout, logicalDeviceAddress, deviceAssociation);
    this.recoverKeyProcessInitiator = recoverKeyProcessInitiator;
  }

  @Override
  public DlmsConnection connect(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final DlmsMessageListener dlmsMessageListener)
      throws OsgpException {

    // Make sure neither device or device.getIpAddress() is null.
    this.checkDevice(device);
    this.checkIpAddress(device);

    try {
      return this.createConnection(messageMetadata, device, dlmsMessageListener);
    } catch (final UnknownHostException e) { // Unknown IP, unrecoverable.
      LOGGER.error("The IP address is not found: {}", device.getIpAddress(), e);
      throw new TechnicalException(
          ComponentType.PROTOCOL_DLMS, "The IP address is not found: " + device.getIpAddress());
    } catch (final IOException e) { // Queue key recovery process
      if (this.secretManagementService.hasNewSecretOfType(
          messageMetadata, device.getDeviceIdentification(), E_METER_ENCRYPTION)) {
        this.recoverKeyProcessInitiator.initiate(
            messageMetadata, device.getDeviceIdentification(), device.getIpAddress());
      }

      final String msg =
          String.format(
              "Error creating connection for device %s with Ip address:%s Port:%d UseHdlc:%b UseSn:%b "
                  + "Message:%s",
              device.getDeviceIdentification(),
              device.getIpAddress(),
              device.getPort(),
              device.isUseHdlc(),
              device.isUseSn(),
              e.getMessage());
      LOGGER.error(msg);
      throw new ConnectionException(msg, e);
    } catch (final EncrypterException e) {
      throw new FunctionalException(
          FunctionalExceptionType.INVALID_DLMS_KEY_FORMAT, ComponentType.PROTOCOL_DLMS, e);
    }
  }

  @Override
  protected void setSecurity(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final TcpConnectionBuilder tcpConnectionBuilder)
      throws OsgpException {

    final String deviceIdentification = device.getDeviceIdentification();
    final byte[] dlmsAuthenticationKey;
    final byte[] dlmsEncryptionKey;
    try {
      final Map<SecurityKeyType, byte[]> encryptedKeys =
          this.secretManagementService.getKeys(
              messageMetadata,
              deviceIdentification,
              Arrays.asList(E_METER_AUTHENTICATION, E_METER_ENCRYPTION));
      dlmsAuthenticationKey = encryptedKeys.get(E_METER_AUTHENTICATION);
      dlmsEncryptionKey = encryptedKeys.get(E_METER_ENCRYPTION);
    } catch (final EncrypterException e) {
      throw new FunctionalException(
          FunctionalExceptionType.INVALID_DLMS_KEY_ENCRYPTION, ComponentType.PROTOCOL_DLMS, e);
    }

    // Validate keys before JDLMS does and throw a FunctionalException if
    // necessary
    this.validateKeys(dlmsAuthenticationKey, dlmsEncryptionKey);

    this.configureIvData(tcpConnectionBuilder, device);

    final SecuritySuite securitySuite =
        SecuritySuite.builder()
            .setAuthenticationKey(dlmsAuthenticationKey)
            .setAuthenticationMechanism(AuthenticationMechanism.HLS5_GMAC)
            .setGlobalUnicastEncryptionKey(dlmsEncryptionKey)
            .setEncryptionMechanism(EncryptionMechanism.AES_GCM_128)
            .build();

    tcpConnectionBuilder.setSecuritySuite(securitySuite).setClientId(this.clientId);
  }

  private void configureIvData(
      final TcpConnectionBuilder tcpConnectionBuilder, final DlmsDevice device) {
    /*
     * HLS5 communication needs an IV (initialization vector) that is unique
     * per encrypted message.
     *
     * This is taken care of by setting up a fixed 8 byte part which is
     * unique per device (the system title) and an invocation counter that
     * is incremented on each communication, and should never be used with
     * the same value for the same encryption key on the device.
     *
     * By setting the system title and frame counter on the connection
     * builder the library is enabled to meet the IV requirements of DLMS
     * HLS5 communication.
     */
    final String manufacturerId;
    if (StringUtils.isEmpty(device.getManufacturerId())) {
      LOGGER.warn(
          "Device {} does not have its manufacturer ID stored in the database. "
              + "Using a default value which makes the system title (part of the IV in HLS 5) less "
              + "unique.",
          device.getDeviceIdentification());
      manufacturerId = "   ";
    } else {
      manufacturerId = device.getManufacturerId();
    }
    tcpConnectionBuilder.setSystemTitle(manufacturerId, device.getDeviceId());

    final long frameCounter = device.getInvocationCounter();

    tcpConnectionBuilder.setFrameCounter(frameCounter);
    LOGGER.debug(
        "Framecounter for device {} set to {}", device.getDeviceIdentification(), frameCounter);
  }

  private void validateKeys(final byte[] encryptionKey, final byte[] authenticationKey)
      throws FunctionalException {
    if (this.checkEmptyKey(encryptionKey)) {
      this.throwFunctionalException(
          "The encryption key is empty", FunctionalExceptionType.KEY_NOT_PRESENT);
    }

    if (this.checkEmptyKey(authenticationKey)) {
      this.throwFunctionalException(
          "The authentication key is empty", FunctionalExceptionType.KEY_NOT_PRESENT);
    }

    if (this.checkLenghtKey(encryptionKey)) {
      this.throwFunctionalException(
          "The encryption key has an invalid length",
          FunctionalExceptionType.INVALID_DLMS_KEY_FORMAT);
    }

    if (this.checkLenghtKey(authenticationKey)) {
      this.throwFunctionalException(
          "The authentication key has an invalid length",
          FunctionalExceptionType.INVALID_DLMS_KEY_FORMAT);
    }
  }

  private boolean checkEmptyKey(final byte[] key) {
    return key == null;
  }

  private boolean checkLenghtKey(final byte[] key) {
    return key.length * 8 != AES_GMC_128;
  }

  private void throwFunctionalException(final String msg, final FunctionalExceptionType type)
      throws FunctionalException {
    LOGGER.error(msg);
    throw new FunctionalException(type, ComponentType.PROTOCOL_DLMS);
  }
}
