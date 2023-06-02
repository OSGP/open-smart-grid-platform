//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.core.application.mapping.DomainCoreMapper;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareFile;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.entities.SsldPendingFirmwareUpdate;
import org.opensmartgridplatform.domain.core.repositories.DeviceFirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldPendingFirmwareUpdateRepository;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareUpdateMessageDataContainer;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunctionGroup;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FirmwareManagementServiceTest {
  private static final String VERSION_1 = "R01";
  private static final String VERSION_2 = "R02";
  private static final String VERSION_3 = "R03";

  private static final String ORGANISATION_IDENTIFICATION = "test-org";
  private static final String DEVICE_IDENTIFICATION = "device-identification";
  private static final String CORRELATION_UID = "correlation-uid";
  private static final CorrelationIds CORRELATION_IDS =
      new CorrelationIds(ORGANISATION_IDENTIFICATION, DEVICE_IDENTIFICATION, CORRELATION_UID);

  private static final OsgpException DEFAULT_EXCEPTION =
      new OsgpException(ComponentType.DOMAIN_CORE, "test");

  @Captor ArgumentCaptor<RequestMessage> requestMessageCaptor;
  @Captor ArgumentCaptor<ResponseMessage> responseMessageCaptor;
  @Captor ArgumentCaptor<String> messageTypeCaptor;
  @Captor ArgumentCaptor<Integer> messagePriorityCaptor;
  @Captor ArgumentCaptor<String> ipAddressCaptor;
  @Captor ArgumentCaptor<Long> scheduledTimeCaptor;
  @Captor ArgumentCaptor<SsldPendingFirmwareUpdate> ssldPendingFirmwareUpdateArgumentCaptor;

  @Mock FirmwareUpdateMessageDataContainer firmwareUpdateMessageDataContainer;
  @Mock private DeviceRepository deviceRepository;
  @Mock private DeviceFirmwareFileRepository deviceFirmwareFileRepository;
  @Mock private ManufacturerRepository manufacturerRepository;
  @Mock private DeviceModelRepository deviceModelRepository;
  @Mock private FirmwareFileRepository firmwareFileRepository;
  @Mock private DomainCoreMapper domainCoreMapper;
  @Mock private SsldPendingFirmwareUpdateRepository ssldPendingFirmwareUpdateRepository;
  @Mock private WebServiceResponseMessageSender webServiceResponseMessageSender;
  @Mock private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;
  @Mock private DeviceDomainService deviceDomainService;
  @Mock private OrganisationDomainService organisationDomainService;

  @InjectMocks private FirmwareManagementService firmwareManagementService;

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
  void setUp() {
    // VERSION 1 and VERSION 2 have already been installed previously (in
    // that same order)
    final Manufacturer manufacturer = new Manufacturer("code", "name", false);
    final DeviceModel deviceModel =
        new DeviceModel(manufacturer, "modelCode", "description", false);
    final Device device = this.createDevice(deviceModel);
    when(this.deviceRepository.findByDeviceIdentification(anyString())).thenReturn(device);
    final DeviceFirmwareFile deviceFirmwareFile1 =
        new DeviceFirmwareFile(
            device, this.createFirmwareFile(VERSION_1), DateUtils.addDays(new Date(), -2), "me");
    final DeviceFirmwareFile deviceFirmwareFile2 =
        new DeviceFirmwareFile(
            device, this.createFirmwareFile(VERSION_2), DateUtils.addDays(new Date(), -1), "me");
    final List<DeviceFirmwareFile> deviceFirmwareFiles =
        Arrays.asList(deviceFirmwareFile1, deviceFirmwareFile2);
    when(this.deviceFirmwareFileRepository.findByDeviceOrderByInstallationDateAsc(
            any(Device.class)))
        .thenReturn(deviceFirmwareFiles);

    when(this.deviceFirmwareFileRepository.save(any(DeviceFirmwareFile.class)))
        .thenReturn(deviceFirmwareFile1);
    when(this.manufacturerRepository.findByCode(anyString())).thenReturn(manufacturer);
    when(this.deviceModelRepository.findByManufacturerAndModelCode(
            any(Manufacturer.class), anyString()))
        .thenReturn(deviceModel);
  }

  @Test
  void testHandleGetFirmwareVersionResponseVersionAlreadyInHistoryButNotLast() {
    // Arrange
    // Mock that VERSION 1 is now installed
    final FirmwareVersionDto firmwareVersionDto1 =
        new FirmwareVersionDto(
            org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.SECURITY, VERSION_1);
    final FirmwareVersionDto firmwareVersionDto2 =
        new FirmwareVersionDto(
            org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.FUNCTIONAL, VERSION_1);
    final List<FirmwareVersionDto> versionsOnDeviceDtos =
        Arrays.asList(firmwareVersionDto1, firmwareVersionDto2);
    final FirmwareVersion firmwareVersion1 =
        new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_1);
    final FirmwareVersion firmwareVersion2 =
        new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_1);
    final List<FirmwareVersion> versionsOnDevice =
        Arrays.asList(firmwareVersion1, firmwareVersion2);
    final FirmwareFile firmwareFile =
        new FirmwareFile.Builder()
            .withFilename("filename")
            .withDescription("description")
            .withPushToNewDevices(false)
            .build();
    final FirmwareModule firmwareModule1 =
        new FirmwareModule(FirmwareModuleType.SECURITY.getDescription().toLowerCase());
    firmwareFile.addFirmwareModule(firmwareModule1, VERSION_1);
    final FirmwareModule firmwareModule2 =
        new FirmwareModule(FirmwareModuleType.FUNCTIONAL.getDescription().toLowerCase());
    firmwareFile.addFirmwareModule(firmwareModule2, VERSION_1);
    when(this.domainCoreMapper.mapAsList(versionsOnDeviceDtos, FirmwareVersion.class))
        .thenReturn(versionsOnDevice);
    when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification("DVC"))
        .thenReturn(Collections.emptyList());
    when(this.firmwareFileRepository.findByDeviceModel(any(DeviceModel.class)))
        .thenReturn(Collections.singletonList(firmwareFile));
    final CorrelationIds ids = new CorrelationIds("ORG", "DVC", "CORR");

    // Act
    this.firmwareManagementService.handleGetFirmwareVersionResponse(
        versionsOnDeviceDtos, ids, "FW", 0, ResponseMessageResultType.OK, null);

    // Validate
    verify(this.deviceFirmwareFileRepository, times(1)).save(any(DeviceFirmwareFile.class));
  }

  @Test
  void testHandleGetFirmwareVersionResponseVersionNotInHistory() {
    // Arrange
    // Mock that VERSION 3 is now installed
    final FirmwareVersionDto firmwareVersionDto1 =
        new FirmwareVersionDto(
            org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.SECURITY, VERSION_3);
    final FirmwareVersionDto firmwareVersionDto2 =
        new FirmwareVersionDto(
            org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.FUNCTIONAL, VERSION_3);
    final List<FirmwareVersionDto> versionsOnDeviceDtos =
        Arrays.asList(firmwareVersionDto1, firmwareVersionDto2);
    final FirmwareVersion firmwareVersion1 =
        new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_3);
    final FirmwareVersion firmwareVersion2 =
        new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_3);
    final List<FirmwareVersion> versionsOnDevice =
        Arrays.asList(firmwareVersion1, firmwareVersion2);
    final FirmwareFile firmwareFile =
        new FirmwareFile.Builder()
            .withFilename("filename")
            .withDescription("description")
            .withPushToNewDevices(false)
            .build();
    final FirmwareModule firmwareModule1 =
        new FirmwareModule(FirmwareModuleType.SECURITY.getDescription().toLowerCase());
    firmwareFile.addFirmwareModule(firmwareModule1, VERSION_3);
    final FirmwareModule firmwareModule2 =
        new FirmwareModule(FirmwareModuleType.FUNCTIONAL.getDescription().toLowerCase());
    firmwareFile.addFirmwareModule(firmwareModule2, VERSION_3);
    when(this.domainCoreMapper.mapAsList(versionsOnDeviceDtos, FirmwareVersion.class))
        .thenReturn(versionsOnDevice);
    when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification("DVC"))
        .thenReturn(Collections.emptyList());
    when(this.firmwareFileRepository.findByDeviceModel(any(DeviceModel.class)))
        .thenReturn(Collections.singletonList(firmwareFile));
    final CorrelationIds ids = new CorrelationIds("ORG", "DVC", "CORR");

    // Act
    this.firmwareManagementService.handleGetFirmwareVersionResponse(
        versionsOnDeviceDtos, ids, "FW", 0, ResponseMessageResultType.OK, null);

    // Validate
    verify(this.deviceFirmwareFileRepository, times(1)).save(any(DeviceFirmwareFile.class));
  }

  @Test
  void testHandleGetFirmwareVersionResponseVersionNotInHistoryButNoCorrespondingFirmwareFile() {
    // Arrange
    // Mock that FUNCTIONAL VERSION 3 and SECURITY VERSION 1 is now
    // installed,
    // no firmware file will hold this combination, so it will fail to save
    final FirmwareVersionDto firmwareVersionDto1 =
        new FirmwareVersionDto(
            org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.SECURITY, VERSION_3);
    final FirmwareVersionDto firmwareVersionDto2 =
        new FirmwareVersionDto(
            org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.FUNCTIONAL, VERSION_3);
    final List<FirmwareVersionDto> versionsOnDeviceDtos =
        Arrays.asList(firmwareVersionDto1, firmwareVersionDto2);
    final FirmwareVersion firmwareVersion1 =
        new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_1);
    final FirmwareVersion firmwareVersion3 =
        new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_3);
    final List<FirmwareVersion> versionsOnDevice =
        Arrays.asList(firmwareVersion1, firmwareVersion3);
    final FirmwareFile firmwareFile =
        new FirmwareFile.Builder()
            .withFilename("filename")
            .withDescription("description")
            .withPushToNewDevices(false)
            .build();
    final FirmwareModule firmwareModule1 =
        new FirmwareModule(FirmwareModuleType.SECURITY.getDescription().toLowerCase());
    firmwareFile.addFirmwareModule(firmwareModule1, VERSION_3);
    final FirmwareModule firmwareModule2 =
        new FirmwareModule(FirmwareModuleType.FUNCTIONAL.getDescription().toLowerCase());
    firmwareFile.addFirmwareModule(firmwareModule2, VERSION_3);
    when(this.domainCoreMapper.mapAsList(versionsOnDeviceDtos, FirmwareVersion.class))
        .thenReturn(versionsOnDevice);
    when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification("DVC"))
        .thenReturn(Collections.emptyList());
    when(this.firmwareFileRepository.findByDeviceModel(any(DeviceModel.class)))
        .thenReturn(Collections.singletonList(firmwareFile));
    final CorrelationIds ids = new CorrelationIds("ORG", "DVC", "CORR");

    // Act
    this.firmwareManagementService.handleGetFirmwareVersionResponse(
        versionsOnDeviceDtos, ids, "FW", 0, ResponseMessageResultType.OK, null);

    // Validate
    verify(this.deviceFirmwareFileRepository, never()).save(any(DeviceFirmwareFile.class));
  }

  @Test
  void testHandleGetFirmwareVersionResponseVersionAlreadyInAndLast() {
    // Arrange
    // Mock that VERSION 2 is now installed
    final FirmwareVersionDto firmwareVersionDto1 =
        new FirmwareVersionDto(
            org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.SECURITY, VERSION_2);
    final FirmwareVersionDto firmwareVersionDto2 =
        new FirmwareVersionDto(
            org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.FUNCTIONAL, VERSION_2);
    final List<FirmwareVersionDto> versionsOnDeviceDtos =
        Arrays.asList(firmwareVersionDto1, firmwareVersionDto2);
    final FirmwareVersion firmwareVersion1 =
        new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2);
    final FirmwareVersion firmwareVersion2 =
        new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_2);
    final List<FirmwareVersion> versionsOnDevice =
        Arrays.asList(firmwareVersion1, firmwareVersion2);
    final FirmwareFile firmwareFile =
        new FirmwareFile.Builder()
            .withFilename("filename")
            .withDescription("description")
            .withPushToNewDevices(false)
            .build();
    final FirmwareModule firmwareModule1 =
        new FirmwareModule(FirmwareModuleType.SECURITY.getDescription().toLowerCase());
    firmwareFile.addFirmwareModule(firmwareModule1, VERSION_2);
    final FirmwareModule firmwareModule2 =
        new FirmwareModule(FirmwareModuleType.FUNCTIONAL.getDescription().toLowerCase());
    firmwareFile.addFirmwareModule(firmwareModule2, VERSION_2);
    when(this.domainCoreMapper.mapAsList(versionsOnDeviceDtos, FirmwareVersion.class))
        .thenReturn(versionsOnDevice);
    when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification("DVC"))
        .thenReturn(Collections.emptyList());
    when(this.firmwareFileRepository.findByDeviceModel(any(DeviceModel.class)))
        .thenReturn(Collections.singletonList(firmwareFile));
    final CorrelationIds ids = new CorrelationIds("ORG", "DVC", "CORR");

    // Act
    this.firmwareManagementService.handleGetFirmwareVersionResponse(
        versionsOnDeviceDtos, ids, "FW", 0, ResponseMessageResultType.OK, null);

    // Validate
    verify(this.deviceFirmwareFileRepository, never()).save(any(DeviceFirmwareFile.class));
  }

  @Test
  void testCheckFirmwareHistoryForExistingVersion() {
    // Arrange
    final FirmwareVersion firmwareVersion1 =
        new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2);
    final FirmwareVersion firmwareVersion2 =
        new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_2);
    final List<FirmwareVersion> versionsOnDevice =
        Arrays.asList(firmwareVersion1, firmwareVersion2);

    // Act
    final List<FirmwareVersion> versionsNotInHistory =
        this.firmwareManagementService.checkFirmwareHistoryForVersion("DVC", versionsOnDevice);

    // Validate
    assertThat(versionsNotInHistory).withFailMessage("List should be empty").isEmpty();
  }

  @Test
  void testCheckFirmwareHistoryForNonExistingVersion() {

    // Arrange
    final FirmwareVersion firmwareVersion1 =
        new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2);
    final FirmwareVersion firmwareVersion2 =
        new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_3);
    final List<FirmwareVersion> versionsOnDevice =
        Arrays.asList(firmwareVersion1, firmwareVersion2);

    final List<FirmwareVersion> expected = Collections.singletonList(firmwareVersion2);

    // Act
    final List<FirmwareVersion> versionsNotInHistory =
        this.firmwareManagementService.checkFirmwareHistoryForVersion("DVC", versionsOnDevice);

    // Assert
    assertThat(versionsNotInHistory).withFailMessage("Lists should be equal").isEqualTo(expected);
  }

  @Test
  void testTryToAddFirmwareVersionToHistoryWhenFileIsAvailable() {

    // Arrange
    final FirmwareFile firmwareFile =
        new FirmwareFile.Builder()
            .withFilename("filename")
            .withDescription("description")
            .withPushToNewDevices(false)
            .build();
    final FirmwareModule firmwareModule =
        new FirmwareModule(FirmwareModuleType.SECURITY.getDescription().toLowerCase());
    firmwareFile.addFirmwareModule(firmwareModule, VERSION_2);
    when(this.firmwareFileRepository.findByDeviceModel(any(DeviceModel.class)))
        .thenReturn(Collections.singletonList(firmwareFile));
    final List<FirmwareVersion> firmwareVersions =
        Collections.singletonList(new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2));

    // Act
    this.firmwareManagementService.tryToAddDeviceFirmwareFile("DVC", firmwareVersions);

    // Assert
    verify(this.deviceFirmwareFileRepository, times(1)).save(any(DeviceFirmwareFile.class));
  }

  @Test
  void testTryToAddFirmwareVersionToHistoryWhenFileIsNotAvailable() {

    // Arrange
    final FirmwareFile firmwareFile =
        new FirmwareFile.Builder()
            .withFilename("filename")
            .withDescription("description")
            .withPushToNewDevices(false)
            .build();
    when(this.firmwareFileRepository.findByDeviceModel(any(DeviceModel.class)))
        .thenReturn(Collections.singletonList(firmwareFile));
    final List<FirmwareVersion> firmwareVersions =
        Collections.singletonList(new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2));

    // Act
    this.firmwareManagementService.tryToAddDeviceFirmwareFile("DVC", firmwareVersions);

    // Assert
    verify(this.deviceFirmwareFileRepository, never()).save(any(DeviceFirmwareFile.class));
  }

  @Test
  void handlesZeroSsldPendingFirmwareUpdatesDoingNothing() {
    final String deviceIdentification = "Test-SSLD-1";
    when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(deviceIdentification))
        .thenReturn(Collections.emptyList());

    this.firmwareManagementService.handleSsldPendingFirmwareUpdate(deviceIdentification);

    verify(this.ssldPendingFirmwareUpdateRepository)
        .findByDeviceIdentification(deviceIdentification);
    verifyNoMoreInteractions(this.ssldPendingFirmwareUpdateRepository);
    verifyNoInteractions(this.osgpCoreRequestMessageSender);
  }

  @Test
  void handlesOneSsldPendingFirmwareUpdateRetrievingFirmwareVersion() throws Exception {
    final String deviceIdentification = "Test-SSLD-1";
    final Ssld ssld = new Ssld(deviceIdentification);
    final String correlationUid = "correlation-uid-pending-firmware-update";
    final SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate =
        this.anSsldPendingFirmwareUpdate(1L, new Date(), deviceIdentification, correlationUid);
    final Organisation organisation =
        new Organisation(
            ssldPendingFirmwareUpdate.getOrganisationIdentification(),
            "Organisation",
            "ORG",
            PlatformFunctionGroup.USER);
    when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(deviceIdentification))
        .thenReturn(Collections.singletonList(ssldPendingFirmwareUpdate));
    when(this.deviceDomainService.searchActiveDevice(
            eq(deviceIdentification), any(ComponentType.class)))
        .thenReturn(ssld);
    when(this.organisationDomainService.searchOrganisation(
            organisation.getOrganisationIdentification()))
        .thenReturn(organisation);

    this.firmwareManagementService.handleSsldPendingFirmwareUpdate(deviceIdentification);

    verify(this.ssldPendingFirmwareUpdateRepository, never()).delete(ssldPendingFirmwareUpdate);

    /*
     * Verify the firmware version request is made for the device with the
     * SsldPendingFirmwareUpdate and that it uses the correlation UID from
     * SsldPendingFirmwareUpdate, as this is important for the way the
     * firmware version response will be treated later-on in a more complete
     * firmware update scenario than the fragment seen here in this unit
     * test.
     */
    this.assertFirmwareVersionRequested(
        organisation.getOrganisationIdentification(), deviceIdentification, correlationUid);
  }

  @Test
  void handlesMultipleSsldPendingFirmwareUpdatesWithoutFailure() throws Exception {
    final String deviceIdentification = "Test-SSLD-1";
    final Ssld ssld = new Ssld(deviceIdentification);
    final String correlationUidMostRecentPendingFirmwareUpdate = "correlation-uid-most-recent";
    final long mostRecentCreationMillis = System.currentTimeMillis();
    final SsldPendingFirmwareUpdate olderPendingFirmwareUpdate1 =
        this.anSsldPendingFirmwareUpdate(
            134562345L,
            new Date(mostRecentCreationMillis - 3_000_000_000L),
            deviceIdentification,
            "correlation-uid-1");
    final SsldPendingFirmwareUpdate olderPendingFirmwareUpdate2 =
        this.anSsldPendingFirmwareUpdate(
            227587L,
            new Date(mostRecentCreationMillis - 604_800_000L),
            deviceIdentification,
            "correlation-uid-2");
    final SsldPendingFirmwareUpdate olderPendingFirmwareUpdate3 =
        this.anSsldPendingFirmwareUpdate(
            308943152L,
            new Date(mostRecentCreationMillis - 123L),
            deviceIdentification,
            "correlation-uid-3");
    final SsldPendingFirmwareUpdate mostRecentPendingFirmwareUpdate =
        this.anSsldPendingFirmwareUpdate(
            4459483L,
            new Date(mostRecentCreationMillis),
            deviceIdentification,
            correlationUidMostRecentPendingFirmwareUpdate);
    final Organisation organisation =
        new Organisation(
            mostRecentPendingFirmwareUpdate.getOrganisationIdentification(),
            "Organisation",
            "ORG",
            PlatformFunctionGroup.USER);
    when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(deviceIdentification))
        .thenReturn(
            Arrays.asList(
                olderPendingFirmwareUpdate1,
                olderPendingFirmwareUpdate2,
                mostRecentPendingFirmwareUpdate,
                olderPendingFirmwareUpdate3));
    when(this.deviceDomainService.searchActiveDevice(
            eq(deviceIdentification), any(ComponentType.class)))
        .thenReturn(ssld);
    when(this.organisationDomainService.searchOrganisation(
            organisation.getOrganisationIdentification()))
        .thenReturn(organisation);

    this.firmwareManagementService.handleSsldPendingFirmwareUpdate(deviceIdentification);

    /*
     * Verify the older pending firmware updates are deleted. This appears
     * to be a reasonable way to deal with multiple records being present.
     * The most recent pending update should not be deleted at this point,
     * as it is important for the way the firmware version response will be
     * treated later-on in a more complete firmware update scenario than the
     * fragment seen here in this unit test.
     *
     * The check is here to confirm the code works as it was meant to be
     * implemented. Not so much as a definitive specification as how it
     * should work.
     */
    final ArgumentCaptor<SsldPendingFirmwareUpdate> pendingUpdateCaptor =
        ArgumentCaptor.forClass(SsldPendingFirmwareUpdate.class);
    verify(this.ssldPendingFirmwareUpdateRepository, atLeastOnce())
        .delete(pendingUpdateCaptor.capture());
    final List<SsldPendingFirmwareUpdate> deletedPendingUpdates =
        pendingUpdateCaptor.getAllValues();
    assertThat(deletedPendingUpdates)
        .containsExactlyInAnyOrder(
            olderPendingFirmwareUpdate1, olderPendingFirmwareUpdate2, olderPendingFirmwareUpdate3);

    /*
     * Check that a get firmware version message is sent for the most recent
     * pending firmware update.
     */
    this.assertFirmwareVersionRequested(
        organisation.getOrganisationIdentification(),
        deviceIdentification,
        correlationUidMostRecentPendingFirmwareUpdate);
  }

  private void assertFirmwareVersionRequested(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid) {

    final ArgumentCaptor<RequestMessage> requestMessageCaptor =
        ArgumentCaptor.forClass(RequestMessage.class);
    verify(this.osgpCoreRequestMessageSender)
        .sendWithDelay(
            requestMessageCaptor.capture(),
            eq(MessageType.GET_FIRMWARE_VERSION.name()),
            anyInt(),
            any(),
            any());

    final RequestMessage actualRequestMessage = requestMessageCaptor.getValue();
    assertThat(actualRequestMessage.getCorrelationUid()).isEqualTo(correlationUid);
    assertThat(actualRequestMessage.getDeviceIdentification()).isEqualTo(deviceIdentification);
    assertThat(actualRequestMessage.getOrganisationIdentification())
        .isEqualTo(organisationIdentification);
  }

  private SsldPendingFirmwareUpdate anSsldPendingFirmwareUpdate(
      final Long id,
      final Date creationTime,
      final String deviceIdentification,
      final String correlationUid) {

    final FirmwareModuleType firmwareModuleType = FirmwareModuleType.FUNCTIONAL;
    final String firmwareVersion = "test-version";
    final SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate =
        new SsldPendingFirmwareUpdate(
            deviceIdentification,
            firmwareModuleType,
            firmwareVersion,
            ORGANISATION_IDENTIFICATION,
            correlationUid);
    ReflectionTestUtils.setField(ssldPendingFirmwareUpdate, "id", id, Long.class);
    ReflectionTestUtils.setField(
        ssldPendingFirmwareUpdate, "creationTime", creationTime, Date.class);
    return ssldPendingFirmwareUpdate;
  }

  @Test
  void checkSsldPendingFirmwareUpdateReturnsFalseIfThereAreNoPendingUpdates() {
    final String correlationUid = "correlation-uid-no-pending-updates";
    final CorrelationIds ids =
        new CorrelationIds(ORGANISATION_IDENTIFICATION, DEVICE_IDENTIFICATION, correlationUid);
    final List<FirmwareVersion> firmwareVersions =
        Collections.singletonList(new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_3));
    when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(Collections.emptyList());

    final boolean hasPendingFirmwareUpdate =
        this.firmwareManagementService.checkSsldPendingFirmwareUpdate(ids, firmwareVersions);

    assertThat(hasPendingFirmwareUpdate).isFalse();

    verify(this.ssldPendingFirmwareUpdateRepository)
        .findByDeviceIdentification(DEVICE_IDENTIFICATION);
    verifyNoMoreInteractions(this.ssldPendingFirmwareUpdateRepository);
  }

  @Test
  void checkSsldPendingFirmwareUpdateReturnsFalseIfPendingUpdatesAreForDifferentCorrelationUids() {
    final String correlationUid = "correlation-uid-not-with-pending-updates";
    final CorrelationIds ids =
        new CorrelationIds(ORGANISATION_IDENTIFICATION, DEVICE_IDENTIFICATION, correlationUid);
    final List<FirmwareVersion> firmwareVersions =
        Arrays.asList(
            new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2),
            new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_1));
    when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(
            Collections.singletonList(
                this.anSsldPendingFirmwareUpdate(
                    4579L, new Date(), DEVICE_IDENTIFICATION, "some-other-correlation-uid")));

    final boolean hasPendingFirmwareUpdate =
        this.firmwareManagementService.checkSsldPendingFirmwareUpdate(ids, firmwareVersions);

    assertThat(hasPendingFirmwareUpdate).isFalse();

    verify(this.ssldPendingFirmwareUpdateRepository)
        .findByDeviceIdentification(DEVICE_IDENTIFICATION);
    verifyNoMoreInteractions(this.ssldPendingFirmwareUpdateRepository);
  }

  @Test
  void
      checkSsldPendingFirmwareUpdateReturnsTrueAndDeletesPendingUpdateWithMatchingCorrelationUid() {
    final String correlationUid = "correlation-uid-matching-pending-update";
    final CorrelationIds ids =
        new CorrelationIds(ORGANISATION_IDENTIFICATION, DEVICE_IDENTIFICATION, correlationUid);
    final List<FirmwareVersion> firmwareVersions =
        Collections.singletonList(new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_2));
    final SsldPendingFirmwareUpdate matchingPendingFirmwareUpdate =
        this.anSsldPendingFirmwareUpdate(437L, new Date(), DEVICE_IDENTIFICATION, correlationUid);
    when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(
            Arrays.asList(
                this.anSsldPendingFirmwareUpdate(
                    457198L, new Date(), DEVICE_IDENTIFICATION, "some-other-correlation-uid"),
                matchingPendingFirmwareUpdate,
                this.anSsldPendingFirmwareUpdate(
                    94085089L, new Date(), DEVICE_IDENTIFICATION, "yet-another-correlation-uid")));

    final boolean hasPendingFirmwareUpdate =
        this.firmwareManagementService.checkSsldPendingFirmwareUpdate(ids, firmwareVersions);

    assertThat(hasPendingFirmwareUpdate).isTrue();

    verify(this.ssldPendingFirmwareUpdateRepository).delete(matchingPendingFirmwareUpdate);
  }

  /*
   * Returns device of provided class with already mocked IP address
   */
  private <T> Device getMockDevice(final Class<T> deviceClass) {
    final Device device = (Device) Mockito.mock(deviceClass);
    when(device.getIpAddress()).thenReturn("0.0.0.0");
    return device;
  }

  @Test
  void testUpdateFirmwareForNonSsld() throws FunctionalException {
    final Device device = this.getMockDevice(Device.class);

    when(this.firmwareUpdateMessageDataContainer.getFirmwareUrl()).thenReturn("/firmware-test");
    when(this.deviceDomainService.searchActiveDevice(
            CORRELATION_IDS.getDeviceIdentification(), ComponentType.DOMAIN_CORE))
        .thenReturn(device);

    this.firmwareManagementService.updateFirmware(
        CORRELATION_IDS, this.firmwareUpdateMessageDataContainer, 0L, "", 0);

    verify(this.osgpCoreRequestMessageSender)
        .sendWithScheduledTime(
            this.requestMessageCaptor.capture(),
            this.messageTypeCaptor.capture(),
            this.messagePriorityCaptor.capture(),
            this.ipAddressCaptor.capture(),
            this.scheduledTimeCaptor.capture());

    final RequestMessage requestMessage = this.requestMessageCaptor.getValue();
    final RequestMessage expectedRequestMessage =
        new RequestMessage(
            CORRELATION_UID, ORGANISATION_IDENTIFICATION, DEVICE_IDENTIFICATION, null);

    assertThat(requestMessage)
        .usingRecursiveComparison()
        .ignoringFields("request")
        .isEqualTo(expectedRequestMessage);
  }

  @Test
  void testUpdateFirmwareForSsld() throws FunctionalException {
    final Device device = this.getMockDevice(Ssld.class);
    final FirmwareFile firmwareFile =
        new FirmwareFile.Builder().withFilename("firmware-test").build();
    firmwareFile.addFirmwareModule(new FirmwareModule("functional"), VERSION_1);

    when(this.firmwareUpdateMessageDataContainer.getFirmwareUrl()).thenReturn("/firmware-test");
    when(this.deviceDomainService.searchActiveDevice(
            CORRELATION_IDS.getDeviceIdentification(), ComponentType.DOMAIN_CORE))
        .thenReturn(device);
    when(this.firmwareFileRepository.findByFilename("firmware-test"))
        .thenReturn(Collections.singletonList(firmwareFile));

    this.firmwareManagementService.updateFirmware(
        CORRELATION_IDS, this.firmwareUpdateMessageDataContainer, 0L, "", 0);

    verify(this.ssldPendingFirmwareUpdateRepository)
        .save(this.ssldPendingFirmwareUpdateArgumentCaptor.capture());

    final SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate =
        this.ssldPendingFirmwareUpdateArgumentCaptor.getValue();
    final SsldPendingFirmwareUpdate expectedSsldPendingFirmwareUpdate =
        new SsldPendingFirmwareUpdate(
            DEVICE_IDENTIFICATION,
            FirmwareModuleType.FUNCTIONAL,
            VERSION_1,
            ORGANISATION_IDENTIFICATION,
            CORRELATION_UID);

    assertThat(ssldPendingFirmwareUpdate).isEqualTo(expectedSsldPendingFirmwareUpdate);
  }

  @Test
  void testUpdateFirmwareWithNoFirmwareFiles() throws FunctionalException {
    final Device device = this.getMockDevice(Ssld.class);

    when(this.firmwareUpdateMessageDataContainer.getFirmwareUrl()).thenReturn("/firmware-test");
    when(this.deviceDomainService.searchActiveDevice(any(), eq(ComponentType.DOMAIN_CORE)))
        .thenReturn(device);
    when(this.firmwareFileRepository.findByFilename("firmware-test"))
        .thenReturn(Collections.emptyList());

    this.firmwareManagementService.updateFirmware(
        CORRELATION_IDS, this.firmwareUpdateMessageDataContainer, 0L, "", 0);

    verifyNoInteractions(this.ssldPendingFirmwareUpdateRepository);
  }

  @Test
  void testUpdateFirmwareWithNoFirmwareModuleVersions() throws FunctionalException {
    final Device device = this.getMockDevice(Ssld.class);
    final FirmwareFile firmwareFile = Mockito.mock(FirmwareFile.class);
    firmwareFile.addFirmwareModule(new FirmwareModule("functional"), VERSION_1);

    when(this.firmwareUpdateMessageDataContainer.getFirmwareUrl()).thenReturn("/firmware-test");
    when(this.deviceDomainService.searchActiveDevice(any(), eq(ComponentType.DOMAIN_CORE)))
        .thenReturn(device);
    when(this.firmwareFileRepository.findByFilename("firmware-test"))
        .thenReturn(Collections.singletonList(firmwareFile));
    when(firmwareFile.getModuleVersions()).thenReturn(new HashMap<>());

    this.firmwareManagementService.updateFirmware(
        CORRELATION_IDS, this.firmwareUpdateMessageDataContainer, 0L, "", 0);

    verifyNoInteractions(this.ssldPendingFirmwareUpdateRepository);
  }

  @Test
  void testUpdateFirmwareWithIncorrectFirmwareUrl() throws FunctionalException {
    final Device device = this.getMockDevice(Ssld.class);
    final FirmwareFile firmwareFile = Mockito.mock(FirmwareFile.class);
    firmwareFile.addFirmwareModule(new FirmwareModule("functional"), VERSION_1);

    when(this.firmwareUpdateMessageDataContainer.getFirmwareUrl()).thenReturn("/");
    when(this.deviceDomainService.searchActiveDevice(any(), eq(ComponentType.DOMAIN_CORE)))
        .thenReturn(device);

    this.firmwareManagementService.updateFirmware(
        CORRELATION_IDS, this.firmwareUpdateMessageDataContainer, 0L, "", 0);

    verifyNoInteractions(this.ssldPendingFirmwareUpdateRepository);
  }

  @Test
  void testHandleGetFirmwareVersionWithMatchingFirmwareVersion() {
    final List<FirmwareVersionDto> firmwareVersionDtos = Collections.emptyList();

    final SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate =
        Mockito.mock(SsldPendingFirmwareUpdate.class);
    final List<SsldPendingFirmwareUpdate> ssldPendingFirmwareUpdates =
        Collections.singletonList(ssldPendingFirmwareUpdate);

    when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(any(String.class)))
        .thenReturn(ssldPendingFirmwareUpdates);
    when(ssldPendingFirmwareUpdate.getCorrelationUid())
        .thenReturn(CORRELATION_IDS.getCorrelationUid());
    when(ssldPendingFirmwareUpdate.getFirmwareModuleType()).thenReturn(FirmwareModuleType.SECURITY);
    when(ssldPendingFirmwareUpdate.getFirmwareVersion()).thenReturn(VERSION_1);
    when(this.domainCoreMapper.mapAsList(firmwareVersionDtos, FirmwareVersion.class))
        .thenReturn(
            Collections.singletonList(new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_1)));

    this.firmwareManagementService.handleGetFirmwareVersionResponse(
        firmwareVersionDtos, CORRELATION_IDS, "messageType", 1, ResponseMessageResultType.OK, null);

    verifyNoInteractions(this.webServiceResponseMessageSender);
    verify(this.ssldPendingFirmwareUpdateRepository).delete(any());
  }

  @Test
  void testHandleGetFirmwareVersionResponseNotOk() {
    final List<FirmwareVersionDto> versionsOnDevice = new ArrayList<>();

    this.firmwareManagementService.handleGetFirmwareVersionResponse(
        versionsOnDevice,
        CORRELATION_IDS,
        "messageType",
        1,
        ResponseMessageResultType.NOT_OK,
        null);

    verify(this.webServiceResponseMessageSender).send(this.responseMessageCaptor.capture());

    final ResponseMessage responseMessage = this.responseMessageCaptor.getValue();

    assertThat(responseMessage.getResult()).isEqualTo(ResponseMessageResultType.NOT_OK);
    assertThat(responseMessage.getOsgpException().getMessage())
        .isEqualTo("Exception occurred while getting device firmware version");
  }

  @Test
  void testHandleGetFirmwareVersionErrorNotNull() {
    final List<FirmwareVersionDto> versionsOnDevice = new ArrayList<>();

    this.firmwareManagementService.handleGetFirmwareVersionResponse(
        versionsOnDevice,
        CORRELATION_IDS,
        "messageType",
        1,
        ResponseMessageResultType.OK,
        DEFAULT_EXCEPTION);

    verify(this.webServiceResponseMessageSender).send(this.responseMessageCaptor.capture());
    verify(this.ssldPendingFirmwareUpdateRepository, never()).delete(any());

    final ResponseMessage responseMessage = this.responseMessageCaptor.getValue();
    final ResponseMessage expectedResponseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(CORRELATION_IDS)
            .withResult(ResponseMessageResultType.NOT_OK)
            .withOsgpException(
                new TechnicalException("Exception occurred while getting device firmware version"))
            .withMessagePriority(1)
            .withMessageType(MessageType.GET_FIRMWARE_VERSION.name())
            .build();

    assertThat(responseMessage)
        .usingRecursiveComparison()
        .ignoringFields("dataObject")
        .isEqualTo(expectedResponseMessage);
  }

  @Test
  void testHandleGetFirmwareVersionWithPendingUpdateIsNull() {
    final List<FirmwareVersionDto> versionsOnDevice = new ArrayList<>();

    when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(any()))
        .thenReturn(null);

    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () -> {
              this.firmwareManagementService.handleGetFirmwareVersionResponse(
                  versionsOnDevice,
                  CORRELATION_IDS,
                  "messageType",
                  1,
                  ResponseMessageResultType.OK,
                  null);
            });
  }

  @Test
  void testHandleGetFirmwareVersionWithNonMatchingCorrelationUid() {
    final List<FirmwareVersionDto> versionsOnDevice = new ArrayList<>();
    final SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate =
        Mockito.mock(SsldPendingFirmwareUpdate.class);
    final List<SsldPendingFirmwareUpdate> ssldPendingFirmwareUpdates =
        Collections.singletonList(ssldPendingFirmwareUpdate);

    when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(any()))
        .thenReturn(ssldPendingFirmwareUpdates);
    when(ssldPendingFirmwareUpdate.getCorrelationUid()).thenReturn("differentUid");

    this.firmwareManagementService.handleGetFirmwareVersionResponse(
        versionsOnDevice, CORRELATION_IDS, "messageType", 1, ResponseMessageResultType.OK, null);

    verify(this.webServiceResponseMessageSender).send(this.responseMessageCaptor.capture());

    final ResponseMessage responseMessage = this.responseMessageCaptor.getValue();
    final ResponseMessage expectedResponseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(CORRELATION_IDS)
            .withResult(ResponseMessageResultType.OK)
            .withMessagePriority(1)
            .withMessageType(MessageType.GET_FIRMWARE_VERSION.name())
            .build();

    assertThat(responseMessage)
        .usingRecursiveComparison()
        .ignoringFields("dataObject")
        .isEqualTo(expectedResponseMessage);
  }
}
