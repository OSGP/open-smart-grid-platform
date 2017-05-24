/**
 * Copyright 2015 Smart Society Services B.V.
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
     *            deviceIdentification and the organisation
     * @param requestData
     *            the requestData of the message, including the identificatin of
     *            the m-bus device and the channel
     */
    public void coupleMbusDevice(final DeviceMessageMetadata deviceMessageMetadata,
            final CoupleMbusDeviceRequestData requestData) throws FunctionalException {

        final String deviceIdentification = deviceMessageMetadata.getDeviceIdentification();
        final String mbusDeviceIdentification = requestData.getMbusDeviceIdentification();

        LOGGER.debug("coupleMbusDevice for organisationIdentification: {} for gateway: {}, m-bus device {} ",
                deviceMessageMetadata.getOrganisationIdentification(), deviceIdentification, mbusDeviceIdentification);

        try {
            final SmartMeter gatewayDevice = this.domainHelperService.findSmartMeter(deviceIdentification);
            final SmartMeter gasMeterDevice = this.domainHelperService.findSmartMeter(mbusDeviceIdentification);

            this.checkGasMeter(gasMeterDevice);
            final MbusChannelElementsDto mbusChannelElementsDto = this.makeMbusChannelElementsDto(gasMeterDevice);
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

        this.checkAndHandleIfChannelNotFound(mbusChannelElementsResponseDto);
        final String deviceIdentification = deviceMessageMetadata.getDeviceIdentification();
        final SmartMeter gatewayDevice = this.domainHelperService.findSmartMeter(deviceIdentification);
        this.checkAndHandleChannelOnGateway(gatewayDevice, mbusChannelElementsResponseDto);
        this.handleChannelFound(deviceMessageMetadata, mbusChannelElementsResponseDto);
    }

    private void handleChannelFound(final DeviceMessageMetadata deviceMessageMetadata,
            final MbusChannelElementsResponseDto mbusChannelElementsResponseDto) throws FunctionalException {

        final String deviceIdentification = deviceMessageMetadata.getDeviceIdentification();
        final SmartMeter gatewayDevice = this.domainHelperService.findActiveSmartMeter(deviceIdentification);
        final String mbusDeviceIdentification = mbusChannelElementsResponseDto.getMbusChannelElementsDto()
                .getMbusDeviceIdentification();
        final SmartMeter gasMeterDevice = this.domainHelperService.findSmartMeter(mbusDeviceIdentification);

        gasMeterDevice.setChannel(mbusChannelElementsResponseDto.getChannel().shortValue());
        gasMeterDevice.updateGatewayDevice(gatewayDevice);
        this.smartMeteringDeviceRepository.save(gasMeterDevice);
        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                ResponseMessageResultType.OK, null, null, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

    private MbusChannelElementsDto makeMbusChannelElementsDto(final SmartMeter gasMeterDevice) {

        final String mbusIdentificationNumber = gasMeterDevice.getMbusDeviceTypeIdentification();
        final String mbusManufacturerIdentification = gasMeterDevice.getMbusManufacturerIdentification();
        final String mbusVersion = gasMeterDevice.getMbusVersion();
        final String mbusDeviceTypeIdentification = gasMeterDevice.getMbusDeviceTypeIdentification();

        return new MbusChannelElementsDto(gasMeterDevice.getDeviceIdentification(), mbusIdentificationNumber,
                mbusManufacturerIdentification, mbusVersion, mbusDeviceTypeIdentification);
    }

    /**
     * This method check if a channel was found on the gateway, and if not it
     * will throw a FunctionalException
     */
    private void checkAndHandleIfChannelNotFound(final MbusChannelElementsResponseDto mbusChannelElementsResponseDto)
            throws FunctionalException {
        if (!mbusChannelElementsResponseDto.isChannelFound()) {
            throw new FunctionalException(FunctionalExceptionType.NO_MBUS_DEVICE_CHANNEL_FOUND,
                    ComponentType.DOMAIN_SMART_METERING,
                    new MBusChannelNotFoundException(this.buildErrorMessage(mbusChannelElementsResponseDto)));
        }
    }

    /**
     * This method checks if gateway is not already with another gas-meter. In
     * that case it will throw a FunctionalException, if it already connected
     * with the gasmeter on the given chanel, it will only display a warning.
     */
    private void checkAndHandleChannelOnGateway(final SmartMeter gateway,
            final MbusChannelElementsResponseDto mbusChannelElementsResponseDto) throws FunctionalException {
        final List<SmartMeter> alreadyCoupled = this.smartMeteringDeviceRepository
                .getMbusDevicesForGateway(gateway.getId());

        for (final SmartMeter coupledDevice : alreadyCoupled) {
            if (this.alReadyCoupledWithOtherDevice(gateway, coupledDevice, mbusChannelElementsResponseDto)) {
                LOGGER.info("There is already an M-bus device {} coupled to gateway {} on channel {}",
                        coupledDevice.getDeviceIdentification(), gateway.getDeviceIdentification(),
                        coupledDevice.getChannel());

                throw new FunctionalException(FunctionalExceptionType.CHANNEL_ON_DEVICE_ALREADY_COUPLED,
                        ComponentType.DOMAIN_SMART_METERING,
                        new ChannelAlreadyOccupiedException(coupledDevice.getChannel()));
            }
        }
    }

    private boolean alReadyCoupledWithOtherDevice(final SmartMeter gateway, final SmartMeter coupledDevice,
            final MbusChannelElementsResponseDto mbusChannelElementsResponseDto) {
        final String gasMeterIdentification = mbusChannelElementsResponseDto.getMbusChannelElementsDto()
                .getMbusDeviceIdentification();
        final int channel = mbusChannelElementsResponseDto.getChannel();

        if (coupledDevice.getChannel() == null) {
            return false;
        } else {
            if (coupledDevice.getDeviceIdentification().equals(gasMeterIdentification)
                    && coupledDevice.getChannel() == channel) {
                final String msg = String.format("device %s is already coupled with %s on channeld %d",
                        gateway.getDeviceIdentification(), coupledDevice.getDeviceIdentification(), channel);
                LOGGER.warn(msg);
                return false;
            } else {
                return true;
            }

        }
    }

    private void checkGasMeter(final SmartMeter gasMeter) throws FunctionalException {
        if (!gasMeter.isActive()) {
            throw new FunctionalException(FunctionalExceptionType.INACTIVE_DEVICE, ComponentType.DOMAIN_SMART_METERING,
                    new InactiveDeviceException(gasMeter.getDeviceIdentification()));
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

}
