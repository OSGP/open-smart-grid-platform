// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_AUTHENTICATION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_ENCRYPTION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_MASTER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_ENCRYPTION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_MASTER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_OPTICAL_PORT_KEY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.commons.lang3.ArrayUtils;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.InstallationMapper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.CoupleMBusDeviceCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.CoupleMbusDeviceByChannelCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.DecoupleMBusDeviceCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service(value = "dlmsInstallationService")
public class InstallationService {

  @Autowired private DlmsDeviceRepository dlmsDeviceRepository;

  @Autowired private InstallationMapper installationMapper;

  @Autowired
  @Qualifier("decrypterForGxfSmartMetering")
  private RsaEncrypter decrypterForGxfSmartMetering;

  @Autowired private SecretManagementService secretManagementService;

  @Autowired private CoupleMBusDeviceCommandExecutor coupleMBusDeviceCommandExecutor;

  @Autowired private DecoupleMBusDeviceCommandExecutor decoupleMBusDeviceCommandExecutor;

  @Autowired
  private CoupleMbusDeviceByChannelCommandExecutor coupleMbusDeviceByChannelCommandExecutor;

  public void addMeter(
      final MessageMetadata messageMetadata, final SmartMeteringDeviceDto smartMeteringDevice)
      throws FunctionalException {
    this.checkDeviceIdentification(smartMeteringDevice);
    this.storeAndActivateKeys(messageMetadata, smartMeteringDevice);
    this.saveDevice(smartMeteringDevice);
  }

