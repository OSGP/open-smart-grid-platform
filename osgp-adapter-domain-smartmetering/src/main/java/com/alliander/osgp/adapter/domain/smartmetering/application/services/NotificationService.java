/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderUUIDService;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetMBusDeviceOnChannelRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushNotificationAlarm;
import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushNotificationAlarmDto;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

import ma.glasnost.orika.MapperFactory;

@Service(value = "domainSmartMeteringNotificationService")
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private WebServiceResponseMessageSender webServiceResponseMessageSender;

    @Autowired
    private MBusGatewayService mBusGatewayService;

    @Autowired
    private CorrelationIdProviderUUIDService correlationIdProviderService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    public void handlePushNotificationAlarm(final DeviceMessageMetadata deviceMessageMetadata,
            final PushNotificationAlarmDto pushNotificationAlarm) throws FunctionalException {

        LOGGER.info("handlePushNotificationAlarm for MessageType: {}", deviceMessageMetadata.getMessageType());

        final PushNotificationAlarm pushNotificationAlarmDomain = this.mapperFactory.getMapperFacade()
                .map(pushNotificationAlarm, PushNotificationAlarm.class);

        /*
         * If the push notification alarm contains a message regarding the
         * discovery of a new mbus device, some further steps are necessary to
         * complete the coupling of this mbus device.
         */
        for (final AlarmType alarmType : pushNotificationAlarmDomain.getAlarms()) {
            if (this.isNewMBusDeviceDiscoveredAlarm(alarmType)) {
                this.getMBusDeviceOnChannel(deviceMessageMetadata, alarmType);
            }
        }

        /*
         * Send the push notification alarm as a response message to the web
         * service, so it can be handled similar to response messages based on
         * earlier web service requests.
         */
        this.webServiceResponseMessageSender.send(
                new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                        deviceMessageMetadata.getOrganisationIdentification(),
                        deviceMessageMetadata.getDeviceIdentification(), ResponseMessageResultType.OK, null,
                        pushNotificationAlarmDomain, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

    private void getMBusDeviceOnChannel(final DeviceMessageMetadata deviceMessageMetadata, final AlarmType alarm)
            throws FunctionalException {

        final String gatewayDeviceIdentification = deviceMessageMetadata.getDeviceIdentification();
        final String organisationIdentification = deviceMessageMetadata.getOrganisationIdentification();
        final byte channel = this.getChannelInfoFromAlarm(alarm);
        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                gatewayDeviceIdentification);

        final DeviceMessageMetadata newMetaData = new DeviceMessageMetadata(gatewayDeviceIdentification,
                organisationIdentification, correlationUid, DeviceFunction.GET_M_BUS_DEVICE_ON_CHANNEL.toString(),
                deviceMessageMetadata.getMessagePriority());
        final GetMBusDeviceOnChannelRequestData requestData = new GetMBusDeviceOnChannelRequestData(
                gatewayDeviceIdentification, channel);

        this.mBusGatewayService.getMBusDeviceOnChannel(newMetaData, requestData);
    }

    private byte getChannelInfoFromAlarm(final AlarmType alarmType) {

        switch (alarmType) {
        case NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1:
            return 1;
        case NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2:
            return 2;
        case NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3:
            return 3;
        case NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4:
            return 4;
        default:
            throw new IllegalArgumentException("Expected an alarm of type NEW_M_BUS_DEVICE_DISCOVERD");
        }
    }

    private boolean isNewMBusDeviceDiscoveredAlarm(final AlarmType alarmType) {

        return alarmType.equals(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1)
                || alarmType.equals(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2)
                || alarmType.equals(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3)
                || alarmType.equals(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4);
    }

    public void handleGetMBusDeviceOnChannelResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ChannelElementValuesDto channelElementValuesDto) {

        final Long mbusIdentificationNumber = Long.parseLong(channelElementValuesDto.getIdentificationNumber());
        final String mbusManufacturerIdentification = channelElementValuesDto.getManufacturerIdentification();

        final SmartMeter mbusSmartMeter = this.smartMeterRepository
                .findByMBusIdentificationNumber(mbusIdentificationNumber, mbusManufacturerIdentification);

        mbusSmartMeter.setChannel(channelElementValuesDto.getChannel());
        mbusSmartMeter.setMbusPrimaryAddress(channelElementValuesDto.getPrimaryAddress());
        this.smartMeterRepository.save(mbusSmartMeter);

        final Device gatewayDevice = this.deviceRepository
                .findByDeviceIdentification(deviceMessageMetadata.getDeviceIdentification());
        final Device mbusDevice = this.deviceRepository
                .findByDeviceIdentification(mbusSmartMeter.getDeviceIdentification());
        mbusDevice.updateGatewayDevice(gatewayDevice);
        this.deviceRepository.save(mbusDevice);

        LOGGER.info("Done handling the coupling of mBus device after NEW_MBUS_DEVICE_DISCOVERED_ON_CHANNEL_{} alarm",
                channelElementValuesDto.getChannel());

    }

}
