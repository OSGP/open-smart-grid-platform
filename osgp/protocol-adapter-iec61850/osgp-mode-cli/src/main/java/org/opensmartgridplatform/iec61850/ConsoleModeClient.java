package org.opensmartgridplatform.iec61850;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.openmuc.openiec61850.BdaInt8;
import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.ClientEventListener;
import org.openmuc.openiec61850.ClientSap;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.Report;
import org.openmuc.openiec61850.SclParseException;
import org.openmuc.openiec61850.ServerModel;
import org.openmuc.openiec61850.internal.cli.Action;
import org.openmuc.openiec61850.internal.cli.ActionException;
import org.openmuc.openiec61850.internal.cli.ActionListener;
import org.openmuc.openiec61850.internal.cli.ActionProcessor;
import org.openmuc.openiec61850.internal.cli.CliParameter;
import org.openmuc.openiec61850.internal.cli.CliParameterBuilder;
import org.openmuc.openiec61850.internal.cli.CliParseException;
import org.openmuc.openiec61850.internal.cli.CliParser;
import org.openmuc.openiec61850.internal.cli.IntCliParameter;
import org.openmuc.openiec61850.internal.cli.StringCliParameter;

public class ConsoleModeClient {
    // -h 84.30.69.148

    private static final String READ_MODE_VALUE_KEY = "c";
    private static final String RETRIEVE_MODE_VALUE_KEY_DESCRIPTION = "Get current mode (local or remote)";

    private static final String SET_LOCAL_MODE_KEY = "l";
    private static final String SET_LOCAL_MODE_KEY_DESCRIPTION = "Set mode to LOCAL";

    private static final String SET_REMOTE_MODE_KEY = "r";
    private static final String SET_REMOTE_MODE_KEY_DESCRIPTION = "Set mode to REMOTE";

    private static final byte LOCAL_MODE = (byte) 0;
    private static final byte REMOTE_MODE = (byte) 1;

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
    private static ServerModel serverModel;
    private static final ActionProcessor actionProcessor = new ActionProcessor(new ActionExecutor());

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

    private static class ActionExecutor implements ActionListener {

        @Override
        public void actionCalled(final String actionKey) throws ActionException {
            try {
                switch (actionKey) {
                case SET_LOCAL_MODE_KEY:
                    this.setLocalMode();
                    break;
                case SET_REMOTE_MODE_KEY:
                    this.setRemoteMode();
                    break;
                case READ_MODE_VALUE_KEY:
                    this.readMode();
                    break;
                default:
                    break;
                }
            } catch (final Exception e) {
                throw new ActionException(e);
            }
        }

        private void readModelFromFile(final String modelFileName) throws OperationFailedException {
            System.out.println("** Reading model from file.");

            try {
                serverModel = association.getModelFromSclFile(modelFileName);
            } catch (final SclParseException e) {
                System.out.println("Error parsing SCL file: " + e.getMessage());
                throw new OperationFailedException("Error reading model", e);
            }

            System.out.println("Successfully read model");
        }

        private void readMode() throws OperationFailedException {
            System.out.println("** Retrieving the mode (local or remote)");

            final BdaInt8 ctlVal = this.getCtlValNode();
            final String mode;
            if (ctlVal.getValue() == 0) {
                mode = "LOCAL";
            } else {
                mode = "REMOTE";
            }
            System.out.println("Device is running in mode: " + mode);
        }

        private void setLocalMode() throws OperationFailedException {
            System.out.println("Set LOCAL mode");
            // this.getCtlValNode().setValue(LOCAL_MODE);
        }

        private void setRemoteMode() throws OperationFailedException {
            System.out.println("Set REMOTE mode");
            // this.getCtlValNode().setValue(REMOTE_MODE);
        }

        private BdaInt8 getCtlValNode() throws OperationFailedException {
            if (serverModel == null) {
                this.readModelFromFile(MODEL_FILE_NAME.getValue());
            }
            return (BdaInt8) serverModel.findModelNode(SERVER_NAME.getValue() + "/LLN0.Mod.Oper.ctlVal", Fc.CO);
        }

        @Override
        public void quit() {
            System.out.println("** Closing connection.");
            association.close();
            return;
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

        actionProcessor.addAction(new Action(READ_MODE_VALUE_KEY, RETRIEVE_MODE_VALUE_KEY_DESCRIPTION));
        actionProcessor.addAction(new Action(SET_LOCAL_MODE_KEY, SET_LOCAL_MODE_KEY_DESCRIPTION));
        actionProcessor.addAction(new Action(SET_REMOTE_MODE_KEY, SET_REMOTE_MODE_KEY_DESCRIPTION));

        actionProcessor.start();
    }

}
