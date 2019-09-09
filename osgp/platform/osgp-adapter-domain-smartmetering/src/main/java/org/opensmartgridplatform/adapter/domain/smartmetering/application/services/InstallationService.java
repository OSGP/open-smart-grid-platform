/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import ma.glasnost.orika.MapperFactory;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.CommonMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // No-args constructor required for transactions...
    }

    public void addMeter(final DeviceMessageMetadata deviceMessageMetadata,
            final AddSmartMeterRequest addSmartMeterRequest) throws FunctionalException {
        final String organisationId = deviceMessageMetadata.getOrganisationIdentification();
        final String deviceId = deviceMessageMetadata.getDeviceIdentification();
        LOGGER.debug("addMeter for organisationIdentification: {} for deviceIdentification: {}", organisationId,
                deviceId);
        final SmartMeteringDevice smartMeteringDevice = addSmartMeterRequest.getDevice();
        final SmartMeter smartMeter = this.getSmartMeter(deviceId, smartMeteringDevice);
        this.addMeter(organisationId, addSmartMeterRequest, smartMeter);
        this.osgpCoreRequestMessageSender.send(this.getRequestMessage(deviceMessageMetadata, smartMeteringDevice),
                deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                deviceMessageMetadata.getScheduleTime());
    }

    private SmartMeter getSmartMeter(final String deviceId, final SmartMeteringDevice smartMeteringDevice)
            throws FunctionalException {
        if (this.smartMeteringDeviceRepository.findByDeviceIdentification(deviceId) != null) {
            throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICE, ComponentType.DOMAIN_SMART_METERING);
        }
        return this.mapperFactory.getMapperFacade().map(smartMeteringDevice, SmartMeter.class);
    }

    private void addMeter(final String organisationIdentification, final AddSmartMeterRequest addSmartMeterRequest,
            SmartMeter smartMeter) throws FunctionalException {
        final SmartMeteringDevice smartMeteringDevice = addSmartMeterRequest.getDevice();
        smartMeter.updateProtocol(this.getProtocolInfo(smartMeteringDevice));
        smartMeter.setDeviceModel(this.getDeviceModel(addSmartMeterRequest.getDeviceModel()));
        smartMeter = this.smartMeteringDeviceRepository.save(smartMeter);
        this.storeAuthorization(organisationIdentification, smartMeter);
    }

    private ProtocolInfo getProtocolInfo(final SmartMeteringDevice smartMeteringDevice) throws FunctionalException {
        final ProtocolInfo protocolInfo = this.protocolInfoRepository.findByProtocolAndProtocolVersion(
                smartMeteringDevice.getProtocolName(), smartMeteringDevice.getProtocolVersion());
        if (protocolInfo == null) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_PROTOCOL_NAME_OR_VERSION,
                    ComponentType.DOMAIN_SMART_METERING);
        }
        return protocolInfo;
    }

    private DeviceModel getDeviceModel(
            final org.opensmartgridplatform.domain.core.valueobjects.DeviceModel deviceModel) {
        final Manufacturer manufacturer = this.manufacturerRepository.findByCode(deviceModel.getManufacturer());
        return this.deviceModelRepository.findByManufacturerAndModelCode(manufacturer, deviceModel.getModelCode());
    }

    private void storeAuthorization(final String organisationIdentification, final SmartMeter smartMeter) {
        final Organisation organisation = this.organisationRepository.findByOrganisationIdentification(
                organisationIdentification);
        final DeviceAuthorization authorization = smartMeter.addAuthorization(organisation, DeviceFunctionGroup.OWNER);
        this.deviceAuthorizationRepository.save(authorization);
    }

    private RequestMessage getRequestMessage(final DeviceMessageMetadata deviceMessageMetadata,
            final SmartMeteringDevice smartMeteringDevice) {
        return new RequestMessage(
                deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(),
                deviceMessageMetadata.getDeviceIdentification(),
                this.mapperFactory.getMapperFacade().map(smartMeteringDevice, SmartMeteringDeviceDto.class));
    }

    /**
     * In case of errors that prevented adding the meter to the protocol database,
     * the meter should be removed from the core database as well.
     */
    @Transactional
    public void removeMeter(final DeviceMessageMetadata deviceMessageMetadata) {

        final SmartMeter device = this.smartMeteringDeviceRepository.findByDeviceIdentification(
                deviceMessageMetadata.getDeviceIdentification());

        LOGGER.warn("Removing meter {} for organization {}, because adding it to the protocol database failed with "
                        + "correlation UID {}", deviceMessageMetadata.getDeviceIdentification(),
                deviceMessageMetadata.getOrganisationIdentification(),
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

        final ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
                .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
                .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
                .withResult(responseMessageResultType)
                .withOsgpException(osgpException)
                .withDataObject(this.commonMapper.map(dataObject, CoupleMbusDeviceByChannelResponse.class))
                .withMessagePriority(deviceMessageMetadata.getMessagePriority())
                .build();

        this.webServiceResponseMessageSender.send(responseMessage, deviceMessageMetadata.getMessageType());
    }

    public void handleResponse(final String methodName, final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.debug("{} for MessageType: {}", methodName, deviceMessageMetadata.getMessageType());

        final ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
                .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
                .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
                .withResult(this.getResponseMessageResultType(deviceResult, exception)).withOsgpException(exception)
                .withMessagePriority(deviceMessageMetadata.getMessagePriority())
                .build();

        this.webServiceResponseMessageSender.send(responseMessage, deviceMessageMetadata.getMessageType());
    }

    private ResponseMessageResultType getResponseMessageResultType(final ResponseMessageResultType deviceResult,
            final OsgpException exception) {
        if (exception != null) {
            LOGGER.error("Device Response not ok. Unexpected Exception", exception);
            return ResponseMessageResultType.NOT_OK;
        }
        return deviceResult;
    }
}
