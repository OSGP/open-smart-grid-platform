/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.infra.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.oslp.application.mapping.OslpMapper;
import com.alliander.osgp.adapter.protocol.oslp.application.services.oslp.OslpDeviceSettingsService;
import com.alliander.osgp.adapter.protocol.oslp.device.DeviceMessageStatus;
import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.GetPowerUsageHistoryDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.ResumeScheduleDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetConfigurationDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetEventNotificationsDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetLightDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetScheduleDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetTransitionDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.UpdateFirmwareDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.responses.EmptyDeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.device.responses.GetActualPowerUsageDeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.device.responses.GetConfigurationDeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.device.responses.GetFirmwareVersionDeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.device.responses.GetPowerUsageHistoryDeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.device.responses.GetStatusDeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.OslpLogItemRequestMessage;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.OslpLogItemRequestMessageSender;
import com.alliander.osgp.dto.valueobjects.Configuration;
import com.alliander.osgp.dto.valueobjects.DeviceStatus;
import com.alliander.osgp.dto.valueobjects.EventNotificationType;
import com.alliander.osgp.dto.valueobjects.LightType;
import com.alliander.osgp.dto.valueobjects.LightValue;
import com.alliander.osgp.dto.valueobjects.LinkType;
import com.alliander.osgp.dto.valueobjects.PowerUsageData;
import com.alliander.osgp.dto.valueobjects.Schedule;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.GetFirmwareVersionRequest;
import com.alliander.osgp.oslp.Oslp.SetScheduleRequest;
import com.alliander.osgp.oslp.Oslp.UpdateFirmwareRequest;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.google.protobuf.ByteString;

