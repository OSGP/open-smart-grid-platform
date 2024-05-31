// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_AUTHENTICATION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_ENCRYPTION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.LLS_PASSWORD;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import org.openmuc.jdlms.AuthenticationMechanism;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.SecuritySuite;
import org.openmuc.jdlms.SecuritySuite.EncryptionMechanism;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
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

public class Lls1Connector extends SecureDlmsConnector {

  private static final Logger LOGGER = LoggerFactory.getLogger(Lls1Connector.class);

  private static final int AES_GCM_128 = 128;

  private final SecretManagementService secretManagementService;

  public Lls1Connector(
      final int responseTimeout,
      final int logicalDeviceAddress,
      final DlmsDeviceAssociation deviceAssociation,
      final SecretManagementService secretManagementService,
      final ProtocolAdapterMetrics protocolAdapterMetrics) {
    super(responseTimeout, logicalDeviceAddress, deviceAssociation, protocolAdapterMetrics);
    this.secretManagementService = secretManagementService;
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
      return this.createConnection(
          messageMetadata, device, dlmsMessageListener, this.secretManagementService::getKeys);
    } catch (final UnknownHostException e) {
      LOGGER.warn("The IP address is not found: {}", device.getIpAddress(), e);
      // Unknown IP, unrecoverable.
      throw new TechnicalException(
          ComponentType.PROTOCOL_DLMS, "The IP address is not found: " + device.getIpAddress());
    } catch (final IOException e) {
      throw getExceptionWithExceptionType(device, e);
    } catch (final EncrypterException e) {
      LOGGER.error(
          "decryption of security keys failed for device: {}", device.getDeviceIdentification(), e);
      throw new TechnicalException(
          ComponentType.PROTOCOL_DLMS,
          "decryption of security keys failed for device: " + device.getDeviceIdentification());
    }
  }

  @Override
  protected void setSecurity(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final SecurityKeyProvider keyProvider,
      final TcpConnectionBuilder tcpConnectionBuilder)
      throws OsgpException {

    final Map<SecurityKeyType, byte[]> encryptedKeys =
        keyProvider.getKeys(
            messageMetadata,
            device.getDeviceIdentification(),
            Arrays.asList(LLS_PASSWORD, E_METER_ENCRYPTION, E_METER_AUTHENTICATION));
    final byte[] password = this.getKey(encryptedKeys, LLS_PASSWORD, device);
    final byte[] encryptionKey = this.getKey(encryptedKeys, E_METER_ENCRYPTION, device);
    final byte[] authenticationKey = this.getKey(encryptedKeys, E_METER_AUTHENTICATION, device);

    final SecuritySuite securitySuite =
        SecuritySuite.builder()
            .setAuthenticationMechanism(AuthenticationMechanism.LOW)
            .setPassword(password)
            .setGlobalUnicastEncryptionKey(encryptionKey)
            .setEncryptionMechanism(EncryptionMechanism.AES_GCM_128)
            .setAuthenticationKey(authenticationKey)
            .build();

    tcpConnectionBuilder.setSecuritySuite(securitySuite).setClientId(this.clientId);
  }

  final byte[] getKey(
      final Map<SecurityKeyType, byte[]> encryptedKeys,
      final SecurityKeyType keyType,
      final DlmsDevice device)
      throws FunctionalException {
    final byte[] key = encryptedKeys.get(keyType);

    if (key == null) {
      LOGGER.error(
          "There is no {} key available for device {}", keyType, device.getDeviceIdentification());
      throw new FunctionalException(
          FunctionalExceptionType.KEY_NOT_PRESENT, ComponentType.PROTOCOL_DLMS);
    }

    if (keyType != LLS_PASSWORD && key.length * 8 != AES_GCM_128) {
      LOGGER.error(
          "The {} key has an invalid length for device {}",
          keyType,
          device.getDeviceIdentification());
      throw new FunctionalException(
          FunctionalExceptionType.INVALID_DLMS_KEY_FORMAT, ComponentType.PROTOCOL_DLMS);
    }

    return key;
  }
}
