/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.basicfunctions;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.OperationNotSupportedException;
import javax.transaction.NotSupportedException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.codec.binary.Base64;
import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.jboss.netty.channel.Channel;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.alliander.osgp.acceptancetests.OslpTestUtils;
import com.alliander.osgp.acceptancetests.ProtocolInfoTestUtils;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDeviceBuilder;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerClient;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpDeviceService;
import com.alliander.osgp.adapter.ws.core.application.mapping.AdHocManagementMapper;
import com.alliander.osgp.adapter.ws.core.application.mapping.ConfigurationManagementMapper;
import com.alliander.osgp.adapter.ws.core.application.mapping.DeviceInstallationMapper;
import com.alliander.osgp.adapter.ws.core.application.mapping.DeviceManagementMapper;
import com.alliander.osgp.adapter.ws.core.endpoints.AdHocManagementEndpoint;
import com.alliander.osgp.adapter.ws.core.endpoints.ConfigurationManagementEndpoint;
import com.alliander.osgp.adapter.ws.core.endpoints.DeviceInstallationEndpoint;
import com.alliander.osgp.adapter.ws.core.endpoints.DeviceManagementEndpoint;
import com.alliander.osgp.adapter.ws.core.endpoints.FirmwareManagementEndpoint;
import com.alliander.osgp.adapter.ws.publiclighting.application.mapping.DeviceMonitoringMapper;
import com.alliander.osgp.adapter.ws.publiclighting.application.mapping.ScheduleManagementMapper;
import com.alliander.osgp.adapter.ws.publiclighting.endpoints.DeviceMonitoringEndpoint;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeviceAuthorisation;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDeviceAuthorisationsRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateDeviceAuthorisationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.adhocmanagement.SetRebootRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.GetConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.SetConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StopDeviceTestRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindEventsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetLightRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.TransitionType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetActualPowerUsageRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.HistoryTermType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.TimePeriod;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.ActionTimeType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.Schedule;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.WeekDayType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.WindowType;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.TariffSchedule;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.TariffValue;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceAuthorizationBuilder;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.OrganisationBuilder;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;
import com.alliander.osgp.domain.core.exceptions.NotAuthorizedException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.OslpLogItemRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.oslp.Oslp.LightType;
import com.alliander.osgp.oslp.Oslp.LightValue;
import com.alliander.osgp.oslp.Oslp.LinkType;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.MeterType;
import com.alliander.osgp.oslp.Oslp.PowerUsageData;
import com.alliander.osgp.oslp.Oslp.Status;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.google.protobuf.ByteString;

