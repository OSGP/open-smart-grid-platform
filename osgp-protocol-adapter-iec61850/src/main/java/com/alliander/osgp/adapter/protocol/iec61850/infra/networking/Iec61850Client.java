/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import java.io.IOException;
import java.net.InetAddress;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.ClientSap;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.SclParseException;
import org.openmuc.openiec61850.ServerModel;
import org.openmuc.openiec61850.ServiceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ConnectionFailureException;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.Function;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import com.alliander.osgp.core.db.api.iec61850.entities.Ssld;
import com.alliander.osgp.core.db.api.iec61850.repositories.SsldDataRepository;

@Component
public class Iec61850Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850Client.class);

    @Autowired
    private DeviceManagementService deviceManagementService;

    @Autowired
    private Iec61850DeviceService iec61850DeviceService;

    @Autowired
    private SsldDataRepository ssldDataRepository;

    @Autowired
    private int iec61850PortClient;

    @Autowired
    private int iec61850PortClientLocal;

    @Autowired
    private int iec61850PortServer;

    @Resource
    private int maxRetryCount;

    @PostConstruct
    private void init() {
        LOGGER.info("portClient: {}, portClientLocal: {}, portServer: {}", this.iec61850PortClient,
                this.iec61850PortClientLocal, this.iec61850PortServer);
    }

    /**
     * Connect to a given device. This will read the device model, create a
     * client association and read all data values from the device.
     *
     * @param deviceIdentification
     *            The device identification.
     * @param ipAddress
     *            The IP address of the device.
     *
     * @return An {@link Iec61850ClientAssociation} instance.
     *
     * @throws ServiceError
     *             In case the connection to the device could not be
     *             established.
     */
    public Iec61850ClientAssociation connect(final String deviceIdentification, final InetAddress ipAddress)
            throws ServiceError {
        // Alternatively you could use ClientSap(SocketFactory factory) to e.g.
        // connect using SSL.

        final ClientSap clientSap = new ClientSap();
        final Iec61850ClientAssociation clientAssociation;

        LOGGER.info("Attempting to connect to server: {} on port: {}", ipAddress.getHostAddress(),
                this.iec61850PortServer);
        try {
            final Iec61850ClientEventListener reportListener = new Iec61850ClientEventListener(deviceIdentification,
                    this.deviceManagementService);
            final ClientAssociation association = clientSap.associate(ipAddress, this.iec61850PortServer, null,
                    reportListener);
            clientAssociation = new Iec61850ClientAssociation(association, reportListener);
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Error setting up ClientEventListener for server association", e);
            return null;
        } catch (final IOException e) {
            // an IOException will always indicate a fatal exception. It
            // indicates that the association was closed and
            // cannot be recovered. You will need to create a new association
            // using ClientSap.associate() in order to
            // reconnect.
            LOGGER.error("Error connecting to server", e);
            return null;
        }

        LOGGER.info("Connected to device: {}", deviceIdentification);
        return clientAssociation;
    }

    /**
     * Disconnect from the device.
     *
     * @param clientAssociation
     *            The {@link ClientAssociation} instance.
     * @param deviceIdentification
     *            The device identification.
     */
    public void disconnect(final ClientAssociation clientAssociation, final String deviceIdentification) {
        LOGGER.info("disconnecting from device: {}...", deviceIdentification);
        clientAssociation.disconnect();
        LOGGER.info("disconnected from device: {}", deviceIdentification);
    }

    /**
     * Read the device model from the device.
     *
     * @param clientAssociation
     *            The {@link ClientAssociation} instance.
     *
     * @return A {@link ServerModel} instance.
     */
    public ServerModel readServerModelFromDevice(final ClientAssociation clientAssociation) {
        ServerModel serverModel;
        try {
            // RetrieveModel() will call all GetDirectory and GetDefinition ACSI
            // services needed to get the complete server model.
            serverModel = clientAssociation.retrieveModel();
            return serverModel;
        } catch (final ServiceError e) {
            LOGGER.error("Service Error requesting model.", e);
            clientAssociation.close();
            return null;
        } catch (final IOException e) {
            LOGGER.error("Fatal IOException requesting model.", e);
            return null;
        }
    }

    /**
     * Use an ICD file (model file) to read the device model.
     *
     * @param clientAssociation
     *            Instance of {@link ClientAssociation}
     * @param filePath
     *            "../sampleServer/sampleModel.icd"
     * @return Instance of {@link ServerModel}
     */
    public ServerModel readServerModelFromSclFile(final ClientAssociation clientAssociation, final String filePath) {
        // Instead of calling retrieveModel you could read the model directly
        // from an SCL file.
        try {
            return clientAssociation.getModelFromSclFile(filePath);
        } catch (final SclParseException e) {
            LOGGER.error("Error parsing SCL file.", e);
            return null;
        }
    }

    /**
     * After the device has registered with the platform successfully, the
     * device has to be informed that the registration worked. Disable an
     * attribute so the device will stop attempting to register once a minute.
     *
     * @param deviceIdentification
     *            The device identification.
     * @param ipAddress
     *            The IP address of the device.
     *
     * @throws ProtocolAdapterException
     *             In case the connection to the device can not be established.
     */
    public void disableRegistration(final String deviceIdentification, final InetAddress ipAddress)
            throws ProtocolAdapterException {
        final Iec61850ClientAssociation iec61850ClientAssociation;
        final ServerModel serverModel;
        try {
            iec61850ClientAssociation = this.connect(deviceIdentification, ipAddress);
        } catch (final ServiceError e) {
            throw new ProtocolAdapterException("Unexpected error connecting to device to disable registration.", e);
        }
        if (iec61850ClientAssociation == null || iec61850ClientAssociation.getClientAssociation() == null) {
            throw new ProtocolAdapterException("Unable to connect to device to disable registration.");
        }
        serverModel = this.readServerModelFromDevice(iec61850ClientAssociation.getClientAssociation());

        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {
                final DeviceConnection deviceConnection = new DeviceConnection(new Iec61850Connection(
                        iec61850ClientAssociation, serverModel), deviceIdentification);

                // Set the location information for this device.
                final Ssld ssld = Iec61850Client.this.ssldDataRepository
                        .findByDeviceIdentification(deviceIdentification);
                if (ssld != null) {
                    final Float longitude = ssld.getGpsLongitude();
                    final Float latitude = ssld.getGpsLatitude();
                    if (longitude != null && latitude != null) {
                        final NodeContainer astronomical = deviceConnection.getFcModelNode(
                                LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.ASTRONOMICAL, Fc.CF);
                        astronomical.writeFloat(SubDataAttribute.GPS_LONGITUDE, ssld.getGpsLongitude());
                        astronomical.writeFloat(SubDataAttribute.GPS_LATITUDE, ssld.getGpsLatitude());
                    }
                }

                // Disable the registration by the device by setting attribute
                // of property Reg to false.
                final NodeContainer deviceRegistration = deviceConnection.getFcModelNode(
                        LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.REGISTRATION, Fc.CF);
                deviceRegistration.writeBoolean(SubDataAttribute.DEVICE_REGISTRATION_ENABLED, false);

                // Make sure the device can send a report.
                Iec61850Client.this.iec61850DeviceService.enableReportingOnDevice(deviceConnection,
                        deviceIdentification);
                return null;
            }
        };

        this.sendCommandWithRetry(function);
    }

    /**
     * Read the values of all data attributes of all Logical Nodes.
     *
     * @param clientAssociation
     *            An {@link ClientAssociation} instance.
     *
     * @return True if all values have been read successfully.
     */
    public boolean readAllDataValues(final ClientAssociation clientAssociation) {
        // Get the values of all data attributes in the model.
        try {
            clientAssociation.getAllDataValues();
            return true;
        } catch (ServiceError | IOException e) {
            LOGGER.error("Unexpected excpetion during readAllDataValues", e);
            return false;
        }
    }

    /**
     * Executes the apply method of the given {@link Function} with retries.
     * Returns the given T
     */
    public <T> T sendCommandWithRetry(final Function<T> function) throws ProtocolAdapterException {
        T output = null;

        try {
            output = function.apply();
        } catch (final ServiceError e) {
            // Service exception means we have to retry
            LOGGER.error("Caught ServiceError, retrying", e);
            this.sendCommandWithRetry(function, 0);
        } catch (final Exception e) {
            throw new ProtocolAdapterException("Could not execute command", e);
        }

        return output;
    }

    /*
     * Basically the same as sendCommandWithRetry, but with a retry parameter.
     */
    private <T> T sendCommandWithRetry(final Function<T> function, final int retryCount)
            throws ProtocolAdapterException {

        T output = null;

        try {
            output = function.apply();
        } catch (final ServiceError e) {
            if (retryCount > this.maxRetryCount) {
                throw new ConnectionFailureException("Could not send command after " + this.maxRetryCount + " attemps",
                        e);
            } else {
                this.sendCommandWithRetry(function, retryCount + 1);
            }
        } catch (final Exception e) {
            throw new ProtocolAdapterException("Could not execute command", e);
        }

        return output;
    }
}
