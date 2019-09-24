package org.opensmartgridplatform.adapter.ws.core.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceFirmwareFileRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableFirmwareFileRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableManufacturerRepository;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareFile;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

@RunWith(MockitoJUnitRunner.class)
public class FirmwareManagementServiceTest {

    private static final String VERSION_1 = "R01";
    private static final String VERSION_2 = "R02";
    private static final String VERSION_3 = "R03";

    @Mock
    private DomainHelperService domainHelperService;

    @Mock
    private WritableDeviceRepository deviceRepository;

    @Mock
    private WritableDeviceFirmwareFileRepository deviceFirmwareFileRepository;

    @Mock
    private WritableManufacturerRepository manufacturerRepository;

    @Mock
    private WritableDeviceModelRepository deviceModelRepository;

    @Mock
    private WritableFirmwareFileRepository firmwareFileRepository;

    @InjectMocks
    private FirmwareManagementService firmwareManagementService;

    @Before
    public void setUp() throws FunctionalException {
        doNothing().when(this.domainHelperService).isAllowed(isA(Organisation.class), isA(PlatformFunction.class));
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
        when(this.domainHelperService.findActiveDevice(anyString())).thenReturn(device);
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
                .checkFirmwareHistoryForVersion("", "", versionsOnDevice);

        // Validate
        assertTrue("List should be empty", versionsNotInHistory.isEmpty());

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
                .checkFirmwareHistoryForVersion("", "", versionsOnDevice);

        // Assert
        assertEquals("Lists should be equal", expected, versionsNotInHistory);

    }

    @Test
    public void testTryToAddFirmwareVersionToHistoryWhenFileIsAvailable() throws FunctionalException {

        // Arrange
        final FirmwareFile firmwareFile = new FirmwareFile("filename", "description", false);
        final FirmwareModule firmwareModule = new FirmwareModule(FirmwareModuleType.SECURITY.getDescription().toLowerCase());
        firmwareFile.addFirmwareModule(firmwareModule, VERSION_2);
        when(this.firmwareFileRepository.findByDeviceModel(any(DeviceModel.class)))
                .thenReturn(Arrays.asList(firmwareFile));
        final FirmwareVersion firmwareVersion = new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2);

        // Act
        this.firmwareManagementService.tryToAddFirmwareVersionToHistory("", "", firmwareVersion);

        // Assert
        verify(this.deviceFirmwareFileRepository, times(1)).save(any(DeviceFirmwareFile.class));
    }

    @Test
    public void testTryToAddFirmwareVersionToHistoryWhenFileIsNotAvailable() throws FunctionalException {

        // Arrange
        final FirmwareFile firmwareFile = new FirmwareFile("filename", "description", false);
        when(this.firmwareFileRepository.findByDeviceModel(any(DeviceModel.class)))
                .thenReturn(Arrays.asList(firmwareFile));
        final FirmwareVersion firmwareVersion1 = new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2);

        // Act
        this.firmwareManagementService.tryToAddFirmwareVersionToHistory("", "", firmwareVersion1);

        // Assert
        verify(this.deviceFirmwareFileRepository, never()).save(any(DeviceFirmwareFile.class));
    }

    private FirmwareFile createFirmwareFile(final String version) {
        final FirmwareFile firmwareFile = new FirmwareFile();
        final FirmwareModule module1 = new FirmwareModule("Functional");
        final FirmwareModule module2 = new FirmwareModule("Security");
        firmwareFile.addFirmwareModule(module1, version);
        firmwareFile.addFirmwareModule(module2, version);
        return firmwareFile;
    }

    private Device createDevice(final DeviceModel deviceModel) {
        final Device device = new Device();
        device.setDeviceModel(deviceModel);
        return device;

    }

}
