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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetMBusDeviceOnChannelRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
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
     * @param organisationIdentification
     *            the organisation requesting the coupling of devices
     * @param deviceIdentification
     *            the identification of the gateway device
     * @param mbusDeviceIdentification
     *            the identification of the m-bus device
     * @param messagePriority
     *            the priority of the message
     * @param scheduleTime
     *            the time the request should be carried out
     * @return the correlationUid identifying the operation
     */
    public String enqueueCoupleMbusDeviceRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @Identification final String mbusDeviceIdentification,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.COUPLE_MBUS_DEVICE);

        LOGGER.debug("enqueueCoupleMbusDeviceRequest called with organisation {}, gateway {} and mbus device {}",
                organisationIdentification, deviceIdentification, mbusDeviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.COUPLE_MBUS_DEVICE.toString(), messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata)
                .request(new CoupleMbusDeviceRequestData(mbusDeviceIdentification)).build();

        final CoupleMbusDeviceRequestData coupleMbusDeviceRequestData = new CoupleMbusDeviceRequestData(
                mbusDeviceIdentification);
        coupleMbusDeviceRequestData.validate();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    /**
     * @param organisationIdentification
     *            the organisation requesting the decoupling of devices
     * @param deviceIdentification
     *            the identification of the gateway device
     * @param mbusDeviceIdentification
     *            the identification of the m-bus device
     * @param messagePriority
     *            the priority of the message
     * @param scheduleTime
     *            the time the request should be carried out
     * @return the correlationUid identifying the operation
     */
    public String enqueueDeCoupleMbusDeviceRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @Identification final String mbusDeviceIdentification,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.DE_COUPLE_MBUS_DEVICE);

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

    /**
     * @param organisationIdentification
     *            the organisation requesting the coupling of devices
     * @param deviceIdentification
     *            the identification of the gateway device
     * @param messagePriority
     *            the priority of the message
     * @param scheduleTime
     *            the time the request should be carried out
     * @return the correlationUid identifying the operation
     */
    public String enqueueGetMBusDeviceOnChannelRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final int messagePriority, final Long scheduleTime)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_M_BUS_DEVICE_ON_CHANNEL);

        LOGGER.debug("enqueueGetMBusDeviceOnChannelRequest called with organisation {}, gateway {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.GET_M_BUS_DEVICE_ON_CHANNEL.toString(), messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata)
                .request(new GetMBusDeviceOnChannelRequestData(deviceIdentification, (short) 1)).build();

        final GetMBusDeviceOnChannelRequestData getMBusDeviceOnChannelRequestData = new GetMBusDeviceOnChannelRequestData(
                deviceIdentification, (short) 1);
        getMBusDeviceOnChannelRequestData.validate();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

}