@Component
public class OslpDeviceService implements DeviceService {

    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final String TIME_FORMAT = "HHmmss";
    private static final String DATETIME_FORMAT = DATE_FORMAT + TIME_FORMAT;

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpDeviceService.class);

    @Autowired
    private PrivateKey privateKey;

    @Autowired
    private OslpChannelHandlerClient oslpChannelHandler;

    @Autowired
    private OslpMapper mapper;

    @Resource
    private String oslpSignatureProvider;

    @Resource
    private String oslpSignature;

    @Resource
    private int oslpPortClient;

    @Resource
    private int oslpPortClientLocal;

    @Autowired
    private OslpDeviceSettingsService oslpDeviceSettingsService;

    @Autowired
    private OslpLogItemRequestMessageSender oslpLogItemRequestMessageSender;

    @Override
    public void startSelfTest(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final String ipAddress) throws IOException {
        LOGGER.debug("Starting self test for device: {}.");

        final OslpEnvelope oslpRequest = this.buildOslpRequestStartSelfTest(deviceRequest);

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

        this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, responseHandler,
                deviceRequest.getDeviceIdentification());
    }

    @Override
    public void stopSelfTest(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final String ipAddress) throws IOException {
        LOGGER.debug("Stopping self test for device: {}.");

        final OslpEnvelope oslpRequest = this.buildOslpRequestStopSelfTest(deviceRequest);

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler responseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope response) {
                OslpDeviceService.this.handleOslpResponseStopSelfTest(deviceRequest, response, deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);

            }
        };

        this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, responseHandler,
                deviceRequest.getDeviceIdentification());
    }

    @Override
    public void setLight(final SetLightDeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final String ipAddress) throws IOException {
        LOGGER.debug("Setting light for device: {}.", deviceRequest.getDeviceIdentification());

        final OslpEnvelope oslpRequest = this.buildOslpRequestSetLight(deviceRequest);

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseSetLight(deviceRequest, oslpResponse, deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);

            }
        };

        this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, oslpResponseHandler,
                deviceRequest.getDeviceIdentification());
    }

    @Override
    public void setEventNotifications(final SetEventNotificationsDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.debug("Setting event notifications for device: {}.", deviceRequest.getDeviceIdentification());

        final OslpEnvelope oslpRequest = this.buildOslpRequestSetEventNotifications(deviceRequest);

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

        this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, oslpResponseHandler,
                deviceRequest.getDeviceIdentification());
    }

    @Override
    public void updateFirmware(final UpdateFirmwareDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.debug("Updating firmware for device: {}.");

        final OslpEnvelope oslpRequest = this.buildOslpRequestUpdateFirmware(deviceRequest);

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

        this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, oslpResponseHandler,
                deviceRequest.getDeviceIdentification());
    }

    @Override
    public void getFirmwareVersion(final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.debug("Getting firmware version for device: {}.");

        final OslpEnvelope oslpRequest = this.buildOslpRequestGetFirmwareVersion(deviceRequest);

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

        this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, oslpResponseHandler,
                deviceRequest.getDeviceIdentification());
    }

    @Override
    public void setSchedule(final SetScheduleDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.debug("Setting schedules for device: {}.");

        final int pageSize = 5;
        final int numberOfPages = (int) Math.ceil((double) deviceRequest.getSchedules().size() / pageSize);

        if (numberOfPages == 1) {
            this.processOslpRequestSetScheduleSingle(deviceRequest, deviceResponseHandler, ipAddress);
        } else {
            final Pager pager = new Pager(deviceRequest.getSchedules().size(), pageSize);

            this.processOslpRequestSetSchedulePaged(deviceRequest, deviceResponseHandler, ipAddress, pager);
        }
    }

    private void processOslpRequestSetScheduleSingle(final SetScheduleDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {

        LOGGER.debug("Processing single set schedule request for device: {}.", deviceRequest.getDeviceIdentification());

        final OslpEnvelope oslpRequest = this.buildOslpRequestSetScheduleSingle(deviceRequest);

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

        this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, oslpResponseHandler,
                deviceRequest.getDeviceIdentification());
    }

    protected void handleException(final Throwable t, final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler) {

        final DeviceResponse deviceResponse = new DeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid());

        deviceResponseHandler.handleException(t, deviceResponse);

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
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private OslpEnvelope buildOslpRequestSetScheduleSingle(final SetScheduleDeviceRequest deviceRequest) {
        final List<Oslp.Schedule> oslpSchedules = this.convertToOslpSchedules(deviceRequest.getSchedules());

        final Oslp.SetScheduleRequest.Builder request = SetScheduleRequest
                .newBuilder()
                .addAllSchedules(oslpSchedules)
                .setScheduleType(
                        this.mapper.map(deviceRequest.getRelayType(), com.alliander.osgp.oslp.Oslp.RelayType.class));

        return this.getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(Oslp.Message.newBuilder().setSetScheduleRequest(request).build()).build();
    }

    private void processOslpRequestSetSchedulePaged(final SetScheduleDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress, final Pager pager)
                    throws IOException {
        LOGGER.debug("Processing paged set schedule request for device: {}, page {} of {}",
                deviceRequest.getDeviceIdentification(), pager.getCurrentPage(), pager.numberOfPages);

        final OslpEnvelope oslpRequest = this.buildOslpRequestSetSchedulePaged(deviceRequest, pager);

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseSetSchedulePaged(deviceRequest, oslpResponse, ipAddress,
                        pager, deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);

            }
        };

        this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, oslpResponseHandler,
                deviceRequest.getDeviceIdentification());
    }

    private void handleOslpResponseSetSchedulePaged(final SetScheduleDeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final String ipAddress, final Pager pager,
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

            final DeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), status);
            deviceResponseHandler.handleResponse(deviceResponse);

        } else {
            // Process next page
            pager.nextPage();
            try {
                this.processOslpRequestSetSchedulePaged(deviceRequest, deviceResponseHandler, ipAddress, pager);
            } catch (final IOException e) {
                LOGGER.error("IOException", e);
                final DeviceResponse deviceResponse = new EmptyDeviceResponse(
                        deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                        deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);
                deviceResponseHandler.handleResponse(deviceResponse);
            }
        }
    }

    private OslpEnvelope buildOslpRequestSetSchedulePaged(final SetScheduleDeviceRequest deviceRequest,
            final Pager pager) {

        final List<Oslp.Schedule> oslpSchedules = this.convertToOslpSchedules(deviceRequest.getSchedules().subList(
                pager.getIndexFrom(), pager.getIndexTo()));

        final Oslp.SetScheduleRequest.Builder oslpRequestBuilder = SetScheduleRequest
                .newBuilder()
                .addAllSchedules(oslpSchedules)
                .setScheduleType(
                        this.mapper.map(deviceRequest.getRelayType(), com.alliander.osgp.oslp.Oslp.RelayType.class))
                        .setPageInfo(
                                Oslp.PageInfo.newBuilder().setCurrentPage(pager.getCurrentPage())
                                .setPageSize(pager.getPageSize()).setTotalPages(pager.getNumberOfPages()));

        return this.getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(Oslp.Message.newBuilder().setSetScheduleRequest(oslpRequestBuilder).build())
                .build();
    }

    // 1-based pager
    private static class Pager {
        private int currentPage = 1;
        private int itemCount = 0;
        private int pageSize = 5;
        private int numberOfPages = 1;

        public Pager() {

        }

        public Pager(final int itemCount, final int pageSize) {
            this.itemCount = itemCount;
            this.pageSize = pageSize;
            this.numberOfPages = (int) Math.ceil((double) itemCount / (double) pageSize);
        }

        public int getCurrentPage() {
            return this.currentPage;
        }

        public int getIndexFrom() {
            return this.pageSize * (this.currentPage - 1);
        }

        public int getIndexTo() {
            return Math.min(this.pageSize * this.currentPage, this.itemCount);
        }

        public int getPageSize() {
            return this.pageSize;
        }

        public int getNumberOfPages() {
            return this.numberOfPages;
        }

        public void setNumberOfPages(final int numberOfPages) {
            this.numberOfPages = numberOfPages;
        }

        public void nextPage() {
            this.currentPage++;
        }

        public boolean isLastPage() {
            return this.currentPage == this.numberOfPages;
        }
    }

    private List<Oslp.Schedule> convertToOslpSchedules(final List<Schedule> schedules) {
        final List<Oslp.Schedule> oslpSchedules = new ArrayList<Oslp.Schedule>();

        for (final Schedule schedule : schedules) {
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

            for (final LightValue lightValue : schedule.getLightValue()) {
                scheduleBuilder.addValue(this.buildLightValue(lightValue));
            }

            if (schedule.getTriggerType() != null) {
                scheduleBuilder.setTriggerType(Oslp.TriggerType.valueOf(schedule.getTriggerType().ordinal() + 1));
            }

            oslpSchedules.add(scheduleBuilder.build());
        }
        return oslpSchedules;
    }

    @Override
    public void setConfiguration(final SetConfigurationDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.debug("Setting configuration for device: {}.", deviceRequest.getDeviceIdentification());

        final OslpEnvelope oslpRequest = this.buildOslpRequestSetConfiguration(deviceRequest);

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseSetConfiguration(deviceRequest, oslpResponse,
                        deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);

            }
        };

        this.oslpChannelHandler.send(this.createAddress(InetAddress.getByName(ipAddress)), oslpRequest,
                oslpResponseHandler, deviceRequest.getDeviceIdentification());
    }

    @Override
    public void getConfiguration(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final String ipAddress) throws IOException {
        LOGGER.debug("Getting configuration for device: {}.", deviceRequest.getDeviceIdentification());

        final OslpEnvelope oslpRequest = this.buildOslpRequestGetConfiguration(deviceRequest);

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

        this.oslpChannelHandler.send(this.createAddress(InetAddress.getByName(ipAddress)), oslpRequest,
                oslpResponseHandler, deviceRequest.getDeviceIdentification());
    }

    @Override
    public void getActualPowerUsage(final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.debug("Get actual power use for device: {}.");

        final OslpEnvelope oslpRequest = this.buildOslpRequestGetActualPowerUsage(deviceRequest);

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

        this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, oslpResponseHandler,
                deviceRequest.getDeviceIdentification());
    }

    @Override
    public void getPowerUsageHistory(final GetPowerUsageHistoryDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.debug("Get power usage history for device: {}.", deviceRequest.getDeviceIdentification());

        final Pager pager = new Pager();
        final List<PowerUsageData> powerUsageHistoryData = new ArrayList<>();

        this.processOslpRequestGetPowerUsageHistory(deviceRequest, pager, powerUsageHistoryData, deviceResponseHandler,
                ipAddress);
    }

    private void processOslpRequestGetPowerUsageHistory(final GetPowerUsageHistoryDeviceRequest deviceRequest,
            final Pager pager, final List<PowerUsageData> powerUsageHistoryData,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.info("GetPowerUsageHistory() for device: {}, page: {}", deviceRequest.getDeviceIdentification(),
                pager.getCurrentPage());

        final OslpEnvelope oslpRequest = this.buildOslpRequestGetPowerUsageHistory(deviceRequest, pager);

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler oslpResponseHandler = new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
                OslpDeviceService.this.handleOslpResponseGetPowerUsageHistory(deviceRequest, oslpResponse, pager,
                        powerUsageHistoryData, deviceResponseHandler, ipAddress);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);

            }
        };

        this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, oslpResponseHandler,
                deviceRequest.getDeviceIdentification());
    }

    private void handleOslpResponseGetPowerUsageHistory(final GetPowerUsageHistoryDeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final Pager pager, final List<PowerUsageData> powerUsageHistoryData,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) {

        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        // Get response status
        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasGetPowerUsageHistoryResponse()) {
            final Oslp.GetPowerUsageHistoryResponse getPowerUsageHistoryResponse = oslpResponse.getPayloadMessage()
                    .getGetPowerUsageHistoryResponse();
            status = this.mapper.map(getPowerUsageHistoryResponse.getStatus(), DeviceMessageStatus.class);
            powerUsageHistoryData.addAll(this.mapper.mapAsList(getPowerUsageHistoryResponse.getPowerUsageDataList(),
                    PowerUsageData.class));

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
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), status, powerUsageHistoryData);
            deviceResponseHandler.handleResponse(deviceResponse);

        } else {
            // Process next page
            pager.nextPage();
            try {
                this.processOslpRequestGetPowerUsageHistory(deviceRequest, pager, powerUsageHistoryData,
                        deviceResponseHandler, ipAddress);
            } catch (final IOException e) {
                LOGGER.error("IOException", e);
                final GetPowerUsageHistoryDeviceResponse deviceResponse = new GetPowerUsageHistoryDeviceResponse(
                        deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                        deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE, null);
                deviceResponseHandler.handleResponse(deviceResponse);
            }
        }
    }

    private OslpEnvelope buildOslpRequestGetPowerUsageHistory(final GetPowerUsageHistoryDeviceRequest deviceRequest,
            final Pager pager) {
        final Oslp.HistoryTermType oslpHistoryTermType = this.mapper.map(deviceRequest.getHistoryTermType(),
                Oslp.HistoryTermType.class);

        final Oslp.Message.Builder oslpMessageBuilder = Oslp.Message.newBuilder();
        final Oslp.GetPowerUsageHistoryRequest.Builder oslpRequestBuilder = Oslp.GetPowerUsageHistoryRequest
                .newBuilder();
        final Oslp.TimePeriod.Builder oslpTimePeriodBuilder = Oslp.TimePeriod.newBuilder();
        final String startTime = deviceRequest.getTimePeriod().getStartTime().toDateTime(DateTimeZone.UTC)
                .toString(DATETIME_FORMAT);
        final String endTime = deviceRequest.getTimePeriod().getEndTime().toDateTime(DateTimeZone.UTC)
                .toString(DATETIME_FORMAT);

        return this
                .getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(
                        oslpMessageBuilder.setGetPowerUsageHistoryRequest(
                                oslpRequestBuilder
                                .setTimePeriod(
                                        oslpTimePeriodBuilder.setStartTime(startTime).setEndTime(endTime))
                                        .setTermType(oslpHistoryTermType).setPage(pager.getCurrentPage())).build())
                                        .build();
    }

    @Override
    public void getStatus(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final String ipAddress) throws IOException {
        LOGGER.debug("Getting status for device: {}.", deviceRequest.getDeviceIdentification());

        final OslpEnvelope oslpRequest = this.buildOslpRequestGetStatus(deviceRequest);

        this.saveOslpRequestLogEntry(deviceRequest, oslpRequest);

        final OslpResponseHandler responseHandler = new OslpResponseHandler() {
            @Override
            public void handleResponse(final OslpEnvelope response) {
                OslpDeviceService.this.handleOslpResponseGetStatus(deviceRequest, response, deviceResponseHandler);
            }

            @Override
            public void handleException(final Throwable t) {
                OslpDeviceService.this.handleException(t, deviceRequest, deviceResponseHandler);

            }
        };

        this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, responseHandler,
                deviceRequest.getDeviceIdentification());
    }

    @Override
    public void resumeSchedule(final ResumeScheduleDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.debug("Resuming schedule for device: {}.", deviceRequest.getDeviceIdentification());

        final OslpEnvelope oslpRequest = this.buildOslpRequestResumeSchedule(deviceRequest);

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

        this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, oslpResponseHandler,
                deviceRequest.getDeviceIdentification());
    }

    @Override
    public void setReboot(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final String ipAddress) throws IOException {
        LOGGER.debug("Setting reboot for device: {}.", deviceRequest.getDeviceIdentification());

        final OslpEnvelope oslpRequest = this.buildOslpRequestSetReboot(deviceRequest);

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

        this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, oslpResponseHandler,
                deviceRequest.getDeviceIdentification());
    }

    @Override
    public void setTransition(final SetTransitionDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler, final String ipAddress) throws IOException {
        LOGGER.debug("Setting transition for device: {}.", deviceRequest.getDeviceIdentification());

        final OslpEnvelope oslpRequest = this.buildOslpRequestSetTransition(deviceRequest);

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

        this.oslpChannelHandler.send(this.createAddress(ipAddress), oslpRequest, oslpResponseHandler,
                deviceRequest.getDeviceIdentification());
    }

    private DeviceResponse buildDeviceResponseGetActualPowerUsage(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse) {
        PowerUsageData actualPowerUsageData = null;
        DeviceMessageStatus status = null;

        if (oslpResponse.getPayloadMessage().hasGetActualPowerUsageResponse()) {
            final Oslp.GetActualPowerUsageResponse response = oslpResponse.getPayloadMessage()
                    .getGetActualPowerUsageResponse();
            actualPowerUsageData = this.mapper.map(response.getPowerUsageData(), PowerUsageData.class);
            status = this.mapper.map(response.getStatus(), DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        return new GetActualPowerUsageDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(), status,
                actualPowerUsageData);
    }

    private DeviceResponse buildDeviceResponseGetConfiguration(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse) {
        Configuration configuration = null;
        DeviceMessageStatus status = null;

        if (oslpResponse.getPayloadMessage().hasGetConfigurationResponse()) {
            final Oslp.GetConfigurationResponse getConfigurationResponse = oslpResponse.getPayloadMessage()
                    .getGetConfigurationResponse();
            configuration = this.mapper.map(getConfigurationResponse, Configuration.class);
            status = this.mapper.map(getConfigurationResponse.getStatus(), DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        return new GetConfigurationDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(), status, configuration);
    }

    private OslpEnvelope buildOslpRequestGetActualPowerUsage(final DeviceRequest deviceRequest) {
        return this
                .getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(
                        Oslp.Message.newBuilder()
                        .setGetActualPowerUsageRequest(Oslp.GetActualPowerUsageRequest.newBuilder()).build())
                        .build();
    }

    private OslpEnvelope buildOslpRequestGetConfiguration(final DeviceRequest deviceRequest) {
        final Oslp.GetConfigurationRequest getConfigurationRequest = Oslp.GetConfigurationRequest.newBuilder().build();

        final Oslp.Message.Builder messageBuilder = Oslp.Message.newBuilder().setGetConfigurationRequest(
                getConfigurationRequest);

        return this.getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(messageBuilder.build()).build();
    }

    private OslpEnvelope buildOslpRequestGetFirmwareVersion(final DeviceRequest deviceRequest) {
        return this
                .getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(
                        Oslp.Message.newBuilder().setGetFirmwareVersionRequest(GetFirmwareVersionRequest.newBuilder())
                        .build()).build();
    }

    private OslpEnvelope buildOslpRequestGetStatus(final DeviceRequest deviceRequest) {
        final Oslp.GetStatusRequest.Builder getStatusRequestBuilder = Oslp.GetStatusRequest.newBuilder();

        return this.getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(Oslp.Message.newBuilder().setGetStatusRequest(getStatusRequestBuilder).build())
                .build();
    }

    private OslpEnvelope buildOslpRequestResumeSchedule(final ResumeScheduleDeviceRequest deviceRequest) {
        final Oslp.ResumeScheduleRequest.Builder resumeScheduleRequestBuilder = Oslp.ResumeScheduleRequest.newBuilder();
        if (deviceRequest.getIndex() != null) {
            resumeScheduleRequestBuilder.setIndex(ByteString
                    .copyFrom(new byte[] { deviceRequest.getIndex().byteValue() }));

        }
        resumeScheduleRequestBuilder.setImmediate(deviceRequest.isImmediate());

        return this
                .getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(
                        Oslp.Message.newBuilder().setResumeScheduleRequest(resumeScheduleRequestBuilder).build())
                        .build();
    }

    private OslpEnvelope buildOslpRequestSetConfiguration(final SetConfigurationDeviceRequest deviceRequest) {
        final Oslp.SetConfigurationRequest setConfigurationRequest = this.mapper.map(deviceRequest.getConfiguration(),
                Oslp.SetConfigurationRequest.class);

        final Oslp.Message.Builder messageBuilder = Oslp.Message.newBuilder();
        messageBuilder.setSetConfigurationRequest(setConfigurationRequest);

        final Oslp.Message message = messageBuilder.build();

        return this.getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification()).withPayloadMessage(message)
                .build();
    }

    private OslpEnvelope buildOslpRequestSetEventNotifications(final SetEventNotificationsDeviceRequest deviceRequest) {
        final Oslp.SetEventNotificationsRequest.Builder builder = Oslp.SetEventNotificationsRequest.newBuilder();

        int bitMask = 0;
        for (final EventNotificationType ent : deviceRequest.getEventNotifications()) {
            bitMask += ent.getValue();
        }

        builder.setNotificationMask(bitMask);

        return this.getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(Oslp.Message.newBuilder().setSetEventNotificationsRequest(builder).build()).build();
    }

    private OslpEnvelope buildOslpRequestSetLight(final SetLightDeviceRequest deviceRequest) {
        final Oslp.SetLightRequest.Builder setLightRequestBuilder = Oslp.SetLightRequest.newBuilder();

        for (final LightValue lightValue : deviceRequest.getLightValues()) {
            setLightRequestBuilder.addValues(this.buildLightValue(lightValue));
        }

        return this.getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(Oslp.Message.newBuilder().setSetLightRequest(setLightRequestBuilder).build())
                .build();
    }

    private OslpEnvelope buildOslpRequestSetReboot(final DeviceRequest deviceRequest) {
        final Oslp.SetRebootRequest.Builder setRebootRequestBuilder = Oslp.SetRebootRequest.newBuilder();

        return this.getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(Oslp.Message.newBuilder().setSetRebootRequest(setRebootRequestBuilder).build())
                .build();
    }

    private OslpEnvelope buildOslpRequestSetTransition(final SetTransitionDeviceRequest deviceRequest) {
        final Oslp.SetTransitionRequest.Builder setTransitionBuilder = Oslp.SetTransitionRequest.newBuilder()
                .setTransitionType(
                        this.mapper.map(deviceRequest.getTransitionType(),
                                com.alliander.osgp.oslp.Oslp.TransitionType.class));
        if (deviceRequest.getTransitionTime() != null) {
            setTransitionBuilder.setTime(deviceRequest.getTransitionTime().toString(TIME_FORMAT));
        }

        return this.getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(Oslp.Message.newBuilder().setSetTransitionRequest(setTransitionBuilder).build())
                .build();
    }

    private OslpEnvelope buildOslpRequestStartSelfTest(final DeviceRequest deviceRequest) {
        return this
                .getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(
                        Oslp.Message.newBuilder().setStartSelfTestRequest(Oslp.StartSelfTestRequest.newBuilder())
                        .build()).build();
    }

    private OslpEnvelope buildOslpRequestStopSelfTest(final DeviceRequest deviceRequest) {
        return this
                .getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(
                        Oslp.Message.newBuilder().setStopSelfTestRequest(Oslp.StopSelfTestRequest.newBuilder()).build())
                        .build();
    }

    private OslpEnvelope buildOslpRequestUpdateFirmware(final UpdateFirmwareDeviceRequest deviceRequest) {
        return this
                .getBasicEnvelopeBuilder(deviceRequest.getDeviceIdentification())
                .withPayloadMessage(
                        Oslp.Message
                        .newBuilder()
                        .setUpdateFirmwareRequest(
                                UpdateFirmwareRequest.newBuilder()
                                .setFirmwareDomain(deviceRequest.getFirmwareDomain())
                                .setFirmwareUrl(deviceRequest.getFirmwareUrl())).build()).build();
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

    private void handleOslpResponseGetFirmwareVersion(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        String firmwareVersion = "";

        if (oslpResponse.getPayloadMessage().hasGetFirmwareVersionResponse()) {
            firmwareVersion = oslpResponse.getPayloadMessage().getGetFirmwareVersionResponse().getFirmwareVersion();
        }

        final DeviceResponse deviceResponse = new GetFirmwareVersionDeviceResponse(
                deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), firmwareVersion);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseGetStatus(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceStatus deviceStatus = null;

        if (oslpResponse.getPayloadMessage().hasGetStatusResponse()) {
            final Oslp.GetStatusResponse getStatusResponse = oslpResponse.getPayloadMessage().getGetStatusResponse();
            final Oslp.Status oslpStatus = getStatusResponse.getStatus();
            // TODO handle failure by throwing exceptions
            if (oslpStatus == Oslp.Status.OK) {
                deviceStatus = new DeviceStatus(this.mapper.mapAsList(getStatusResponse.getValueList(),
                        LightValue.class), this.mapper.map(getStatusResponse.getPreferredLinktype(), LinkType.class),
                        this.mapper.map(getStatusResponse.getActualLinktype(), LinkType.class), this.mapper.map(
                                getStatusResponse.getLightType(), LightType.class),
                                getStatusResponse.getEventNotificationMask());
            }
        }

        final DeviceResponse deviceResponse = new GetStatusDeviceResponse(
                deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), deviceStatus);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseResumeSchedule(final ResumeScheduleDeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final DeviceResponseHandler deviceResponseHandler) {
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
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseSetConfiguration(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler deviceResponseHandler) {

        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasSetConfigurationResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getSetConfigurationResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseSetEventNotifications(final DeviceRequest deviceRequest,
            final OslpEnvelope oslpResponse, final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasSetEventNotificationsResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getSetEventNotificationsResponse()
                    .getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseSetLight(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceMessageStatus status;

        if (oslpResponse.getPayloadMessage().hasSetLightResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getSetLightResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
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
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(), status);
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
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseStartSelfTest(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceMessageStatus status = null;
        if (oslpResponse.getPayloadMessage().hasStartSelfTestResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getStartSelfTestResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private void handleOslpResponseStopSelfTest(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse,
            final DeviceResponseHandler deviceResponseHandler) {
        this.saveOslpResponseLogEntry(deviceRequest, oslpResponse);

        this.updateSequenceNumber(deviceRequest.getDeviceIdentification(), oslpResponse);

        DeviceMessageStatus status = null;
        if (oslpResponse.getPayloadMessage().hasStopSelfTestResponse()) {
            final Oslp.Status oslpStatus = oslpResponse.getPayloadMessage().getStopSelfTestResponse().getStatus();
            status = this.mapper.map(oslpStatus, DeviceMessageStatus.class);
        } else {
            status = DeviceMessageStatus.FAILURE;
        }

        final DeviceResponse deviceResponse = new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(), status);
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
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(), status);
        deviceResponseHandler.handleResponse(deviceResponse);
    }

    private OslpEnvelope.Builder getBasicEnvelopeBuilder(final String deviceIdentification) {
        final OslpDevice oslpDevice = this.oslpDeviceSettingsService
                .getDeviceByDeviceIdentification(deviceIdentification);

        final byte[] deviceId = Base64.decodeBase64(oslpDevice.getDeviceUid());
        final byte[] sequenceNumber = SequenceNumberUtils.convertIntegerToByteArray(oslpDevice.getSequenceNumber());

        return new OslpEnvelope.Builder().withSignature(this.oslpSignature).withProvider(this.oslpSignatureProvider)
                .withPrimaryKey(this.privateKey).withDeviceId(deviceId).withSequenceNumber(sequenceNumber);
    }

    private Oslp.LightValue buildLightValue(final LightValue lightValue) {
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
     *
     * @param address
     * @return
     */
    private InetSocketAddress createAddress(final InetAddress address) {
        if (address.isLoopbackAddress()) {
            return new InetSocketAddress(address, this.oslpPortClientLocal);
        }

        return new InetSocketAddress(address, this.oslpPortClient);
    }

    private InetSocketAddress createAddress(final String ipAddress) throws UnknownHostException {
        final InetAddress inetAddress = InetAddress.getByName(ipAddress);

        return this.createAddress(inetAddress);
    }

    private void saveOslpResponseLogEntry(final DeviceRequest deviceRequest, final OslpEnvelope oslpResponse) {
        final OslpDevice oslpDevice = this.oslpDeviceSettingsService.getDeviceByDeviceIdentification(deviceRequest
                .getDeviceIdentification());

        final OslpLogItemRequestMessage oslpLogItemRequestMessage = new OslpLogItemRequestMessage(
                deviceRequest.getOrganisationIdentification(), oslpDevice.getDeviceUid(),
                deviceRequest.getDeviceIdentification(), true, oslpResponse.isValid(),
                oslpResponse.getPayloadMessage(), oslpResponse.getSize());

        this.oslpLogItemRequestMessageSender.send(oslpLogItemRequestMessage);
    }

    private void saveOslpRequestLogEntry(final DeviceRequest deviceRequest, final OslpEnvelope oslpRequest) {
        final OslpDevice oslpDevice = this.oslpDeviceSettingsService.getDeviceByDeviceIdentification(deviceRequest
                .getDeviceIdentification());

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

    // === PROTECTED SETTERS FOR TESTING ===

    public void setPrivateKey(final PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public void setProvider(final String provider) {
        this.oslpSignatureProvider = provider;
    }

    public void setSignature(final String signature) {
        this.oslpSignature = signature;
    }

    public void setOslpPortClient(final int oslpPortClient) {
        this.oslpPortClient = oslpPortClient;
    }

    public void setOslpPortClientLocal(final int oslpPortClientLocal) {
        this.oslpPortClientLocal = oslpPortClientLocal;
    }

    public void setMapper(final OslpMapper mapper) {
        this.mapper = mapper;
    }

    public void setOslpChannelHandler(final OslpChannelHandlerClient channelHandler) {
        this.oslpChannelHandler = channelHandler;
    }
}
