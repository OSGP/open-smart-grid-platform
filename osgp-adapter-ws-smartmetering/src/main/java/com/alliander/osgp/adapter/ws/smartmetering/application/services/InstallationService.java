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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CoupleMbusDeviceRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DeCoupleMbusDeviceRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.UnknownCorrelationUidException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;

@Service(value = "wsSmartMeteringInstallationService")
@Validated
// @Transactional(value = "coreTransactionManager")
public class InstallationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstallationService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    @Autowired
    private MeterResponseDataService meterResponseDataService;

    public String enqueueAddSmartMeterRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SmartMeteringDevice device,
            final int messagePriority, final Long scheduleTime) {

        LOGGER.debug("enqueueAddSmartMeterRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, SmartMeteringRequestMessageType.ADD_METER.toString(),
                messagePriority, scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(device).build();
        // @formatter:on

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    /**
     * @param correlationUid
     *            the correlationUid to dequeue
     * @return the MeterResponseData belonging to the request with the same
     *         correlationUid
     * @throws UnknownCorrelationUidException
     *             correlationUid is unkown in the queue
     */
    public MeterResponseData dequeueResponse(final String correlationUid) throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
    }

    /**
     * @param organisationIdentification
     *            the organisation requesting the coupling of devices
     * @param deviceIdentification
     *            the identification of the master device
     * @param mbusDeviceIdentification
     *            the identifation of the m-bus device
     * @param channel
     *            the channel the m-bus device should be coupled onto
     * @param messagePriority
     *            the priority of the message
     * @param scheduleTime
     *            the time the request should be carried out
     * @return the correlationUid identifying the operation
     */
    public String enqueueCoupleMbusDeviceRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @Identification final String mbusDeviceIdentification,
            final short channel, final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.COUPLE_MBUS_DEVICE);

        LOGGER.debug(
                "enqueueCoupleMbusDeviceRequest called with organisation {}, gateway {} and mbus device {} on channel {}",
                organisationIdentification, deviceIdentification, mbusDeviceIdentification, channel);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.COUPLE_MBUS_DEVICE.toString(), messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata)
                .request(new CoupleMbusDeviceRequestData(mbusDeviceIdentification, channel)).build();

        final CoupleMbusDeviceRequestData coupleMbusDeviceRequestData = new CoupleMbusDeviceRequestData(
                mbusDeviceIdentification, channel);
        coupleMbusDeviceRequestData.validate();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    /**
     * @param organisationIdentification
     *            the organisation requesting the decoupling of devices
     * @param deviceIdentification
     *            the identification of the master device
     * @param mbusDeviceIdentification
     *            the identifation of the m-bus device
     * @param messagePriority
     *            the priority of the message
     * @param scheduleTime
     *            the time the request should be carried out
     * @return the correlationUid identifying the operation
     */
    public String enqueueDeCoupleMbusDeviceRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @Identification final String mbusDeviceIdentification,
            final int messagePriority, final Long scheduleTime) {

        LOGGER.debug("enqueueDeCoupleMbusDeviceRequest called with organisation {}, gateway {} and mbus device {}",
                organisationIdentification, deviceIdentification, mbusDeviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.DE_COUPLE_MBUS_DEVICE.toString(), messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata)
                .request(new DeCoupleMbusDeviceRequestData(mbusDeviceIdentification)).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

}
