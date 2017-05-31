/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.exceptions.ChannelAlreadyOccupiedException;
import com.alliander.osgp.domain.core.exceptions.InactiveDeviceException;
import com.alliander.osgp.domain.core.exceptions.MBusChannelNotFoundException;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CoupleMbusDeviceRequestData;
import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelElementValues;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsResponseDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainSmartMeteringMBusGatewayService")
@Transactional(value = "transactionManager")
public class MBusGatewayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBusGatewayService.class);
    private static final String FUNCT_EXP_MSG_TEMPLATE = "channel: %d, deviceId: %d, manufacturerId: %d, version: %d, type: %d, \n";

    @Autowired
    @Qualifier(value = "domainSmartMeteringOutgoingOsgpCoreRequestMessageSender")
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    private SmartMeterRepository smartMeteringDeviceRepository;

    @Autowired
    private WebServiceResponseMessageSender webServiceResponseMessageSender;

    @Autowired
    private DomainHelperService domainHelperService;

    public MBusGatewayService() {
        // Parameterless constructor required for transactions...
    }

    /**
     * @param deviceMessageMetadata
     *            the metadata of the message, including the correlationUid, the
     *            deviceIdentification and the organization
     * @param requestData
     *            the requestData of the message, including the identification
     *            of the m-bus device and the channel
     */
    public void coupleMbusDevice(final DeviceMessageMetadata deviceMessageMetadata,
            final CoupleMbusDeviceRequestData requestData) throws FunctionalException {

        final String deviceIdentification = deviceMessageMetadata.getDeviceIdentification();
        final String mbusDeviceIdentification = requestData.getMbusDeviceIdentification();

        LOGGER.debug("coupleMbusDevice for organizationIdentification: {} for gateway: {}, m-bus device {} ",
                deviceMessageMetadata.getOrganisationIdentification(), deviceIdentification, mbusDeviceIdentification);

        try {
            final SmartMeter gatewayDevice = this.domainHelperService.findSmartMeter(deviceIdentification);
            final SmartMeter mbusDevice = this.domainHelperService.findSmartMeter(mbusDeviceIdentification);

            this.checkMbusDevice(mbusDevice);
            this.checkAndHandleIfGivenMBusAlreadyCoupled(gatewayDevice, mbusDeviceIdentification);
            final MbusChannelElementsDto mbusChannelElementsDto = this.makeMbusChannelElementsDto(mbusDevice);
            final RequestMessage requestMessage = new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                    deviceMessageMetadata.getOrganisationIdentification(),
                    deviceMessageMetadata.getDeviceIdentification(), gatewayDevice.getIpAddress(),
                    mbusChannelElementsDto);
            this.osgpCoreRequestMessageSender.send(requestMessage, deviceMessageMetadata.getMessageType(),
                    deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());
        } catch (final FunctionalException ex) {
            throw ex;
        }
    }

    public void handleCoupleMbusDeviceResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final MbusChannelElementsResponseDto mbusChannelElementsResponseDto) throws FunctionalException {

        final String deviceIdentification = deviceMessageMetadata.getDeviceIdentification();
        final SmartMeter gatewayDevice = this.domainHelperService.findSmartMeter(deviceIdentification);

        this.checkAndHandleIfChannelNotFound(mbusChannelElementsResponseDto);
        this.checkAndHandleChannelOnGateway(gatewayDevice, mbusChannelElementsResponseDto);
        this.doCoupleMBusDevice(gatewayDevice, mbusChannelElementsResponseDto);
        this.sendResponse(deviceMessageMetadata);
    }

    private void sendResponse(final DeviceMessageMetadata deviceMessageMetadata) {
        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                ResponseMessageResultType.OK, null, null, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

    /**
     * this will perform the actual dbs transactions that completed the couple
     * mbus device request.
     *
     * @param gatewayDevice
     * @param mbusChannelElementsResponseDto
     * @throws FunctionalException
     */
    private void doCoupleMBusDevice(final SmartMeter gatewayDevice,
            final MbusChannelElementsResponseDto mbusChannelElementsResponseDto) throws FunctionalException {

        final String mbusDeviceIdentification = mbusChannelElementsResponseDto.getMbusChannelElementsDto()
                .getMbusDeviceIdentification();
        final SmartMeter mbusDevice = this.domainHelperService.findSmartMeter(mbusDeviceIdentification);

        final short channel = mbusChannelElementsResponseDto.getChannel().shortValue();
        mbusDevice.setChannel(channel);
        mbusDevice.setMbusPrimaryAddress(this.getPrimaryAddress(mbusChannelElementsResponseDto, channel));

        mbusDevice.updateGatewayDevice(gatewayDevice);
        this.smartMeteringDeviceRepository.save(mbusDevice);
    }

    private MbusChannelElementsDto makeMbusChannelElementsDto(final SmartMeter mbusDevice) {

        final String mbusIdentificationNumber = mbusDevice.getMbusIdentificationNumber();
        final String mbusManufacturerIdentification = mbusDevice.getMbusManufacturerIdentification();
        final String mbusVersion = mbusDevice.getMbusVersion();
        final String mbusDeviceTypeIdentification = mbusDevice.getMbusDeviceTypeIdentification();

        return new MbusChannelElementsDto(mbusDevice.getDeviceIdentification(), mbusIdentificationNumber,
                mbusManufacturerIdentification, mbusVersion, mbusDeviceTypeIdentification);
    }

    /**
     * This method checks if a channel was found on the gateway, and if not it
     * will throw a FunctionalException with the NO_MBUS_DEVICE_CHANNEL_FOUND
     * type.
     */
    private void checkAndHandleIfChannelNotFound(final MbusChannelElementsResponseDto mbusChannelElementsResponseDto)
            throws FunctionalException {
        if (!this.isChannelFound(mbusChannelElementsResponseDto)) {
            throw new FunctionalException(FunctionalExceptionType.NO_MBUS_DEVICE_CHANNEL_FOUND,
                    ComponentType.DOMAIN_SMART_METERING,
                    new MBusChannelNotFoundException(this.buildErrorMessage(mbusChannelElementsResponseDto)));
        }
    }

    private boolean isChannelFound(final MbusChannelElementsResponseDto mbusChannelElementsResponseDto) {
        return mbusChannelElementsResponseDto.getChannel() != null;
    }

    /**
     * This method checks if a the given gateway is not already connected with
     * the given mbus-device. In that case it will throw a FunctionalException.
     */
    private void checkAndHandleIfGivenMBusAlreadyCoupled(final SmartMeter gateway,
            final String mbusDeviceIdentification) throws FunctionalException {
        final List<SmartMeter> alreadyCoupled = this.smartMeteringDeviceRepository
                .getMbusDevicesForGateway(gateway.getId());

        for (final SmartMeter coupledDevice : alreadyCoupled) {
            if (this.allReadyCoupledWithGivenDevice(coupledDevice, mbusDeviceIdentification)) {
                LOGGER.info("The given M-bus device {} is coupled to gateway {} on channel {}",
                        coupledDevice.getDeviceIdentification(), gateway.getDeviceIdentification(),
                        coupledDevice.getChannel());

                throw new FunctionalException(FunctionalExceptionType.GIVEN_MBUS_DEVICE_ALREADY_COUPLED,
                        ComponentType.DOMAIN_SMART_METERING,
                        new ChannelAlreadyOccupiedException(coupledDevice.getChannel()));
            }
        }
    }

    /**
     * This method checks if a gateway is not already connected with another
     * mbus-device. In that case it will throw a FunctionalException, if it
     * already connected with the provided mbus-device on the given channel, it
     * will only display a warning.
     */
    private void checkAndHandleChannelOnGateway(final SmartMeter gateway,
            final MbusChannelElementsResponseDto mbusChannelElementsResponseDto) throws FunctionalException {
        final List<SmartMeter> alreadyCoupled = this.smartMeteringDeviceRepository
                .getMbusDevicesForGateway(gateway.getId());

        for (final SmartMeter coupledDevice : alreadyCoupled) {
            if (this.allReadyCoupledWithOtherDevice(coupledDevice, mbusChannelElementsResponseDto)) {
                LOGGER.info("There is already an M-bus device {} coupled to gateway {} on channel {}",
                        coupledDevice.getDeviceIdentification(), gateway.getDeviceIdentification(),
                        coupledDevice.getChannel());

                throw new FunctionalException(FunctionalExceptionType.CHANNEL_ON_DEVICE_ALREADY_COUPLED,
                        ComponentType.DOMAIN_SMART_METERING,
                        new ChannelAlreadyOccupiedException(coupledDevice.getChannel()));
            }
        }
    }

    private boolean allReadyCoupledWithGivenDevice(final SmartMeter coupledDevice,
            final String mbusDeviceIdentification) {

        return coupledDevice.getChannel() != null
                && coupledDevice.getDeviceIdentification().equals(mbusDeviceIdentification);
    }

    private boolean allReadyCoupledWithOtherDevice(final SmartMeter coupledDevice,
            final MbusChannelElementsResponseDto mbusChannelElementsResponseDto) {

        final int channel = mbusChannelElementsResponseDto.getChannel();
        return channel == coupledDevice.getChannel();
    }

    private void checkMbusDevice(final SmartMeter mbusDevice) throws FunctionalException {
        if (!mbusDevice.isActive()) {
            throw new FunctionalException(FunctionalExceptionType.INACTIVE_DEVICE, ComponentType.DOMAIN_SMART_METERING,
                    new InactiveDeviceException(mbusDevice.getDeviceIdentification()));
        }
    }

    private String buildErrorMessage(final MbusChannelElementsResponseDto mbusChannelElementsResponseDto) {
        final StringBuilder sb = new StringBuilder();
        mbusChannelElementsResponseDto.getChannelElements().forEach(f -> this.appendMessage(sb, f));
        return sb.toString();
    }

    private void appendMessage(final StringBuilder sb, final ChannelElementValues channelElements) {
        final String msg = String.format(FUNCT_EXP_MSG_TEMPLATE, channelElements.getChannel(),
                channelElements.getDeviceTypeIdentification(), channelElements.getManufacturerIdentification(),
                channelElements.getVersion(), channelElements.getDeviceTypeIdentification());
        sb.append(msg);
    }

    private short getPrimaryAddress(final MbusChannelElementsResponseDto mbusChannelElementsResponseDto,
            final int channel) {
        // because the List is 0-based, we need to subtract 1
        return mbusChannelElementsResponseDto.getChannelElements().get(channel - 1).getPrimaryAddress();
    }
}
