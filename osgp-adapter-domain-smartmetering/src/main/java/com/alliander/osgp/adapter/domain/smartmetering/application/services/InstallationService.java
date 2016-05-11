/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.services;

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

    public InstallationService() {
        // Parameterless constructor required for transactions...
    }

    public void addMeter(final DeviceMessageMetadata deviceMessageMetadata,
            final SmartMeteringDevice smartMeteringDeviceValueObject) throws FunctionalException {

        LOGGER.info("addMeter for organisationIdentification: {} for deviceIdentification: {}",
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

        LOGGER.info("handleDefaultDeviceResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

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
