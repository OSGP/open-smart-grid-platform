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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.core.OsgpCoreRequestMessageSender;
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
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunctionGroup;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FirmwareManagementServiceTest {

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
    private SsldPendingFirmwareUpdateRepository ssldPendingFirmwareUpdateRepository;

    @Mock
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Mock
    private DeviceDomainService deviceDomainService;

    @Mock
    private OrganisationDomainService organisationDomainService;

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
    void setUp() throws FunctionalException {
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
    void testCheckFirmwareHistoryForExistingVersion() throws FunctionalException {

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
    void testCheckFirmwareHistoryForNonExistingVersion() throws FunctionalException {

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
    void testTryToAddFirmwareVersionToHistoryWhenFileIsAvailable() throws FunctionalException {

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
        final FirmwareVersion firmwareVersion = new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2);

        // Act
        this.firmwareManagementService.tryToAddFirmwareVersionToHistory("", firmwareVersion);

        // Assert
        verify(this.deviceFirmwareFileRepository, times(1)).save(any(DeviceFirmwareFile.class));
    }

    @Test
    void testTryToAddFirmwareVersionToHistoryWhenFileIsNotAvailable() throws FunctionalException {

        // Arrange
        final FirmwareFile firmwareFile = new FirmwareFile.Builder().withFilename("filename")
                .withDescription("description")
                .withPushToNewDevices(false)
                .build();
        when(this.firmwareFileRepository.findByDeviceModel(any(DeviceModel.class)))
                .thenReturn(Arrays.asList(firmwareFile));
        final FirmwareVersion firmwareVersion1 = new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2);

        // Act
        this.firmwareManagementService.tryToAddFirmwareVersionToHistory("", firmwareVersion1);

        // Assert
        verify(this.deviceFirmwareFileRepository, never()).save(any(DeviceFirmwareFile.class));
    }

    @Test
    void handlesZeroSsldPendingFirmwareUpdatesDoingNothing() {
        final String deviceIdentification = "Test-SSLD-1";
        when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(deviceIdentification))
                .thenReturn(Collections.emptyList());

        this.firmwareManagementService.handleSsldPendingFirmwareUpdate(deviceIdentification);

        verify(this.ssldPendingFirmwareUpdateRepository).findByDeviceIdentification(deviceIdentification);
        verifyNoMoreInteractions(this.ssldPendingFirmwareUpdateRepository);
        verifyNoInteractions(this.osgpCoreRequestMessageSender);
    }

    @Test
    void handlesOneSsldPendingFirmwareUpdateRetrievingFirmwareVersion() throws Exception {
        final String deviceIdentification = "Test-SSLD-1";
        final Ssld ssld = new Ssld(deviceIdentification);
        final String correlationUid = "correlation-uid-pending-firmware-update";
        final SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate = this.anSsldPendingFirmwareUpdate(1L, new Date(),
                deviceIdentification, correlationUid);
        final Organisation organisation = new Organisation(ssldPendingFirmwareUpdate.getOrganisationIdentification(),
                "Organisation", "ORG", PlatformFunctionGroup.USER);
        when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(deviceIdentification))
                .thenReturn(Collections.singletonList(ssldPendingFirmwareUpdate));
        when(this.deviceDomainService.searchActiveDevice(eq(deviceIdentification), any(ComponentType.class)))
                .thenReturn(ssld);
        when(this.organisationDomainService.searchOrganisation(organisation.getOrganisationIdentification()))
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
        this.assertFirmwareVersionRequested(organisation.getOrganisationIdentification(), deviceIdentification,
                correlationUid);
    }

    @Test
    void handlesMultipleSsldPendingFirmwareUpdatesWithoutFailure() throws Exception {
        final String deviceIdentification = "Test-SSLD-1";
        final Ssld ssld = new Ssld(deviceIdentification);
        final String correlationUidMostRecentPendingFirmwareUpdate = "correlation-uid-most-recent";
        final long mostRecentCreationMillis = System.currentTimeMillis();
        final SsldPendingFirmwareUpdate olderPendingFirmwareUpdate1 = this.anSsldPendingFirmwareUpdate(134562345L,
                new Date(mostRecentCreationMillis - 3_000_000_000L), deviceIdentification, "correlation-uid-1");
        final SsldPendingFirmwareUpdate olderPendingFirmwareUpdate2 = this.anSsldPendingFirmwareUpdate(227587L,
                new Date(mostRecentCreationMillis - 604_800_000L), deviceIdentification, "correlation-uid-2");
        final SsldPendingFirmwareUpdate olderPendingFirmwareUpdate3 = this.anSsldPendingFirmwareUpdate(308943152L,
                new Date(mostRecentCreationMillis - 123L), deviceIdentification, "correlation-uid-3");
        final SsldPendingFirmwareUpdate mostRecentPendingFirmwareUpdate = this.anSsldPendingFirmwareUpdate(4459483L,
                new Date(mostRecentCreationMillis), deviceIdentification,
                correlationUidMostRecentPendingFirmwareUpdate);
        final Organisation organisation = new Organisation(
                mostRecentPendingFirmwareUpdate.getOrganisationIdentification(), "Organisation", "ORG",
                PlatformFunctionGroup.USER);
        when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(deviceIdentification))
                .thenReturn(Arrays.asList(olderPendingFirmwareUpdate1, olderPendingFirmwareUpdate2,
                        mostRecentPendingFirmwareUpdate, olderPendingFirmwareUpdate3));
        when(this.deviceDomainService.searchActiveDevice(eq(deviceIdentification), any(ComponentType.class)))
                .thenReturn(ssld);
        when(this.organisationDomainService.searchOrganisation(organisation.getOrganisationIdentification()))
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
        final ArgumentCaptor<SsldPendingFirmwareUpdate> pendingUpdateCaptor = ArgumentCaptor
                .forClass(SsldPendingFirmwareUpdate.class);
        verify(this.ssldPendingFirmwareUpdateRepository, atLeastOnce()).delete(pendingUpdateCaptor.capture());
        final List<SsldPendingFirmwareUpdate> deletedPendingUpdates = pendingUpdateCaptor.getAllValues();
        assertThat(deletedPendingUpdates).containsExactlyInAnyOrder(olderPendingFirmwareUpdate1,
                olderPendingFirmwareUpdate2, olderPendingFirmwareUpdate3);

        /*
         * Check that a get firmware version message is sent for the most recent
         * pending firmware update.
         */
        this.assertFirmwareVersionRequested(organisation.getOrganisationIdentification(), deviceIdentification,
                correlationUidMostRecentPendingFirmwareUpdate);
    }

    private void assertFirmwareVersionRequested(final String organisationIdentification,
            final String deviceIdentification, final String correlationUid) {

        final ArgumentCaptor<RequestMessage> requestMessageCaptor = ArgumentCaptor.forClass(RequestMessage.class);
        verify(this.osgpCoreRequestMessageSender).sendWithDelay(requestMessageCaptor.capture(),
                eq(MessageType.GET_FIRMWARE_VERSION.name()), anyInt(), any(), any());

        final RequestMessage actualRequestMessage = requestMessageCaptor.getValue();
        assertThat(actualRequestMessage.getCorrelationUid()).isEqualTo(correlationUid);
        assertThat(actualRequestMessage.getDeviceIdentification()).isEqualTo(deviceIdentification);
        assertThat(actualRequestMessage.getOrganisationIdentification()).isEqualTo(organisationIdentification);
    }

    private SsldPendingFirmwareUpdate anSsldPendingFirmwareUpdate(final Long id, final Date creationTime,
            final String deviceIdentification, final String correlationUid) {

        final FirmwareModuleType firmwareModuleType = FirmwareModuleType.FUNCTIONAL;
        final String firmwareVersion = "test-version";
        final String organisationIdentification = "test-org";
        final SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate = new SsldPendingFirmwareUpdate(deviceIdentification,
                firmwareModuleType, firmwareVersion, organisationIdentification, correlationUid);
        ReflectionTestUtils.setField(ssldPendingFirmwareUpdate, "id", id, Long.class);
        ReflectionTestUtils.setField(ssldPendingFirmwareUpdate, "creationTime", creationTime, Date.class);
        return ssldPendingFirmwareUpdate;
    }

    @Test
    void checkSsldPendingFirmwareUpdateReturnsFalseIfThereAreNoPendingUpdates() {
        final String organisationIdentification = "test-org";
        final String deviceIdentification = "device-identification";
        final String correlationUid = "correlation-uid-no-pending-updates";
        final CorrelationIds ids = new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
        final List<FirmwareVersion> firmwareVersions = Collections
                .singletonList(new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_3));
        when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(deviceIdentification))
                .thenReturn(Collections.emptyList());

        final boolean hasPendingFirmwareUpdate = this.firmwareManagementService.checkSsldPendingFirmwareUpdate(ids,
                firmwareVersions);

        assertThat(hasPendingFirmwareUpdate).isFalse();

        verify(this.ssldPendingFirmwareUpdateRepository).findByDeviceIdentification(deviceIdentification);
        verifyNoMoreInteractions(this.ssldPendingFirmwareUpdateRepository);
    }

    @Test
    void checkSsldPendingFirmwareUpdateReturnsFalseIfPendingUpdatesAreForDifferentCorrelationUids() {
        final String organisationIdentification = "test-org";
        final String deviceIdentification = "device-identification";
        final String correlationUid = "correlation-uid-not-with-pending-updates";
        final CorrelationIds ids = new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
        final List<FirmwareVersion> firmwareVersions = Arrays.asList(
                new FirmwareVersion(FirmwareModuleType.SECURITY, VERSION_2),
                new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_1));
        when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(deviceIdentification))
                .thenReturn(Collections.singletonList(this.anSsldPendingFirmwareUpdate(4579L, new Date(),
                        deviceIdentification, "some-other-correlation-uid")));

        final boolean hasPendingFirmwareUpdate = this.firmwareManagementService.checkSsldPendingFirmwareUpdate(ids,
                firmwareVersions);

        assertThat(hasPendingFirmwareUpdate).isFalse();

        verify(this.ssldPendingFirmwareUpdateRepository).findByDeviceIdentification(deviceIdentification);
        verifyNoMoreInteractions(this.ssldPendingFirmwareUpdateRepository);
    }

    @Test
    void checkSsldPendingFirmwareUpdateReturnsTrueAndDeletesPendingUpdateWithMatchingCorrelationUid() {
        final String organisationIdentification = "test-org";
        final String deviceIdentification = "device-identification";
        final String correlationUid = "correlation-uid-matching-pending-update";
        final CorrelationIds ids = new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
        final List<FirmwareVersion> firmwareVersions = Collections
                .singletonList(new FirmwareVersion(FirmwareModuleType.FUNCTIONAL, VERSION_2));
        final SsldPendingFirmwareUpdate matchingPendingFirmwareUpdate = this.anSsldPendingFirmwareUpdate(437L,
                new Date(), deviceIdentification, correlationUid);
        when(this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(deviceIdentification))
                .thenReturn(Arrays.asList(
                        this.anSsldPendingFirmwareUpdate(457198L, new Date(), deviceIdentification,
                                "some-other-correlation-uid"),
                        matchingPendingFirmwareUpdate, this.anSsldPendingFirmwareUpdate(94085089L, new Date(),
                                deviceIdentification, "yet-another-correlation-uid")));

        final boolean hasPendingFirmwareUpdate = this.firmwareManagementService.checkSsldPendingFirmwareUpdate(ids,
                firmwareVersions);

        assertThat(hasPendingFirmwareUpdate).isTrue();

        verify(this.ssldPendingFirmwareUpdateRepository).delete(matchingPendingFirmwareUpdate);
    }
}
