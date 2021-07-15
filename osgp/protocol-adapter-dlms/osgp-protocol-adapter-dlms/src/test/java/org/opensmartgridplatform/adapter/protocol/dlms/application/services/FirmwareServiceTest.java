/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.GetFirmwareVersionsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.UpdateFirmwareCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.FirmwareFileCachingRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.FirmwareFileDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FirmwareServiceTest {

  @Mock private FirmwareFileCachingRepository firmwareFileCachingRepository;

  @Mock private GetFirmwareVersionsCommandExecutor getFirmwareVersionsCommandExecutor;

  @Mock private UpdateFirmwareCommandExecutor updateFirmwareCommandExecutor;

  @Mock private DlmsConnectionManager dlmsConnectionManagerMock;

  @Mock private DlmsDevice dlmsDeviceMock;

  @InjectMocks private FirmwareService firmwareService;

  private static MessageMetadata messageMetadata;
  private static final String firmwareIdentification = "fw";

  @BeforeAll
  public static void init() {
    messageMetadata =
        MessageMetadata.newMessageMetadataBuilder().withCorrelationUid("123456").build();
  }

  @Test
  public void getFirmwareVersionsShouldCallExecutor() throws ProtocolAdapterException {
    // Arrange

    // Act
    this.firmwareService.getFirmwareVersions(
        this.dlmsConnectionManagerMock, this.dlmsDeviceMock, messageMetadata);

    // Assert
    verify(this.getFirmwareVersionsCommandExecutor, times(1))
        .execute(this.dlmsConnectionManagerMock, this.dlmsDeviceMock, null, messageMetadata);
  }

  @Test
  public void updateFirmwareShouldCallExecutorWhenFirmwareFileInCache() throws OsgpException {
    // Arrange
    final byte[] firmwareFile = firmwareIdentification.getBytes();

    when(this.firmwareFileCachingRepository.isAvailable(firmwareIdentification)).thenReturn(true);
    when(this.firmwareFileCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(firmwareFile);

    // Act
    this.firmwareService.updateFirmware(
        this.dlmsConnectionManagerMock,
        this.dlmsDeviceMock,
        firmwareIdentification,
        messageMetadata);

    // Assert
    verify(this.updateFirmwareCommandExecutor, times(1))
        .execute(
            this.dlmsConnectionManagerMock,
            this.dlmsDeviceMock,
            firmwareIdentification,
            messageMetadata);
  }

  @Test
  public void updateFirmwareShouldThrowExceptionWhenFirmwareFileNotInCache() throws OsgpException {
    // Arrange
    when(this.firmwareFileCachingRepository.retrieve(firmwareIdentification)).thenReturn(null);

    // Act
    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              this.firmwareService.updateFirmware(
                  this.dlmsConnectionManagerMock,
                  this.dlmsDeviceMock,
                  firmwareIdentification,
                  messageMetadata);

              // Assert
              // Nothing to do, as exception will be thrown;
            });
  }

  @Test
  public void updateFirmwareShouldNotCallExecutorWhenFirmwareFileNotInCache() throws OsgpException {
    // Arrange
    when(this.firmwareFileCachingRepository.retrieve(firmwareIdentification)).thenReturn(null);

    // Act
    try {
      this.firmwareService.updateFirmware(
          this.dlmsConnectionManagerMock,
          this.dlmsDeviceMock,
          firmwareIdentification,
          messageMetadata);
    } catch (final ProtocolAdapterException e) {
      // Ignore exception.
    }

    // Assert
    verify(this.updateFirmwareCommandExecutor, never())
        .execute(
            this.dlmsConnectionManagerMock,
            this.dlmsDeviceMock,
            firmwareIdentification,
            messageMetadata);
  }

  @Test
  public void updateFirmwareUsingFirmwareFileShouldStoreFirmwareFileAndCallExecutor()
      throws OsgpException {
    // Arrange
    final byte[] firmwareFile = firmwareIdentification.getBytes();
    final FirmwareFileDto firmwareFileDto =
        new FirmwareFileDto(firmwareIdentification, firmwareFile);

    when(this.firmwareFileCachingRepository.isAvailable(firmwareIdentification)).thenReturn(true);
    when(this.firmwareFileCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(firmwareFile);

    // Act
    this.firmwareService.updateFirmware(
        this.dlmsConnectionManagerMock, this.dlmsDeviceMock, firmwareFileDto, messageMetadata);

    // Assert
    verify(this.firmwareFileCachingRepository, times(1))
        .store(firmwareIdentification, firmwareFile);
    verify(this.updateFirmwareCommandExecutor, times(1))
        .execute(
            this.dlmsConnectionManagerMock,
            this.dlmsDeviceMock,
            firmwareIdentification,
            messageMetadata);
  }

  @Test
  public void isFirmwareAvailableShouldReturnTrueWhenFirmwareFileAvailable() {
    // Arrange
    when(this.firmwareFileCachingRepository.isAvailable(firmwareIdentification)).thenReturn(true);
    final boolean expected = true;

    // Act
    final boolean actual = this.firmwareService.isFirmwareFileAvailable(firmwareIdentification);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void isFirmwareAvailableShouldReturnFalseWhenFirmwareFileNotAvailable() {
    // Arrange
    when(this.firmwareFileCachingRepository.isAvailable(firmwareIdentification)).thenReturn(false);
    final boolean expected = false;

    // Act
    final boolean actual = this.firmwareService.isFirmwareFileAvailable(firmwareIdentification);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }
}
