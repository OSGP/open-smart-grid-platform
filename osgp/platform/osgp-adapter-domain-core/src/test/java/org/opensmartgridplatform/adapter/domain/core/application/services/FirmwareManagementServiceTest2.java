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

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.core.application.mapping.DomainCoreMapper;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
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
import org.opensmartgridplatform.domain.core.repositories.SsldPendingFirmwareUpdateRepository;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FirmwareManagementServiceTest2 {
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
    @Mock
    private DomainCoreMapper domainCoreMapper;
    @Mock
    private SsldPendingFirmwareUpdateRepository ssldPendingFirmwareUpdateRepository;
    @Mock
    private WebServiceResponseMessageSender webServiceResponseMessageSender;
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

    }

    @Test
    public void testHandleGetFirmwareVersionResponseVersionAlreadyInHistoryButNotLast() throws FunctionalException {
        // Arrange
        // Mock that VERSION 1 is getting installed
        // and VERSION 1 and VERSION 2 have already been installed previously
        // (in that same order)
        final Manufacturer manufacturer = new Manufacturer("code", "name", false);
        final DeviceModel deviceModel = new DeviceModel(manufacturer, "modelCode", "description", false);
        final Device device = this.createDevice(deviceModel);
        when(this.deviceRepository.findByDeviceIdentification(anyString())).thenReturn(device);
        final DeviceFirmwareFile deviceFirmwareFile1 = new DeviceFirmwareFile(device,
                this.createFirmwareFile(VERSION_1), DateUtils.addDays(new Date(), -1), "me");
        final DeviceFirmwareFile deviceFirmwareFile2 = new DeviceFirmwareFile(device,
                this.createFirmwareFile(VERSION_2), new Date(), "me");
        final List<DeviceFirmwareFile> deviceFirmwareFiles = Arrays.asList(deviceFirmwareFile1, deviceFirmwareFile2);
        when(this.deviceFirmwareFileRepository.findByDeviceOrderByInstallationDateAsc(any(Device.class)))
                .thenReturn(deviceFirmwareFiles);

        when(this.deviceFirmwareFileRepository.save(any(DeviceFirmwareFile.class))).thenReturn(deviceFirmwareFile1);
        when(this.manufacturerRepository.findByCode(anyString())).thenReturn(manufacturer);
        when(this.deviceModelRepository.findByManufacturerAndModelCode(any(Manufacturer.class), anyString()))
                .thenReturn(deviceModel);

        final FirmwareVersionDto firmwareVersionDto1 = new FirmwareVersionDto(
                org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.SECURITY, VERSION_1);
        final FirmwareVersionDto firmwareVersionDto2 = new FirmwareVersionDto(
                org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.FUNCTIONAL, VERSION_1);
        final List<FirmwareVersionDto> versionsOnDeviceDtos = Arrays.asList(firmwareVersionDto1, firmwareVersionDto2);
        final FirmwareVersion firmwareVersion1 = new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_1);
        final FirmwareVersion firmwareVersion2 = new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_1);
        final List<FirmwareVersion> versionsOnDevice = Arrays.asList(firmwareVersion1, firmwareVersion2);
        final FirmwareFile firmwareFile = new FirmwareFile.Builder().withFilename("filename")
                .withDescription("description")
                .withPushToNewDevices(false)
                .build();
        final FirmwareModule firmwareModule1 = new FirmwareModule(
                FirmwareModuleType.SECURITY.getDescription().toLowerCase());
        firmwareFile.addFirmwareModule(firmwareModule1, VERSION_1);
        final FirmwareModule firmwareModule2 = new FirmwareModule(
                FirmwareModuleType.FUNCTIONAL.getDescription().toLowerCase());
        firmwareFile.addFirmwareModule(firmwareModule2, VERSION_1);
        when(this.domainCoreMapper.mapAsList(versionsOnDeviceDtos, FirmwareVersion.class)).thenReturn(versionsOnDevice);
        when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification("DVC")).thenReturn(null);
        when(this.firmwareFileRepository.findByDeviceModel(any(DeviceModel.class)))
                .thenReturn(Arrays.asList(firmwareFile));
        final CorrelationIds ids = new CorrelationIds("ORG", "DVC", "CORR");
        // Act
        this.firmwareManagementService.handleGetFirmwareVersionResponse(versionsOnDeviceDtos, ids, "FW", 0,
                ResponseMessageResultType.OK, null);
        // Validate
        verify(this.deviceFirmwareFileRepository, times(1)).save(any(DeviceFirmwareFile.class));
    }

    @Test
    public void testHandleGetFirmwareVersionResponseVersionNotInHistory() throws FunctionalException {
        // Arrange
        // Mock that VERSION 1 is getting installed
        // and VERSION 2 has already been installed previously
        final Manufacturer manufacturer = new Manufacturer("code", "name", false);
        final DeviceModel deviceModel = new DeviceModel(manufacturer, "modelCode", "description", false);
        final Device device = this.createDevice(deviceModel);
        when(this.deviceRepository.findByDeviceIdentification(anyString())).thenReturn(device);
        final DeviceFirmwareFile deviceFirmwareFile2 = new DeviceFirmwareFile(device,
                this.createFirmwareFile(VERSION_2), new Date(), "me");
        final List<DeviceFirmwareFile> deviceFirmwareFiles = Arrays.asList(deviceFirmwareFile2);
        when(this.deviceFirmwareFileRepository.findByDeviceOrderByInstallationDateAsc(any(Device.class)))
                .thenReturn(deviceFirmwareFiles);

        when(this.deviceFirmwareFileRepository.save(any(DeviceFirmwareFile.class))).thenReturn(deviceFirmwareFile2);
        when(this.manufacturerRepository.findByCode(anyString())).thenReturn(manufacturer);
        when(this.deviceModelRepository.findByManufacturerAndModelCode(any(Manufacturer.class), anyString()))
                .thenReturn(deviceModel);

        final FirmwareVersionDto firmwareVersionDto1 = new FirmwareVersionDto(
                org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.SECURITY, VERSION_1);
        final FirmwareVersionDto firmwareVersionDto2 = new FirmwareVersionDto(
                org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.FUNCTIONAL, VERSION_1);
        final List<FirmwareVersionDto> versionsOnDeviceDtos = Arrays.asList(firmwareVersionDto1, firmwareVersionDto2);
        final FirmwareVersion firmwareVersion1 = new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_1);
        final FirmwareVersion firmwareVersion2 = new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_1);
        final List<FirmwareVersion> versionsOnDevice = Arrays.asList(firmwareVersion1, firmwareVersion2);
        final FirmwareFile firmwareFile = new FirmwareFile.Builder().withFilename("filename")
                .withDescription("description")
                .withPushToNewDevices(false)
                .build();
        final FirmwareModule firmwareModule1 = new FirmwareModule(
                FirmwareModuleType.SECURITY.getDescription().toLowerCase());
        firmwareFile.addFirmwareModule(firmwareModule1, VERSION_1);
        final FirmwareModule firmwareModule2 = new FirmwareModule(
                FirmwareModuleType.FUNCTIONAL.getDescription().toLowerCase());
        firmwareFile.addFirmwareModule(firmwareModule2, VERSION_1);
        when(this.domainCoreMapper.mapAsList(versionsOnDeviceDtos, FirmwareVersion.class)).thenReturn(versionsOnDevice);
        when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification("DVC")).thenReturn(null);
        when(this.firmwareFileRepository.findByDeviceModel(any(DeviceModel.class)))
                .thenReturn(Arrays.asList(firmwareFile));
        final CorrelationIds ids = new CorrelationIds("ORG", "DVC", "CORR");
        // Act
        this.firmwareManagementService.handleGetFirmwareVersionResponse(versionsOnDeviceDtos, ids, "FW", 0,
                ResponseMessageResultType.OK, null);
        // Validate
        verify(this.deviceFirmwareFileRepository, times(1)).save(any(DeviceFirmwareFile.class));
    }

    @Test
    public void testHandleGetFirmwareVersionResponseVersionAlreadyInAndLast() throws FunctionalException {
        // Arrange
        // Mock that VERSION 2 is getting installed
        // and VERSION 1 and VERSION 2 have already been installed previously
        // (in that same order)
        final Manufacturer manufacturer = new Manufacturer("code", "name", false);
        final DeviceModel deviceModel = new DeviceModel(manufacturer, "modelCode", "description", false);
        final Device device = this.createDevice(deviceModel);
        when(this.deviceRepository.findByDeviceIdentification(anyString())).thenReturn(device);
        final DeviceFirmwareFile deviceFirmwareFile1 = new DeviceFirmwareFile(device,
                this.createFirmwareFile(VERSION_1), DateUtils.addDays(new Date(), -1), "me");
        final DeviceFirmwareFile deviceFirmwareFile2 = new DeviceFirmwareFile(device,
                this.createFirmwareFile(VERSION_2), new Date(), "me");
        final List<DeviceFirmwareFile> deviceFirmwareFiles = Arrays.asList(deviceFirmwareFile1, deviceFirmwareFile2);
        when(this.deviceFirmwareFileRepository.findByDeviceOrderByInstallationDateAsc(any(Device.class)))
                .thenReturn(deviceFirmwareFiles);

        when(this.deviceFirmwareFileRepository.save(any(DeviceFirmwareFile.class))).thenReturn(deviceFirmwareFile1);
        when(this.manufacturerRepository.findByCode(anyString())).thenReturn(manufacturer);
        when(this.deviceModelRepository.findByManufacturerAndModelCode(any(Manufacturer.class), anyString()))
                .thenReturn(deviceModel);

        final FirmwareVersionDto firmwareVersionDto1 = new FirmwareVersionDto(
                org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.SECURITY, VERSION_2);
        final FirmwareVersionDto firmwareVersionDto2 = new FirmwareVersionDto(
                org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.FUNCTIONAL, VERSION_2);
        final List<FirmwareVersionDto> versionsOnDeviceDtos = Arrays.asList(firmwareVersionDto1, firmwareVersionDto2);
        final FirmwareVersion firmwareVersion1 = new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2);
        final FirmwareVersion firmwareVersion2 = new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_2);
        final List<FirmwareVersion> versionsOnDevice = Arrays.asList(firmwareVersion1, firmwareVersion2);
        final FirmwareFile firmwareFile = new FirmwareFile.Builder().withFilename("filename")
                .withDescription("description")
                .withPushToNewDevices(false)
                .build();
        final FirmwareModule firmwareModule1 = new FirmwareModule(
                FirmwareModuleType.SECURITY.getDescription().toLowerCase());
        firmwareFile.addFirmwareModule(firmwareModule1, VERSION_2);
        final FirmwareModule firmwareModule2 = new FirmwareModule(
                FirmwareModuleType.FUNCTIONAL.getDescription().toLowerCase());
        firmwareFile.addFirmwareModule(firmwareModule2, VERSION_2);
        when(this.domainCoreMapper.mapAsList(versionsOnDeviceDtos, FirmwareVersion.class)).thenReturn(versionsOnDevice);
        when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification("DVC")).thenReturn(null);
        when(this.firmwareFileRepository.findByDeviceModel(any(DeviceModel.class)))
                .thenReturn(Arrays.asList(firmwareFile));
        final CorrelationIds ids = new CorrelationIds("ORG", "DVC", "CORR");
        // Act
        this.firmwareManagementService.handleGetFirmwareVersionResponse(versionsOnDeviceDtos, ids, "FW", 0,
                ResponseMessageResultType.OK, null);
        // Validate
        verify(this.deviceFirmwareFileRepository, never()).save(any(DeviceFirmwareFile.class));
    }

}