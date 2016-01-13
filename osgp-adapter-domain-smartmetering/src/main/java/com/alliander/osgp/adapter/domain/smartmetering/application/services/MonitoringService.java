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
import com.alliander.osgp.domain.core.entities.GasMeterDevice;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsQuery;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
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
            @Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final String correlationUid,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery periodicMeterReadsValueQuery,
            final String messageType) throws FunctionalException {

        LOGGER.info("requestPeriodicMeterReads for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        // TODO: bypassing authorization, this should be fixed.

        if (periodicMeterReadsValueQuery.isGas()) {
            final GasMeterDevice findGASMeteringDevice = this.domainHelperService
                    .findGASMeteringDevice(deviceIdentification);
            // NOTICE no mapping for GAS because channel comes from
            // administration, not from value object
            final com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery periodicMeterReadsQuery = new PeriodicMeterReadsQuery(
                    PeriodType.valueOf(periodicMeterReadsValueQuery.getPeriodType().name()),
                    periodicMeterReadsValueQuery.getBeginDate(), periodicMeterReadsValueQuery.getEndDate(),
                    findGASMeteringDevice.getChannel());
            final SmartMeter smartMeteringDevice = this.domainHelperService
                    .findSmartMeteringDevice(findGASMeteringDevice.getSmartMeterId());
            this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                    findGASMeteringDevice.getSmartMeterId(), smartMeteringDevice.getIpAddress(),
                    periodicMeterReadsQuery), messageType);
        } else {
            // call triggers functionalexception when no device found
            final SmartMeter smartMeteringDevice = this.domainHelperService
                    .findSmartMeteringDevice(deviceIdentification);
            this.osgpCoreRequestMessageSender.send(
                    new RequestMessage(correlationUid, organisationIdentification, deviceIdentification,
                            smartMeteringDevice.getIpAddress(), this.monitoringMapper.map(periodicMeterReadsValueQuery,
                                    PeriodicMeterReadsQuery.class)), messageType);
        }
    }

    public void handlePeriodicMeterReadsresponse(final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final PeriodicMeterReadsContainer periodMeterReadsValueDTO) {

        LOGGER.info("handlePeriodicMeterReadsresponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender
                .send(new ResponseMessage(
                        correlationUid,
                        organisationIdentification,
                        deviceIdentification,
                        result,
                        exception,
                        this.monitoringMapper
                                .map(periodMeterReadsValueDTO,
                                        com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadContainer.class)),
                        messageType);

    }

    public void handlePeriodicMeterReadsresponse(final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final PeriodicMeterReadsContainerGas periodMeterReadsValueDTO) {

        LOGGER.info("handlePeriodicMeterReadsresponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender
                .send(new ResponseMessage(
                        correlationUid,
                        organisationIdentification,
                        deviceIdentification,
                        result,
                        exception,
                        this.monitoringMapper
                                .map(periodMeterReadsValueDTO,
                                        com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas.class)),
                        messageType);

    }

    public void requestActualMeterReads(
            @Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final String correlationUid,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsQuery actualMeterReadsQuery,
            final String messageType) throws FunctionalException {

        LOGGER.info("requestActualMeterReads for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        if (actualMeterReadsQuery.isGas()) {
            final GasMeterDevice findGASMeteringDevice = this.domainHelperService
                    .findGASMeteringDevice(deviceIdentification);
            final SmartMeter smartMeteringDevice = this.domainHelperService
                    .findSmartMeteringDevice(findGASMeteringDevice.getSmartMeterId());
            this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                    findGASMeteringDevice.getSmartMeterId(), smartMeteringDevice.getIpAddress(),
                    new ActualMeterReadsQuery(findGASMeteringDevice.getChannel())), messageType);
        } else {
            // call triggers functionalexception when no device found
            final SmartMeter smartMeteringDevice = this.domainHelperService
                    .findSmartMeteringDevice(deviceIdentification);
            this.osgpCoreRequestMessageSender
            .send(new RequestMessage(correlationUid, organisationIdentification, deviceIdentification,
                    smartMeteringDevice.getIpAddress(), new ActualMeterReadsQuery()), messageType);
        }
    }

    public void handleActualMeterReadsResponse(@Identification final String deviceIdentification,
            @Identification final String organisationIdentification, final String correlationUid,
            final String messageType, final ResponseMessageResultType deviceResult, final OsgpException exception,
            final ActualMeterReads actualMeterReadsDto) {

        LOGGER.info("handleActualMeterReadsResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(
                new ResponseMessage(correlationUid, organisationIdentification, deviceIdentification, result,
                        exception, this.monitoringMapper.map(actualMeterReadsDto,
                                com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReads.class)),
                messageType);
    }

    public void handleActualMeterReadsResponse(@Identification final String deviceIdentification,
            @Identification final String organisationIdentification, final String correlationUid,
            final String messageType, final ResponseMessageResultType deviceResult, final OsgpException exception,
            final MeterReadsGas meterReadsGas) {

        LOGGER.info("handleActualMeterReadsResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(
                new ResponseMessage(correlationUid, organisationIdentification, deviceIdentification, result,
                        exception, this.monitoringMapper.map(meterReadsGas,
                                com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas.class)),
                messageType);
    }

    public void requestReadAlarmRegister(
            @Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final String correlationUid,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest readAlarmRegisterRequestValueObject,
            final String messageType) throws FunctionalException {

        LOGGER.info("requestReadAlarmRegister for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeteringDevice(deviceIdentification);

        final com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequest readAlarmRegisterRequestDto = this.monitoringMapper
                .map(readAlarmRegisterRequestValueObject,
                        com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequest.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, smartMeteringDevice.getIpAddress(), readAlarmRegisterRequestDto), messageType);
    }

    public void handleReadAlarmRegisterResponse(@Identification final String deviceIdentification,
            @Identification final String organisationIdentification, final String correlationUid,
            final String messageType, final ResponseMessageResultType deviceResult, final OsgpException exception,
            final com.alliander.osgp.dto.valueobjects.smartmetering.AlarmRegister alarmRegisterDto) {

        LOGGER.info("handleReadAlarmRegisterResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        final AlarmRegister alarmRegisterValueDomain = this.monitoringMapper.map(alarmRegisterDto, AlarmRegister.class);

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, alarmRegisterValueDomain), messageType);
    }
}
