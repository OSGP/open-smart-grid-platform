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
import java.net.UnknownHostException;

import org.openmuc.openiec61850.BdaFloat32;
import org.openmuc.openiec61850.BdaQuality;
import org.openmuc.openiec61850.BdaTimestamp;
import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.ClientEventListener;
import org.openmuc.openiec61850.ClientSap;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.FcModelNode;
import org.openmuc.openiec61850.Report;
import org.openmuc.openiec61850.ServerModel;
import org.openmuc.openiec61850.ServiceError;
import org.openmuc.openiec61850.Urcb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stefan Feuerhahn
 *
 */
public class SampleClient implements ClientEventListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(SampleClient.class);

    /**
     * Connect to a IEC61850 device using <host> <port> as arguments.
     *
     * @param args
     * @throws ServiceError
     * @throws IOException
     */
    public void connect(final String[] args) throws ServiceError, IOException {

        final String usageString = "usage: org.openmuc.openiec61850.sample.SampleClient <host> <port>";

        if (args.length != 2) {
            System.out.println(usageString);
            return;
        }

        final String remoteHost = args[0];
        InetAddress address;
        try {
            address = InetAddress.getByName(remoteHost);
        } catch (final UnknownHostException e) {
            LOGGER.error("Unknown host: " + remoteHost);
            return;
        }

        int remotePort;
        try {
            remotePort = Integer.parseInt(args[1]);
        } catch (final NumberFormatException e) {
            System.out.println(usageString);
            return;
        }

        final ClientSap clientSap = new ClientSap();
        // alternatively you could use ClientSap(SocketFactory factory) to e.g.
        // connect using SSL

        // optionally you can set some association parameters (but usually the
        // default should work):
        // clientSap.setTSelRemote(new byte[] { 0, 1 });
        // clientSap.setTSelLocal(new byte[] { 0, 0 });

        final SampleClient eventHandler = new SampleClient();
        ClientAssociation association;

        LOGGER.info("Attempting to connect to server " + remoteHost + " on port " + remotePort);
        try {
            association = clientSap.associate(address, remotePort, null, eventHandler);
        } catch (final IOException e) {
            // an IOException will always indicate a fatal exception. It
            // indicates that the association was closed and
            // cannot be recovered. You will need to create a new association
            // using ClientSap.associate() in order to
            // reconnect.
            LOGGER.error("Error connecting to server: " + e.getMessage());
            return;
        }

        ServerModel serverModel;
        try {
            // requestModel() will call all GetDirectory and GetDefinition ACSI
            // services needed to get the complete
            // server model
            serverModel = association.retrieveModel();
        } catch (final ServiceError e) {
            LOGGER.error("Service Error requesting model.", e);
            association.close();
            return;
        } catch (final IOException e) {
            LOGGER.error("Fatal IOException requesting model.", e);
            return;
        }

        // instead of calling retrieveModel you could read the model directly
        // from an SCL file:
        // try {
        // serverModel =
        // association.getModelFromSclFile("../sampleServer/sampleModel.icd");
        // } catch (SclParseException e1) {
        // logger.error("Error parsing SCL file.", e1);
        // return;
        // }

        // get the values of all data attributes in the model:
        association.getAllDataValues();

        // example for writing a variable:
        final FcModelNode modCtlModel = (FcModelNode) serverModel.findModelNode("ied1lDevice1/CSWI1.Mod.ctlModel",
                Fc.CF);
        association.setDataValues(modCtlModel);

        // example for enabling reporting
        final Urcb urcb = serverModel.getUrcb("ied1lDevice1/LLN0.urcb1");
        if (urcb == null) {
            LOGGER.error("ReportControlBlock not found");
        } else {
            association.getRcbValues(urcb);
            LOGGER.info("urcb name: " + urcb.getName());
            LOGGER.info("RptId: " + urcb.getRptId());
            LOGGER.info("RptEna: " + urcb.getRptEna().getValue());
            association.reserveUrcb(urcb);
            association.enableReporting(urcb);
            association.startGi(urcb);
            association.disableReporting(urcb);
            association.cancelUrcbReservation(urcb);
        }

        // example for reading a variable:
        final FcModelNode totW = (FcModelNode) serverModel.findModelNode("ied1lDevice1/MMXU1.TotW", Fc.MX);
        final BdaFloat32 totWmag = (BdaFloat32) totW.getChild("mag").getChild("f");
        final BdaTimestamp totWt = (BdaTimestamp) totW.getChild("t");
        final BdaQuality totWq = (BdaQuality) totW.getChild("q");

        while (true) {
            association.getDataValues(totW);
            LOGGER.info("got totW: mag " + totWmag.getFloat() + ", time " + totWt.getDate() + ", quality "
                    + totWq.getValidity());

            try {
                Thread.sleep(5000);
            } catch (final InterruptedException e) {
            }

        }

    }

    @Override
    public void newReport(final Report report) {
        LOGGER.info("got report with dataset ref: " + report.getDataSet().getReferenceStr());
        // do something with the report

    }

    @Override
    public void associationClosed(final IOException e) {
        LOGGER.info("Association was closed");
    }

}