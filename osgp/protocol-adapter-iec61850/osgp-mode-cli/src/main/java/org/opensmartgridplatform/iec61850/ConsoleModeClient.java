/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec61850;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.ClientEventListener;
import org.openmuc.openiec61850.ClientSap;
import org.openmuc.openiec61850.Report;
import org.openmuc.openiec61850.internal.cli.Action;
import org.openmuc.openiec61850.internal.cli.ActionProcessor;
import org.openmuc.openiec61850.internal.cli.CliParameter;
import org.openmuc.openiec61850.internal.cli.CliParameterBuilder;
import org.openmuc.openiec61850.internal.cli.CliParseException;
import org.openmuc.openiec61850.internal.cli.CliParser;
import org.openmuc.openiec61850.internal.cli.IntCliParameter;
import org.openmuc.openiec61850.internal.cli.StringCliParameter;

public class ConsoleModeClient {
    private static final String RETRIEVE_MODE_VALUE_KEY_DESCRIPTION = "Get current mode (local or remote)";
    private static final String SET_LOCAL_MODE_KEY_DESCRIPTION = "Set mode to LOCAL";
    private static final String SET_REMOTE_MODE_KEY_DESCRIPTION = "Set mode to REMOTE";

    private static final StringCliParameter HOST = new CliParameterBuilder("-h")
            .setDescription("The IP/domain address of the server you want to access.").setMandatory()
            .buildStringParameter("host");

    private static final IntCliParameter PORT = new CliParameterBuilder("-p").setDescription("The port to connect to.")
            .buildIntParameter("port", 102);

    private static final StringCliParameter SERVER_NAME = new CliParameterBuilder("-s")
            .setDescription("The server name of the IEC 61850 device to connect to.")
            .buildStringParameter("serverName", "");

    private static final StringCliParameter MODEL_FILE_NAME = new CliParameterBuilder("-m")
            .setDescription("The path and name of the model file to import.").buildStringParameter("modelFileName", "");

    private static volatile ClientAssociation association;
    private static ActionProcessor actionProcessor;

    private static class EventListener implements ClientEventListener {

        @Override
        public void newReport(final Report report) {
            System.out.println("Received report: " + report);
        }

        @Override
        public void associationClosed(final IOException e) {
            System.out.print("Received connection closed signal. Reason: ");
            if (!e.getMessage().isEmpty()) {
                System.out.println(e.getMessage());
            } else {
                System.out.println("unknown");
            }
            actionProcessor.close();
        }
    }

    public static void main(final String[] args) {

        final List<CliParameter> cliParameters = new ArrayList<>();
        cliParameters.add(HOST);
        cliParameters.add(PORT);
        cliParameters.add(SERVER_NAME);
        cliParameters.add(MODEL_FILE_NAME);

        final CliParser cliParser = new CliParser("openiec61850-console-client",
                "A client application to access IEC 61850 MMS servers.");
        cliParser.addParameters(cliParameters);

        try {
            cliParser.parseArguments(args);
        } catch (final CliParseException e1) {
            System.err.println("Error parsing command line parameters: " + e1.getMessage());
            System.out.println(cliParser.getUsageString());
            System.exit(1);
        }

        final InetAddress address;
        try {
            address = InetAddress.getByName(HOST.getValue());
        } catch (final UnknownHostException e) {
            System.out.println("Unknown host: " + HOST.getValue());
            return;
        }

        final ClientSap clientSap = new ClientSap();

        try {
            System.out.println("Connecting to remote host " + HOST.getValue());
            association = clientSap.associate(address, PORT.getValue(), null, new EventListener());
            actionProcessor = new ActionProcessor(
                    new ActionExecutor(MODEL_FILE_NAME.getValue(), SERVER_NAME.getValue(), association));
        } catch (final IOException e) {
            System.out.println("Unable to connect to remote host.");
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                association.close();
            }
        });

        System.out.println("Successfully connected");

        actionProcessor.addAction(new Action(ActionExecutor.READ_MODE_VALUE_KEY, RETRIEVE_MODE_VALUE_KEY_DESCRIPTION));
        actionProcessor.addAction(new Action(ActionExecutor.SET_LOCAL_MODE_KEY, SET_LOCAL_MODE_KEY_DESCRIPTION));
        actionProcessor.addAction(new Action(ActionExecutor.SET_REMOTE_MODE_KEY, SET_REMOTE_MODE_KEY_DESCRIPTION));

        actionProcessor.start();
    }

}