  private void checkDeviceIdentification(final SmartMeteringDeviceDto smartMeteringDevice)
      throws FunctionalException {
    if (smartMeteringDevice.getDeviceIdentification() == null) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.PROTOCOL_DLMS,
          new IllegalArgumentException("Provided device does not contain device identification"));
    }
  }

  private void saveDevice(final SmartMeteringDeviceDto smartMeteringDevice) {
    final DlmsDevice dlmsDevice =
        this.installationMapper.map(smartMeteringDevice, DlmsDevice.class);
    if (smartMeteringDevice.isOverwrite()) {
      DlmsDevice existingDlmsDevice =
          this.dlmsDeviceRepository.findByDeviceIdentification(
              smartMeteringDevice.getDeviceIdentification());
      if (existingDlmsDevice != null) { // overwrite existing device
        existingDlmsDevice = this.installationMapper.map(smartMeteringDevice, DlmsDevice.class);
        this.dlmsDeviceRepository.save(existingDlmsDevice);
      }
    } else {
      this.dlmsDeviceRepository.save(dlmsDevice);
    }
  }

  private void storeAndActivateKeys(
      final MessageMetadata messageMetadata, final SmartMeteringDeviceDto deviceDto)
      throws FunctionalException {
    final Map<SecurityKeyType, byte[]> keysByType = new EnumMap<>(SecurityKeyType.class);
    final List<SecurityKeyType> keyTypesToStore = this.determineKeyTypesToStore(deviceDto);
    for (final SecurityKeyType keyType : keyTypesToStore) {
      final byte[] key = this.getKeyFromDeviceDto(deviceDto, keyType);
      if (ArrayUtils.isNotEmpty(key)) {
        keysByType.put(keyType, this.decrypterForGxfSmartMetering.decrypt(key));
      } else {
        final Exception rootCause = new NoSuchElementException(keyType.name());
        throw new FunctionalException(
            FunctionalExceptionType.KEY_NOT_PRESENT, ComponentType.PROTOCOL_DLMS, rootCause);
      }
    }
    this.secretManagementService.storeNewKeys(
        messageMetadata, deviceDto.getDeviceIdentification(), keysByType);
    this.secretManagementService.activateNewKeys(
        messageMetadata, deviceDto.getDeviceIdentification(), keyTypesToStore);
  }

  private List<SecurityKeyType> determineKeyTypesToStore(final SmartMeteringDeviceDto deviceDto)
      throws FunctionalException {
    final List<SecurityKeyType> eMeterKeyTypes = this.getEMeterKeyTypes(deviceDto);
    final List<SecurityKeyType> gMeterKeyTypes = this.getGMeterKeyTypes(deviceDto);

    if (gMeterKeyTypes.contains(G_METER_MASTER)) {
      if (!eMeterKeyTypes.isEmpty()) {
        final String msg =
            "Device "
                + deviceDto.getDeviceIdentification()
                + " to install contains a G-Meter Master key, but contains E-Meter key(s) as well";
        throw new FunctionalException(
            FunctionalExceptionType.VALIDATION_ERROR,
            ComponentType.PROTOCOL_DLMS,
            new IllegalArgumentException(msg));
      }
      return Arrays.asList(
          G_METER_MASTER,
          G_METER_ENCRYPTION,
          G_METER_FIRMWARE_UPDATE_AUTHENTICATION,
          G_METER_OPTICAL_PORT_KEY);
    } else {
      return Arrays.asList(E_METER_MASTER, E_METER_AUTHENTICATION, E_METER_ENCRYPTION);
    }
  }

  private List<SecurityKeyType> getEMeterKeyTypes(final SmartMeteringDeviceDto deviceDto) {
    final byte[] deviceEmeterMasterKey = this.getKeyFromDeviceDto(deviceDto, E_METER_MASTER);
    final byte[] deviceEmeterAuthenticationKey =
        this.getKeyFromDeviceDto(deviceDto, E_METER_AUTHENTICATION);
    final byte[] deviceEmeterEncryptionKey =
        this.getKeyFromDeviceDto(deviceDto, E_METER_ENCRYPTION);

    final List<SecurityKeyType> securityEmeterKeyTypes = new ArrayList<>();
    if (this.isKeyPresent(deviceEmeterMasterKey)) {
      securityEmeterKeyTypes.add(E_METER_MASTER);
    }
    if (this.isKeyPresent(deviceEmeterAuthenticationKey)) {
      securityEmeterKeyTypes.add(E_METER_AUTHENTICATION);
    }
    if (this.isKeyPresent(deviceEmeterEncryptionKey)) {
      securityEmeterKeyTypes.add(E_METER_ENCRYPTION);
    }
    return securityEmeterKeyTypes;
  }

  private List<SecurityKeyType> getGMeterKeyTypes(final SmartMeteringDeviceDto deviceDto) {
    final byte[] deviceGmeterMasterKey = this.getKeyFromDeviceDto(deviceDto, G_METER_MASTER);
    final byte[] deviceGmeterEncryption = this.getKeyFromDeviceDto(deviceDto, G_METER_ENCRYPTION);
    final byte[] deviceGmeterFirmwareUpdateAuthenticationKey =
        this.getKeyFromDeviceDto(deviceDto, G_METER_FIRMWARE_UPDATE_AUTHENTICATION);
    final byte[] deviceGmeterP0Key = this.getKeyFromDeviceDto(deviceDto, G_METER_OPTICAL_PORT_KEY);

    final List<SecurityKeyType> securityGmeterKeyTypes = new ArrayList<>();
    if (this.isKeyPresent(deviceGmeterMasterKey)) {
      securityGmeterKeyTypes.add(G_METER_MASTER);
    }
    if (this.isKeyPresent(deviceGmeterEncryption)) {
      securityGmeterKeyTypes.add(G_METER_ENCRYPTION);
    }
    if (this.isKeyPresent(deviceGmeterFirmwareUpdateAuthenticationKey)) {
      securityGmeterKeyTypes.add(G_METER_FIRMWARE_UPDATE_AUTHENTICATION);
    }
    if (this.isKeyPresent(deviceGmeterP0Key)) {
      securityGmeterKeyTypes.add(G_METER_OPTICAL_PORT_KEY);
    }
    return securityGmeterKeyTypes;
  }

  private boolean isKeyPresent(final byte[] deviceKey) {
    return deviceKey != null && deviceKey.length > 0;
  }

  private byte[] getKeyFromDeviceDto(
      final SmartMeteringDeviceDto deviceDto, final SecurityKeyType keyType) {
    switch (keyType) {
      case E_METER_MASTER:
        return deviceDto.getMasterKey();
      case E_METER_AUTHENTICATION:
        return deviceDto.getAuthenticationKey();
      case E_METER_ENCRYPTION:
        return deviceDto.getGlobalEncryptionUnicastKey();
      case G_METER_MASTER:
        return deviceDto.getMbusDefaultKey();
      case G_METER_ENCRYPTION:
        return deviceDto.getMbusUserKey();
      case G_METER_FIRMWARE_UPDATE_AUTHENTICATION:
        return deviceDto.getMbusFirmwareUpdateAuthenticationKey();
      case G_METER_OPTICAL_PORT_KEY:
        return deviceDto.getMbusP0Key();

      default:
        throw new IllegalArgumentException("Unknown type " + keyType);
    }
  }

  public CoupleMbusDeviceResponseDto coupleMbusDevice(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final CoupleMbusDeviceRequestDataDto requestDataDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    return this.coupleMBusDeviceCommandExecutor.execute(
        conn, device, requestDataDto, messageMetadata);
  }

  public CoupleMbusDeviceByChannelResponseDto coupleMbusDeviceByChannel(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final CoupleMbusDeviceByChannelRequestDataDto requestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    return this.coupleMbusDeviceByChannelCommandExecutor.execute(
        conn, device, requestDto, messageMetadata);
  }

  public DecoupleMbusDeviceResponseDto decoupleMbusDevice(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final DecoupleMbusDeviceDto decoupleMbusDeviceDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    return this.decoupleMBusDeviceCommandExecutor.execute(
        conn, device, decoupleMbusDeviceDto, messageMetadata);
  }
}
