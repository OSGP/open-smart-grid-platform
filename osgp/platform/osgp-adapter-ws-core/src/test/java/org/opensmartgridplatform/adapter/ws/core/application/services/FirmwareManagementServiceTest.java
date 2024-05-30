// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableFirmwareFileRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableManufacturerRepository;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile.Builder;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceModel;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData;
import org.opensmartgridplatform.dto.valueobjects.HashTypeDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class FirmwareManagementServiceTest {

  private static final DeviceModel DEVICE_MODEL =
      new DeviceModel(
          "LGB",
          "Landis and Gyr-T SMR5.1 energy meter",
          "Poly phase SMR5.1 energy meter, manufacturer Landis and Gyr");

  private static final Manufacturer MANUFACTURER_ENTITY =
      new Manufacturer("LGB", "Landis and Gyr", false);

  private static final org.opensmartgridplatform.domain.core.entities.DeviceModel
      DEVICE_MODEL_ENTITY =
          new org.opensmartgridplatform.domain.core.entities.DeviceModel(
              MANUFACTURER_ENTITY,
              "Landis and Gyr-T SMR5.1 energy meter",
              "Poly phase SMR5.1 energy meter, manufacturer Landis and Gyr");

  private static final String ORG_ID = "organisationIdentification";
  private static final String FW_ID = "firmwareIdentification";
  private static final String DESCRIPTION = "some description";
  private static final String FILE_NAME = "some name";
  private static final byte[] IMG_ID = "imageIdentifier".getBytes();
  private static final boolean ACTIVE = Boolean.TRUE;
  private static final boolean PUSH_TO_NEW_DEVICES = Boolean.FALSE;
  private static final byte[] FIRMWARE_FILE = "Firmware file content".getBytes();
  private static final String FILE_SHA256_DIGEST =
      "3292c982337e6ba20dc7435d0545b57a534971d1a88eb2d942d53d6685005f03";

  @Mock private DomainHelperService domainHelperService;
  @Mock private WritableManufacturerRepository manufacturerRepository;
  @Mock private WritableDeviceModelRepository deviceModelRepository;
  @Mock private WritableFirmwareFileRepository firmwareFileRepository;
  @Mock private FirmwareFileStorageService storageService;

  @Mock private FirmwareModuleData firmwareModuleData;

  @InjectMocks private FirmwareManagementService service;

  @Captor ArgumentCaptor<FirmwareFile> fwfCaptor;

  @BeforeEach
  void setup() throws TechnicalException {
    // Only testing the file storage solution as used in SmartMetering application
    // Database storage as used in other applications is not covered yet
    ReflectionTestUtils.setField(this.service, "firmwareFileStorage", true);

    when(this.manufacturerRepository.findByCodeIgnoreCase("LGB")).thenReturn(MANUFACTURER_ENTITY);
    when(this.deviceModelRepository.findByManufacturerAndModelCodeIgnoreCase(
            MANUFACTURER_ENTITY, DEVICE_MODEL.getModelCode()))
        .thenReturn(DEVICE_MODEL_ENTITY);
    when(this.firmwareModuleData.getVersionsByModule(any(), eq(true))).thenReturn(Map.of());

    when(this.firmwareFileRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    when(this.storageService.createDigest(HashTypeDto.SHA256.getAlgorithmName(), FW_ID))
        .thenReturn(FILE_SHA256_DIGEST);
  }

  @Test
  void addOrChangeFirmwareFirstTime() throws OsgpException {
    when(this.firmwareFileRepository.findByIdentification(FW_ID)).thenReturn(null);

    final FirmwareFileAttributes fwAttr = buildFirmwareFileAttributes();

    this.service.addOrChangeFirmware(
        ORG_ID, fwAttr, FIRMWARE_FILE, List.of(DEVICE_MODEL), this.firmwareModuleData);

    verify(this.storageService).storeFirmwareFile(FIRMWARE_FILE, FW_ID);
    verify(this.storageService).storeImageIdentifier(IMG_ID, FW_ID);

    verify(this.firmwareFileRepository, times(2)).save(this.fwfCaptor.capture());
    final FirmwareFile firmwareFile = this.fwfCaptor.getValue();

    assertThat(firmwareFile.getFile()).isNull();
    assertThat(firmwareFile.getIdentification()).isEqualTo(FW_ID);
    assertThat(firmwareFile.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(firmwareFile.getFilename()).isEqualTo(FILE_NAME);
    assertThat(firmwareFile.getPushToNewDevices()).isEqualTo(PUSH_TO_NEW_DEVICES);
    assertThat(firmwareFile.isActive()).isFalse();
    assertThat(firmwareFile.getHash()).isEqualTo(FILE_SHA256_DIGEST);
    assertThat(firmwareFile.getHashType()).isEqualTo(HashTypeDto.SHA256.name());
  }

  @Test
  void addOrChangeFirmwareSecondTime() throws OsgpException {
    final FirmwareFile existingFirmwareFile =
        new Builder().withIdentification("existing-firmware").build();
    when(this.firmwareFileRepository.findByIdentification(FW_ID)).thenReturn(existingFirmwareFile);

    final FirmwareFileAttributes fwAttr = buildFirmwareFileAttributes();

    this.service.addOrChangeFirmware(
        ORG_ID, fwAttr, FIRMWARE_FILE, List.of(DEVICE_MODEL), this.firmwareModuleData);

    verify(this.firmwareFileRepository, times(2)).save(this.fwfCaptor.capture());
    final FirmwareFile firmwareFile = this.fwfCaptor.getValue();

    assertThat(firmwareFile).isEqualTo(existingFirmwareFile);
    assertThat(firmwareFile.getFile()).isNull();
    assertThat(firmwareFile.getFilename()).isEqualTo(FILE_NAME);
    assertThat(firmwareFile.getDescription()).isEqualTo(DESCRIPTION);

    // properties that are not changed
    assertThat(firmwareFile.getIdentification()).isEqualTo("existing-firmware");
    assertThat(firmwareFile.getPushToNewDevices()).isFalse();
    assertThat(firmwareFile.isActive()).isFalse();
  }

  private static FirmwareFileAttributes buildFirmwareFileAttributes() {
    final FirmwareFileAttributes fwAttr =
        FirmwareFileAttributes.builder()
            .identification(FW_ID)
            .description(DESCRIPTION)
            .fileName(FILE_NAME)
            .pushToNewDevices(PUSH_TO_NEW_DEVICES)
            .active(ACTIVE)
            .imageIdentifier(IMG_ID)
            .hash(null)
            .hashType(null)
            .build();
    return fwAttr;
  }
}
