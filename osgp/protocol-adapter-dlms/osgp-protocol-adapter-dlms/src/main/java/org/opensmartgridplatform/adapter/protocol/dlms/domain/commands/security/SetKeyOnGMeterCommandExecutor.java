//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_ENCRYPTION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_MASTER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_OPTICAL_PORT_KEY;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.method.MBusClientMethod;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeyOnGMeterRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.ws.schema.core.secret.management.SecretType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class SetKeyOnGMeterCommandExecutor
    extends AbstractCommandExecutor<SetKeyOnGMeterRequestDto, MethodResultCode> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetKeyOnGMeterCommandExecutor.class);

  private static final int CLASS_ID = 72;
  private static final ObisCode OBIS_CODE_INTERVAL_MBUS_1 = new ObisCode("0.1.24.1.0.255");
  private static final ObisCode OBIS_CODE_INTERVAL_MBUS_2 = new ObisCode("0.2.24.1.0.255");
  private static final ObisCode OBIS_CODE_INTERVAL_MBUS_3 = new ObisCode("0.3.24.1.0.255");
  private static final ObisCode OBIS_CODE_INTERVAL_MBUS_4 = new ObisCode("0.4.24.1.0.255");

  private static final Map<Integer, ObisCode> OBIS_HASHMAP = new HashMap<>();

  private static final List<SecurityKeyType> validKeyTypes =
      Arrays.asList(
          G_METER_ENCRYPTION, G_METER_OPTICAL_PORT_KEY, G_METER_FIRMWARE_UPDATE_AUTHENTICATION);

  private static final int KEY_SIZE = 16;

  private final SetKeyOnGMeterKeyEncryptionAndMacGeneration keyEncryptionAndMacGeneration =
      new SetKeyOnGMeterKeyEncryptionAndMacGeneration();

  static {
    OBIS_HASHMAP.put(1, OBIS_CODE_INTERVAL_MBUS_1);
    OBIS_HASHMAP.put(2, OBIS_CODE_INTERVAL_MBUS_2);
    OBIS_HASHMAP.put(3, OBIS_CODE_INTERVAL_MBUS_3);
    OBIS_HASHMAP.put(4, OBIS_CODE_INTERVAL_MBUS_4);
  }

  @Autowired private SecretManagementService secretManagementService;

  @Autowired private DlmsDeviceRepository dlmsDeviceRepository;

  public SetKeyOnGMeterCommandExecutor() {
    super(SetKeyOnGMeterRequestDto.class);
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

    final int channel = setEncryptionKeyRequest.getChannel();

    try {
      if (keyType == G_METER_OPTICAL_PORT_KEY
          || keyType == G_METER_FIRMWARE_UPDATE_AUTHENTICATION) {
        this.sendKeyUsingDataSend(conn, channel, encryptedKey);
      } else if (keyType == G_METER_ENCRYPTION) {
        this.sendEncryptionKeyUsingTransferKeyAndSetEncryptionKey(
            conn, channel, newKey, encryptedKey);
      }

      this.secretManagementService.activateNewKey(
          messageMetadata, mbusDeviceIdentification, keyType);

    } catch (final IOException e) {
      throw new ConnectionException(e);
    } catch (final EncrypterException e) {
      throw new ProtocolAdapterException(
          "Unexpected exception during encryption of security keys, reason = " + e.getMessage(), e);
    }

    return MethodResultCode.SUCCESS;
  }

  private void sendKeyUsingDataSend(
      final DlmsConnectionManager conn, final int channel, final byte[] encryptedKey)
      throws ProtocolAdapterException, IOException {
    final MethodResult methodResultCode = this.dataSend(conn, channel, encryptedKey);
    this.checkMethodResultCode(methodResultCode, "M-Bus Setup data_send", channel);
  }

  private MethodResult dataSend(
      final DlmsConnectionManager conn, final int channel, final byte[] encryptedKey)
      throws IOException {
    final MethodParameter methodDataSend = this.getDataSendMethodParameter(channel, encryptedKey);
    conn.getDlmsMessageListener()
        .setDescription(this.describeMethod(channel, "data_send", methodDataSend));

    return conn.getConnection().action(methodDataSend);
  }

  private MethodParameter getDataSendMethodParameter(final int channel, final byte[] encryptedKey) {
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
    return new MethodParameter(
        method.getInterfaceClass().id(), OBIS_HASHMAP.get(channel), method.getMethodId(), array);
  }

  private void sendEncryptionKeyUsingTransferKeyAndSetEncryptionKey(
      final DlmsConnectionManager conn,
      final int channel,
      final byte[] newKey,
      final byte[] encryptedKey)
      throws ProtocolAdapterException, IOException {

    // Transfer key to g-meter
    MethodResult methodResultCode = this.transferKey(conn, channel, encryptedKey);
    this.checkMethodResultCode(methodResultCode, "M-Bus Setup transfer_key", channel);

    // Set encryption key in e-meter
    methodResultCode = this.setEncryptionKey(conn, channel, newKey);
    this.checkMethodResultCode(methodResultCode, "M-Bus Setup set_encryption_key", channel);
  }

  private MethodResult setEncryptionKey(
      final DlmsConnectionManager conn, final int channel, final byte[] encryptedKey)
      throws IOException {
    final MethodParameter methodSetEncryptionKey =
        this.getSetEncryptionKeyMethodParameter(OBIS_HASHMAP.get(channel), encryptedKey);
    conn.getDlmsMessageListener()
        .setDescription(this.describeMethod(channel, "set_encryption_key", methodSetEncryptionKey));
    return conn.getConnection().action(methodSetEncryptionKey);
  }

  private MethodResult transferKey(
      final DlmsConnectionManager conn, final int channel, final byte[] encryptedKey)
      throws IOException {
    final MethodParameter methodTransferKey =
        this.getTransferKeyMethodParameter(channel, encryptedKey);
    conn.getDlmsMessageListener()
        .setDescription(this.describeMethod(channel, "transfer_key", methodTransferKey));

    return conn.getConnection().action(methodTransferKey);
  }

  private MethodParameter getTransferKeyMethodParameter(
      final int channel, final byte[] encryptedKey) {
    final DataObject methodParameter = DataObject.newOctetStringData(encryptedKey);
    final MBusClientMethod method = MBusClientMethod.TRANSFER_KEY;
    return new MethodParameter(
        method.getInterfaceClass().id(),
        OBIS_HASHMAP.get(channel),
        method.getMethodId(),
        methodParameter);
  }

  private void checkMethodResultCode(
      final MethodResult methodResultCode, final String methodParameterName, final int channel)
      throws ProtocolAdapterException {
    if (methodResultCode == null
        || !MethodResultCode.SUCCESS.equals(methodResultCode.getResultCode())) {
      String message = "Error while executing " + methodParameterName + ".";
      if (methodResultCode != null) {
        message += " Reason = " + methodResultCode.getResultCode();
      }
      throw new ProtocolAdapterException(message);
    } else {
      LOGGER.debug(
          "Successfully invoked '{}' method: class_id {} obis_code {}",
          methodParameterName,
          CLASS_ID,
          OBIS_HASHMAP.get(channel));
    }
  }

  private MethodParameter getSetEncryptionKeyMethodParameter(
      final ObisCode obisCode, final byte[] encryptedKey) {
    final DataObject methodParameter = DataObject.newOctetStringData(encryptedKey);
    final MBusClientMethod method = MBusClientMethod.SET_ENCRYPTION_KEY;
    return new MethodParameter(
        method.getInterfaceClass().id(), obisCode, method.getMethodId(), methodParameter);
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

  private String describeMethod(
      final int channel, final String method, final MethodParameter parameter) {
    return "SetKeyOnGMeter for channel "
        + channel
        + ", call M-Bus Setup "
        + method
        + ": "
        + JdlmsObjectToStringUtil.describeMethod(parameter);
  }
}
