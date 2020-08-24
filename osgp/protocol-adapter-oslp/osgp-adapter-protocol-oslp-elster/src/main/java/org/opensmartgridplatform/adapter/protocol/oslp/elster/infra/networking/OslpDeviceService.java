/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping.OslpMapper;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.oslp.OslpDeviceSettingsService;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.oslp.OslpSigningService;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.GetPowerUsageHistoryDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.GetStatusDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.ResumeScheduleDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetConfigurationDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetDeviceVerificationKeyDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetEventNotificationsDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetLightDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetScheduleDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetTransitionDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SwitchConfigurationBankRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SwitchFirmwareDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.UpdateDeviceSslCertificationDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.UpdateFirmwareDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses.EmptyDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses.GetActualPowerUsageDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses.GetConfigurationDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses.GetFirmwareVersionDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses.GetPowerUsageHistoryDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses.GetStatusDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.OslpDevice;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.valueobjects.Pager;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpLogItemRequestMessage;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpLogItemRequestMessageSender;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.DeviceStatusDto;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationTypeDto;
import org.opensmartgridplatform.dto.valueobjects.LightTypeDto;
import org.opensmartgridplatform.dto.valueobjects.LightValueDto;
import org.opensmartgridplatform.dto.valueobjects.LinkTypeDto;
import org.opensmartgridplatform.dto.valueobjects.PageInfoDto;
import org.opensmartgridplatform.dto.valueobjects.PowerUsageDataDto;
import org.opensmartgridplatform.dto.valueobjects.PowerUsageHistoryResponseMessageDataContainerDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleEntryDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleMessageDataContainerDto;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.oslp.Oslp.GetConfigurationRequest;
import org.opensmartgridplatform.oslp.Oslp.GetFirmwareVersionRequest;
import org.opensmartgridplatform.oslp.Oslp.GetStatusRequest;
import org.opensmartgridplatform.oslp.Oslp.SetScheduleRequest;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;

