/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ClearAlarmRegisterRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmRegisterResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ClearAlarmRegisterRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGasResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadGasResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequestDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainSmartMeteringMonitoringService")
@Transactional(value = "transactionManager")
public class MonitoringService {

    private static final String DEVICE_RESPONSE_NOT_OK_LOG_MSG = "Device Response not ok. Unexpected Exception";

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringService.class);

    @Autowired
    @Qualifier(value = "domainSmartMeteringOutgoingOsgpCoreRequestMessageSender")
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    private MonitoringMapper monitoringMapper;

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private WebServiceResponseMessageSender webServiceResponseMessageSender;

    public MonitoringService() {
        // Parameterless constructor required for transactions...
    }

    public void requestPeriodicMeterReads(final DeviceMessageMetadata deviceMessageMetadata,
            final PeriodicMeterReadsQuery periodicMeterReadsValueQuery) throws FunctionalException {

        LOGGER.info("requestPeriodicMeterReads for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeter = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        if (periodicMeterReadsValueQuery.isMbusDevice()) {

            if (smartMeter.getChannel() == null) {
                /*
                 * For now, throw a FunctionalException. As soon as we can
                 * communicate with some types of gas meters directly, and not
                 * through an M-Bus port of an energy meter, this will have to
                 * be changed.
                 */
                throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                        ComponentType.DOMAIN_SMART_METERING,
                        new AssertionError("Meter for gas reads should have a channel configured."));
            }
            final PeriodicMeterReadsRequestDto periodicMeterReadsQuery = new PeriodicMeterReadsRequestDto(
                    PeriodTypeDto.valueOf(periodicMeterReadsValueQuery.getPeriodType().name()),
                    periodicMeterReadsValueQuery.getBeginDate(), periodicMeterReadsValueQuery.getEndDate(),
                    ChannelDto.fromNumber(smartMeter.getChannel()));
            final Device gatewayDevice = smartMeter.getGatewayDevice();
            if (gatewayDevice == null) {
                /*
                 * For now throw a FunctionalException, based on the same
                 * reasoning as with the channel a couple of lines up. As soon
                 * as we have scenario's with direct communication with gas
                 * meters this will have to be changed.
                 */
                throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                        ComponentType.DOMAIN_SMART_METERING,
                        new AssertionError("Meter for gas reads should have an energy meter as gateway device."));
            }
            this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                    deviceMessageMetadata.getOrganisationIdentification(), gatewayDevice.getDeviceIdentification(),
                    gatewayDevice.getIpAddress(), periodicMeterReadsQuery), deviceMessageMetadata.getMessageType(),
                    deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());
        } else {

            this.osgpCoreRequestMessageSender.send(
                    new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                            deviceMessageMetadata.getOrganisationIdentification(),
                            deviceMessageMetadata.getDeviceIdentification(), smartMeter.getIpAddress(),
                            this.monitoringMapper.map(periodicMeterReadsValueQuery,
                                    PeriodicMeterReadsRequestDto.class)),
                    deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                    deviceMessageMetadata.getScheduleTime());
        }
    }

    public void handlePeriodicMeterReadsresponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final PeriodicMeterReadsResponseDto periodMeterReadsValueDTO) {

        LOGGER.info("handlePeriodicMeterReadsresponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception,
                this.monitoringMapper.map(periodMeterReadsValueDTO, PeriodicMeterReadsContainer.class),
                deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata.getMessageType());
    }

    public void handlePeriodicMeterReadsresponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final PeriodicMeterReadGasResponseDto periodMeterReadsValueDTO) {

        LOGGER.info("handlePeriodicMeterReadsresponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception,
                this.monitoringMapper.map(periodMeterReadsValueDTO, PeriodicMeterReadsContainerGas.class),
                deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata.getMessageType());
    }

    public void requestActualMeterReads(final DeviceMessageMetadata deviceMessageMetadata,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsQuery actualMeterReadsQuery)
            throws FunctionalException {

        LOGGER.info("requestActualMeterReads for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeter = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        if (actualMeterReadsQuery.isMbusDevice()) {

            if (smartMeter.getChannel() == null) {
                /*
                 * For now, throw a FunctionalException. As soon as we can
                 * communicate with some types of gas meters directly, and not
                 * through an M-Bus port of an energy meter, this will have to
                 * be changed.
                 */
                throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                        ComponentType.DOMAIN_SMART_METERING,
                        new AssertionError("Meter for gas reads should have a channel configured."));
            }
            final Device gatewayDevice = smartMeter.getGatewayDevice();
            if (gatewayDevice == null) {
                /*
                 * For now throw a FunctionalException, based on the same
                 * reasoning as with the channel a couple of lines up. As soon
                 * as we have scenario's with direct communication with gas
                 * meters this will have to be changed.
                 */
                throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                        ComponentType.DOMAIN_SMART_METERING,
                        new AssertionError("Meter for gas reads should have an energy meter as gateway device."));
            }
            this.osgpCoreRequestMessageSender.send(
                    new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                            deviceMessageMetadata.getOrganisationIdentification(),
                            gatewayDevice.getDeviceIdentification(), gatewayDevice.getIpAddress(),
                            new ActualMeterReadsQueryDto(ChannelDto.fromNumber(smartMeter.getChannel()))),
                    deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                    deviceMessageMetadata.getScheduleTime());
        } else {
            this.osgpCoreRequestMessageSender.send(
                    new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                            deviceMessageMetadata.getOrganisationIdentification(),
                            deviceMessageMetadata.getDeviceIdentification(), smartMeter.getIpAddress(),
                            new ActualMeterReadsQueryDto()),
                    deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                    deviceMessageMetadata.getScheduleTime());
        }
    }

    public void handleActualMeterReadsResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final MeterReadsResponseDto actualMeterReadsDto) {

        LOGGER.info("handleActualMeterReadsResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, this.monitoringMapper.map(actualMeterReadsDto, MeterReads.class),
                deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata.getMessageType());
    }

    public void handleActualMeterReadsResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final MeterReadsGasResponseDto actualMeterReadsGas) {

        LOGGER.info("handleActualMeterReadsResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, this.monitoringMapper.map(actualMeterReadsGas, MeterReadsGas.class),
                deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata.getMessageType());
    }

    public void requestReadAlarmRegister(final DeviceMessageMetadata deviceMessageMetadata,
            final ReadAlarmRegisterRequest readAlarmRegisterRequestValueObject) throws FunctionalException {

        LOGGER.info("requestReadAlarmRegister for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        final ReadAlarmRegisterRequestDto readAlarmRegisterRequestDto = this.monitoringMapper
                .map(readAlarmRegisterRequestValueObject, ReadAlarmRegisterRequestDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), readAlarmRegisterRequestDto),
                deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                deviceMessageMetadata.getScheduleTime());
    }

    public void handleReadAlarmRegisterResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final AlarmRegisterResponseDto alarmRegisterDto) {

        LOGGER.info("handleReadAlarmRegisterResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        final AlarmRegister alarmRegisterValueDomain = this.monitoringMapper.map(alarmRegisterDto, AlarmRegister.class);

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, alarmRegisterValueDomain, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

    public void requestProfileGenericData(final DeviceMessageMetadata deviceMessageMetadata,
            final ProfileGenericDataRequest request) throws FunctionalException {

        LOGGER.info("requestProfileGenericData for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeter = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        final ProfileGenericDataRequestDto requestDto = this.monitoringMapper.map(request,
                ProfileGenericDataRequestDto.class);

        final RequestMessage requestMessage = new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeter.getIpAddress(), requestDto);

        this.osgpCoreRequestMessageSender.send(requestMessage, deviceMessageMetadata.getMessageType(),
                deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());
    }

    public void handleProfileGenericDataResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final ProfileGenericDataResponseDto profileGenericDataResponseDto) {

        LOGGER.info("handleProfileGenericDataResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        final ProfileGenericDataResponse responseVo = this.monitoringMapper.map(profileGenericDataResponseDto,
                ProfileGenericDataResponse.class);

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, responseVo, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());

    }

    public void requestClearAlarmRegister(final DeviceMessageMetadata deviceMessageMetadata,
            final ClearAlarmRegisterRequest clearAlarmRegisterRequestValueObject) throws FunctionalException {

        LOGGER.info("Request clear alarm register for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        final ClearAlarmRegisterRequestDto clearAlarmRegisterRequestDto = this.monitoringMapper
                .map(clearAlarmRegisterRequestValueObject, ClearAlarmRegisterRequestDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), clearAlarmRegisterRequestDto),
                deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                deviceMessageMetadata.getScheduleTime());
    }

    public void handleClearAlarmRegisterResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("Handle clear alarm register response for MessageType: {}", deviceMessageMetadata.getMessageType());

        this.handleMetadataOnlyResponseMessage(deviceMessageMetadata, deviceResult, exception);
    }

    private void handleMetadataOnlyResponseMessage(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, null, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

}
