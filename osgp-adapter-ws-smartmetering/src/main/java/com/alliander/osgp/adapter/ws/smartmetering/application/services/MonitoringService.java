/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageType;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsQuery;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushNotificationAlarm;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest;
import com.alliander.osgp.shared.exceptionhandling.CorrelationUidException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

@Service(value = "wsSmartMeteringMonitoringService")
@Validated
public class MonitoringService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    @Autowired
    private MeterResponseDataService meterResponseDataService;

    public String enqueuePeriodicMeterReadsRequestData(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final PeriodicMeterReadsQuery requestData)
                    throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.REQUEST_PERIODIC_METER_DATA);

        LOGGER.debug("enqueuePeriodicMeterReadsRequestData called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.REQUEST_PERIODIC_METER_DATA, correlationUid,
                organisationIdentification, deviceIdentification, requestData);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeuePeriodicMeterReadsResponse(final String correlationUid)
            throws CorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid, PeriodicMeterReadContainer.class);
    }

    public MeterResponseData dequeuePeriodicMeterReadsGasResponse(final String correlationUid)
            throws CorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid, PeriodicMeterReadsContainerGas.class);
    }

    public String enqueueActualMeterReadsRequestData(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final ActualMeterReadsQuery requestData)
                    throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.REQUEST_ACTUAL_METER_DATA);

        LOGGER.debug("enqueueActualMeterReadsRequestData called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.REQUEST_ACTUAL_METER_DATA, correlationUid, organisationIdentification,
                deviceIdentification, requestData);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueActualMeterReadsResponse(final String correlationUid)
            throws CorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid, MeterReads.class);
    }

    public MeterResponseData dequeueActualMeterReadsGasResponse(final String correlationUid)
            throws CorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid, MeterReadsGas.class);
    }

    public String enqueueReadAlarmRegisterRequestData(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final ReadAlarmRegisterRequest requestData)
                    throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.READ_ALARM_REGISTER);

        LOGGER.debug("enqueueReadAlarmRegisterRequestData called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.READ_ALARM_REGISTER, correlationUid, organisationIdentification,
                deviceIdentification, requestData);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueReadAlarmRegisterResponse(final String correlationUid)
            throws CorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid, AlarmRegister.class);
    }

    public MeterResponseData dequeueRetrievePushNotificationAlarmResponse(final String correlationUid)
            throws CorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid, PushNotificationAlarm.class);
    }
}