@Component
public class OslpDeviceService implements DeviceService {

    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final String TIME_FORMAT = "HHmmss";
    private static final String DATETIME_FORMAT = DATE_FORMAT + TIME_FORMAT;

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpDeviceService.class);

    @Autowired
    private OslpChannelHandlerClient oslpChannelHandler;

    @Autowired
    private OslpMapper mapper;

    @Autowired
    private int oslpPortClient;

    @Autowired
    private int oslpPortClientLocal;

    @Autowired
    private boolean executeResumeScheduleAfterSetLight;

    @Autowired
    private boolean executeRebootAfterSetConfiguration;

    @Autowired
    private OslpDeviceSettingsService oslpDeviceSettingsService;

    @Autowired
    private OslpLogItemRequestMessageSender oslpLogItemRequestMessageSender;

    @Autowired
    private OslpSigningService oslpSigningService;

    @Override
    public void startSelfTest(final DeviceRequest deviceRequest) {
        LOGGER.info("startSelfTest() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestStartSelfTest(deviceRequest);
    }

    @Override
    public void doStartSelfTest(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.info("doStartSelfTest() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler responseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseStartSelfTest(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, responseHandler, deviceRequest);
    }

    @Override
    public void stopSelfTest(final DeviceRequest deviceRequest) {
        LOGGER.info("stopSelfTest() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestStopSelfTest(deviceRequest);
    }

    @Override
    public void doStopSelfTest(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.info("doStopSelfTest() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope response) {
                OslpDeviceService.this.handleOslpResponseStopSelfTest(deviceRequest, response, deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    @Override
    public void setLight(final SetLightDeviceRequest deviceRequest) {
        LOGGER.info("setLight() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestSetLight(deviceRequest);
    }

    @Override
    public void doSetLight(final OslpEnvelope oslpRequest, final DeviceRequest setLightDeviceRequest,
            final ResumeScheduleDeviceRequest resumeScheduleDeviceRequest,
            final DeviceResponseHandler setLightDeviceResponseHandler,
            final DeviceResponseHandler resumeScheduleDeviceResponseHandler, final String ipAddress)
            throws IOException {
        LOGGER.info("doSetLight() for device: {}.", setLightDeviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(setLightDeviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseSetLight(setLightDeviceRequest, resumeScheduleDeviceRequest,
                        oslpResponse, setLightDeviceResponseHandler, resumeScheduleDeviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, setLightDeviceRequest, setLightDeviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, setLightDeviceRequest);
    }

    @Override
    public void setEventNotifications(final SetEventNotificationsDeviceRequest deviceRequest) {
        LOGGER.info("setEventNotifications() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestSetEventNotifications(deviceRequest);
    }

    @Override
    public void doSetEventNotifications(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.info("doSetEventNotifications() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseSetEventNotifications(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    @Override
    public void updateFirmware(final UpdateFirmwareDeviceRequest deviceRequest) {
        LOGGER.info("updateFirmware() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestUpdateFirmware(deviceRequest);
    }

    @Override
    public void doUpdateFirmware(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.info("doUpdateFirmware() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseUpdateFirmware(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    @Override
    public void getFirmwareVersion(final DeviceRequest deviceRequest) {
        LOGGER.info("getFirmwareVersion() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestGetFirmwareVersion(deviceRequest);
    }

    @Override
    public void doGetFirmwareVersion(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.info("doGetFirmwareVersion() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseGetFirmwareVersion(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    @Override
    public void switchFirmware(final SwitchFirmwareDeviceRequest deviceRequest) {
        LOGGER.info("switchFirmware() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestSwitchFirmware(deviceRequest);
    }

    private void buildOslpRequestSwitchFirmware(final SwitchFirmwareDeviceRequest deviceRequest) {
        final Oslp.SwitchFirmwareRequest switchFirmwareRequest = Oslp.SwitchFirmwareRequest.newBuilder()
                .setNewFirmwareVersion(deviceRequest.getVersion())
                .build();

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setSwitchFirmwareRequest(switchFirmwareRequest).build(),
                deviceRequest.getVersion());
    }

    @Override
    public void doSwitchFirmware(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {

        LOGGER.info("doSwitchFirmware() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseSwitchFirmware(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    @Override
    public void updateDeviceSslCertification(final UpdateDeviceSslCertificationDeviceRequest deviceRequest) {
        LOGGER.info("UpdateDeviceSslCertification() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestUpdateDeviceSslCertification(deviceRequest);
    }

    private void buildOslpRequestUpdateDeviceSslCertification(
            final UpdateDeviceSslCertificationDeviceRequest deviceRequest) {
        final Oslp.UpdateDeviceSslCertificationRequest updateDeviceSslCertificationRequest = Oslp.UpdateDeviceSslCertificationRequest
                .newBuilder()
                .setCertificateDomain(deviceRequest.getCertification().getCertificateDomain())
                .setCertificateUrl(deviceRequest.getCertification().getCertificateUrl())
                .build();

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder()
                        .setUpdateDeviceSslCertificationRequest(updateDeviceSslCertificationRequest)
                        .build(),
                deviceRequest.getCertification());

    }

    @Override
    public void doUpdateDeviceSslCertification(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {

        LOGGER.info("doUpdateDeviceSslCertification() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseUpdateDeviceSslCertification(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    @Override
    public void setDeviceVerificationKey(final SetDeviceVerificationKeyDeviceRequest deviceRequest) {
        LOGGER.info("SetDeviceVerificationKey() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestSetDeviceVerificationKey(deviceRequest);
    }

    @Override
    public void doSetDeviceVerificationKey(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {

        LOGGER.info("doSetDeviceVerificationKey() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseSetDeviceVerificationKey(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    @Override
    public void setSchedule(final SetScheduleDeviceRequest deviceRequest) {
        LOGGER.info("setSchedule() for device: {}.", deviceRequest.getDeviceIdentification());

        switch (deviceRequest.getScheduleMessageDataContainer().getScheduleMessageType()) {
        case RETRIEVE_CONFIGURATION:
            this.processOslpRequestGetConfigurationBeforeSetSchedule(deviceRequest);
            break;
        case SET_ASTRONOMICAL_OFFSETS:
            this.processOslpRequestSetScheduleAstronomicalOffsets(deviceRequest);
            break;
        default:
            this.processOslpRequestSetSchedule(deviceRequest);
        }
    }

    private void processOslpRequestSetSchedule(final SetScheduleDeviceRequest deviceRequest) {
        final int pageSize = 5;
        final int numberOfPages = (int) Math
                .ceil((double) deviceRequest.getScheduleMessageDataContainer().getSchedule().getScheduleList().size()
                        / pageSize);

        if (numberOfPages == 1) {
            this.processOslpRequestSetScheduleSingle(deviceRequest);
        } else {
            final Pager pager = new Pager(
                    deviceRequest.getScheduleMessageDataContainer().getSchedule().getScheduleList().size(), pageSize);

            this.processOslpRequestSetSchedulePaged(deviceRequest, pager);
        }
    }

    private void processOslpRequestGetConfigurationBeforeSetSchedule(final SetScheduleDeviceRequest deviceRequest) {
        LOGGER.debug("Processing get configuration before set schedule request for device: {}.",
                deviceRequest.getDeviceIdentification());

        this.buildOslpRequestGetConfigurationBeforeSetSchedule(deviceRequest);
    }

    private void processOslpRequestSetScheduleAstronomicalOffsets(final SetScheduleDeviceRequest deviceRequest) {
        LOGGER.debug("Processing set schedule astronomical offsets request for device: {}.",
                deviceRequest.getDeviceIdentification());

        this.buildOslpRequestSetScheduleAstronomicalOffsets(deviceRequest);
    }

    @Override
    public void doSetSchedule(final OslpEnvelope oslpRequest, final SetScheduleDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress, final String domain,
            final String domainVersion, final String messageType, final int messagePriority, final int retryCount,
            final boolean isScheduled, final PageInfoDto pageInfo) throws IOException {
        LOGGER.info("doSetSchedule() for device: {}.", deviceRequest.getDeviceIdentification());

        switch (deviceRequest.getScheduleMessageDataContainer().getScheduleMessageType()) {
        case RETRIEVE_CONFIGURATION:
            this.doProcessOslpRequestSetScheduleGetConfiguration(oslpRequest, deviceRequest, deviceResponseHandler,
                    ipAddress);
            break;
        case SET_ASTRONOMICAL_OFFSETS:
            this.doProcessOslpRequestSetScheduleAstronomicalOffsets(oslpRequest, deviceRequest, deviceResponseHandler,
                    ipAddress);
            break;
        case SET_SCHEDULE:
        default:
            this.doProcessOslpRequestSetSchedule(oslpRequest, deviceRequest, deviceResponseHandler, ipAddress, domain,
                    domainVersion, messageType, retryCount, isScheduled, pageInfo);
        }
    }

    private void doProcessOslpRequestSetSchedule(final OslpEnvelope oslpRequest,
            final SetScheduleDeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final String ipAddress, final String domain, final String domainVersion, final String messageType,
            final int retryCount, final boolean isScheduled, final PageInfoDto pageInfo) throws IOException {
        if (pageInfo == null) {
            this.doProcessOslpRequestSetScheduleSingle(oslpRequest, deviceRequest, deviceResponseHandler, ipAddress);
        } else {
            final Pager pager = new Pager(
                    deviceRequest.getScheduleMessageDataContainer().getSchedule().getScheduleList().size(), 5);
            pager.setCurrentPage(pageInfo.getCurrentPage());
            pager.setNumberOfPages(pageInfo.getTotalPages());
            this.doProcessOslpRequestSetSchedulePaged(oslpRequest, deviceRequest, deviceResponseHandler, ipAddress,
                    domain, domainVersion, messageType, retryCount, isScheduled, pager);
        }
    }

    private void processOslpRequestSetScheduleSingle(final SetScheduleDeviceRequest deviceRequest) {

        LOGGER.debug("Processing single set schedule request for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestSetScheduleSingle(deviceRequest);
    }

    private void doProcessOslpRequestSetScheduleGetConfiguration(final OslpEnvelope oslpRequest,
            final SetScheduleDeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final String ipAddress) throws IOException {

        LOGGER.debug("Processing a get configuration before setting schedule with astronomical offsets to device: {}.",
                deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseGetConfiguration(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    private void doProcessOslpRequestSetScheduleAstronomicalOffsets(final OslpEnvelope oslpRequest,
            final SetScheduleDeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final String ipAddress) throws IOException {

        LOGGER.debug("Processing set schedule astronomical offsets request for device: {}.",
                deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseSetScheduleAstronomicalOffsets(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    private void doProcessOslpRequestSetScheduleSingle(final OslpEnvelope oslpRequest,
            final SetScheduleDeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final String ipAddress) throws IOException {

        LOGGER.debug("Processing single set schedule request for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseSetScheduleSingle(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    protected void handleException(final Throwable t, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler) {

        final DeviceResponse deviceResponse = new DeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority());

        if (t instanceof IOException) {
            // Replace t by an OSGP Exception
            final ConnectionFailureException ex = new ConnectionFailureException(ComponentType.PROTOCOL_OSLP,
                    "Connection failure");
            deviceResponseHandler.handleException(ex, deviceResponse);
        } else {
            deviceResponseHandler.handleException(t, deviceResponse);
        }
    }

    private void handleOslpResponseSetScheduleAstronomicalOffsets(final SetScheduleDeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasSetConfigurationResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getSetConfigurationResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseSetScheduleSingle(final SetScheduleDeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasSetScheduleResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getSetScheduleResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void buildOslpRequestGetConfigurationBeforeSetSchedule(final SetScheduleDeviceRequest deviceRequest) {

        final ScheduleMessageDataContainerDto scheduleMessageDataContainer = deviceRequest
                .getScheduleMessageDataContainer();

        final Oslp.GetConfigurationRequest.Builder request = GetConfigurationRequest.newBuilder();
        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setGetConfigurationRequest(request.build()).build(),
                scheduleMessageDataContainer);
    }

    private void buildOslpRequestSetScheduleAstronomicalOffsets(final SetScheduleDeviceRequest deviceRequest) {

        final ScheduleMessageDataContainerDto scheduleMessageDataContainer = deviceRequest
                .getScheduleMessageDataContainer();
        final ScheduleDto schedule = scheduleMessageDataContainer.getSchedule();
        final ConfigurationDto configuration = scheduleMessageDataContainer.getConfiguration();

        // First, sort the relay mapping on (internal) index number (FLEX-2514)
        if (configuration.getRelayConfiguration() != null) {
            Collections.sort(configuration.getRelayConfiguration().getRelayMap(),
                    (rm1, rm2) -> rm1.getIndex().compareTo(rm2.getIndex()));
        }

        int sunriseOffset = 0;
        if (schedule.getAstronomicalSunriseOffset() != null) {
            sunriseOffset = schedule.getAstronomicalSunriseOffset();
        }

        int sunsetOffset = 0;
        if (schedule.getAstronomicalSunsetOffset() != null) {
            sunsetOffset = schedule.getAstronomicalSunsetOffset();
        }

        configuration.setAstroGateSunRiseOffset(sunriseOffset);
        configuration.setAstroGateSunSetOffset(sunsetOffset);

        final Oslp.SetConfigurationRequest request = this.mapper.map(configuration, Oslp.SetConfigurationRequest.class);

        this.buildAndSignEnvelope(deviceRequest, Oslp.Message.newBuilder().setSetConfigurationRequest(request).build(),
                scheduleMessageDataContainer);
    }

    private void buildOslpRequestSetScheduleSingle(final SetScheduleDeviceRequest deviceRequest) {
        final List<Oslp.Schedule> oslpSchedules = this.convertToOslpSchedules(
                deviceRequest.getScheduleMessageDataContainer().getSchedule().getScheduleList());

        final Oslp.SetScheduleRequest.Builder request = SetScheduleRequest.newBuilder()
                .addAllSchedules(oslpSchedules)
                .setScheduleType(this.mapper.map(deviceRequest.getRelayType(),
                        org.opensmartgridplatform.oslp.Oslp.RelayType.class));

        final ScheduleDto schedule = new ScheduleDto(
                deviceRequest.getScheduleMessageDataContainer().getSchedule().getScheduleList());
        final ScheduleMessageDataContainerDto scheduleMessageDataContainer = new ScheduleMessageDataContainerDto.Builder(
                schedule).build();

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setSetScheduleRequest(request.build()).build(), scheduleMessageDataContainer);
    }

    private void processOslpRequestSetSchedulePaged(final SetScheduleDeviceRequest deviceRequest, final Pager pager) {
        LOGGER.debug("Processing paged set schedule request for device: {}, page {} of {}",
                deviceRequest.getDeviceIdentification(), pager.getCurrentPage(), pager.getNumberOfPages());

        this.buildOslpRequestSetSchedulePaged(deviceRequest, pager);
    }

    private void doProcessOslpRequestSetSchedulePaged(final OslpEnvelope oslpRequest,
            final SetScheduleDeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final String ipAddress, final String domain, final String domainVersion, final String messageType,
            final int retryCount, final boolean isScheduled, final Pager pager) throws IOException {
        LOGGER.debug("Processing paged set schedule request for device: {}, page {} of {}",
                deviceRequest.getDeviceIdentification(), pager.getCurrentPage(), pager.getNumberOfPages());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseSetSchedulePaged(deviceRequest, oslpResponse, domain,
                        domainVersion, messageType, retryCount, isScheduled, pager, deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    private void handleOslpResponseSetSchedulePaged(final SetScheduleDeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final String domain, final String domainVersion, final String messageType,
            final int retryCount, final boolean isScheduled, final Pager pager,
            final DeviceResponseHandler deviceResponseHandler) {

        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        // Get response status
        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasSetScheduleResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getSetScheduleResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        if (pager.isLastPage() || status != DeviceMessageStatus.OK) {
            // Stop processing pages and handle device response.
            this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

            final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                    deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                    deviceRequest.getMessagePriority(), status);
            deviceResponseHandler.handleResponse(deviceResponse);
        } else {
            // Process next page
            pager.nextPage();
            this.processOslpRequestSetSchedulePaged(deviceRequest, pager);
        }
    }

    private void buildOslpRequestSetSchedulePaged(final SetScheduleDeviceRequest deviceRequest, final Pager pager) {

        final List<Oslp.Schedule> oslpSchedules = this
                .convertToOslpSchedules(deviceRequest.getScheduleMessageDataContainer()
                        .getSchedule()
                        .getScheduleList()
                        .subList(pager.getIndexFrom(), pager.getIndexTo()));

        final Oslp.SetScheduleRequest.Builder oslpRequestBuilder = SetScheduleRequest.newBuilder()
                .addAllSchedules(oslpSchedules)
                .setScheduleType(this.mapper.map(deviceRequest.getRelayType(),
                        org.opensmartgridplatform.oslp.Oslp.RelayType.class))
                .setPageInfo(Oslp.PageInfo.newBuilder()
                        .setCurrentPage(pager.getCurrentPage())
                        .setPageSize(pager.getPageSize())
                        .setTotalPages(pager.getNumberOfPages()));

        final PageInfoDto pageInfo = new PageInfoDto(pager.getCurrentPage(), pager.getPageSize(),
                pager.getNumberOfPages());
        final ScheduleDto schedule = new ScheduleDto(
                deviceRequest.getScheduleMessageDataContainer().getSchedule().getScheduleList());
        final ScheduleMessageDataContainerDto scheduleMessageDataContainer = new ScheduleMessageDataContainerDto.Builder(
                schedule).withPageInfo(pageInfo).build();

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setSetScheduleRequest(oslpRequestBuilder.build()).build(),
                scheduleMessageDataContainer);
    }

    private List<Oslp.Schedule> convertToOslpSchedules(final List<ScheduleEntryDto> schedules) {
        final List<Oslp.Schedule> oslpSchedules = new ArrayList<>();

        for (final ScheduleEntryDto schedule : schedules) {
            oslpSchedules.add(this.convertToOslpSchedule(schedule));
        }
        return oslpSchedules;
    }

    private Oslp.Schedule convertToOslpSchedule(final ScheduleEntryDto schedule) {
        Oslp.Schedule.Builder scheduleBuilder = Oslp.Schedule.newBuilder()
                .setWeekday(Oslp.Weekday.valueOf(schedule.getWeekDay().ordinal() + 1))
                .setActionTime(Oslp.ActionTime.valueOf(schedule.getActionTime().ordinal() + 1));

        if (schedule.getStartDay() != null) {
            scheduleBuilder = scheduleBuilder.setStartDay(schedule.getStartDay().toString(DATE_FORMAT));
        }

        if (schedule.getEndDay() != null) {
            scheduleBuilder = scheduleBuilder.setEndDay(schedule.getEndDay().toString(DATE_FORMAT));
        }

        if (StringUtils.isNotBlank(schedule.getTime())) {
            scheduleBuilder = scheduleBuilder.setTime(LocalTime.parse(schedule.getTime()).toString(TIME_FORMAT));
        }

        if (schedule.getTriggerWindow() != null) {
            scheduleBuilder = scheduleBuilder.setWindow(Oslp.Window.newBuilder()
                    .setMinutesBefore((int) schedule.getTriggerWindow().getMinutesBefore())
                    .setMinutesAfter((int) schedule.getTriggerWindow().getMinutesAfter()));
        }

        for (final LightValueDto lightValue : schedule.getLightValue()) {
            scheduleBuilder.addValue(this.buildLightValue(lightValue));
        }

        if (schedule.getTriggerType() != null) {
            scheduleBuilder.setTriggerType(Oslp.TriggerType.valueOf(schedule.getTriggerType().ordinal() + 1));
        }

        if (schedule.getIndex() != null) {
            scheduleBuilder.setIndex(schedule.getIndex());
        }

        if (schedule.getIsEnabled() != null) {
            scheduleBuilder.setIsEnabled(schedule.getIsEnabled());
        }

        if (schedule.getMinimumLightsOn() != null) {
            scheduleBuilder.setMinimumLightsOn(schedule.getMinimumLightsOn());
        }

        return scheduleBuilder.build();
    }

    @Override
    public void setConfiguration(final SetConfigurationDeviceRequest deviceRequest) {
        LOGGER.info("setConfiguration() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestSetConfiguration(deviceRequest);
    }

    @Override
    public void doSetConfiguration(final OslpEnvelope oslpRequest, final DeviceRequest setConfigurationDeviceRequest,
            final DeviceRequest setRebootDeviceRequest,
            final DeviceResponseHandler setConfigurationDeviceResponseHandler,
            final DeviceResponseHandler setRebootDeviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.info("doSetConfiguration() for device: {}.", setConfigurationDeviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(setConfigurationDeviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseSetConfiguration(setConfigurationDeviceRequest,
                        setRebootDeviceRequest, oslpResponse, setConfigurationDeviceResponseHandler,
                        setRebootDeviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, setConfigurationDeviceRequest,
                        setConfigurationDeviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, setConfigurationDeviceRequest);
    }

    @Override
    public void getConfiguration(final DeviceRequest deviceRequest) {
        LOGGER.info("getConfiguration() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestGetConfiguration(deviceRequest);
    }

    @Override
    public void doGetConfiguration(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.info("doGetConfiguration() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseGetConfiguration(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    @Override
    public void switchConfiguration(final SwitchConfigurationBankRequest deviceRequest) {
        LOGGER.info("switchConfiguration() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestSwitchConfiguration(deviceRequest);
    }

    @Override
    public void doSwitchConfiguration(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.info("doSwitchConfiguration() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseSwitchConfiguration(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    @Override
    public void getActualPowerUsage(final DeviceRequest deviceRequest) {
        LOGGER.info("getActualPowerUsage() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestGetActualPowerUsage(deviceRequest);
    }

    @Override
    public void doGetActualPowerUsage(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.info("doGetActualPowerUsage() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseGetActualPowerUsage(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    @Override
    public void getPowerUsageHistory(final GetPowerUsageHistoryDeviceRequest deviceRequest) {
        LOGGER.info("getPowerUsageHistory() for device: {}.", deviceRequest.getDeviceIdentification());

        final Pager pager = new Pager();
        final List<PowerUsageDataDto> powerUsageHistoryData = new ArrayList<>();

        this.buildOslpRequestGetPowerUsageHistory(deviceRequest, pager, powerUsageHistoryData);
    }

    @Override
    public void doGetPowerUsageHistory(final OslpEnvelope oslpRequest,
            final PowerUsageHistoryResponseMessageDataContainerDto powerUsageHistoryResponseMessageDataContainer,
            final GetPowerUsageHistoryDeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final String ipAddress) throws IOException {
        LOGGER.info("doGetPowerUsageHistory() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final List<PowerUsageDataDto> powerUsageHistoryData = powerUsageHistoryResponseMessageDataContainer
                .getPowerUsageData();
        final PageInfoDto pageInfo = powerUsageHistoryResponseMessageDataContainer.getPageInfo();
        final Pager pager = new Pager(pageInfo.getTotalPages(), pageInfo.getPageSize(), pageInfo.getCurrentPage());

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseGetPowerUsageHistory(deviceRequest, oslpResponse, pager,
                        powerUsageHistoryData, deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    private void processOslpRequestGetPowerUsageHistory(final GetPowerUsageHistoryDeviceRequest deviceRequest,
            final Pager pager, final List<PowerUsageDataDto> powerUsageHistoryData) {
        LOGGER.info("GetPowerUsageHistory() for device: {}, page: {} of {}", deviceRequest.getDeviceIdentification(),
                pager.getCurrentPage(), pager.getNumberOfPages());

        this.buildOslpRequestGetPowerUsageHistory(deviceRequest, pager, powerUsageHistoryData);
    }

    private void handleOslpResponseGetPowerUsageHistory(final GetPowerUsageHistoryDeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final Pager pager, final List<PowerUsageDataDto> powerUsageHistoryData,
            final DeviceResponseHandler deviceResponseHandler) {

        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        // Get response status
        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasGetPowerUsageHistoryResponse()) {
            final Oslp.GetPowerUsageHistoryResponse getPowerUsageHistoryResponse = oslpResponse.getPayloadMessage()
                    .getGetPowerUsageHistoryResponse();
            status = this.mapper.map(getPowerUsageHistoryResponse.getStatus(), DeviceMessageStatus.class);
            powerUsageHistoryData.addAll(this.mapper.mapAsList(getPowerUsageHistoryResponse.getPowerUsageDataList(),
                    PowerUsageDataDto.class));

            if (pager.getNumberOfPages() == 1 && getPowerUsageHistoryResponse.hasPageInfo()) {
                pager.setNumberOfPages(getPowerUsageHistoryResponse.getPageInfo().getTotalPages());
            }

        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        if (pager.isLastPage() || status != DeviceMessageStatus.OK) {
            // Stop processing pages and handle device response.
            this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

            final GetPowerUsageHistoryDeviceResponse deviceResponse = new GetPowerUsageHistoryDeviceResponse(
                    deviceRequest, status, powerUsageHistoryData);
            deviceResponseHandler.handleResponse(deviceResponse);

        } else {
            // Process next page
            pager.nextPage();
            try {
                this.processOslpRequestGetPowerUsageHistory(deviceRequest, pager, powerUsageHistoryData);
            } catch (final Exception e) {
                LOGGER.error("Exception", e);
                final GetPowerUsageHistoryDeviceResponse deviceResponse = new GetPowerUsageHistoryDeviceResponse(
                        deviceRequest, DeviceMessageStatus.FAILURE, null);
                deviceResponseHandler.handleResponse(deviceResponse);
            }
        }
    }

    private void buildOslpRequestGetPowerUsageHistory(final GetPowerUsageHistoryDeviceRequest deviceRequest,
            final Pager pager, final List<PowerUsageDataDto> powerUsageHistoryData) {
        final Oslp.HistoryTermType oslpHistoryTermType = this.mapper
                .map(deviceRequest.getPowerUsageHistoryContainer().getHistoryTermType(), Oslp.HistoryTermType.class);
        final Oslp.TimePeriod.Builder oslpTimePeriodBuilder = Oslp.TimePeriod.newBuilder();
        final String startTime = deviceRequest.getPowerUsageHistoryContainer()
                .getTimePeriod()
                .getStartTime()
                .toDateTime(DateTimeZone.UTC)
                .toString(DATETIME_FORMAT);
        final String endTime = deviceRequest.getPowerUsageHistoryContainer()
                .getTimePeriod()
                .getEndTime()
                .toDateTime(DateTimeZone.UTC)
                .toString(DATETIME_FORMAT);

        final Oslp.GetPowerUsageHistoryRequest getPowerUsageHistoryRequest = Oslp.GetPowerUsageHistoryRequest
                .newBuilder()
                .setTimePeriod(oslpTimePeriodBuilder.setStartTime(startTime).setEndTime(endTime))
                .setTermType(oslpHistoryTermType)
                .setPage(pager.getCurrentPage())
                .build();

        final PowerUsageHistoryResponseMessageDataContainerDto powerUsageHistoryResponseMessageDataContainer = new PowerUsageHistoryResponseMessageDataContainerDto(
                powerUsageHistoryData);
        final PageInfoDto pageInfo = new PageInfoDto(pager.getCurrentPage(), pager.getPageSize(),
                pager.getNumberOfPages());
        powerUsageHistoryResponseMessageDataContainer.setPageInfo(pageInfo);
        powerUsageHistoryResponseMessageDataContainer
                .setStartTime(deviceRequest.getPowerUsageHistoryContainer().getTimePeriod().getStartTime());
        powerUsageHistoryResponseMessageDataContainer
                .setEndTime(deviceRequest.getPowerUsageHistoryContainer().getTimePeriod().getEndTime());
        powerUsageHistoryResponseMessageDataContainer
                .setHistoryTermType(deviceRequest.getPowerUsageHistoryContainer().getHistoryTermType());
        powerUsageHistoryResponseMessageDataContainer
                .setRequestContainer(deviceRequest.getPowerUsageHistoryContainer());

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setGetPowerUsageHistoryRequest(getPowerUsageHistoryRequest).build(),
                powerUsageHistoryResponseMessageDataContainer);
    }

    @Override
    public void getStatus(final GetStatusDeviceRequest deviceRequest) {
        LOGGER.info("getStatus() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestGetStatus(deviceRequest);
    }

    @Override
    public void doGetStatus(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.info("doGetStatus() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseGetStatus(deviceRequest, oslpResponse, deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    @Override
    public void resumeSchedule(final ResumeScheduleDeviceRequest deviceRequest) {
        LOGGER.info("resumeSchedule() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestResumeSchedule(deviceRequest);
    }

    @Override
    public void doResumeSchedule(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.info("doResumeSchedule() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseResumeSchedule(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    @Override
    public void setReboot(final DeviceRequest deviceRequest) {
        LOGGER.info("setReboot() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestSetReboot(deviceRequest);
    }

    @Override
    public void doSetReboot(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.info("doSetReboot() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseSetReboot(deviceRequest, oslpResponse, deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    @Override
    public void setTransition(final SetTransitionDeviceRequest deviceRequest) {
        LOGGER.info("setTransition() for device: {}.", deviceRequest.getDeviceIdentification());

        this.buildOslpRequestSetTransition(deviceRequest);
    }

    @Override
    public void doSetTransition(final OslpEnvelope oslpRequest, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.info("doSetTransition() for device: {}.", deviceRequest.getDeviceIdentification());

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseSetTransition(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);
            }
        };

        this.sendMessage(ipAddress, oslpRequest, oslpResponseHandler, deviceRequest);
    }

    private DeviceResponse buildDeviceResponseGetActualPowerUsage(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse) {
        PowerUsageDataDto actualPowerUsageData = null;
        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasGetActualPowerUsageResponse()) {
            final Oslp.GetActualPowerUsageResponse response = oslpResponse.getPayloadMessage()
                    .getGetActualPowerUsageResponse();
            actualPowerUsageData = this.mapper.map(response.getPowerUsageData(), PowerUsageDataDto.class);
            status = this.mapper.map(response.getStatus(), DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        return new GetActualPowerUsageDeviceResponse(deviceRequest, status, actualPowerUsageData);
    }

    private DeviceResponse buildDeviceResponseGetConfiguration(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse) {
        ConfigurationDto configuration = null;
        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasGetConfigurationResponse()) {
            final Oslp.GetConfigurationResponse getConfigurationResponse = oslpResponse.getPayloadMessage()
                    .getGetConfigurationResponse();
            configuration = this.mapper.map(getConfigurationResponse, ConfigurationDto.class);
            status = this.mapper.map(getConfigurationResponse.getStatus(), DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        return new GetConfigurationDeviceResponse(deviceRequest, status, configuration);
    }

    private DeviceResponse buildDeviceResponseSwitchConfiguration(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse) {
        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasSwitchConfigurationResponse()) {
            final Oslp.SwitchConfigurationResponse switchConfigurationResponse = oslpResponse.getPayloadMessage()
                    .getSwitchConfigurationResponse();
            status = this.mapper.map(switchConfigurationResponse.getStatus(), DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        return new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority(), status);
    }

    private DeviceResponse buildDeviceResponseSwitchFirmware(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse) {
        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasSwitchFirmwareResponse()) {
            final Oslp.SwitchFirmwareResponse switchFirmwareResponse = oslpResponse.getPayloadMessage()
                    .getSwitchFirmwareResponse();
            status = this.mapper.map(switchFirmwareResponse.getStatus(), DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        return new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority(), status);
    }

    private DeviceResponse buildDeviceResponseUpdateDeviceSslCertification(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse) {
        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasUpdateDeviceSslCertificationResponse()) {
            final Oslp.UpdateDeviceSslCertificationResponse updateDeviceSslCertificationResponse = oslpResponse
                    .getPayloadMessage()
                    .getUpdateDeviceSslCertificationResponse();

            status = this.mapper.map(updateDeviceSslCertificationResponse.getStatus(), DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        return new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority(), status);
    }

    private DeviceResponse buildDeviceResponseSetDeviceVerificationKey(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse) {
        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasSetDeviceVerificationKeyResponse()) {
            final Oslp.SetDeviceVerificationKeyResponse setDeviceVerificationKeyResponse = oslpResponse
                    .getPayloadMessage()
                    .getSetDeviceVerificationKeyResponse();

            status = this.mapper.map(setDeviceVerificationKeyResponse.getStatus(), DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        return new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority(), status);
    }

    private void buildOslpRequestGetActualPowerUsage(final DeviceRequest deviceRequest) {
        final Oslp.GetActualPowerUsageRequest getActualPowerUsageRequest = Oslp.GetActualPowerUsageRequest.newBuilder()
                .build();

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setGetActualPowerUsageRequest(getActualPowerUsageRequest).build(), null);
    }

    private void buildOslpRequestGetConfiguration(final DeviceRequest deviceRequest) {
        final Oslp.GetConfigurationRequest getConfigurationRequest = Oslp.GetConfigurationRequest.newBuilder().build();

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setGetConfigurationRequest(getConfigurationRequest).build(), null);
    }

    private void buildOslpRequestSwitchConfiguration(final SwitchConfigurationBankRequest deviceRequest) {
        final Oslp.SwitchConfigurationRequest switchConfigurationRequest = Oslp.SwitchConfigurationRequest.newBuilder()
                .setNewConfigurationSet(ByteString.copyFrom(deviceRequest.getConfigurationBank().getBytes()))
                .build();

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setSwitchConfigurationRequest(switchConfigurationRequest).build(),
                deviceRequest.getConfigurationBank());
    }

    private void buildOslpRequestGetFirmwareVersion(final DeviceRequest deviceRequest) {
        final Oslp.GetFirmwareVersionRequest getFirmwareVersionRequest = GetFirmwareVersionRequest.newBuilder().build();

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setGetFirmwareVersionRequest(getFirmwareVersionRequest).build(), null);
    }

    private void buildOslpRequestGetStatus(final DeviceRequest deviceRequest) {
        final Oslp.GetStatusRequest getStatusRequest = GetStatusRequest.newBuilder().build();

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setGetStatusRequest(getStatusRequest).build(), null);
    }

    private void buildOslpRequestResumeSchedule(final ResumeScheduleDeviceRequest deviceRequest) {
        final Oslp.ResumeScheduleRequest.Builder resumeScheduleRequestBuilder = Oslp.ResumeScheduleRequest.newBuilder();
        if (deviceRequest.getResumeScheduleContainer().getIndex() != null) {
            resumeScheduleRequestBuilder.setIndex(ByteString
                    .copyFrom(new byte[] { deviceRequest.getResumeScheduleContainer().getIndex().byteValue() }));

        }
        resumeScheduleRequestBuilder.setImmediate(deviceRequest.getResumeScheduleContainer().isImmediate());

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setResumeScheduleRequest(resumeScheduleRequestBuilder.build()).build(),
                deviceRequest.getResumeScheduleContainer());
    }

    private void buildOslpRequestSetConfiguration(final SetConfigurationDeviceRequest deviceRequest) {
        // First, sort the relay mapping on (internal) index number (FLEX-2514)
        if (deviceRequest.getConfiguration().getRelayConfiguration() != null) {
            Collections.sort(deviceRequest.getConfiguration().getRelayConfiguration().getRelayMap(),
                    (o1, o2) -> o1.getIndex().compareTo(o2.getIndex()));
        }

        final Oslp.SetConfigurationRequest setConfigurationRequest = this.mapper.map(deviceRequest.getConfiguration(),
                Oslp.SetConfigurationRequest.class);

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setSetConfigurationRequest(setConfigurationRequest).build(),
                deviceRequest.getConfiguration());
    }

    private void buildOslpRequestSetEventNotifications(final SetEventNotificationsDeviceRequest deviceRequest) {
        final Oslp.SetEventNotificationsRequest.Builder builder = Oslp.SetEventNotificationsRequest.newBuilder();

        int bitMask = 0;
        for (final EventNotificationTypeDto ent : deviceRequest.getEventNotificationsContainer()
                .getEventNotifications()) {
            bitMask += ent.getValue();
        }

        builder.setNotificationMask(bitMask);

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setSetEventNotificationsRequest(builder.build()).build(),
                deviceRequest.getEventNotificationsContainer());
    }

    private void buildOslpRequestSetLight(final SetLightDeviceRequest deviceRequest) {
        final Oslp.SetLightRequest.Builder setLightRequestBuilder = Oslp.SetLightRequest.newBuilder();

        for (final LightValueDto lightValue : deviceRequest.getLightValuesContainer().getLightValues()) {
            setLightRequestBuilder.addValues(this.buildLightValue(lightValue));
        }

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setSetLightRequest(setLightRequestBuilder.build()).build(),
                deviceRequest.getLightValuesContainer());
    }

    private void buildOslpRequestSetReboot(final DeviceRequest deviceRequest) {
        final Oslp.SetRebootRequest setRebootRequest = Oslp.SetRebootRequest.newBuilder().build();

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setSetRebootRequest(setRebootRequest).build(), null);
    }

    private void buildOslpRequestSetTransition(final SetTransitionDeviceRequest deviceRequest) {
        final Oslp.SetTransitionRequest.Builder setTransitionBuilder = Oslp.SetTransitionRequest.newBuilder()
                .setTransitionType(this.mapper.map(deviceRequest.getTransitionTypeContainer().getTransitionType(),
                        org.opensmartgridplatform.oslp.Oslp.TransitionType.class));
        if (deviceRequest.getTransitionTypeContainer().getDateTime() != null) {
            setTransitionBuilder
                    .setTime(deviceRequest.getTransitionTypeContainer().getDateTime().toString(TIME_FORMAT));
        }

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setSetTransitionRequest(setTransitionBuilder.build()).build(),
                deviceRequest.getTransitionTypeContainer());
    }

    private void buildOslpRequestStartSelfTest(final DeviceRequest deviceRequest) {
        final Oslp.StartSelfTestRequest startSelftestRequest = Oslp.StartSelfTestRequest.newBuilder().build();

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setStartSelfTestRequest(startSelftestRequest).build(), null);
    }

    private void buildOslpRequestStopSelfTest(final DeviceRequest deviceRequest) {
        final Oslp.StopSelfTestRequest stopSelftestRequest = Oslp.StopSelfTestRequest.newBuilder().build();

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setStopSelfTestRequest(stopSelftestRequest).build(), null);
    }

    private void buildOslpRequestUpdateFirmware(final UpdateFirmwareDeviceRequest deviceRequest) {
        final Oslp.UpdateFirmwareRequest updateFirmwareRequest = Oslp.UpdateFirmwareRequest.newBuilder()
                .setFirmwareDomain(deviceRequest.getFirmwareDomain())
                .setFirmwareUrl(deviceRequest.getFirmwareUrl())
                .build();

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setUpdateFirmwareRequest(updateFirmwareRequest).build(), null);
    }

    private void buildOslpRequestSetDeviceVerificationKey(final SetDeviceVerificationKeyDeviceRequest deviceRequest) {
        final Oslp.SetDeviceVerificationKeyRequest setDeviceVerificationKey = Oslp.SetDeviceVerificationKeyRequest
                .newBuilder()
                .setCertificateChunk(ByteString.copyFrom(deviceRequest.getVerificationKey().getBytes()))
                .build();

        this.buildAndSignEnvelope(deviceRequest,
                Oslp.Message.newBuilder().setSetDeviceVerificationKeyRequest(setDeviceVerificationKey).build(),
                deviceRequest.getVerificationKey());
    }

    private void handleOslpResponseGetActualPowerUsage(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final DeviceResponseHandler deviceResponseHandler) {

        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        final DeviceResponse deviceResponse = this.buildDeviceResponseGetActualPowerUsage(deviceRequest, oslpResponse);

        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseGetConfiguration(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler deviceResponseHandler) {

        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        final DeviceResponse deviceResponse = this.buildDeviceResponseGetConfiguration(deviceRequest, oslpResponse);

        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseSwitchConfiguration(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final DeviceResponseHandler deviceResponseHandler) {

        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        final DeviceResponse deviceResponse = this.buildDeviceResponseSwitchConfiguration(deviceRequest, oslpResponse);

        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseGetFirmwareVersion(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        String firmwareVersion = "";

        if (oslpResponse.getPayloadMessage().hasGetFirmwareVersionResponse()) {
            firmwareVersion = oslpResponse.getPayloadMessage().getGetFirmwareVersionResponse().getFirmwareVersion();
        }

        final DeviceResponse deviceResponse = new GetFirmwareVersionDeviceResponse(deviceRequest, firmwareVersion);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseSwitchFirmware(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler deviceResponseHandler) {

        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        final DeviceResponse deviceResponse = this.buildDeviceResponseSwitchFirmware(deviceRequest, oslpResponse);

        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseUpdateDeviceSslCertification(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final DeviceResponseHandler deviceResponseHandler) {

        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        final DeviceResponse deviceResponse = this.buildDeviceResponseUpdateDeviceSslCertification(deviceRequest,
                oslpResponse);

        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseSetDeviceVerificationKey(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final DeviceResponseHandler deviceResponseHandler) {

        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        final DeviceResponse deviceResponse = this.buildDeviceResponseSetDeviceVerificationKey(deviceRequest,
                oslpResponse);

        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseGetStatus(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceStatusDto deviceStatus = null;

        if (oslpResponse.getPayloadMessage().hasGetStatusResponse()) {
            final Oslp.GetStatusResponse getStatusResponse = oslpResponse.getPayloadMessage().getGetStatusResponse();
            final Oslp.Status oslpStatus = getStatusResponse.getStatus();
            if (oslpStatus == Oslp.Status.OK) {
                // Required properties.
                final List<LightValueDto> lightValues = this.mapper.mapAsList(getStatusResponse.getValueList(),
                        LightValueDto.class);
                final LinkTypeDto preferredType = this.getPreferredLinktype(getStatusResponse);
                final LinkTypeDto actualLinkType = this.getActualLinktype(getStatusResponse);
                final LightTypeDto lightType = this.getLightType(getStatusResponse);
                final int eventNotificationMask = getStatusResponse.getEventNotificationMask();

                deviceStatus = new DeviceStatusDto(lightValues, preferredType, actualLinkType, lightType,
                        eventNotificationMask);

                // Optional properties.
                this.setBootLoaderVersion(deviceStatus, getStatusResponse);
                this.setCurrentConfigurationBankUsed(deviceStatus, getStatusResponse);
                this.setCurrentIp(deviceStatus, getStatusResponse);
                this.setCurrentTime(deviceStatus, getStatusResponse);
                this.setDcOutputVoltageCurrent(deviceStatus, getStatusResponse);
                this.setDcOutputVoltageMaximum(deviceStatus, getStatusResponse);
                this.setEventNotificationsMask(deviceStatus, getStatusResponse);
                this.setExternalFlashMemSize(deviceStatus, getStatusResponse);
                this.setFirmwareVersion(deviceStatus, getStatusResponse);
                this.setHardwareId(deviceStatus, getStatusResponse);
                this.setInternalFlashMemSize(deviceStatus, getStatusResponse);
                this.setLastInternalTestResultCode(deviceStatus, getStatusResponse);
                this.setMacAddress(deviceStatus, getStatusResponse);
                this.setMaximumOutputPowerOnDcOutput(deviceStatus, getStatusResponse);
                this.setName(deviceStatus, getStatusResponse);
                this.setNumberOfOutputs(deviceStatus, getStatusResponse);
                this.setSerialNumber(deviceStatus, getStatusResponse);
                this.setStartupCounter(deviceStatus, getStatusResponse);
            } else {
                // handle failure by throwing exceptions if needed
                LOGGER.error("Unable to convert Oslp.GetStatusResponse");
            }
        }

        final DeviceResponse deviceResponse = new GetStatusDeviceResponse(deviceRequest, deviceStatus);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private LinkTypeDto getPreferredLinktype(final Oslp.GetStatusResponse getStatusResponse) {
        return getStatusResponse.getPreferredLinktype().equals(Oslp.LinkType.LINK_NOT_SET) ? null
                : this.mapper.map(getStatusResponse.getPreferredLinktype(), LinkTypeDto.class);
    }

    private LinkTypeDto getActualLinktype(final Oslp.GetStatusResponse getStatusResponse) {
        return getStatusResponse.getActualLinktype().equals(Oslp.LinkType.LINK_NOT_SET) ? null
                : this.mapper.map(getStatusResponse.getActualLinktype(), LinkTypeDto.class);
    }

    private LightTypeDto getLightType(final Oslp.GetStatusResponse getStatusResponse) {
        return getStatusResponse.getLightType().equals(Oslp.LightType.LT_NOT_SET) ? null
                : this.mapper.map(getStatusResponse.getLightType(), LightTypeDto.class);
    }

    private void setBootLoaderVersion(final DeviceStatusDto deviceStatus,
            final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasBootLoaderVersion()) {
            deviceStatus.setBootLoaderVersion(getStatusResponse.getBootLoaderVersion());
        }
    }

    private void setCurrentConfigurationBankUsed(final DeviceStatusDto deviceStatus,
            final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.getCurrentConfigurationBackUsed() != null
                && getStatusResponse.getCurrentConfigurationBackUsed().toByteArray().length == 1) {
            deviceStatus.setCurrentConfigurationBackUsed(
                    this.convertCurrentConfigurationBankUsed(getStatusResponse.getCurrentConfigurationBackUsed()));
        }
    }

    private void setCurrentIp(final DeviceStatusDto deviceStatus, final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasCurrentIp()) {
            deviceStatus.setCurrentIp(getStatusResponse.getCurrentIp());
        }
    }

    private void setCurrentTime(final DeviceStatusDto deviceStatus, final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasCurrentTime()) {
            deviceStatus.setCurrentTime(getStatusResponse.getCurrentTime());
        }
    }

    private void setDcOutputVoltageCurrent(final DeviceStatusDto deviceStatus,
            final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasDcOutputVoltageCurrent()) {
            deviceStatus.setDcOutputVoltageCurrent(getStatusResponse.getDcOutputVoltageCurrent());
        }
    }

    private void setDcOutputVoltageMaximum(final DeviceStatusDto deviceStatus,
            final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasDcOutputVoltageMaximum()) {
            deviceStatus.setDcOutputVoltageMaximum(getStatusResponse.getDcOutputVoltageMaximum());
        }
    }

    private void setEventNotificationsMask(final DeviceStatusDto deviceStatus,
            final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasEventNotificationMask()) {
            deviceStatus.setEventNotificationsMask(getStatusResponse.getEventNotificationMask());
        }
    }

    private void setExternalFlashMemSize(final DeviceStatusDto deviceStatus,
            final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasExternalFlashMemSize()) {
            deviceStatus.setExternalFlashMemSize(getStatusResponse.getExternalFlashMemSize());
        }
    }

    private void setFirmwareVersion(final DeviceStatusDto deviceStatus,
            final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasFirmwareVersion()) {
            deviceStatus.setFirmwareVersion(getStatusResponse.getFirmwareVersion());
        }
    }

    private void setHardwareId(final DeviceStatusDto deviceStatus, final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasHardwareId()) {
            deviceStatus.setHardwareId(getStatusResponse.getHardwareId());
        }
    }

    private void setInternalFlashMemSize(final DeviceStatusDto deviceStatus,
            final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasInternalFlashMemSize()) {
            deviceStatus.setInternalFlashMemSize(getStatusResponse.getInternalFlashMemSize());
        }
    }

    private void setLastInternalTestResultCode(final DeviceStatusDto deviceStatus,
            final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasLastInternalTestResultCode()) {
            deviceStatus.setLastInternalTestResultCode(getStatusResponse.getLastInternalTestResultCode());
        }
    }

    private void setMacAddress(final DeviceStatusDto deviceStatus, final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.getMacAddress() != null && !getStatusResponse.getMacAddress().isEmpty()) {
            deviceStatus.setMacAddress(this.convertMacAddress(getStatusResponse.getMacAddress()));
        }
    }

    private void setMaximumOutputPowerOnDcOutput(final DeviceStatusDto deviceStatus,
            final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasMaximumOutputPowerOnDcOutput()) {
            deviceStatus.setMaximumOutputPowerOnDcOutput(getStatusResponse.getMaximumOutputPowerOnDcOutput());
        }
    }

    private void setName(final DeviceStatusDto deviceStatus, final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasName()) {
            deviceStatus.setName(getStatusResponse.getName());
        }
    }

    private void setNumberOfOutputs(final DeviceStatusDto deviceStatus,
            final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasNumberOfOutputs()) {
            deviceStatus.setNumberOfOutputs(getStatusResponse.getNumberOfOutputs());
        }
    }

    private void setSerialNumber(final DeviceStatusDto deviceStatus, final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasSerialNumber()) {
            deviceStatus.setSerialNumber(this.convertSerialNumber(getStatusResponse.getSerialNumber()));
        }
    }

    private void setStartupCounter(final DeviceStatusDto deviceStatus, final Oslp.GetStatusResponse getStatusResponse) {
        if (getStatusResponse.hasStartupCounter()) {
            deviceStatus.setStartupCounter(getStatusResponse.getStartupCounter());
        }
    }

    private String convertCurrentConfigurationBankUsed(final ByteString byteString) {
        return String.valueOf(byteString.toByteArray()[0]);
    }

    private String convertMacAddress(final ByteString byteString) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final byte b : byteString.toByteArray()) {
            stringBuilder.append(String.format("%02X", b)).append("-");
        }
        final String macAddress = stringBuilder.toString();
        LOGGER.info("macAddress: {}", macAddress);
        return macAddress.substring(0, macAddress.length() - 1);
    }

    private String convertSerialNumber(final ByteString byteString) {
        if (byteString == null) {
            return null;
        }
        final StringBuilder stringBuilder = new StringBuilder();
        for (final byte b : byteString.toByteArray()) {
            stringBuilder.append(String.format("%02X", b));
        }
        return stringBuilder.toString();
    }

    private void handleOslpResponseResumeSchedule(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasResumeScheduleResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getResumeScheduleResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseSetConfiguration(final DeviceRequest setConfigurationDeviceRequest,
            final DeviceRequest setRebootDeviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler setConfigurationDeviceResponseHandler,
            final DeviceResponseHandler setRebootDeviceResponseHandler) {

        this.saveOslpResponseLogEntry(setConfigurationDeviceRequest, oslpResponse);

        this.updateSequenceNumber(setConfigurationDeviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasSetConfigurationResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getSetConfigurationResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(
                setConfigurationDeviceRequest.getOrganisationIdentification(),
                setConfigurationDeviceRequest.getDeviceIdentification(),
                setConfigurationDeviceRequest.getCorrelationUid(), setConfigurationDeviceRequest.getMessagePriority(),
                status);
        setConfigurationDeviceResponseHandler.handleResponse(deviceResponse);

        if (this.executeRebootAfterSetConfiguration && status.equals(DeviceMessageStatus.OK)) {
            LOGGER.info("Sending SetRebootRequest for device: {}",
                    setConfigurationDeviceRequest.getDeviceIdentification());
            this.setReboot(setRebootDeviceRequest);
        } else {
            LOGGER.info(
                    "Not sending SetRebootRequest for device: {} because executeRebootAfterSetConfiguration is false or DeviceMessageStatus is not OK",
                    setConfigurationDeviceRequest.getDeviceIdentification());

            final DeviceResponse emptyDeviceResponse = new EmptyDeviceResponse(
                    setConfigurationDeviceRequest.getOrganisationIdentification(),
                    setConfigurationDeviceRequest.getDeviceIdentification(),
                    setConfigurationDeviceRequest.getCorrelationUid(),
                    setConfigurationDeviceRequest.getMessagePriority(), status);
            setRebootDeviceResponseHandler.handleResponse(emptyDeviceResponse);
        }
    }

    private void handleOslpResponseSetEventNotifications(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasSetEventNotificationsResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage()
                    .getSetEventNotificationsResponse()
                    .getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseSetLight(final DeviceRequest deviceRequest,
            final ResumeScheduleDeviceRequest resumeScheduleDeviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler setLightDeviceResponseHandler,
            final DeviceResponseHandler resumeScheduleDeviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasSetLightResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getSetLightResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        // Send response to the message processor's device response handler.
        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority(), status);
        setLightDeviceResponseHandler.handleResponse(deviceResponse);

        if (this.executeResumeScheduleAfterSetLight && status.equals(DeviceMessageStatus.OK)) {
            LOGGER.info("Sending ResumeScheduleRequest for device: {}", deviceRequest.getDeviceIdentification());
            this.resumeSchedule(resumeScheduleDeviceRequest);
        } else {
            LOGGER.info(
                    "Not sending ResumeScheduleRequest for device: {} because executeResumeScheduleAfterSetLight is false or DeviceMessageStatus is not OK",
                    deviceRequest.getDeviceIdentification());

            final DeviceResponse emptyDeviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), deviceRequest.getMessagePriority(), status);
            resumeScheduleDeviceResponseHandler.handleResponse(emptyDeviceResponse);
        }
    }

    private void handleOslpResponseSetReboot(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasSetRebootResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getSetRebootResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseSetTransition(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler deviceResponseHandler) {

        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);
        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasSetTransitionResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getSetTransitionResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseStartSelfTest(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceMessageStatus status;
        if (oslpResponse.getPayloadMessage().hasStartSelfTestResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getStartSelfTestResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseStopSelfTest(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceMessageStatus status;
        if (oslpResponse.getPayloadMessage().hasStopSelfTestResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getStopSelfTestResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseUpdateFirmware(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasUpdateFirmwareResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getUpdateFirmwareResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(),
                deviceRequest.getMessagePriority(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void buildAndSignEnvelope(final DeviceRequest deviceRequest, final Oslp.Message payloadMessage,
            final Serializable extraData) {

        final String deviceIdentification = deviceRequest.getDeviceIdentification();
        final String organisationIdentification = deviceRequest.getOrganisationIdentification();
        final String correlationUid = deviceRequest.getCorrelationUid();
        final String ipAddress = deviceRequest.getIpAddress();
        final String domain = deviceRequest.getDomain();
        final String domainVersion = deviceRequest.getDomainVersion();
        final String messageType = deviceRequest.getMessageType();
        final int messagePriority = deviceRequest.getMessagePriority();
        final int retryCount = deviceRequest.getRetryCount();
        final boolean isScheduled = deviceRequest.isScheduled();

        // Get some values from the database.
        final OslpDevice oslpDevice = this.oslpDeviceSettingsService
                .getDeviceByDeviceIdentification(deviceIdentification);
        if (oslpDevice == null) {
            LOGGER.error("Unable to find OSLP device: {}", deviceIdentification);
            return;
        }

        final byte[] deviceId = Base64.decodeBase64(oslpDevice.getDeviceUid());
        final byte[] sequenceNumber = SequenceNumberUtils.convertIntegerToByteArray(oslpDevice.getSequenceNumber());

        this.oslpSigningService.buildAndSignEnvelope(organisationIdentification, deviceIdentification, correlationUid,
                deviceId, sequenceNumber, ipAddress, domain, domainVersion, messageType, messagePriority, retryCount,
                isScheduled, payloadMessage, extraData);
    }

    private Oslp.LightValue buildLightValue(final LightValueDto lightValue) {
        final Oslp.LightValue.Builder builder = Oslp.LightValue.newBuilder();

        if (lightValue.getIndex() != null) {
            builder.setIndex(ByteString.copyFrom(new byte[] { lightValue.getIndex().byteValue() }));
        }

        builder.setOn(lightValue.isOn());

        if (lightValue.getDimValue() != null) {
            builder.setDimValue(ByteString.copyFrom(new byte[] { lightValue.getDimValue().byteValue() }));
        }

        return builder.build();
    }

    /**
     * Return the correct port, depending on loopback or external.
     */
    private InetSocketAddress createAddress(final InetAddress address) {
        if (address.isLoopbackAddress()) {
            return new InetSocketAddress(address, this.oslpPortClientLocal);
        }

        return new InetSocketAddress(address, this.oslpPortClient);
    }

    private InetSocketAddress createAddress(final String ipAddress) throws UnknownHostException {
        if (StringUtils.isEmpty(ipAddress)) {
            throw new UnknownHostException("IP address is emtpy!");
        }

        final InetAddress inetAddress = InetAddress.getByName(ipAddress);

        return this.createAddress(inetAddress);
    }

    private void saveOslpResponseLogEntry(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse) {
        final OslpDevice oslpDevice = this.oslpDeviceSettingsService
                .getDeviceByDeviceIdentification(deviceRequest.getDeviceIdentification());

        final OslpLogItemRequestMessage oslpLogItemRequestMessage = new OslpLogItemRequestMessage(
                deviceRequest.getOrganisationIdentification(), oslpDevice.getDeviceUid(),
                deviceRequest.getDeviceIdentification(), true, oslpResponse.isValid(), oslpResponse.getPayloadMessage(),
                oslpResponse.getSize());

        this.oslpLogItemRequestMessageSender.send(oslpLogItemRequestMessage);
    }

    private void saveOslpRequestLogEntry(final DeviceRequest deviceRequest, final OslpEnvelope oslpRequest) {
        final OslpDevice oslpDevice = this.oslpDeviceSettingsService
                .getDeviceByDeviceIdentification(deviceRequest.getDeviceIdentification());

        final OslpLogItemRequestMessage oslpLogItemRequestMessage = new OslpLogItemRequestMessage(
                deviceRequest.getOrganisationIdentification(), oslpDevice.getDeviceUid(),
                deviceRequest.getDeviceIdentification(), false, true, oslpRequest.getPayloadMessage(),
                oslpRequest.getSize());

        this.oslpLogItemRequestMessageSender.send(oslpLogItemRequestMessage);
    }

    private void updateSequenceNumber(final String deviceIdentification, final OslpEnvelope oslpResponse) {
        final Integer sequenceNumber = SequenceNumberUtils.convertByteArrayToInteger(oslpResponse.getSequenceNumber());

        final OslpDevice oslpDevice = this.oslpDeviceSettingsService
                .getDeviceByDeviceIdentification(deviceIdentification);
        oslpDevice.setSequenceNumber(sequenceNumber);
        this.oslpDeviceSettingsService.updateDeviceAndForceSave(oslpDevice);
    }

    private void sendMessage(final String ipAddress, final OslpEnvelope oslpRequest,
            final OslpResponseHandler oslpResponseHandler, final DeviceRequest deviceRequest) throws IOException {
        try {
            this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, oslpResponseHandler,
                    deviceRequest.getDeviceIdentification());
        } catch (final RuntimeException e) {
            LOGGER.error("Exception during sendMessage()", e);
            throw new IOException(e.getMessage());
        }
    }
}
