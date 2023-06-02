//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.SecurityUtils;
import org.openmuc.jdlms.SecurityUtils.KeyId;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeysRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class AbstractReplaceKeyCommandExecutor<T>
    extends AbstractCommandExecutor<T, ActionResponseDto> {

  @Autowired private SecretManagementService secretManagementService;

  @Autowired
  @Qualifier("decrypterForGxfSmartMetering")
  private RsaEncrypter decrypterForGxfSmartMetering;

  /**
   * Constructor for CommandExecutors that need to be executed in the context of bundle actions.
   *
   * @param clazz the class of the ActionRequestDto subtype for which this CommandExecutor needs to
   *     be called.
   */
  public AbstractReplaceKeyCommandExecutor(final Class<? extends ActionRequestDto> clazz) {
    super(clazz);
  }

  protected ActionResponseDto replaceKeys(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetKeysRequestDto setKeysRequestDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    log.info("Keys set on device :{}", device.getDeviceIdentification());

    SetKeysRequestDto setDecryptedKeysRequestDto = setKeysRequestDto;
    if (!setKeysRequestDto.isGeneratedKeys()) {
      setDecryptedKeysRequestDto = this.decryptRsaKeys(setKeysRequestDto);
    }
    // if keys are generated, then they are unencrypted by the GenerateAndReplaceKeyCommandExecutor

    final DlmsDevice devicePostSave =
        this.storeAndSendToDevice(
            conn,
            device,
            wrap(
                setDecryptedKeysRequestDto.getAuthenticationKey(),
                KeyId.AUTHENTICATION_KEY,
                SecurityKeyType.E_METER_AUTHENTICATION,
                setDecryptedKeysRequestDto.isGeneratedKeys()),
            messageMetadata);

    this.storeAndSendToDevice(
        conn,
        devicePostSave,
        wrap(
            setDecryptedKeysRequestDto.getEncryptionKey(),
            KeyId.GLOBAL_UNICAST_ENCRYPTION_KEY,
            SecurityKeyType.E_METER_ENCRYPTION,
            setDecryptedKeysRequestDto.isGeneratedKeys()),
        messageMetadata);

    return new ActionResponseDto(
        String.format(
            "Replace keys for device %s was successful", device.getDeviceIdentification()));
  }

  private static ReplaceKeyInput wrap(
      final byte[] bytes,
      final KeyId keyId,
      final SecurityKeyType securityKeyType,
      final boolean isGenerated) {
    return new ReplaceKeyInput(bytes, keyId, securityKeyType, isGenerated);
  }

  private SetKeysRequestDto decryptRsaKeys(final SetKeysRequestDto setKeysRequestDto)
      throws FunctionalException {
    try {
      final byte[] authenticationKey =
          this.decrypterForGxfSmartMetering.decrypt(setKeysRequestDto.getAuthenticationKey());
      final byte[] encryptionKey =
          this.decrypterForGxfSmartMetering.decrypt(setKeysRequestDto.getEncryptionKey());

      return new SetKeysRequestDto(authenticationKey, encryptionKey);
    } catch (final EncrypterException e) {
      throw new FunctionalException(
          FunctionalExceptionType.DECRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS, e);
    }
  }

  private DlmsDevice storeAndSendToDevice(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ReplaceKeyInput keyWrapper,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    if (!keyWrapper.isGenerated()) {
      this.secretManagementService.storeNewKey(
          messageMetadata,
          device.getDeviceIdentification(),
          keyWrapper.getSecurityKeyType(),
          keyWrapper.getBytes());
    }

    this.sendToDevice(conn, device.getDeviceIdentification(), keyWrapper, messageMetadata);
    this.secretManagementService.activateNewKey(
        messageMetadata, device.getDeviceIdentification(), keyWrapper.getSecurityKeyType());
    return device;
  }

  /**
   * Send the key to the device.
   *
   * @param conn jDLMS connection.
   * @param deviceIdentification Device identification
   * @param keyWrapper Key data
   * @param messageMetadata
   */
  private void sendToDevice(
      final DlmsConnectionManager conn,
      final String deviceIdentification,
      final ReplaceKeyInput keyWrapper,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    try {
      final byte[] decryptedKey = keyWrapper.getBytes();
      final byte[] decryptedMasterKey =
          this.secretManagementService.getKey(
              messageMetadata, deviceIdentification, SecurityKeyType.E_METER_MASTER);

      final MethodParameter methodParameterAuth =
          SecurityUtils.keyChangeMethodParamFor(
              decryptedMasterKey, decryptedKey, keyWrapper.getKeyId());

      conn.getDlmsMessageListener()
          .setDescription(
              "ReplaceKey for "
                  + keyWrapper.getSecurityKeyType()
                  + " "
                  + keyWrapper.getKeyId()
                  + ", call method: "
                  + JdlmsObjectToStringUtil.describeMethod(methodParameterAuth));

      final MethodResultCode methodResultCode =
          conn.getConnection().action(methodParameterAuth).getResultCode();

      if (!MethodResultCode.SUCCESS.equals(methodResultCode)) {
        throw new ProtocolAdapterException(
            "AccessResultCode for replace keys was not SUCCESS: " + methodResultCode);
      }

      if (keyWrapper.getSecurityKeyType() == SecurityKeyType.E_METER_AUTHENTICATION) {
        conn.getConnection().changeClientGlobalAuthenticationKey(decryptedKey);
      } else if (keyWrapper.getSecurityKeyType() == SecurityKeyType.E_METER_ENCRYPTION) {
        conn.getConnection().changeClientGlobalEncryptionKey(decryptedKey);
      }
    } catch (final IOException e) {
      throw new ConnectionException(e);
    } catch (final EncrypterException e) {
      throw new ProtocolAdapterException(
          "Unexpected exception during decryption of security keys, reason = " + e.getMessage(), e);
    }
  }
}