@Configurable
@DomainSteps()
public class VerifyAuthorizeDeviceFunctionSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyAuthorizeDeviceFunctionSteps.class);

    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapter fields
    private com.alliander.osgp.adapter.ws.admin.endpoints.DeviceManagementEndpoint adminDeviceManagementEndpoint;

    private com.alliander.osgp.adapter.ws.core.endpoints.AdHocManagementEndpoint coreAdHocManagementEndpoint;
    private com.alliander.osgp.adapter.ws.core.endpoints.ConfigurationManagementEndpoint coreConfigurationManagementEndpoint;
    private com.alliander.osgp.adapter.ws.core.endpoints.DeviceInstallationEndpoint coreDeviceInstallationEndpoint;
    private com.alliander.osgp.adapter.ws.core.endpoints.DeviceManagementEndpoint coreDeviceManagementEndpoint;
    private com.alliander.osgp.adapter.ws.core.endpoints.FirmwareManagementEndpoint coreFirmwareManagementEndpoint;

    private com.alliander.osgp.adapter.ws.publiclighting.endpoints.PublicLightingAdHocManagementEndpoint lightAdHocManagementEndpoint;
    private com.alliander.osgp.adapter.ws.publiclighting.endpoints.PublicLightingScheduleManagementEndpoint lightScheduleManagementEndpoint;
    private com.alliander.osgp.adapter.ws.publiclighting.endpoints.DeviceMonitoringEndpoint lightDeviceMonitoringEndpoint;

    private com.alliander.osgp.adapter.ws.tariffswitching.endpoints.TariffSwitchingAdHocManagementEndpoint tariffAdHocManagementEndpoint;
    private com.alliander.osgp.adapter.ws.tariffswitching.endpoints.TariffSwitchingScheduleManagementEndpoint tariffScheduleManagementEndpoint;

    // Application Services
    @Autowired
    @Qualifier(value = "wsAdminDeviceManagementService")
    private com.alliander.osgp.adapter.ws.admin.application.services.DeviceManagementService adminDeviceManagementService;

    @Autowired
    @Qualifier(value = "wsCoreAdHocManagementService")
    private com.alliander.osgp.adapter.ws.core.application.services.AdHocManagementService adHocManagementService;
    @Autowired
    @Qualifier(value = "wsCoreConfigurationManagementService")
    private com.alliander.osgp.adapter.ws.core.application.services.ConfigurationManagementService configurationManagementService;
    @Autowired
    @Qualifier(value = "wsCoreDeviceInstallationService")
    private com.alliander.osgp.adapter.ws.core.application.services.DeviceInstallationService deviceInstallationService;
    @Autowired
    @Qualifier(value = "wsCoreDeviceManagementService")
    private com.alliander.osgp.adapter.ws.core.application.services.DeviceManagementService deviceManagementService;
    @Autowired
    @Qualifier(value = "wsCoreFirmwareManagementService")
    private com.alliander.osgp.adapter.ws.core.application.services.FirmwareManagementService firmwareManagementService;

    @Autowired
    @Qualifier(value = "wsPublicLightingAdHocManagementService")
    private com.alliander.osgp.adapter.ws.publiclighting.application.services.AdHocManagementService lightAdHocManagementService;
    @Autowired
    @Qualifier(value = "wsPublicLightingScheduleManagementService")
    private com.alliander.osgp.adapter.ws.publiclighting.application.services.ScheduleManagementService lightScheduleManagementService;
    @Autowired
    @Qualifier(value = "wsPublicLightingDeviceMonitoringService")
    private com.alliander.osgp.adapter.ws.publiclighting.application.services.DeviceMonitoringService deviceMonitoringService;

    @Autowired
    @Qualifier(value = "wsTariffSwitchingAdHocManagementService")
    private com.alliander.osgp.adapter.ws.tariffswitching.application.services.AdHocManagementService tariffAdHocManagementService;
    @Autowired
    @Qualifier(value = "wsTariffSwitchingScheduleManagementService")
    private com.alliander.osgp.adapter.ws.tariffswitching.application.services.ScheduleManagementService tariffScheduleManagementService;

    // Domain Adapter fields

    private Organisation organisation;
    private Organisation differentOrganisation;

    private Device device;
    private DeviceAuthorization deviceAuthorization;

    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository authorizationRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private OslpLogItemRepository logItemRepositoryMock;
    @Autowired
    private EventRepository eventRepositoryMock;

    // Protocol adapter fields
    @Autowired
    private OslpDeviceService deviceService;
    private OslpDevice oslpDevice;
    @Autowired
    private OslpDeviceRepository oslpDeviceRepositoryMock;

    private OslpChannelHandlerClient oslpChannelHandler;
    @Autowired
    private Channel channelMock;

    private OslpEnvelope oslpEnvelope;

    // Test fields
    private Throwable throwable;
    private Object response;

    @DomainStep("a registered device (.*)")
    public void givenRegisteredDevice(final String device) throws IOException {
        this.setUp();

        LOGGER.info("Creating device: {}", device);

        this.createDevice(device, true);
        when(this.deviceRepositoryMock.findByDeviceIdentification(this.device.getDeviceIdentification())).thenReturn(this.device);
        when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);

    }

    @DomainStep("an organisation (.*) which is member of device function group (.*)")
    public void andAnAuthenticatedOrganisation(final String organisationIdentification, final String group) {
        LOGGER.info("Creating organisation: {}", organisationIdentification);
        this.organisation = new OrganisationBuilder().withOrganisationIdentification(organisationIdentification).withFunctionGroup(PlatformFunctionGroup.USER)
                .build();

        LOGGER.info("Device Functiongroup: {}", com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup.valueOf(group.toUpperCase()));
        this.deviceAuthorization = new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup.valueOf(group.toUpperCase())).build();

        when(this.organisationRepositoryMock.findByOrganisationIdentification(this.organisation.getOrganisationIdentification())).thenReturn(this.organisation);
        when(this.authorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device)).thenReturn(Arrays.asList(this.deviceAuthorization));
    }

    @DomainStep("a different organisation (.*) who has no rights")
    public void andDifferentOrganisation(final String different) {
        LOGGER.info("Creating different organisation: {}", different);
        this.differentOrganisation = new OrganisationBuilder().withOrganisationIdentification(different).withFunctionGroup(PlatformFunctionGroup.USER).build();

        when(this.organisationRepositoryMock.findByOrganisationIdentification(this.differentOrganisation.getOrganisationIdentification())).thenReturn(
                this.differentOrganisation);
    }

    @DomainStep("device function SET_DEVICE_AUTHORIZATION is called to associate (.*) with (.*)")
    public void whenDeviceFunctionSetDeviceAuthorizationIsCalled(final String different, final String group) throws OperationNotSupportedException,
            ArgumentNullOrEmptyException, UnknownEntityException, NotAuthorizedException, NotSupportedException {
        LOGGER.info("Creating device authorization: {}", different);

        try {
            this.setDeviceAuthorization(com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeviceFunctionGroup.valueOf(group.toUpperCase()));
        } catch (final Throwable t) {
            LOGGER.info("Exception: {}", t.getClass().getSimpleName());
            this.throwable = t;
        }
    }

    @DomainStep("device function (.*) is called")
    public void whenDeviceFunctionIsCalled(final String function) {
        LOGGER.info("Executing device function: {}", function);
        try {
            switch (function) {
            case "START_SELF_TEST":
                this.startSelfTest();
                break;
            case "STOP_SELF_TEST":
                this.stopSelfTest();
                break;
            case "SET_LIGHT":
                this.setLight();
                break;
            case "GET_DEVICE_AUTHORIZATION":
                this.getDeviceAuthorization();
                break;
            case "SET_EVENT_NOTIFICATIONS":
                this.setEventNotifications();
                break;
            case "GET_EVENT_NOTIFICATIONS":
                this.getEventNotifications();
                break;
            case "UPDATE_FIRMWARE":
                this.updateFirmware();
                break;
            case "GET_FIRMWARE_VERSION":
                this.getFirmwareVersion();
                break;
            case "SET_SCHEDULE":
                this.setSchedule();
                break;
            case "SET_TARIFF_SCHEDULE":
                this.setTariffSchedule();
                break;
            case "SET_CONFIGURATION":
                this.setConfiguration();
                break;
            case "GET_CONFIGURATION":
                this.getConfiguration();
                break;
            case "GET_STATUS":
                this.getStatus();
                break;
            case "REMOVE_DEVICE":
                this.removeDevice();
                break;
            case "GET_ACTUAL_POWER_USAGE":
                this.getActualPowerUsage();
                break;
            case "GET_POWER_USAGE_HISTORY":
                this.getPowerUsageHistory();
                break;
            case "RESUME_SCHEDULE":
                this.resumeSchedule();
                break;
            case "SET_REBOOT":
                this.setReboot();
                break;
            case "SET_TRANSITION":
                this.setTransition();
                break;
            default:
                throw new OperationNotSupportedException("Function " + function + " does not exist.");
            }

        } catch (final Throwable t) {
            LOGGER.info("Exception: {}", t.getClass().getSimpleName());
            this.throwable = t;
        }
    }

    @DomainStep("the device function is (.*)")
    public boolean thenTheDeviceFunctionIsAllowed(final boolean allowed) {
        LOGGER.info("Allowed: {}", allowed);
        // LOGGER.info("Exception: {}", this.throwable);
        LOGGER.info("Exception: {}", this.throwable != null ? this.throwable.getClass().getSimpleName() : "null");
        LOGGER.info("Response {}", this.response != null);

        if (allowed) {
            return this.throwable == null && this.response != null;
        } else {
            Assert.assertTrue("Throwable should not be null", this.throwable != null);
            Assert.assertTrue("Response should be null", this.response == null);

            return (this.throwable.getCause() instanceof NotAuthorizedException || this.throwable.getCause() instanceof OperationNotSupportedException);
        }
    }

    private void startSelfTest() throws OsgpException {
        final StartDeviceTestRequest request = new StartDeviceTestRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.StartSelfTestResponse oslpResponse = com.alliander.osgp.oslp.Oslp.StartSelfTestResponse.newBuilder()
                .setStatus(Status.OK).build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setStartSelfTestResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);

        this.response = this.coreDeviceInstallationEndpoint.startDeviceTest(this.organisation.getOrganisationIdentification(), request);
    }

    private void stopSelfTest() throws OsgpException {
        final StopDeviceTestRequest request = new StopDeviceTestRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.StopSelfTestResponse oslpResponse = com.alliander.osgp.oslp.Oslp.StopSelfTestResponse.newBuilder()
                .setStatus(Status.OK).setSelfTestResult(ByteString.copyFrom(new byte[] { 0 })).build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setStopSelfTestResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);

        this.response = this.coreDeviceInstallationEndpoint.stopDeviceTest(this.organisation.getOrganisationIdentification(), request);
    }

    private void setLight() throws OsgpException {
        final SetLightRequest request = new SetLightRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());

        final com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.LightValue lv = new com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.LightValue();
        lv.setOn(true);

        request.getLightValue().add(lv);

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.SetLightResponse oslpResponse = com.alliander.osgp.oslp.Oslp.SetLightResponse.newBuilder().setStatus(Status.OK)
                .build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setSetLightResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);

        this.response = this.lightAdHocManagementEndpoint.setLight(this.organisation.getOrganisationIdentification(), request);
    }

    private void getStatus() throws OsgpException {
        final GetStatusRequest request = new GetStatusRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.GetStatusResponse oslpResponse = com.alliander.osgp.oslp.Oslp.GetStatusResponse.newBuilder().setStatus(Status.OK)
                .setPreferredLinktype(LinkType.CDMA).setActualLinktype(LinkType.CDMA).setLightType(LightType.DALI).setEventNotificationMask(0)
                .addValue(LightValue.newBuilder().setOn(true)).build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setGetStatusResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);

        this.response = this.lightAdHocManagementEndpoint.getStatus(this.organisation.getOrganisationIdentification(), request);
    }

    private void removeDevice() throws OsgpException {
        final RemoveDeviceRequest request = new RemoveDeviceRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());
        this.response = this.adminDeviceManagementEndpoint.removeDevice(this.organisation.getOrganisationIdentification(), request);
    }

    private void setDeviceAuthorization(final com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeviceFunctionGroup group) throws OsgpException {
        final UpdateDeviceAuthorisationsRequest request = new UpdateDeviceAuthorisationsRequest();
        final DeviceAuthorisation deviceAuthorisation = new DeviceAuthorisation();
        deviceAuthorisation.setDeviceIdentification(this.device.getDeviceIdentification());
        deviceAuthorisation.setOrganisationIdentification(this.differentOrganisation.getOrganisationIdentification());
        deviceAuthorisation.setFunctionGroup(group);
        request.getDeviceAuthorisations().add(deviceAuthorisation);
        this.response = this.adminDeviceManagementEndpoint.updateDeviceAuthorisations(this.organisation.getOrganisationIdentification(), request);
    }

    private void getDeviceAuthorization() throws OsgpException {
        final FindDeviceAuthorisationsRequest request = new FindDeviceAuthorisationsRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());
        this.response = this.adminDeviceManagementEndpoint.findDeviceAuthorisations(this.organisation.getOrganisationIdentification(), request);
    }

    private void setEventNotifications() throws OsgpException {
        final SetEventNotificationsRequest request = new SetEventNotificationsRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.SetEventNotificationsResponse oslpResponse = com.alliander.osgp.oslp.Oslp.SetEventNotificationsResponse.newBuilder()
                .setStatus(Status.OK).build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setSetEventNotificationsResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);

        this.response = this.coreDeviceManagementEndpoint.setEventNotifications(this.organisation.getOrganisationIdentification(), request);
    }

    @SuppressWarnings("unchecked")
    private void getEventNotifications() throws OsgpException {
        when(this.eventRepositoryMock.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<Event>(new ArrayList<Event>()));
        final FindEventsRequest request = new FindEventsRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());
        this.response = this.coreDeviceManagementEndpoint.findEventsRequest(this.organisation.getOrganisationIdentification(), request);
    }

    private void updateFirmware() throws OsgpException {
        final UpdateFirmwareRequest request = new UpdateFirmwareRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());
        request.setFirmwareIdentification("FW-ID");

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.UpdateFirmwareResponse oslpResponse = com.alliander.osgp.oslp.Oslp.UpdateFirmwareResponse.newBuilder()
                .setStatus(Status.OK).build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setUpdateFirmwareResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);

        this.response = this.coreFirmwareManagementEndpoint.updateFirmware(this.organisation.getOrganisationIdentification(), request);
    }

    private void getFirmwareVersion() throws OsgpException {
        final GetFirmwareVersionRequest request = new GetFirmwareVersionRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.GetFirmwareVersionResponse oslpResponse = com.alliander.osgp.oslp.Oslp.GetFirmwareVersionResponse.newBuilder()
                .setFirmwareVersion("dummy").build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setGetFirmwareVersionResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);

        this.response = this.coreFirmwareManagementEndpoint.getFirmwareVersion(this.organisation.getOrganisationIdentification(), request);
    }

    private void setSchedule() throws OsgpException {
        final SetScheduleRequest request = new SetScheduleRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());
        final Schedule schedule = new Schedule();
        schedule.setWeekDay(WeekDayType.MONDAY);
        schedule.setActionTime(ActionTimeType.SUNSET);
        final WindowType triggerWindow = new WindowType();
        triggerWindow.setMinutesBefore(2);
        triggerWindow.setMinutesAfter(2);
        schedule.setTriggerWindow(triggerWindow);
        final com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.LightValue lightvalue = new com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.LightValue();
        lightvalue.setIndex(1);
        lightvalue.setOn(true);
        lightvalue.setDimValue(1);
        schedule.getLightValue().add(lightvalue);
        request.getSchedules().add(schedule);

        this.mockScheduleResponse();

        this.response = this.lightScheduleManagementEndpoint.setLightSchedule(this.organisation.getOrganisationIdentification(), request);
    }

    private void setTariffSchedule() throws OsgpException {
        final com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleRequest request = new com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());

        final TariffSchedule schedule = new TariffSchedule();
        schedule.setWeekDay(com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.WeekDayType.MONDAY);
        schedule.setTime("23:00:00.000");

        final List<TariffValue> tariffValues = new ArrayList<>();
        final TariffValue tariffValue = new TariffValue();
        tariffValue.setHigh(Boolean.TRUE);

        tariffValues.add(tariffValue);

        schedule.getTariffValue().addAll(tariffValues);
        request.getSchedules().add(schedule);

        this.mockScheduleResponse();

        this.response = this.tariffScheduleManagementEndpoint.setSchedule(this.organisation.getOrganisationIdentification(), request);
    }

    private void mockScheduleResponse() {
        final com.alliander.osgp.oslp.Oslp.SetScheduleResponse oslpResponse = com.alliander.osgp.oslp.Oslp.SetScheduleResponse.newBuilder()
                .setStatus(Status.OK).build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setSetScheduleResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);
    }

    private void setConfiguration() throws OsgpException {
        final SetConfigurationRequest request = new SetConfigurationRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.SetConfigurationResponse oslpResponse = com.alliander.osgp.oslp.Oslp.SetConfigurationResponse.newBuilder()
                .setStatus(Status.OK).build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setSetConfigurationResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);

        this.response = this.coreConfigurationManagementEndpoint.setConfiguration(this.organisation.getOrganisationIdentification(), request);
    }

    private void getConfiguration() throws OsgpException {
        final GetConfigurationRequest request = new GetConfigurationRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.GetConfigurationResponse oslpResponse = com.alliander.osgp.oslp.Oslp.GetConfigurationResponse.newBuilder()
                .setStatus(Status.OK).build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setGetConfigurationResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);

        this.response = this.coreConfigurationManagementEndpoint.getConfiguration(this.organisation.getOrganisationIdentification(), request);
    }

    private void getActualPowerUsage() throws OsgpException {
        final GetActualPowerUsageRequest request = new GetActualPowerUsageRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.GetActualPowerUsageResponse oslpResponse = com.alliander.osgp.oslp.Oslp.GetActualPowerUsageResponse
                .newBuilder()
                .setPowerUsageData(
                        PowerUsageData.newBuilder().setMeterType(MeterType.P1).setRecordTime("20130101120000").setActualConsumedPower(2)
                                .setTotalConsumedEnergy(2).build()).setStatus(Status.OK).build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setGetActualPowerUsageResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);

        this.response = this.lightDeviceMonitoringEndpoint.getActualPowerUsage(this.organisation.getOrganisationIdentification(), request);
    }

    private void getPowerUsageHistory() throws OsgpException {
        final GetPowerUsageHistoryRequest request = new GetPowerUsageHistoryRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());
        request.setHistoryTermType(HistoryTermType.SHORT);
        final TimePeriod timePeriod = new TimePeriod();
        try {
            timePeriod.setStartTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(DateTime.now().minusDays(1).toGregorianCalendar()));
            timePeriod.setEndTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(DateTime.now().toGregorianCalendar()));
        } catch (final DatatypeConfigurationException e) {
            throw new TechnicalException(ComponentType.DOMAIN_PUBLIC_LIGHTING, e);
        }
        request.setTimePeriod(timePeriod);

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.GetPowerUsageHistoryResponse oslpResponse = com.alliander.osgp.oslp.Oslp.GetPowerUsageHistoryResponse
                .newBuilder()
                .addPowerUsageData(
                        PowerUsageData.newBuilder().setMeterType(MeterType.P1).setRecordTime("20130101120000").setActualConsumedPower(2)
                                .setTotalConsumedEnergy(2).build()).setStatus(Status.OK).build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setGetPowerUsageHistoryResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);

        this.response = this.lightDeviceMonitoringEndpoint.getPowerUsageHistory(this.organisation.getOrganisationIdentification(), request);
    }

    private void resumeSchedule() throws OsgpException {
        final ResumeScheduleRequest request = new ResumeScheduleRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());
        request.setIndex(0);
        request.setIsImmediate(true);

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.ResumeScheduleResponse oslpResponse = com.alliander.osgp.oslp.Oslp.ResumeScheduleResponse.newBuilder()
                .setStatus(Status.OK).build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setResumeScheduleResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);

        this.response = this.lightAdHocManagementEndpoint.resumeSchedule(this.organisation.getOrganisationIdentification(), request);
    }

    private void setReboot() throws OsgpException {
        final SetRebootRequest request = new SetRebootRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.SetRebootResponse oslpResponse = com.alliander.osgp.oslp.Oslp.SetRebootResponse.newBuilder().setStatus(Status.OK)
                .build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setSetRebootResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);

        this.response = this.coreAdHocManagementEndpoint.setReboot(this.organisation.getOrganisationIdentification(), request);
    }

    private void setTransition() throws OsgpException {
        final SetTransitionRequest request = new SetTransitionRequest();
        request.setDeviceIdentification(this.device.getDeviceIdentification());
        request.setTransitionType(TransitionType.DAY_NIGHT);

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.SetTransitionResponse oslpResponse = com.alliander.osgp.oslp.Oslp.SetTransitionResponse.newBuilder()
                .setStatus(Status.OK).build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setSetTransitionResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope, this.channelMock, this.device.getNetworkAddress());
        this.deviceService.setOslpChannelHandler(this.oslpChannelHandler);

        this.response = this.lightAdHocManagementEndpoint.setTransition(this.organisation.getOrganisationIdentification(), request);
    }

    private void setUp() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.authorizationRepositoryMock, this.organisationRepositoryMock, this.eventRepositoryMock,
                this.channelMock, this.oslpDeviceRepositoryMock });

        this.adminDeviceManagementEndpoint = new com.alliander.osgp.adapter.ws.admin.endpoints.DeviceManagementEndpoint(this.adminDeviceManagementService,
                new com.alliander.osgp.adapter.ws.admin.application.mapping.DeviceManagementMapper());

        this.coreAdHocManagementEndpoint = new AdHocManagementEndpoint(this.adHocManagementService, new AdHocManagementMapper());
        this.coreConfigurationManagementEndpoint = new ConfigurationManagementEndpoint(this.configurationManagementService, new ConfigurationManagementMapper());
        this.coreDeviceInstallationEndpoint = new DeviceInstallationEndpoint(this.deviceInstallationService, new DeviceInstallationMapper());
        this.coreDeviceManagementEndpoint = new DeviceManagementEndpoint(this.deviceManagementService, new DeviceManagementMapper());
        this.coreFirmwareManagementEndpoint = new FirmwareManagementEndpoint(this.firmwareManagementService);
        this.lightDeviceMonitoringEndpoint = new DeviceMonitoringEndpoint(this.deviceMonitoringService, new DeviceMonitoringMapper());

        this.lightAdHocManagementEndpoint = new com.alliander.osgp.adapter.ws.publiclighting.endpoints.PublicLightingAdHocManagementEndpoint(
                this.lightAdHocManagementService, new com.alliander.osgp.adapter.ws.publiclighting.application.mapping.AdHocManagementMapper());
        this.lightScheduleManagementEndpoint = new com.alliander.osgp.adapter.ws.publiclighting.endpoints.PublicLightingScheduleManagementEndpoint(
                this.lightScheduleManagementService, new ScheduleManagementMapper());

        this.tariffAdHocManagementEndpoint = new com.alliander.osgp.adapter.ws.tariffswitching.endpoints.TariffSwitchingAdHocManagementEndpoint(
                this.tariffAdHocManagementService, new com.alliander.osgp.adapter.ws.tariffswitching.application.mapping.AdHocManagementMapper());
        this.tariffScheduleManagementEndpoint = new com.alliander.osgp.adapter.ws.tariffswitching.endpoints.TariffSwitchingScheduleManagementEndpoint(
                this.tariffScheduleManagementService, new com.alliander.osgp.adapter.ws.tariffswitching.application.mapping.ScheduleManagementMapper());

        this.response = null;
        this.throwable = null;
    }

    private void createDevice(final String deviceIdentification, final Boolean activated) {
        LOGGER.info("Creating device [{}] with active [{}]", deviceIdentification, activated);

        this.device = new DeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withNetworkAddress(activated ? InetAddress.getLoopbackAddress() : null).withPublicKeyPresent(PUBLIC_KEY_PRESENT)
                .withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION)).isActivated(activated).build();

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(deviceIdentification).withDeviceUid(DEVICE_UID).build();
    }
}
