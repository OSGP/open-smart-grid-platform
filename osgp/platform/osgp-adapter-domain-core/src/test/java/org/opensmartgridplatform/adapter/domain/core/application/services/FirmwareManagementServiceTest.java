/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareFile;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.repositories.DeviceFirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FirmwareManagementServiceTest {

    private static final String VERSION_1 = "R01";
    private static final String VERSION_2 = "R02";
    private static final String VERSION_3 = "R03";

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceFirmwareFileRepository deviceFirmwareFileRepository;

    @Mock
    private ManufacturerRepository manufacturerRepository;

    @Mock
    private DeviceModelRepository deviceModelRepository;

    @Mock
    private FirmwareFileRepository firmwareFileRepository;

    @InjectMocks
    private FirmwareManagementService firmwareManagementService;

    private Device createDevice(final DeviceModel deviceModel) {
        final Device device = new Device();
        device.setDeviceModel(deviceModel);
        return device;
    }

    private FirmwareFile createFirmwareFile(final String version) {
        final FirmwareFile firmwareFile = new FirmwareFile.Builder().build();
        final FirmwareModule module1 = new FirmwareModule("Functional");
        final FirmwareModule module2 = new FirmwareModule("Security");
        firmwareFile.addFirmwareModule(module1, version);
        firmwareFile.addFirmwareModule(module2, version);
        return firmwareFile;
    }

    @BeforeEach
    public void setUp() throws FunctionalException {
        final Manufacturer manufacturer = new Manufacturer("code", "name", false);
        final DeviceModel deviceModel = new DeviceModel(manufacturer, "modelCode", "description", false);
        final Device device = this.createDevice(deviceModel);
        when(this.deviceRepository.findByDeviceIdentification(anyString())).thenReturn(device);
        final DeviceFirmwareFile deviceFirmwareFile1 = new DeviceFirmwareFile(device,
                this.createFirmwareFile(VERSION_1), new Date(), "me");
        final DeviceFirmwareFile deviceFirmwareFile2 = new DeviceFirmwareFile(device,
                this.createFirmwareFile(VERSION_2), new Date(), "me");
        final List<DeviceFirmwareFile> deviceFirmwareFiles = Arrays.asList(deviceFirmwareFile1, deviceFirmwareFile2);
        when(this.deviceFirmwareFileRepository.findByDeviceOrderByInstallationDateAsc(any(Device.class)))
                .thenReturn(deviceFirmwareFiles);
        when(this.deviceFirmwareFileRepository.save(any(DeviceFirmwareFile.class))).thenReturn(deviceFirmwareFile1);
        when(this.manufacturerRepository.findByCode(anyString())).thenReturn(manufacturer);
        when(this.deviceModelRepository.findByManufacturerAndModelCode(any(Manufacturer.class), anyString()))
                .thenReturn(deviceModel);
    }

    @Test
    public void testCheckFirmwareHistoryForExistingVersion() throws FunctionalException {

        // Arrange
        final FirmwareVersion firmwareVersion1 = new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2);
        final FirmwareVersion firmwareVersion2 = new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_2);
        final List<FirmwareVersion> versionsOnDevice = Arrays.asList(firmwareVersion1, firmwareVersion2);

        // Act
        final List<FirmwareVersion> versionsNotInHistory = this.firmwareManagementService
                .checkFirmwareHistoryForVersion("", versionsOnDevice);

        // Validate
        assertThat(versionsNotInHistory).withFailMessage("List should be empty").isEmpty();
    }

    @Test
    public void testCheckFirmwareHistoryForNonExistingVersion() throws FunctionalException {

        // Arrange
        final FirmwareVersion firmwareVersion1 = new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2);
        final FirmwareVersion firmwareVersion2 = new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_3);
        final List<FirmwareVersion> versionsOnDevice = Arrays.asList(firmwareVersion1, firmwareVersion2);

        final List<FirmwareVersion> expected = Arrays.asList(firmwareVersion2);

        // Act
        final List<FirmwareVersion> versionsNotInHistory = this.firmwareManagementService
                .checkFirmwareHistoryForVersion("", versionsOnDevice);

        // Assert
        assertThat(versionsNotInHistory).withFailMessage("Lists should be equal").isEqualTo(expected);
    }

    @Test
    public void testTryToAddFirmwareVersionToHistoryWhenFileIsAvailable() throws FunctionalException {

        // Arrange
        final FirmwareFile firmwareFile = new FirmwareFile.Builder().withFilename("filename")
                .withDescription("description")
                .withPushToNewDevices(false)
                .build();
        final FirmwareModule firmwareModule = new FirmwareModule(
                FirmwareModuleType.SECURITY.getDescription().toLowerCase());
        firmwareFile.addFirmwareModule(firmwareModule, VERSION_2);
        when(this.firmwareFileRepository.findByDeviceModel(any(DeviceModel.class)))
                .thenReturn(Arrays.asList(firmwareFile));
        final List<FirmwareVersion> firmwareVersions = Arrays
                .asList(new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2));

        // Act
        this.firmwareManagementService.tryToAddFirmwareVersionToHistory("", firmwareVersions);
        // Assert
        verify(this.deviceFirmwareFileRepository, times(1)).save(any(DeviceFirmwareFile.class));
    }

    @Test
    public void testTryToAddFirmwareVersionToHistoryWhenFileIsNotAvailable() throws FunctionalException {

        // Arrange
        final FirmwareFile firmwareFile = new FirmwareFile.Builder().withFilename("filename")
                .withDescription("description")
                .withPushToNewDevices(false)
                .build();
        when(this.firmwareFileRepository.findByDeviceModel(any(DeviceModel.class)))
                .thenReturn(Arrays.asList(firmwareFile));
        final List<FirmwareVersion> firmwareVersions1 = Arrays
                .asList(new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2));

        // Act
        this.firmwareManagementService.tryToAddFirmwareVersionToHistory("", firmwareVersions1);

        // Assert
        verify(this.deviceFirmwareFileRepository, never()).save(any(DeviceFirmwareFile.class));
    }

}
