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

import org.openmuc.openiec61850.BdaBoolean;
import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.ClientSap;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.FcModelNode;
import org.openmuc.openiec61850.ModelNode;
import org.openmuc.openiec61850.SclParseException;
import org.openmuc.openiec61850.ServerModel;
import org.openmuc.openiec61850.ServiceError;
import org.openmuc.openiec61850.Urcb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ConnectionFailureException;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.Function;

@Component
public class Iec61850Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850Client.class);

    @Autowired
    private DeviceManagementService deviceManagementService;

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

    public Iec61850ClientAssociation connect(final String deviceIdentification, final InetAddress ipAddress)
            throws ServiceError {

        final ClientSap clientSap = new ClientSap();
        // alternatively you could use ClientSap(SocketFactory factory) to e.g.
        // connect using SSL

        // optionally you can set some association parameters (but usually the
        // default should work):
        // clientSap.setTSelRemote(new byte[] { 0, 1 });
        // clientSap.setTSelLocal(new byte[] { 0, 0 });

        // final SampleClient eventHandler = new SampleClient();
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

        LOGGER.info("Connected to device: {} !!!", deviceIdentification);

        // @formatter:off
        // ServerModel serverModel;
        // try {
        // // requestModel() will call all GetDirectory and GetDefinition ACSI
        // // services needed to get the complete
        // // server model
        // serverModel = association.retrieveModel();
        // } catch (final ServiceError e) {
        // LOGGER.error("Service Error requesting model.", e);
        // association.close();
        // return null;
        // } catch (final IOException e) {
        // LOGGER.error("Fatal IOException requesting model.", e);
        // return null;
        // }
        //
        // // instead of calling retrieveModel you could read the model directly
        // // from an SCL file:
        // // try {
        // // serverModel =
        // //
        // association.getModelFromSclFile("../sampleServer/sampleModel.icd");
        // // } catch (SclParseException e1) {
        // // logger.error("Error parsing SCL file.", e1);
        // // return;
        // // }
        //
        //
        // // get the values of all data attributes in the model:
        // try {
        // association.getAllDataValues();
        // } catch (final IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
        // // example for writing a variable:
        // final FcModelNode modCtlModel = (FcModelNode)
        // serverModel.findModelNode("ied1lDevice1/CSWI1.Mod.ctlModel",
        // Fc.CF);
        // association.setDataValues(modCtlModel);
        //
        // // example for enabling reporting:
        // final Urcb urcb = serverModel.getUrcb("ied1lDevice1/LLN0.urcb1");
        // if (urcb == null) {
        // LOGGER.error("ReportControlBlock not found");
        // } else {
        // association.getRcbValues(urcb);
        // LOGGER.info("urcb name: " + urcb.getName());
        // LOGGER.info("RptId: " + urcb.getRptId());
        // LOGGER.info("RptEna: " + urcb.getRptEna().getValue());
        // association.reserveUrcb(urcb);
        // association.enableReporting(urcb);
        // association.startGi(urcb);
        // association.disableReporting(urcb);
        // association.cancelUrcbReservation(urcb);
        // }
        //
        //
        // // example for reading a variable:
        // final FcModelNode totW = (FcModelNode)
        // serverModel.findModelNode("ied1lDevice1/MMXU1.TotW", Fc.MX);
        // final BdaFloat32 totWmag = (BdaFloat32)
        // totW.getChild("mag").getChild("f");
        // final BdaTimestamp totWt = (BdaTimestamp) totW.getChild("t");
        // final BdaQuality totWq = (BdaQuality) totW.getChild("q");
        // @formatter:on

        return clientAssociation;
    }

    public void disconnect(final ClientAssociation clientAssociation, final String deviceIdentification) {
        LOGGER.info("disconnecting from device: {}...", deviceIdentification);
        clientAssociation.disconnect();
        LOGGER.info("disconnected from device: {} !!!", deviceIdentification);
    }

    public ServerModel readServerModelFromDevice(final ClientAssociation clientAssociation) {
        ServerModel serverModel;
        try {
            // retrieveModel() will call all GetDirectory and GetDefinition ACSI
            // services needed to get the complete server model
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
     *
     * @param clientAssociation
     *            Instance of {@link ClientAssociation}
     * @param filePath
     *            "../sampleServer/sampleModel.icd"
     * @return Instance of {@link ServerModel}
     */
    public ServerModel readServerModelFromSclFile(final ClientAssociation clientAssociation, final String filePath) {
        // instead of calling retrieveModel you could read the model directly
        // from an SCL file:
        try {
            return clientAssociation.getModelFromSclFile(filePath);
        } catch (final SclParseException e) {
            LOGGER.error("Error parsing SCL file.", e);
            return null;
        }
    }

    public void disableRegistration(final String deviceIdentification, final InetAddress ipAddress)
            throws ProtocolAdapterException {

        final Iec61850ClientAssociation iec61850ClientAssociation;
        try {
            iec61850ClientAssociation = this.connect(deviceIdentification, ipAddress);
        } catch (final ServiceError e) {
            throw new ProtocolAdapterException("Unexpected error connecting to device to disable registration.", e);
        }
        if (iec61850ClientAssociation == null || iec61850ClientAssociation.getClientAssociation() == null) {
            throw new ProtocolAdapterException("Unable to connect to device to disable registration.");
        }
        final ClientAssociation clientAssociation = iec61850ClientAssociation.getClientAssociation();

        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {

                final ServerModel serverModel = Iec61850Client.this.readServerModelFromDevice(clientAssociation);
                final String objectReferenceRegNode = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                        + LogicalNodeAttributeDefinitons.PROPERTY_REG_CONFIGURATION;
                final FcModelNode registrationNode = (FcModelNode) serverModel.findModelNode(objectReferenceRegNode,
                        Fc.CF);
                final BdaBoolean notificationEnabledNode = (BdaBoolean) registrationNode.getChild("ntfEnb");
                notificationEnabledNode.setValue(false);
                clientAssociation.setDataValues(notificationEnabledNode);

                return null;
            }
        };

        this.sendCommandWithRetry(function);
    }

    public boolean readAllDataValues(final ClientAssociation clientAssociation) {
        // get the values of all data attributes in the model:
        try {
            clientAssociation.getAllDataValues();
            return true;
        } catch (ServiceError | IOException e) {
            LOGGER.error("Unexpected excpetion during readAllDataValues", e);
            return false;
        }
    }

    public void readDataValue(final ServerModel serverModel, final String logicalNodeProperty) {
        LOGGER.info("readDataValue for logicalNodeProperty: {}", logicalNodeProperty);

        // final FcModelNode totW = (FcModelNode)
        // serverModel.findModelNode("ied1lDevice1/MMXU1.TotW", Fc.MX);
        // final BdaFloat32 totWmag = (BdaFloat32)
        // totW.getChild("mag").getChild("f");
        // final BdaTimestamp totWt = (BdaTimestamp) totW.getChild("t");
        // final BdaQuality totWq = (BdaQuality) totW.getChild("q");

        // LOGGER.info("totWmag: {}, totWt: {}, totWq: {}", totWmag.getFloat(),
        // totWt.getDate(), totWq.getValue());

        final String xswc1PositionStateObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                + LogicalNodeAttributeDefinitons.getNodeNameForRelayIndex(1)
                + LogicalNodeAttributeDefinitons.PROPERTY_POSITION;
        final FcModelNode switchPositonState = (FcModelNode) serverModel.findModelNode(
                xswc1PositionStateObjectReference, Fc.ST);
        final BdaBoolean state = (BdaBoolean) switchPositonState
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_POSITION_ATTRIBUTE_STATE);
        LOGGER.info("state: {}", state);
    }

    public void writeDataValue(final ClientAssociation clientAssociation, final ServerModel serverModel,
            final String logicalNodeProperty, final boolean isOn) {
        // LOGGER.info("writeDataValue for logicalNodeProperty: {}",
        // logicalNodeProperty);
        //
        // final FcModelNode modCtlModel = (FcModelNode)
        // serverModel.findModelNode("ied1lDevice1/CSWI1.Mod.ctlModel",
        // Fc.CF);
        // try {
        // clientAssociation.setDataValues(modCtlModel);
        // } catch (ServiceError | IOException e) {
        // LOGGER.error("Unexpected excpetion during readAllDataValues", e);
        // }

        LOGGER.info("writeDataValue for logicalNodeProperty: {}", logicalNodeProperty);

        final String xswc1PositionOperationObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                + LogicalNodeAttributeDefinitons.getNodeNameForRelayIndex(1)
                + LogicalNodeAttributeDefinitons.PROPERTY_POSITION;

        LOGGER.info("xswc1PositionOperationObjectReference: {}", xswc1PositionOperationObjectReference);

        final FcModelNode switchPositionOperation = (FcModelNode) serverModel.findModelNode(
                xswc1PositionOperationObjectReference, Fc.CO);

        try {
            final ModelNode x = switchPositionOperation.getChild("Oper");

            final BdaBoolean position = (BdaBoolean) x.getChild("ctlVal");
            position.setValue(isOn);
            clientAssociation.setDataValues((FcModelNode) x);
        } catch (ServiceError | IOException e) {
            LOGGER.error("Unexpected excpetion during writeDataValue", e);
        }
    }

    public void enableUnbufferedReporting(final ClientAssociation clientAssociation, final ServerModel serverModel,
            final String logicalNodeProperty) {
        // example for enabling unbuffered reporting:
        final Urcb urcb = serverModel.getUrcb("ied1lDevice1/LLN0.urcb1");
        if (urcb == null) {
            LOGGER.error("ReportControlBlock not found");
        } else {
            try {
                clientAssociation.getRcbValues(urcb);
                LOGGER.info("urcb name: " + urcb.getName());
                LOGGER.info("RptId: " + urcb.getRptId());
                LOGGER.info("RptEna: " + urcb.getRptEna().getValue());
                clientAssociation.reserveUrcb(urcb);
                clientAssociation.enableReporting(urcb);
                clientAssociation.startGi(urcb);
                clientAssociation.disableReporting(urcb);
                clientAssociation.cancelUrcbReservation(urcb);
            } catch (ServiceError | IOException e) {
                LOGGER.error("Unexpected excpetion during enableReporting", e);
            }
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
