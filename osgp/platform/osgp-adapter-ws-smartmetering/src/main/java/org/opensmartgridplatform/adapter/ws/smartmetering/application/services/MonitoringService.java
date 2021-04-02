/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import java.io.Serializable;

import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualMeterReadsQuery;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClearAlarmRegisterRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.CorrelationUidException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.validation.Identification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "wsSmartMeteringMonitoringService")
@Validated
public class MonitoringService {

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
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        return this.enqueueRequestData(organisationIdentification, deviceIdentification, requestData, messagePriority,
                scheduleTime, DeviceFunction.REQUEST_PERIODIC_METER_DATA, MessageType.REQUEST_PERIODIC_METER_DATA, bypassRetry);

    }

    public String enqueueActualMeterReadsRequestData(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final ActualMeterReadsQuery requestData,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        return this.enqueueRequestData(organisationIdentification, deviceIdentification, requestData, messagePriority,
                scheduleTime, DeviceFunction.REQUEST_ACTUAL_METER_DATA, MessageType.REQUEST_ACTUAL_METER_DATA, bypassRetry);
    }

    public String enqueueReadAlarmRegisterRequestData(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final ReadAlarmRegisterRequest requestData,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        return this.enqueueRequestData(organisationIdentification, deviceIdentification, requestData, messagePriority,
                scheduleTime, DeviceFunction.READ_ALARM_REGISTER, MessageType.READ_ALARM_REGISTER, bypassRetry);
    }

    public String enqueueGetPowerQualityProfileRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final GetPowerQualityProfileRequest requestData,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        return this.enqueueRequestData(organisationIdentification, deviceIdentification, requestData, messagePriority,
                scheduleTime, DeviceFunction.GET_PROFILE_GENERIC_DATA, MessageType.GET_PROFILE_GENERIC_DATA, bypassRetry);

    }

    public ResponseData dequeueGetPowerQualityProfileDataResponseData(final String correlationUid)
            throws CorrelationUidException {
        return this.responseDataService.dequeue(correlationUid, GetPowerQualityProfileResponse.class,
                ComponentType.WS_SMART_METERING);
    }

    public String enqueueClearAlarmRegisterRequestData(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final ClearAlarmRegisterRequest requestData,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        return this.enqueueRequestData(organisationIdentification, deviceIdentification, requestData, messagePriority,
                scheduleTime, DeviceFunction.CLEAR_ALARM_REGISTER, MessageType.CLEAR_ALARM_REGISTER, bypassRetry);
    }

    public String enqueueActualPowerQualityRequestData(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final ActualPowerQualityRequest requestData,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        return this.enqueueRequestData(organisationIdentification, deviceIdentification, requestData, messagePriority,
                scheduleTime, DeviceFunction.GET_ACTUAL_POWER_QUALITY, MessageType.GET_ACTUAL_POWER_QUALITY, bypassRetry);
    }

    private String enqueueRequestData(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final Serializable requestData,
            final int messagePriority, final Long scheduleTime, final DeviceFunction deviceFunction,
            final MessageType messageType, final boolean bypassRetry) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.checkAllowed(organisation, device, deviceFunction);

        if (log.isDebugEnabled()) {
            log.debug("Enqueue {} request data called with organisation {} and device {}",
                    messageType.name().toLowerCase().replace('_', ' '), organisationIdentification,
                    deviceIdentification);
        }

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, messageType.name(), messagePriority, scheduleTime, bypassRetry);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata)
                .request(requestData)
                .build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

}
