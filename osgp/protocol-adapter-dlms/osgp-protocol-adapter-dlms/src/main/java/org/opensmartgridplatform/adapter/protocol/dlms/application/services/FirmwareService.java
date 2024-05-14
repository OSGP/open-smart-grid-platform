// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.GetFirmwareVersionsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.UpdateFirmwareCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.FirmwareFileCachingRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.FirmwareImageIdentifierCachingRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "dlmsFirmwareService")
public class FirmwareService {

  private static final String EXCEPTION_MSG_FIRMWARE_FILE_NOT_AVAILABLE =
      "Firmware file '%s' is not available.";

  private static final String EXCEPTION_MSG_FIRMWARE_FILE_DOES_NOT_MATCH_HASH =
      "Firmware file '%s' in cache does not match stored hash (%s)";

  private static final String EXCEPTION_MSG_ERROR_CALCULATING_DIGEST =
      "Error calculating digest (%s) for Firmware file '%s'";

  private final FirmwareFileCachingRepository firmwareRepository;
  private final FirmwareImageIdentifierCachingRepository imageIdentifierRepository;
  private final GetFirmwareVersionsCommandExecutor getFirmwareVersionsCommandExecutor;
  private final UpdateFirmwareCommandExecutor updateFirmwareCommandExecutor;
  private final S3BucketService s3BucketService;

  public FirmwareService(
      final FirmwareFileCachingRepository firmwareRepository,
      final FirmwareImageIdentifierCachingRepository imageIdentifierRepository,
      final GetFirmwareVersionsCommandExecutor getFirmwareVersionsCommandExecutor,
      final UpdateFirmwareCommandExecutor updateFirmwareCommandExecutor,
      final S3BucketService s3BucketService) {
    this.firmwareRepository = firmwareRepository;
    this.imageIdentifierRepository = imageIdentifierRepository;
    this.getFirmwareVersionsCommandExecutor = getFirmwareVersionsCommandExecutor;
    this.updateFirmwareCommandExecutor = updateFirmwareCommandExecutor;
    this.s3BucketService = s3BucketService;
  }

  public List<FirmwareVersionDto> getFirmwareVersions(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    return this.getFirmwareVersionsCommandExecutor.execute(conn, device, null, messageMetadata);
  }

  public UpdateFirmwareResponseDto updateFirmware(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final UpdateFirmwareRequestDto updateFirmwareRequestDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    log.info(
        "Updating firmware of device {} to firmware with identification {}",
        device,
        updateFirmwareRequestDto.getUpdateFirmwareRequestDataDto().getFirmwareIdentification());

    return this.executeFirmwareUpdate(conn, device, updateFirmwareRequestDto, messageMetadata);
  }

  private UpdateFirmwareResponseDto executeFirmwareUpdate(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final UpdateFirmwareRequestDto updateFirmwareRequestDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    final String firmwareIdentification =
        updateFirmwareRequestDto.getUpdateFirmwareRequestDataDto().getFirmwareIdentification();
    if (!this.firmwareFileInCache(firmwareIdentification)) {
      this.loadFirmwareFileInCache(updateFirmwareRequestDto);
    }
    if (!this.imageIdentifierInCache(firmwareIdentification)) {
      this.loadImageIdentifierInCache(updateFirmwareRequestDto);
    }
    return this.updateFirmwareCommandExecutor.execute(
        conn, device, updateFirmwareRequestDto, messageMetadata);
  }

  private boolean firmwareFileInCache(final String firmwareIdentification) {
    return this.firmwareRepository.isAvailable(firmwareIdentification);
  }

  private boolean imageIdentifierInCache(final String firmwareIdentification) {
    return this.imageIdentifierRepository.isAvailable(firmwareIdentification);
  }

  private void loadFirmwareFileInCache(final UpdateFirmwareRequestDto updateFirmwareRequestDto)
      throws ProtocolAdapterException {
    final UpdateFirmwareRequestDataDto requestData =
        updateFirmwareRequestDto.getUpdateFirmwareRequestDataDto();
    final String firmwareIdentification = requestData.getFirmwareIdentification();
    final byte[] firmwareFileContent = this.readFirmwareFile(firmwareIdentification);
    this.validateFirmwareFileDigest(firmwareFileContent, requestData);
    this.firmwareRepository.store(firmwareIdentification, firmwareFileContent);
  }

  private void loadImageIdentifierInCache(final UpdateFirmwareRequestDto updateFirmwareRequestDto)
      throws ProtocolAdapterException {
    final UpdateFirmwareRequestDataDto requestData =
        updateFirmwareRequestDto.getUpdateFirmwareRequestDataDto();
    final String firmwareIdentification = requestData.getFirmwareIdentification();
    final byte[] imageIdentifier = this.readImageIdentifier(firmwareIdentification);
    this.imageIdentifierRepository.store(firmwareIdentification, imageIdentifier);
  }

  private void validateFirmwareFileDigest(
      final byte[] firmwareFileContent, final UpdateFirmwareRequestDataDto requestData)
      throws ProtocolAdapterException {
    if (firmwareFileContent == null) {
      throw new ProtocolAdapterException(
          String.format(
              EXCEPTION_MSG_FIRMWARE_FILE_NOT_AVAILABLE, requestData.getFirmwareIdentification()));
    }
    final String algorithm = requestData.getFirmwareHashType().getAlgorithmName();
    final String expectedMessageDigest = requestData.getFirmwareDigest();
    final String firmwareFileIdentification = requestData.getFirmwareIdentification();
    final String calculatedMessageDigest =
        this.calcDigest(firmwareFileContent, algorithm, firmwareFileIdentification);
    if (!expectedMessageDigest.equals(calculatedMessageDigest)) {
      throw new ProtocolAdapterException(
          String.format(
              EXCEPTION_MSG_FIRMWARE_FILE_DOES_NOT_MATCH_HASH,
              firmwareFileIdentification,
              requestData.getFirmwareHashType().getAlgorithmName()));
    }
  }

  private String calcDigest(
      final byte[] content, final String algorithm, final String firmwareFileIdentification)
      throws ProtocolAdapterException {
    final String digestStr;
    try {
      final MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
      final byte[] digest = messageDigest.digest(content);
      digestStr = new BigInteger(1, digest).toString(16);
    } catch (final NoSuchAlgorithmException e) {
      log.error("Error calculating digest", e);
      throw new ProtocolAdapterException(
          String.format(
              EXCEPTION_MSG_ERROR_CALCULATING_DIGEST, algorithm, firmwareFileIdentification));
    }
    return digestStr;
  }

  private byte[] readFirmwareFile(final String firmwareIdentification)
      throws ProtocolAdapterException {
    return this.s3BucketService.readFirmwareFile(firmwareIdentification);
  }

  private byte[] readImageIdentifier(final String firmwareIdentification)
      throws ProtocolAdapterException {
    return this.s3BucketService.readImageIdentifier(firmwareIdentification);
  }
}
