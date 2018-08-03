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

import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageType;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.services.CorrelationIdProviderService;
import org.opensmartgridplatform.domain.core.validation.Identification;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AddSmartMeterRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DeCoupleMbusDeviceRequestData;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;

@Service(value = "wsSmartMeteringInstallationService")
@Validated
public class InstallationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstallationService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    public String enqueueAddSmartMeterRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final AddSmartMeterRequest addSmartMeterRequest,
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
                .deviceMessageMetadata(deviceMessageMetadata).request(addSmartMeterRequest).build();
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
    public String enqueueCoupleMbusDeviceByChannelRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final int messagePriority, final Long scheduleTime,
            final short channel) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.COUPLE_MBUS_DEVICE_BY_CHANNEL);

        LOGGER.debug("enqueueCoupleMbusDeviceByChannelRequest called with organisation {}, gateway {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.COUPLE_MBUS_DEVICE_BY_CHANNEL.toString(), messagePriority,
                scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(new CoupleMbusDeviceByChannelRequestData(channel))
                .build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

}
