/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageType;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.services.CorrelationIdProviderService;
import org.opensmartgridplatform.domain.core.validation.Identification;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualMeterReadsQuery;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClearAlarmRegisterRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.CorrelationUidException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;

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
    private ResponseDataService responseDataService;

    public String enqueuePeriodicMeterReadsRequestData(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final PeriodicMeterReadsQuery requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.REQUEST_PERIODIC_METER_DATA);

        LOGGER.debug("enqueuePeriodicMeterReadsRequestData called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.REQUEST_PERIODIC_METER_DATA.toString(), messagePriority, scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(requestData).build();
        // @formatter:on

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueActualMeterReadsRequestData(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final ActualMeterReadsQuery requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.REQUEST_ACTUAL_METER_DATA);

        LOGGER.debug("enqueueActualMeterReadsRequestData called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.REQUEST_ACTUAL_METER_DATA.toString(), messagePriority, scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(requestData).build();
        // @formatter:on

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueReadAlarmRegisterRequestData(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final ReadAlarmRegisterRequest requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.READ_ALARM_REGISTER);

        LOGGER.debug("enqueueReadAlarmRegisterRequestData called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.READ_ALARM_REGISTER.toString(), messagePriority, scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(requestData).build();
        // @formatter:on

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueProfileGenericDataRequestData(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final ProfileGenericDataRequest requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_PROFILE_GENERIC_DATA);

        LOGGER.debug("enqueueProfileGenericDataRequestData called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.GET_PROFILE_GENERIC_DATA.toString(), messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(requestData).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseData dequeueProfileGenericDataResponse(final String correlationUid) throws CorrelationUidException {
        return this.responseDataService.dequeue(correlationUid, ProfileGenericDataResponse.class,
                ComponentType.WS_SMART_METERING);
    }

    public String enqueueClearAlarmRegisterRequestData(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final ClearAlarmRegisterRequest requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.CLEAR_ALARM_REGISTER);

        LOGGER.debug("Enqueue clear alarm register request data called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.CLEAR_ALARM_REGISTER.toString(), messagePriority, scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(requestData).build();
        // @formatter:on

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

}
