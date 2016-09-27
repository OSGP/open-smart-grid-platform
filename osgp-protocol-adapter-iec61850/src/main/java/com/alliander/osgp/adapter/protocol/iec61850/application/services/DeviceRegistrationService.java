/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.application.services;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.ServerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850ClientAssociation;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Connection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.Function;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.IED;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceConnectionService;
import com.alliander.osgp.core.db.api.iec61850.entities.Ssld;
import com.alliander.osgp.core.db.api.iec61850.repositories.SsldDataRepository;

@Service(value = "iec61850DeviceRegistrationService")
public class DeviceRegistrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRegistrationService.class);

    @Autowired
    private Iec61850DeviceConnectionService iec61850DeviceConnectionService;

    @Autowired
    private DeviceManagementService deviceManagementService;

    @Autowired
    private SsldDataRepository ssldDataRepository;

    @Autowired
    private int delayAfterDeviceRegistration;

    @Autowired
    private boolean isReportingAfterDeviceRegistrationEnabled;

    /**
     * After the device has registered with the platform successfully, the
     * device has to be informed that the registration worked. Disable an
     * attribute so the device will stop attempting to register once a minute.
     *
     * @param deviceIdentification
     *            The device identification.
     * @param ipAddress
     *            The IP address of the device.
     * @Paraam ied The type of IED.
     *
     * @throws ProtocolAdapterException
     *             In case the connection to the device can not be established.
     */
    public void disableRegistration(final String deviceIdentification, final InetAddress ipAddress, final IED ied)
            throws ProtocolAdapterException {
        this.iec61850DeviceConnectionService.connect(ipAddress.getHostAddress(), deviceIdentification, ied,
                LogicalDevice.LIGHTING);
        final Iec61850ClientAssociation iec61850ClientAssociation = this.iec61850DeviceConnectionService
                .getIec61850ClientAssociation(deviceIdentification);
        final ServerModel serverModel = this.iec61850DeviceConnectionService.getServerModel(deviceIdentification);

        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {
                final DeviceConnection deviceConnection = new DeviceConnection(new Iec61850Connection(
                        iec61850ClientAssociation, serverModel), deviceIdentification, IED.FLEX_OVL);

                // Set the location information for this device.
                final Ssld ssld = DeviceRegistrationService.this.ssldDataRepository
                        .findByDeviceIdentification(deviceIdentification);
                if (ssld != null) {
                    final Float longitude = ssld.getGpsLongitude();
                    final Float latitude = ssld.getGpsLatitude();
                    LOGGER.info("Ssld found for device: {} longitude: {}, latitude: {}", deviceIdentification,
                            longitude, latitude);

                    if (longitude != null && latitude != null) {
                        final NodeContainer astronomical = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                                LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.ASTRONOMICAL, Fc.CF);
                        astronomical.writeFloat(SubDataAttribute.GPS_LONGITUDE, ssld.getGpsLongitude());
                        astronomical.writeFloat(SubDataAttribute.GPS_LATITUDE, ssld.getGpsLatitude());
                        LOGGER.info("longitude: {}, latitude: {} written for device: {}", longitude, latitude,
                                deviceIdentification);
                    }
                }

                // Set attribute to false in order to signal the device the
                // registration was successful.
                final NodeContainer deviceRegistration = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                        LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.REGISTRATION, Fc.CF);
                deviceRegistration.writeBoolean(SubDataAttribute.DEVICE_REGISTRATION_ENABLED, false);
                LOGGER.info("Registration disabled for device: {}", deviceIdentification);

                // Enable reporting so the device can send reports.
                if (DeviceRegistrationService.this.isReportingAfterDeviceRegistrationEnabled) {
                    LOGGER.info("Reporting enabled for device: {}", deviceIdentification);
                    DeviceRegistrationService.this.iec61850DeviceConnectionService.enableReportingOnDevice(
                            deviceConnection, deviceIdentification);

                    // Don't disconnect now! The device should be able to send
                    // reports.
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            DeviceRegistrationService.this.iec61850DeviceConnectionService
                            .disconnect(deviceIdentification);
                        }
                    }, DeviceRegistrationService.this.delayAfterDeviceRegistration);
                } else {
                    LOGGER.info("Reporting disabled for device: {}", deviceIdentification);
                    DeviceRegistrationService.this.iec61850DeviceConnectionService.disconnect(deviceIdentification);
                }

                return null;
            }
        };

        this.iec61850DeviceConnectionService.sendCommandWithRetry(function);
    }
}
