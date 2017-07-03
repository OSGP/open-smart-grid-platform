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
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.exceptions.ChannelAlreadyOccupiedException;
import com.alliander.osgp.domain.core.exceptions.InactiveDeviceException;
import com.alliander.osgp.domain.core.exceptions.MBusChannelNotFoundException;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CoupleMbusDeviceRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetMBusDeviceOnChannelRequestData;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMBusDeviceOnChannelRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsResponseDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

@Service(value = "domainSmartMeteringMBusGatewayService")
@Transactional(value = "transactionManager")
public class MBusGatewayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBusGatewayService.class);

    @Autowired
    @Qualifier(value = "domainSmartMeteringOutgoingOsgpCoreRequestMessageSender")
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    private SmartMeterRepository smartMeteringDeviceRepository;

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

            this.checkAndHandleInactiveMbusDevice(mbusDevice);
            this.checkAndHandleIfGivenMBusAlreadyCoupled(mbusDevice);
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
    }

    public void getMBusDeviceOnChannel(final DeviceMessageMetadata deviceMessageMetadata,
            final GetMBusDeviceOnChannelRequestData requestData) throws FunctionalException {

        final String deviceIdentification = deviceMessageMetadata.getDeviceIdentification();

        LOGGER.debug("getMBusDeviceOnChannel for organizationIdentification: {} for gateway: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceIdentification);

        final SmartMeter gatewayDevice = this.domainHelperService.findSmartMeter(deviceIdentification);
        final GetMBusDeviceOnChannelRequestDataDto requestDataDto = new GetMBusDeviceOnChannelRequestDataDto(
                requestData.getGatewayDeviceIdentification(), requestData.getChannel());
        final RequestMessage requestMessage = new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                gatewayDevice.getIpAddress(), requestDataDto);
        this.osgpCoreRequestMessageSender.send(requestMessage, deviceMessageMetadata.getMessageType(),
                deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());

    }

    public void handleGetMBusDeviceOnChannelResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final MbusChannelElementsResponseDto mbusChannelElementsResponseDto) throws FunctionalException {

        final String deviceIdentification = deviceMessageMetadata.getDeviceIdentification();
        final SmartMeter gatewayDevice = this.domainHelperService.findSmartMeter(deviceIdentification);

        this.checkAndHandleIfChannelNotFound(mbusChannelElementsResponseDto);
        this.checkAndHandleChannelOnGateway(gatewayDevice, mbusChannelElementsResponseDto);
        this.doCoupleMBusDevice(gatewayDevice, mbusChannelElementsResponseDto);
    }

    /**
     * Updates the M-Bus device identified in the input part of the
     * {@code mbusChannelElementsResponseDto} with respect to persisted
     * attributes related to the coupling with the given {@code gatewayDevice}.
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

        /*
         * If the flow of handling the response gets to this point, the channel
         * has already been confirmed not be null, so the following should be
         * safe with regards to NullPointerExceptions.
         */
        final short channel = mbusChannelElementsResponseDto.getChannel();
        mbusDevice.setChannel(channel);
        mbusDevice.setMbusPrimaryAddress(this.getPrimaryAddress(mbusChannelElementsResponseDto, channel));

        mbusDevice.updateGatewayDevice(gatewayDevice);
        this.smartMeteringDeviceRepository.save(mbusDevice);
    }

    private MbusChannelElementsDto makeMbusChannelElementsDto(final SmartMeter mbusDevice) {

        final String mbusDeviceIdentification = mbusDevice.getDeviceIdentification();
        final String mbusIdentificationNumber;
        if (mbusDevice.getMbusIdentificationNumber() == null) {
            mbusIdentificationNumber = null;
        } else {
            mbusIdentificationNumber = String.format("%08d", mbusDevice.getMbusIdentificationNumber());
        }
        final String mbusManufacturerIdentification = mbusDevice.getMbusManufacturerIdentification();
        final Short mbusVersion = mbusDevice.getMbusVersion();
        final Short mbusDeviceTypeIdentification = mbusDevice.getMbusDeviceTypeIdentification();

        return new MbusChannelElementsDto(mbusDeviceIdentification, mbusIdentificationNumber,
                mbusManufacturerIdentification, mbusVersion, mbusDeviceTypeIdentification);
    }

    /**
     * This method checks if a channel was found on the gateway, and if not it
     * will throw a FunctionalException with the NO_MBUS_DEVICE_CHANNEL_FOUND
     * type.
     */
    private void checkAndHandleIfChannelNotFound(final MbusChannelElementsResponseDto mbusChannelElementsResponseDto)
            throws FunctionalException {
        if (!mbusChannelElementsResponseDto.isChannelFound()) {
            throw new FunctionalException(FunctionalExceptionType.NO_MBUS_DEVICE_CHANNEL_FOUND,
                    ComponentType.DOMAIN_SMART_METERING, new MBusChannelNotFoundException(
                            String.valueOf(mbusChannelElementsResponseDto.getRetrievedChannelElements())));
        }
    }

    /**
     * This method checks if the given mbusDevice is already coupled with a
     * gateway. In that case it will throw a FunctionalException.
     */
    private void checkAndHandleIfGivenMBusAlreadyCoupled(final SmartMeter mbusDevice) throws FunctionalException {
        if (mbusDevice.getGatewayDevice() != null) {
            LOGGER.info("The given M-bus device {} is already coupled to gateway {} on channel {}",
                    mbusDevice.getDeviceIdentification(), mbusDevice.getGatewayDevice().getDeviceIdentification(),
                    mbusDevice.getChannel());

            throw new FunctionalException(FunctionalExceptionType.GIVEN_MBUS_DEVICE_ALREADY_COUPLED,
                    ComponentType.DOMAIN_SMART_METERING,
                    new OsgpException(ComponentType.DOMAIN_SMART_METERING,
                            mbusDevice.getDeviceIdentification() + " is already coupled to gateway "
                                    + mbusDevice.getGatewayDevice().getDeviceIdentification()));
        }
    }

    /**
     * This method checks if a gateway is not already connected with another
     * M-Bus device. In that case it will throw a FunctionalException.
     * <p>
     * Note: we know it is another M-Bus device (and not the one from the couple
     * device request) because upon receiving the couple device request a check
     * is done to see if the given device is already coupled.
     *
     * @see #coupleMbusDevice(DeviceMessageMetadata,
     *      CoupleMbusDeviceRequestData)
     * @see #checkAndHandleIfGivenMBusAlreadyCoupled(SmartMeter)
     */
    private void checkAndHandleChannelOnGateway(final SmartMeter gateway,
            final MbusChannelElementsResponseDto mbusChannelElementsResponseDto) throws FunctionalException {
        final List<SmartMeter> alreadyCoupled = this.smartMeteringDeviceRepository
                .getMbusDevicesForGateway(gateway.getId());

        for (final SmartMeter coupledDevice : alreadyCoupled) {
            if (this.allReadyCoupledWithOtherDevice(coupledDevice, mbusChannelElementsResponseDto)) {
                LOGGER.warn("There is already an M-bus device {} coupled to gateway {} on channel {}, handling {}",
                        coupledDevice.getDeviceIdentification(), gateway.getDeviceIdentification(),
                        coupledDevice.getChannel(), mbusChannelElementsResponseDto);

                throw new FunctionalException(FunctionalExceptionType.CHANNEL_ON_DEVICE_ALREADY_COUPLED,
                        ComponentType.DOMAIN_SMART_METERING,
                        new ChannelAlreadyOccupiedException(coupledDevice.getChannel()));
            }
        }
    }

    private boolean allReadyCoupledWithOtherDevice(final SmartMeter coupledDevice,
            final MbusChannelElementsResponseDto mbusChannelElementsResponseDto) {

        final Short channel = mbusChannelElementsResponseDto.getChannel();
        return channel != null && channel.equals(coupledDevice.getChannel());
    }

    private void checkAndHandleInactiveMbusDevice(final SmartMeter mbusDevice) throws FunctionalException {
        if (!mbusDevice.isActive()) {
            LOGGER.info("The given M-bus device {} is inactive", mbusDevice.getDeviceIdentification());

            throw new FunctionalException(FunctionalExceptionType.INACTIVE_DEVICE, ComponentType.DOMAIN_SMART_METERING,
                    new InactiveDeviceException(mbusDevice.getDeviceIdentification()));
        }
    }

    private short getPrimaryAddress(final MbusChannelElementsResponseDto mbusChannelElementsResponseDto,
            final short channel) {
        // because the List is 0-based, we need to subtract 1
        return mbusChannelElementsResponseDto.getRetrievedChannelElements().get(channel - 1).getPrimaryAddress();
    }
}
