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
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsQuery;
import com.alliander.osgp.dto.valueobjects.smartmetering.Channel;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery;
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

    public void requestPeriodicMeterReads(
            final DeviceMessageMetadata deviceMessageMetadata,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery periodicMeterReadsValueQuery)
                    throws FunctionalException {

        LOGGER.info("requestPeriodicMeterReads for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        // TODO: bypassing authorization, this should be fixed.

        final SmartMeter smartMeter = this.domainHelperService.findSmartMeter(deviceMessageMetadata
                .getDeviceIdentification());

        if (periodicMeterReadsValueQuery.isMbusDevice()) {

            if (smartMeter.getChannel() == null) {
                /*
                 * For now, throw a FunctionalException. As soon as we can
                 * communicate with some types of gas meters directly, and not
                 * through an M-Bus port of an energy meter, this will have to
                 * be changed.
                 */
                throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                        ComponentType.DOMAIN_SMART_METERING, new AssertionError(
                                "Meter for gas reads should have a channel configured."));
            }
            final com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery periodicMeterReadsQuery = new PeriodicMeterReadsQuery(
                    PeriodType.valueOf(periodicMeterReadsValueQuery.getPeriodType().name()),
                    periodicMeterReadsValueQuery.getBeginDate(), periodicMeterReadsValueQuery.getEndDate(),
                    Channel.fromNumber(smartMeter.getChannel()));
            final Device gatewayDevice = smartMeter.getGatewayDevice();
            if (gatewayDevice == null) {
                /*
                 * For now throw a FunctionalException, based on the same
                 * reasoning as with the channel a couple of lines up. As soon
                 * as we have scenario's with direct communication with gas
                 * meters this will have to be changed.
                 */
                throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                        ComponentType.DOMAIN_SMART_METERING, new AssertionError(
                                "Meter for gas reads should have an energy meter as gateway device."));
            }
            this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                    deviceMessageMetadata.getOrganisationIdentification(), gatewayDevice.getDeviceIdentification(),
                    gatewayDevice.getIpAddress(), periodicMeterReadsQuery), deviceMessageMetadata.getMessageType(),
                    deviceMessageMetadata.getMessagePriority());
        } else {

            this.osgpCoreRequestMessageSender.send(
                    new RequestMessage(deviceMessageMetadata.getCorrelationUid(), deviceMessageMetadata
                            .getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                            smartMeter.getIpAddress(), this.monitoringMapper.map(periodicMeterReadsValueQuery,
                                    PeriodicMeterReadsQuery.class)), deviceMessageMetadata.getMessageType(),
                                    deviceMessageMetadata.getMessagePriority());
        }
    }

    public void handlePeriodicMeterReadsresponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final PeriodicMeterReadsContainer periodMeterReadsValueDTO) {

        LOGGER.info("handlePeriodicMeterReadsresponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender
        .send(new ResponseMessage(
                deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(),
                deviceMessageMetadata.getDeviceIdentification(),
                result,
                exception,
                this.monitoringMapper
                .map(periodMeterReadsValueDTO,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadContainer.class),
                        deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata.getMessageType());
    }

    public void handlePeriodicMeterReadsresponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final PeriodicMeterReadsContainerGas periodMeterReadsValueDTO) {

        LOGGER.info("handlePeriodicMeterReadsresponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender
        .send(new ResponseMessage(
                deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(),
                deviceMessageMetadata.getDeviceIdentification(),
                result,
                exception,
                this.monitoringMapper
                .map(periodMeterReadsValueDTO,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas.class),
                        deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata.getMessageType());
    }

    public void requestActualMeterReads(final DeviceMessageMetadata deviceMessageMetadata,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsQuery actualMeterReadsQuery)
                    throws FunctionalException {

        LOGGER.info("requestActualMeterReads for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeter = this.domainHelperService.findSmartMeter(deviceMessageMetadata
                .getDeviceIdentification());

        if (actualMeterReadsQuery.isMbusDevice()) {

            if (smartMeter.getChannel() == null) {
                /*
                 * For now, throw a FunctionalException. As soon as we can
                 * communicate with some types of gas meters directly, and not
                 * through an M-Bus port of an energy meter, this will have to
                 * be changed.
                 */
                throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                        ComponentType.DOMAIN_SMART_METERING, new AssertionError(
                                "Meter for gas reads should have a channel configured."));
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
                        ComponentType.DOMAIN_SMART_METERING, new AssertionError(
                                "Meter for gas reads should have an energy meter as gateway device."));
            }
            this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                    deviceMessageMetadata.getOrganisationIdentification(), gatewayDevice.getDeviceIdentification(),
                    gatewayDevice.getIpAddress(),
                    new ActualMeterReadsQuery(Channel.fromNumber(smartMeter.getChannel()))), deviceMessageMetadata
                    .getMessageType(), deviceMessageMetadata.getMessagePriority());
        } else {
            this.osgpCoreRequestMessageSender.send(
                    new RequestMessage(deviceMessageMetadata.getCorrelationUid(), deviceMessageMetadata
                            .getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                            smartMeter.getIpAddress(), new ActualMeterReadsQuery()), deviceMessageMetadata
                            .getMessageType(), deviceMessageMetadata.getMessagePriority());
        }
    }

    public void handleActualMeterReadsResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final MeterReads actualMeterReadsDto) {

        LOGGER.info("handleActualMeterReadsResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(
                new ResponseMessage(deviceMessageMetadata.getCorrelationUid(), deviceMessageMetadata
                        .getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(), result,
                        exception, this.monitoringMapper.map(actualMeterReadsDto,
                                com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReads.class),
                                deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata.getMessageType());
    }

    public void handleActualMeterReadsResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final MeterReadsGas actualMeterReadsGas) {

        LOGGER.info("handleActualMeterReadsResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(
                new ResponseMessage(deviceMessageMetadata.getCorrelationUid(), deviceMessageMetadata
                        .getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(), result,
                        exception, this.monitoringMapper.map(actualMeterReadsGas,
                                com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas.class),
                        deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata.getMessageType());
    }

    public void requestReadAlarmRegister(
            final DeviceMessageMetadata deviceMessageMetadata,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest readAlarmRegisterRequestValueObject)
                    throws FunctionalException {

        LOGGER.info("requestReadAlarmRegister for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService.findSmartMeter(deviceMessageMetadata
                .getDeviceIdentification());

        final com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequest readAlarmRegisterRequestDto = this.monitoringMapper
                .map(readAlarmRegisterRequestValueObject,
                        com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequest.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), readAlarmRegisterRequestDto), deviceMessageMetadata
                .getMessageType(), deviceMessageMetadata.getMessagePriority());
    }

    public void handleReadAlarmRegisterResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final com.alliander.osgp.dto.valueobjects.smartmetering.AlarmRegister alarmRegisterDto) {

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
}
