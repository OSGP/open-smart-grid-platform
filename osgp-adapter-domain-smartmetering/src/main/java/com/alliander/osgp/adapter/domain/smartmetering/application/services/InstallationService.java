/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import java.util.List;

import ma.glasnost.orika.MapperFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CoupleMbusDeviceRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DeCoupleMbusDeviceRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainSmartMeteringInstallationService")
@Transactional(value = "transactionManager")
public class InstallationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstallationService.class);

    @Autowired
    @Qualifier(value = "domainSmartMeteringOutgoingOsgpCoreRequestMessageSender")
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    private SmartMeterRepository smartMeteringDeviceRepository;

    @Autowired
    private ProtocolInfoRepository protocolInfoRepository;

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private WebServiceResponseMessageSender webServiceResponseMessageSender;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private DomainHelperService domainHelperService;

    public InstallationService() {
        // Parameterless constructor required for transactions...
    }

    public void addMeter(final DeviceMessageMetadata deviceMessageMetadata,
            final SmartMeteringDevice smartMeteringDeviceValueObject) throws FunctionalException {

        LOGGER.debug("addMeter for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        SmartMeter device = this.smartMeteringDeviceRepository.findByDeviceIdentification(deviceMessageMetadata
                .getDeviceIdentification());
        if (device == null) {

            /*
             * TODO see what needs to be done to have the IP address added to
             * smartMeteringDeviceValueObject (and mapped)
             */
            device = this.mapperFactory.getMapperFacade().map(smartMeteringDeviceValueObject, SmartMeter.class);

            final ProtocolInfo protocolInfo = this.protocolInfoRepository.findByProtocolAndProtocolVersion("DSMR",
                    smartMeteringDeviceValueObject.getDSMRVersion());

            if (protocolInfo == null) {
                throw new FunctionalException(FunctionalExceptionType.UNKNOWN_PROTOCOL_NAME_OR_VERSION,
                        ComponentType.DOMAIN_SMART_METERING);
            }

            device.updateProtocol(protocolInfo);

            device = this.smartMeteringDeviceRepository.save(device);

            final Organisation organisation = this.organisationRepository
                    .findByOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification());
            final DeviceAuthorization authorization = device.addAuthorization(organisation, DeviceFunctionGroup.OWNER);
            this.deviceAuthorizationRepository.save(authorization);

        } else {
            throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICE, ComponentType.DOMAIN_SMART_METERING);
        }

        final SmartMeteringDeviceDto smartMeteringDeviceDto = this.mapperFactory.getMapperFacade().map(
                smartMeteringDeviceValueObject, SmartMeteringDeviceDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDeviceDto), deviceMessageMetadata.getMessageType(), deviceMessageMetadata
                .getMessagePriority(), deviceMessageMetadata.getScheduleTime());
    }

    public void handleAddMeterResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        this.handleResponse("handleDefaultDeviceResponse", deviceMessageMetadata, deviceResult, exception);
    }

    /**
     * @param deviceMessageMetadata
     *            the metadata of the message, including the correlationUid, the
     *            deviceIdentification and the organisation
     * @param requestData
     *            the requestData of the message, including the identificatin of
     *            the m-bus device and the channel
     */
    public void coupleMbusDevice(final DeviceMessageMetadata deviceMessageMetadata,
            final CoupleMbusDeviceRequestData requestData) {

        final String deviceIdentification = deviceMessageMetadata.getDeviceIdentification();
        final String mbusDeviceIdentification = requestData.getMbusDeviceIdentification();
        final short channel = requestData.getChannel();

        LOGGER.debug(
                "coupleMbusDevice for organisationIdentification: {} for gateway: {}, m-bus device {} and channel {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceIdentification, mbusDeviceIdentification,
                channel);

        OsgpException exception = null;
        ResponseMessageResultType result = ResponseMessageResultType.OK;

        try {
            final SmartMeter gateway = this.domainHelperService.findActiveSmartMeter(deviceIdentification);

            final SmartMeter mbusDevice = this.domainHelperService.findActiveSmartMeter(mbusDeviceIdentification);

            final List<SmartMeter> alreadyCoupled = this.smartMeteringDeviceRepository.getMbusDevicesForGateway(gateway
                    .getId());

            for (final SmartMeter coupledDevice : alreadyCoupled) {
                if (channel == coupledDevice.getChannel()) {
                    LOGGER.info("M-bus device {} was coupled to gateway {} on channel {}, this device is decoupled",
                            coupledDevice.getDeviceIdentification(), gateway.getDeviceIdentification(), channel);
                    coupledDevice.setChannel(null);
                    coupledDevice.updateGatewayDevice(null);
                    this.smartMeteringDeviceRepository.save(coupledDevice);
                }
            }

            mbusDevice.setChannel(channel);
            mbusDevice.updateGatewayDevice(gateway);
            this.smartMeteringDeviceRepository.save(mbusDevice);
        } catch (final FunctionalException functionalException) {
            exception = functionalException;
            result = ResponseMessageResultType.NOT_OK;
        }

        this.handleResponse("coupleMbusDevice", deviceMessageMetadata, result, exception);
    }

    /**
     * @param deviceMessageMetadata
     *            the metadata of the message, including the correlationUid, the
     *            deviceIdentification and the organization
     * @param requestData
     *            the requestData of the message, including the identification
     *            of the m-bus device and the channel
     * @throws FunctionalException
     */
    public void deCoupleMbusDevice(final DeviceMessageMetadata deviceMessageMetadata,
            final DeCoupleMbusDeviceRequestData requestData) {

        final String deviceIdentification = deviceMessageMetadata.getDeviceIdentification();
        final String mbusDeviceIdentification = requestData.getMbusDeviceIdentification();
        LOGGER.debug("deCoupleMbusDevice for organisationIdentification: {} for gateway: {}, m-bus device {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceIdentification, mbusDeviceIdentification);

        OsgpException exception = null;
        ResponseMessageResultType result = ResponseMessageResultType.OK;

        try {
            final SmartMeter gateway = this.domainHelperService.findActiveSmartMeter(deviceIdentification);

            final SmartMeter mbusDevice = this.domainHelperService.findActiveSmartMeter(mbusDeviceIdentification);

            final List<SmartMeter> alreadyCoupled = this.smartMeteringDeviceRepository.getMbusDevicesForGateway(gateway
                    .getId());

            if (alreadyCoupled.isEmpty()) {
                throw new FunctionalException(FunctionalExceptionType.DEVICES_NOT_COUPLED,
                        ComponentType.DOMAIN_SMART_METERING);
            }

            for (final SmartMeter coupledDevice : alreadyCoupled) {
                if (coupledDevice.getDeviceIdentification().equals(mbusDevice.getDeviceIdentification())) {
                    coupledDevice.updateGatewayDevice(null);
                    this.smartMeteringDeviceRepository.save(coupledDevice);
                }
            }
        } catch (final FunctionalException functionalException) {
            exception = functionalException;
            result = ResponseMessageResultType.NOT_OK;
        }

        this.handleResponse("deCoupleMbusDevice", deviceMessageMetadata, result, exception);
    }

    private void handleResponse(final String methodName, final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {
        LOGGER.debug("{} for MessageType: {}", methodName, deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Device Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, null, deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata
                .getMessageType());
    }
}
