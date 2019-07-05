/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec61850;

import org.openmuc.openiec61850.BdaInt8;
import org.openmuc.openiec61850.BdaVisibleString;
import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.SclParseException;
import org.openmuc.openiec61850.ServerModel;
import org.openmuc.openiec61850.internal.cli.ActionException;
import org.openmuc.openiec61850.internal.cli.ActionListener;

public class ActionExecutor implements ActionListener {
    public static final String READ_MODE_VALUE_KEY = "c";
    public static final String SET_LOCAL_MODE_KEY = "l";
    public static final String SET_REMOTE_MODE_KEY = "r";

    public static final byte LOCAL_MODE = (byte) 0;
    public static final String LOCAL_MODE_STRING = "LOCAL";

    public static final byte REMOTE_MODE = (byte) 1;
    public static final String REMOTE_MODE_STRING = "REMOTE";

    private ServerModel serverModel;

    private final String modelFileName;
    private final String serverName;
    private final ClientAssociation association;

    public ActionExecutor(final String modelFileName, final String serverName, final ClientAssociation association) {
        this.modelFileName = modelFileName;
        this.serverName = serverName;
        this.association = association;
    }

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
                this.printMode();
                break;
            default:
                break;
            }
        } catch (final Exception e) {
            throw new ActionException(e);
        }
    }

    private void setLocalMode() throws OperationFailedException {
        System.out.println("** Set LOCAL mode");
        this.setMode(LOCAL_MODE);
    }

    private void setRemoteMode() throws OperationFailedException {
        System.out.println("** Set REMOTE mode");
        this.setMode(REMOTE_MODE);
    }

    private void printMode() throws OperationFailedException {
        System.out.println("** Retrieving the mode (local or remote)");
        final byte currentMode = this.readMode();
        System.out.println(
                "Device " + this.getDeviceIdentification() + " is running in mode: " + this.getModeString(currentMode));
    }

    private byte readMode() throws OperationFailedException {
        final BdaInt8 ctlVal = this.getCtlValNode();
        return ctlVal.getValue();
    }

    private String getModeString(final byte value) {
        return value == LOCAL_MODE ? LOCAL_MODE_STRING : REMOTE_MODE_STRING;
    }

    private void setMode(final byte value) throws OperationFailedException {
        final BdaInt8 ctlVal = this.getCtlValNode();

        if (value == ctlVal.getValue()) {
            System.out.println("Device " + this.getDeviceIdentification() + " was already running in mode "
                    + this.getModeString(ctlVal.getValue()) + ", set not executed.");
        } else {
            ctlVal.setValue(value);
            System.out.println("Device " + this.getDeviceIdentification() + " set to mode "
                    + this.getModeString(ctlVal.getValue()));
        }
    }

    private BdaInt8 getCtlValNode() throws OperationFailedException {
        if (this.serverModel == null) {
            this.readModelFromFile(this.modelFileName);
        }
        return (BdaInt8) this.serverModel.findModelNode(this.serverName + "/LLN0.Mod.Oper.ctlVal", Fc.CO);
    }

    private String getDeviceIdentification() throws OperationFailedException {
        if (this.serverModel == null) {
            this.readModelFromFile(this.modelFileName);
        }

        final BdaVisibleString vendor = (BdaVisibleString) this.serverModel
                .findModelNode(this.serverName + "/LPHD.PhyNam.vendor", Fc.DC);

        final BdaVisibleString serialNumber = (BdaVisibleString) this.serverModel
                .findModelNode(this.serverName + "/LPHD.PhyNam.serNum", Fc.DC);

        return vendor.getStringValue() + "-" + serialNumber.getStringValue();
    }

    private void readModelFromFile(final String modelFileName) throws OperationFailedException {
        System.out.println("** Reading model from file.");

        try {
            this.serverModel = this.association.getModelFromSclFile(modelFileName);
        } catch (final SclParseException e) {
            System.out.println("Error parsing SCL file: " + e.getMessage());
            throw new OperationFailedException("Error reading model", e);
        }

        System.out.println("Successfully read model");
    }

    @Override
    public void quit() {
        System.out.println("** Closing connection.");
        this.association.close();
        return;
    }
}
