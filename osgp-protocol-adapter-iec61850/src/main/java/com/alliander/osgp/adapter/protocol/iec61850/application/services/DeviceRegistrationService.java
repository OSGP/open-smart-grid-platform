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

import org.openmuc.openiec61850.ServerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.adapter.protocol.iec61850.exceptions.NodeWriteException;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850ClientAssociation;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Connection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.Function;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.IED;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceConnectionService;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ClearReportCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850DisableRegistrationCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850EnableReportingCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850SetGpsCoordinatesCommand;
import com.alliander.osgp.core.db.api.iec61850.entities.Ssld;
import com.alliander.osgp.core.db.api.iec61850.repositories.SsldDataRepository;

@Service(value = "iec61850DeviceRegistrationService")
public class DeviceRegistrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRegistrationService.class);

    @Autowired
    private Iec61850DeviceConnectionService iec61850DeviceConnectionService;

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
     * @param ied
     *            The type of IED.
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
        final DeviceConnection deviceConnection = new DeviceConnection(new Iec61850Connection(
                iec61850ClientAssociation, serverModel), deviceIdentification, ied);

        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {
                DeviceRegistrationService.this.setLocationInformation(deviceConnection);
                DeviceRegistrationService.this.disableRegistration(deviceConnection);
                if (DeviceRegistrationService.this.isReportingAfterDeviceRegistrationEnabled) {
                    LOGGER.info("Reporting enabled for device: {}", deviceConnection.getDeviceIdentification());
                    new Iec61850EnableReportingCommand().enableReportingOnDeviceWithoutUsingSequenceNumber(
                            DeviceRegistrationService.this.iec61850DeviceConnectionService.getIec61850Client(),
                            deviceConnection);
                    // Don't disconnect now! The device should be able to send
                    // reports.
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                new Iec61850ClearReportCommand().clearReportOnDevice(deviceConnection);
                            } catch (final ProtocolAdapterException e) {
                                LOGGER.error(
                                        "Unable to clear report for device: "
                                                + deviceConnection.getDeviceIdentification(), e);
                            }
                            DeviceRegistrationService.this.iec61850DeviceConnectionService.disconnect(deviceConnection
                                    .getDeviceIdentification());
                        }
                    }, DeviceRegistrationService.this.delayAfterDeviceRegistration);
                } else {
                    LOGGER.info("Reporting disabled for device: {}", deviceIdentification);
                    DeviceRegistrationService.this.iec61850DeviceConnectionService.disconnect(deviceConnection
                            .getDeviceIdentification());
                }
                return null;
            }
        };

        this.iec61850DeviceConnectionService.sendCommandWithRetry(function);
    }

    /**
     * Set the location information for this device. If the osgp_core database
     * contains longitude and latitude information for the given device, those
     * values must be saved to the corresponding data-attributes.
     *
     * @throws NodeWriteException
     *             In case writing of the longitude or latitude fails.
     */
    protected void setLocationInformation(final DeviceConnection deviceConnection) throws NodeWriteException {
        final Ssld ssld = DeviceRegistrationService.this.ssldDataRepository.findByDeviceIdentification(deviceConnection
                .getDeviceIdentification());
        if (ssld != null) {
            final Float longitude = ssld.getGpsLongitude();
            final Float latitude = ssld.getGpsLatitude();
            LOGGER.info("Ssld found for device: {} longitude: {}, latitude: {}",
                    deviceConnection.getDeviceIdentification(), longitude, latitude);

            if (longitude != null && latitude != null) {
                new Iec61850SetGpsCoordinatesCommand().setGpsCoordinates(deviceConnection, longitude, latitude);
            }
        }
    }

    /**
     * Set attribute to false in order to signal the device the registration was
     * successful.
     *
     * @throws NodeWriteException
     *             In case writing of the data-attribute fails.
     */
    protected void disableRegistration(final DeviceConnection deviceConnection) throws NodeWriteException {
        new Iec61850DisableRegistrationCommand().disableRegistration(deviceConnection);
    }
}
