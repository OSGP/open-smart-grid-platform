/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.entities.GASMeterDevice;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestData;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainSmartMeteringMonitoringService")
@Transactional(value = "transactionManager")
public class MonitoringService {

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
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequestData periodicMeterReadsRequestValueObject,
            final String messageType) throws FunctionalException {

        LOGGER.info("requestPeriodicMeterReads for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        // TODO: bypassing authorization, this should be fixed.
        // Organisation organisation =
        // this.findOrganisation(organisationIdentification);
        // final Device device = this.findActiveDevice(deviceIdentification);

        // TODO deviceAuthorization
        // final DeviceAuthorization deviceAuthorization = new
        // DeviceAuthorization(duMy, organisation,
        // com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup.OWNER);
        // this.deviceAuthorizationRepository.save(deviceAuthorization);

        if (periodicMeterReadsRequestValueObject.isGas()) {
            final GASMeterDevice findGASMeteringDevice = this.domainHelperService
                    .findGASMeteringDevice(deviceIdentification);
            final com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestData periodicMeterReadsRequestDto = new PeriodicMeterReadsRequestData(
                    findGASMeteringDevice.getSmartMeterId(), PeriodType.valueOf(periodicMeterReadsRequestValueObject
                            .getPeriodType().name()), periodicMeterReadsRequestValueObject.getBeginDate(),
                    periodicMeterReadsRequestValueObject.getEndDate(), true, findGASMeteringDevice.getChannel());
            this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                    findGASMeteringDevice.getSmartMeterId(), periodicMeterReadsRequestDto), messageType);
        } else {
            // call triggers functionalexception when no device found
            this.domainHelperService.findSmartMeteringDevice(deviceIdentification);
            final com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestData periodicMeterReadsRequestDto = new PeriodicMeterReadsRequestData(
                    deviceIdentification, PeriodType.valueOf(periodicMeterReadsRequestValueObject.getPeriodType()
                            .name()), periodicMeterReadsRequestValueObject.getBeginDate(),
                    periodicMeterReadsRequestValueObject.getEndDate());
            this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                    deviceIdentification, periodicMeterReadsRequestDto), messageType);
        }
    }

    public void handlePeriodicMeterReadsresponse(final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final PeriodicMeterReadsContainer periodMeterReadsValueDTO) {

        LOGGER.info("handlePeriodicMeterReadsresponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Device Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads> r = new ArrayList<>(
                periodMeterReadsValueDTO.getPeriodicMeterReads().size());
        for (final PeriodicMeterReads pmr : periodMeterReadsValueDTO.getPeriodicMeterReads()) {
            r.add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads(pmr.getLogTime(),
                    pmr.getActiveEnergyImportTariffOne(), pmr.getActiveEnergyImportTariffTwo(), pmr
                            .getActiveEnergyExportTariffOne(), pmr.getActiveEnergyExportTariffTwo(),
                    com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(pmr.getPeriodType()
                            .name())));
        }

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadContainer periodMeterReadsValueDomain = new PeriodicMeterReadContainer(
                periodMeterReadsValueDTO.getDeviceIdentification(), r);

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, periodMeterReadsValueDomain), messageType);

    }

    public void handlePeriodicMeterReadsresponse(final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final PeriodicMeterReadsContainerGas periodMeterReadsValueDTO) {

        LOGGER.info("handlePeriodicMeterReadsresponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Device Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas> r = new ArrayList<>(
                periodMeterReadsValueDTO.getPeriodicMeterReadsGas().size());
        for (final PeriodicMeterReadsGas pmr : periodMeterReadsValueDTO.getPeriodicMeterReadsGas()) {
            r.add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas(pmr.getLogTime(),
                    com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(pmr.getPeriodType()
                            .name()), pmr.getConsumption(), pmr.getCaptureTime()));
        }
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas pmMeterReadsContainerGas = new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas(
                periodMeterReadsValueDTO.getDeviceIdentification(), r);

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, pmMeterReadsContainerGas), messageType);

    }

    public void requestActualMeterReads(
            @Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final String correlationUid,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequest actualMeterReadsRequestValueObject,
            final String messageType) throws FunctionalException {

        LOGGER.info("requestActualMeterReads for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        if (actualMeterReadsRequestValueObject.isGas()) {
            final GASMeterDevice findGASMeteringDevice = this.domainHelperService
                    .findGASMeteringDevice(deviceIdentification);
            final com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsRequest periodicMeterReadsRequestDto = new ActualMeterReadsRequest(
                    findGASMeteringDevice.getSmartMeterId(), true, findGASMeteringDevice.getChannel());
            this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                    findGASMeteringDevice.getSmartMeterId(), periodicMeterReadsRequestDto), messageType);
        } else {
            // call triggers functionalexception when no device found
            this.domainHelperService.findSmartMeteringDevice(deviceIdentification);
            final com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsRequest periodicMeterReadsRequestDto = new ActualMeterReadsRequest(
                    deviceIdentification);
            this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                    deviceIdentification, periodicMeterReadsRequestDto), messageType);
        }
    }

    public void handleActualMeterReadsResponse(@Identification final String deviceIdentification,
            @Identification final String organisationIdentification, final String correlationUid,
            final String messageType, final ResponseMessageResultType deviceResult, final OsgpException exception,
            final ActualMeterReads actualMeterReadsDto) {

        LOGGER.info("handleActualMeterReadsResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Device Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReads actualMeterReadsValueDomain = this.monitoringMapper
                .map(actualMeterReadsDto,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReads.class);

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, actualMeterReadsValueDomain), messageType);
    }

    public void handleActualMeterReadsResponse(@Identification final String deviceIdentification,
            @Identification final String organisationIdentification, final String correlationUid,
            final String messageType, final ResponseMessageResultType deviceResult, final OsgpException exception,
            final MeterReadsGas meterReadsGas) {

        LOGGER.info("handleActualMeterReadsResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Device Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas actualMeterReadsValueDomain = this.monitoringMapper
                .map(meterReadsGas, com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas.class);

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, actualMeterReadsValueDomain), messageType);
    }

}
