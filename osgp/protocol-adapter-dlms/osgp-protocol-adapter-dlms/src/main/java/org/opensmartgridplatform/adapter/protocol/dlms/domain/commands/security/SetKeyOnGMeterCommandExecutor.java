// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_ENCRYPTION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_MASTER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_OPTICAL_PORT_KEY;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.CosemObjectAccessor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.method.MBusClientMethod;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeyOnGMeterRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.ws.schema.core.secret.management.SecretType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class SetKeyOnGMeterCommandExecutor
    extends AbstractCommandExecutor<SetKeyOnGMeterRequestDto, MethodResultCode> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetKeyOnGMeterCommandExecutor.class);

  private static final List<SecurityKeyType> validKeyTypes =
      Arrays.asList(
          G_METER_ENCRYPTION, G_METER_OPTICAL_PORT_KEY, G_METER_FIRMWARE_UPDATE_AUTHENTICATION);

  private static final int KEY_SIZE = 16;

  private final SetKeyOnGMeterKeyEncryptionAndMacGeneration keyEncryptionAndMacGeneration =
      new SetKeyOnGMeterKeyEncryptionAndMacGeneration();

  private final SecretManagementService secretManagementService;
  private final DlmsDeviceRepository dlmsDeviceRepository;
  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  public SetKeyOnGMeterCommandExecutor(
      final SecretManagementService secretManagementService,
      final DlmsDeviceRepository dlmsDeviceRepository,
      final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(SetKeyOnGMeterRequestDto.class);
    this.secretManagementService = secretManagementService;
    this.dlmsDeviceRepository = dlmsDeviceRepository;
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  @Override
  public ActionResponseDto asBundleResponse(final MethodResultCode executionResult)
      throws ProtocolAdapterException {
    this.checkMethodResultCode(executionResult);
    return new ActionResponseDto("M-Bus key exchange on Gas meter was successful");
  }

  @Override
  public MethodResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetKeyOnGMeterRequestDto setEncryptionKeyRequest,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    LOGGER.debug("SetKeyOnGMeterCommandExecutor.execute called");

    final CosemObjectAccessor cosemObjectAccessor =
        new CosemObjectAccessor(
            conn,
            this.objectConfigServiceHelper,
            DlmsObjectType.MBUS_CLIENT_SETUP,
            Protocol.forDevice(device),
            (short) setEncryptionKeyRequest.getChannel());

    final SecurityKeyType keyType =
        SecurityKeyType.fromSecretType(
            SecretType.fromValue(setEncryptionKeyRequest.getSecretType().name()));

    final String mbusDeviceIdentification = setEncryptionKeyRequest.getMbusDeviceIdentification();
    final DlmsDevice mBusDevice = this.getAndValidateDevice(mbusDeviceIdentification);

    final byte[] newKey =
        this.generateNewKey(
            keyType,
            messageMetadata,
            mbusDeviceIdentification,
            setEncryptionKeyRequest.getCloseOpticalPort());

    final byte[] encryptedKey =
        this.encryptKey(
            messageMetadata, mbusDeviceIdentification, device, mBusDevice, keyType, newKey);

    try {
      if (keyType == G_METER_OPTICAL_PORT_KEY
          || keyType == G_METER_FIRMWARE_UPDATE_AUTHENTICATION) {
        this.sendKeyUsingDataSend(cosemObjectAccessor, encryptedKey);
      } else if (keyType == G_METER_ENCRYPTION) {
        this.sendEncryptionKeyUsingTransferKeyAndSetEncryptionKey(
            cosemObjectAccessor, newKey, encryptedKey);
      }

      this.secretManagementService.activateNewKey(
          messageMetadata, mbusDeviceIdentification, keyType);

    } catch (final EncrypterException e) {
      throw new ProtocolAdapterException(
          "Unexpected exception during encryption of security keys, reason = " + e.getMessage(), e);
    }

    return MethodResultCode.SUCCESS;
  }

  private void sendKeyUsingDataSend(
      final CosemObjectAccessor cosemObjectAccessor, final byte[] encryptedKey)
      throws ProtocolAdapterException {
    final MethodParameter methodDataSend =
        this.getDataSendMethodParameter(cosemObjectAccessor, encryptedKey);
    final MethodResultCode methodResultCode =
        cosemObjectAccessor.callMethod(
            this.getClass().getSimpleName(),
            MBusClientMethod.DATA_SEND,
            methodDataSend.getParameter());
    this.checkMethodResultCode(methodResultCode, MBusClientMethod.DATA_SEND, cosemObjectAccessor);
  }

  private MethodParameter getDataSendMethodParameter(
      final CosemObjectAccessor cosemObjectAccessor, final byte[] encryptedKey) {
    // The parameter for the data_send method is an array with 1 element, consisting of:
    // - data_information_block: value 0x0D meaning variable length data
    // - value_information_block: value 0xFD19 meaning key exchange
    // - data: the encrypted key
    final DataObject dataInformationBlock = DataObject.newOctetStringData(new byte[] {(byte) 0x0D});
    final DataObject valueInformationBlock =
        DataObject.newOctetStringData(new byte[] {(byte) 0xFD, (byte) 0x19});
    final DataObject data = DataObject.newOctetStringData(encryptedKey);
    final DataObject dataDefinitionElement =
        DataObject.newStructureData(dataInformationBlock, valueInformationBlock, data);
    final DataObject array =
        DataObject.newArrayData(Collections.singletonList(dataDefinitionElement));
    final MBusClientMethod method = MBusClientMethod.DATA_SEND;
    return cosemObjectAccessor.createMethodParameter(method, array);
  }

  private void sendEncryptionKeyUsingTransferKeyAndSetEncryptionKey(
      final CosemObjectAccessor cosemObjectAccessor, final byte[] newKey, final byte[] encryptedKey)
      throws ProtocolAdapterException {

    // Transfer key to g-meter
    MethodResultCode methodResultCode =
        this.callKeyMethod(cosemObjectAccessor, MBusClientMethod.TRANSFER_KEY, encryptedKey);
    this.checkMethodResultCode(
        methodResultCode, MBusClientMethod.TRANSFER_KEY, cosemObjectAccessor);

    // Set encryption key in e-meter
    methodResultCode =
        this.callKeyMethod(cosemObjectAccessor, MBusClientMethod.SET_ENCRYPTION_KEY, newKey);
    this.checkMethodResultCode(
        methodResultCode, MBusClientMethod.SET_ENCRYPTION_KEY, cosemObjectAccessor);
  }

  private MethodResultCode callKeyMethod(
      final CosemObjectAccessor cosemObjectAccessor,
      final MBusClientMethod mBusClientMethod,
      final byte[] encryptedKey)
      throws ProtocolAdapterException {
    final MethodParameter methodTransferKey =
        this.getMethodParameter(cosemObjectAccessor, mBusClientMethod, encryptedKey);
    return cosemObjectAccessor.callMethod(
        this.getClass().getSimpleName(), mBusClientMethod, methodTransferKey.getParameter());
  }

  private MethodParameter getMethodParameter(
      final CosemObjectAccessor cosemObjectAccessor,
      final MBusClientMethod method,
      final byte[] encryptedKey) {
    final DataObject methodParameter = DataObject.newOctetStringData(encryptedKey);
    return new MethodParameter(
        method.getInterfaceClass().id(),
        cosemObjectAccessor.getObisCode(),
        method.getMethodId(),
        methodParameter);
  }

  private void checkMethodResultCode(
      final MethodResultCode methodResultCode,
      final MBusClientMethod method,
      final CosemObjectAccessor cosemObjectAccessor)
      throws ProtocolAdapterException {
    if (!MethodResultCode.SUCCESS.equals(methodResultCode)) {
      String message = "Error while executing " + method.getMethodName() + ".";
      if (methodResultCode != null) {
        message += " Reason = " + methodResultCode;
      }
      throw new ProtocolAdapterException(message);
    } else {
      LOGGER.debug(
          "Successfully invoked '{}' method: class_id {} obis_code {}",
          method.getMethodName(),
          cosemObjectAccessor.getClassId(),
          cosemObjectAccessor.getObisCode());
    }
  }

  private DlmsDevice getAndValidateDevice(final String mbusDeviceIdentification)
      throws ProtocolAdapterException {
    final DlmsDevice mbusDevice =
        this.dlmsDeviceRepository.findByDeviceIdentification(mbusDeviceIdentification);
    if (mbusDevice == null) {
      throw new ProtocolAdapterException("Unknown M-Bus device: " + mbusDeviceIdentification);
    }

    return mbusDevice;
  }

  private byte[] generateNewKey(
      final SecurityKeyType keyType,
      final MessageMetadata messageMetadata,
      final String mbusDeviceIdentification,
      final boolean closeOpticalPort)
      throws ProtocolAdapterException {

    if (!validKeyTypes.contains(keyType)) {
      throw new ProtocolAdapterException(
          String.format("Invalid key type %s in request to set key on g-meter", keyType.name()));
    }

    if (keyType.equals(G_METER_OPTICAL_PORT_KEY) && closeOpticalPort) {
      // To close the optical port, a key of 16 zero bytes is used
      return new byte[KEY_SIZE];
    }

    return this.secretManagementService.generate128BitsKeyAndStoreAsNewKey(
        messageMetadata, mbusDeviceIdentification, keyType);
  }

  private byte[] encryptKey(
      final MessageMetadata messageMetadata,
      final String mbusDeviceIdentification,
      final DlmsDevice device,
      final DlmsDevice mbusDevice,
      final SecurityKeyType keyType,
      final byte[] newKey)
      throws ProtocolAdapterException {
    final byte[] mbusDefaultKey =
        this.secretManagementService.getKey(
            messageMetadata, mbusDeviceIdentification, G_METER_MASTER);

    final Protocol protocol = Protocol.forDevice(device);

    if (protocol.isSmr5()) {
      return this.keyEncryptionAndMacGeneration.encryptAndAddGcmAuthenticationTagSmr5(
          mbusDevice,
          GMeterKeyId.fromSecurityKeyType(keyType).getKeyId(),
          KEY_SIZE,
          null,
          mbusDefaultKey,
          newKey);
    } else if (protocol.equals(Protocol.DSMR_4_2_2) && keyType.equals(G_METER_ENCRYPTION)) {
      return this.keyEncryptionAndMacGeneration.encryptMbusUserKeyDsmr4(mbusDefaultKey, newKey);
    } else {
      throw new ProtocolAdapterException(
          String.format(
              "Unsupported combination of protocol %s %s and key type %s in request to set key on g-meter",
              protocol.getName(), protocol.getVersion(), keyType.name()));
    }
  }
}
