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
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.ClientSap;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.FcModelNode;
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
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.IED;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting.Iec61850ClientBaseEventListener;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting.Iec61850ClientSSLDEventListener;
import com.alliander.osgp.core.db.api.iec61850.entities.Ssld;
import com.alliander.osgp.core.db.api.iec61850.repositories.SsldDataRepository;

@Component
public class Iec61850Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850Client.class);

    @Autowired
    private int iec61850PortClient;

    @Autowired
    private int iec61850PortClientLocal;

    @Autowired
    private int iec61850SsldPortServer;

    @Autowired
    private int iec61850RtuPortServer;

    @Autowired
    private int maxRetryCount;

    @Autowired
    private int delayAfterDeviceRegistration;

    @Autowired
    private boolean isReportingAfterDeviceRegistrationEnabled;

    @Autowired
    private SsldDataRepository ssldDataRepository;

    @Autowired
    private DeviceManagementService deviceManagementService;

    @PostConstruct
    private void init() {
        LOGGER.info(
                "portClient: {}, portClientLocal: {}, iec61850SsldPortServer: {}, iec61850RtuPortServer: {}, maxRetryCount: {}, delayAfterDeviceRegistration: {}, isReportingAfterDeviceRegistrationEnabled: {}",
                this.iec61850PortClient, this.iec61850PortClientLocal, this.iec61850SsldPortServer,
                this.iec61850RtuPortServer, this.maxRetryCount, this.delayAfterDeviceRegistration,
                this.isReportingAfterDeviceRegistrationEnabled);
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
    public Iec61850ClientAssociation connect(final String deviceIdentification, final InetAddress ipAddress,
            final Iec61850ClientBaseEventListener reportListener, final int port) throws ServiceError {
        // Alternatively you could use ClientSap(SocketFactory factory) to e.g.
        // connect using SSL.
        final ClientSap clientSap = new ClientSap();
        final Iec61850ClientAssociation clientAssociation;
        LOGGER.info("Attempting to connect to server: {} on port: {} with max retry count: {}",
                ipAddress.getHostAddress(), port, this.maxRetryCount);

        try {
            final ClientAssociation association = clientSap.associate(ipAddress, port, null, reportListener);
            clientAssociation = new Iec61850ClientAssociation(association, reportListener);
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
            LOGGER.debug("Start reading server model from device");
            // RetrieveModel() will call all GetDirectory and GetDefinition ACSI
            // services needed to get the complete server model.
            serverModel = clientAssociation.retrieveModel();
            LOGGER.debug("Completed reading server model from device");
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
     * @throws ProtocolAdapterException
     *             In case the file path is empty.
     */
    public ServerModel readServerModelFromSclFile(final ClientAssociation clientAssociation, final String filePath)
            throws ProtocolAdapterException {
        if (StringUtils.isEmpty(filePath)) {
            throw new ProtocolAdapterException("File path is empty");
        }

        // Instead of calling retrieveModel you could read the model directly
        // from an SCL file.
        try {
            return clientAssociation.getModelFromSclFile(filePath);
        } catch (final SclParseException e) {
            LOGGER.error("Error parsing SCL file: " + filePath, e);
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
     * @Paraam ied The type of IED.
     *
     * @throws ProtocolAdapterException
     *             In case the connection to the device can not be established.
     */
    public void disableRegistration(final String deviceIdentification, final InetAddress ipAddress, final IED ied)
            throws ProtocolAdapterException {
        final Iec61850ClientAssociation iec61850ClientAssociation;
        try {
            // Currently, only the SSLD devices use this method.
            switch (ied) {
            case FLEX_OVL:
                final Iec61850ClientSSLDEventListener iec61850ClientSSLDEventListener = new Iec61850ClientSSLDEventListener(
                        deviceIdentification, this.deviceManagementService);
                iec61850ClientAssociation = this.connect(deviceIdentification, ipAddress,
                        iec61850ClientSSLDEventListener, this.iec61850SsldPortServer);
                break;
            case ZOWN_RTU:
                iec61850ClientAssociation = this.connect(deviceIdentification, ipAddress, null,
                        this.iec61850RtuPortServer);
                break;
            default:
                throw new ProtocolAdapterException("Unable to execute Disable Registration for IED "
                        + ied.getDescription() + " with deviceIdentification: " + deviceIdentification);
            }
        } catch (final ServiceError e) {
            throw new ProtocolAdapterException("Unexpected ServiceError connecting to device to disable registration.",
                    e);
        }
        if (iec61850ClientAssociation == null || iec61850ClientAssociation.getClientAssociation() == null) {
            throw new ProtocolAdapterException("Unable to connect to device: " + deviceIdentification
                    + " to disable registration.");
        }

        final ServerModel serverModel = this
                .readServerModelFromDevice(iec61850ClientAssociation.getClientAssociation());

        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {
                final DeviceConnection deviceConnection = new DeviceConnection(new Iec61850Connection(
                        iec61850ClientAssociation, serverModel), deviceIdentification, IED.FLEX_OVL);

                // Set the location information for this device.
                final Ssld ssld = Iec61850Client.this.ssldDataRepository
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
                if (Iec61850Client.this.isReportingAfterDeviceRegistrationEnabled) {
                    LOGGER.info("Reporting enabled for device: {}", deviceIdentification);
                    Iec61850Client.this.enableReportingOnDevice(deviceConnection, deviceIdentification);

                    // Don't disconnect now! The device should be able to send
                    // reports.
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Iec61850Client.this.disconnect(iec61850ClientAssociation.getClientAssociation(),
                                    deviceIdentification);
                        }
                    }, Iec61850Client.this.delayAfterDeviceRegistration);
                } else {
                    LOGGER.info("Reporting disabled for device: {}", deviceIdentification);
                    Iec61850Client.this.disconnect(iec61850ClientAssociation.getClientAssociation(),
                            deviceIdentification);
                }

                return null;
            }
        };

        this.sendCommandWithRetry(function);
    }

    public void enableReportingOnDevice(final DeviceConnection deviceConnection, final String deviceIdentification) {
        final NodeContainer reporting = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.LOGICAL_NODE_ZERO, DataAttribute.REPORTING, Fc.BR);
        this.readNodeDataValues(deviceConnection.getConnection().getClientAssociation(), reporting.getFcmodelNode());

        final Iec61850ClientBaseEventListener reportListener = deviceConnection.getConnection()
                .getIec61850ClientAssociation().getReportListener();

        final short sqNum = reporting.getUnsignedByte(SubDataAttribute.SEQUENCE_NUMBER).getValue();
        reportListener.setSqNum(sqNum);
        reporting.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
        LOGGER.info("Allowing device {} to send events", deviceIdentification);
    }

    /**
     * Read the values of all data attributes of all data objects of all Logical
     * Nodes.
     *
     * @param clientAssociation
     *            An {@link ClientAssociation} instance.
     *
     * @return True if all values have been read successfully.
     */
    public boolean readAllDataValues(final ClientAssociation clientAssociation) {
        try {
            LOGGER.debug("Start getAllDataValues from device");
            clientAssociation.getAllDataValues();
            LOGGER.debug("Completed getAllDataValues from device");
            return true;
        } catch (ServiceError | IOException e) {
            LOGGER.error("Unexpected exception during readAllDataValues", e);
            return false;
        }
    }

    /**
     * Read the values of all data attributes of a data object of a Logical
     * Node.
     *
     * @param clientAssociation
     *            An {@link ClientAssociation} instance.
     *
     * @return True if the node data values have been read successfully.
     */
    public boolean readNodeDataValues(final ClientAssociation clientAssociation, final FcModelNode modelNode) {
        try {
            clientAssociation.getDataValues(modelNode);
            return true;
        } catch (ServiceError | IOException e) {
            LOGGER.error("Unexpected excpetion during readNodeDataValues", e);
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
