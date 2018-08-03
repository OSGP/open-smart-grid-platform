/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.CommonMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceModel;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AddSmartMeterRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DeCoupleMbusDeviceRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DeCoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

import ma.glasnost.orika.MapperFactory;

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
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

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
    private MBusGatewayService mBusGatewayService;

    @Autowired
    private CommonMapper commonMapper;

    public InstallationService() {
        // Parameterless constructor required for transactions...
    }

    public void addMeter(final DeviceMessageMetadata deviceMessageMetadata,
            final AddSmartMeterRequest addSmartMeterRequest) throws FunctionalException {

        LOGGER.debug("addMeter for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        SmartMeter device = this.smartMeteringDeviceRepository
                .findByDeviceIdentification(deviceMessageMetadata.getDeviceIdentification());
        final SmartMeteringDevice smartMeteringDeviceValueObject = addSmartMeterRequest.getDevice();

        if (device == null) {

            device = this.mapperFactory.getMapperFacade().map(smartMeteringDeviceValueObject, SmartMeter.class);

            final ProtocolInfo protocolInfo = this.protocolInfoRepository.findByProtocolAndProtocolVersion("DSMR",
                    smartMeteringDeviceValueObject.getDSMRVersion());

            if (protocolInfo == null) {
                throw new FunctionalException(FunctionalExceptionType.UNKNOWN_PROTOCOL_NAME_OR_VERSION,
                        ComponentType.DOMAIN_SMART_METERING);
            }

            device.updateProtocol(protocolInfo);

            final DeviceModel deviceModelValueObject = addSmartMeterRequest.getDeviceModel();
            final Manufacturer manufacturer = this.manufacturerRepository
                    .findByCode(deviceModelValueObject.getManufacturer());
            device.setDeviceModel(this.deviceModelRepository.findByManufacturerAndModelCode(manufacturer,
                    deviceModelValueObject.getModelCode()));

            device = this.smartMeteringDeviceRepository.save(device);

            final Organisation organisation = this.organisationRepository
                    .findByOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification());
            final DeviceAuthorization authorization = device.addAuthorization(organisation, DeviceFunctionGroup.OWNER);
            this.deviceAuthorizationRepository.save(authorization);

        } else {
            throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICE, ComponentType.DOMAIN_SMART_METERING);
        }

        final SmartMeteringDeviceDto smartMeteringDeviceDto = this.mapperFactory.getMapperFacade()
                .map(smartMeteringDeviceValueObject, SmartMeteringDeviceDto.class);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                        deviceMessageMetadata.getOrganisationIdentification(),
                        deviceMessageMetadata.getDeviceIdentification(), smartMeteringDeviceDto),
                deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                deviceMessageMetadata.getScheduleTime());
    }

    /**
     * In case of errors that prevented adding the meter to the protocol database,
     * the meter should be removed from the core database as well.
     *
     * @param deviceMessageMetadata
     */
    @Transactional
    public void removeMeter(final DeviceMessageMetadata deviceMessageMetadata) {

        final String deviceIdentification = deviceMessageMetadata.getDeviceIdentification();
        final SmartMeter device = this.smartMeteringDeviceRepository.findByDeviceIdentification(deviceIdentification);

        LOGGER.warn(
                "Removing meter {} for organization {}, because adding it to the protocol database failed with correlation UID {}",
                deviceIdentification, deviceMessageMetadata.getOrganisationIdentification(),
                deviceMessageMetadata.getCorrelationUid());

        this.deviceAuthorizationRepository.delete(device.getAuthorizations());
        this.smartMeteringDeviceRepository.delete(device);
    }

    public void handleAddMeterResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        this.handleResponse("handleDefaultDeviceResponse", deviceMessageMetadata, deviceResult, exception);
    }

    public void coupleMbusDevice(final DeviceMessageMetadata deviceMessageMetadata,
            final CoupleMbusDeviceRequestData requestData) throws FunctionalException {
        this.mBusGatewayService.coupleMbusDevice(deviceMessageMetadata, requestData);
    }

    public void deCoupleMbusDevice(final DeviceMessageMetadata deviceMessageMetadata,
            final DeCoupleMbusDeviceRequestData requestData) throws FunctionalException {
        this.mBusGatewayService.deCoupleMbusDevice(deviceMessageMetadata, requestData);
    }

    public void coupleMbusDeviceByChannel(final DeviceMessageMetadata deviceMessageMetadata,
            final CoupleMbusDeviceByChannelRequestData requestData) throws FunctionalException {
        this.mBusGatewayService.coupleMbusDeviceByChannel(deviceMessageMetadata, requestData);
    }

    public void handleCoupleMbusDeviceResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType result, final OsgpException exception,
            final MbusChannelElementsResponseDto dataObject) throws FunctionalException {
        if (exception == null) {
            this.mBusGatewayService.handleCoupleMbusDeviceResponse(deviceMessageMetadata, dataObject);
        }
        this.handleResponse("coupleMbusDevice", deviceMessageMetadata, result, exception);
    }

    public void handleDeCoupleMbusDeviceResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType result, final OsgpException exception,
            final DeCoupleMbusDeviceResponseDto deCoupleMbusDeviceResponseDto) throws FunctionalException {
        if (exception == null) {
            this.mBusGatewayService.handleDeCoupleMbusDeviceResponse(deCoupleMbusDeviceResponseDto);
        }
        this.handleResponse("deCoupleMbusDevice", deviceMessageMetadata, result, exception);
    }

    public void handleCoupleMbusDeviceByChannelResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType responseMessageResultType, final OsgpException osgpException,
            final CoupleMbusDeviceByChannelResponseDto dataObject) throws FunctionalException {
        this.mBusGatewayService.handleCoupleMbusDeviceByChannelResponse(deviceMessageMetadata, dataObject);

        final CoupleMbusDeviceByChannelResponse response = this.commonMapper.map(dataObject,
                CoupleMbusDeviceByChannelResponse.class);

        final ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
                .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
                .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
                .withResult(responseMessageResultType).withOsgpException(osgpException).withDataObject(response)
                .withMessagePriority(deviceMessageMetadata.getMessagePriority()).build();

        this.webServiceResponseMessageSender.send(responseMessage, deviceMessageMetadata.getMessageType());
    }

    public void handleResponse(final String methodName, final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {
        LOGGER.debug("{} for MessageType: {}", methodName, deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Device Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
                .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
                .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification()).withResult(result)
                .withOsgpException(exception).withMessagePriority(deviceMessageMetadata.getMessagePriority()).build();
        this.webServiceResponseMessageSender.send(responseMessage,
                deviceMessageMetadata.getMessageType());
    }
}
