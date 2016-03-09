/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import java.io.IOException;

import org.openmuc.openiec61850.BdaBoolean;
import org.openmuc.openiec61850.BdaInt8;
import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.FcModelNode;
import org.openmuc.openiec61850.ModelNode;
import org.openmuc.openiec61850.ServerModel;
import org.openmuc.openiec61850.ServiceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.device.requests.GetStatusDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetLightDeviceRequest;
import com.alliander.osgp.dto.valueobjects.LightValue;

@Component
public class Iec61850DeviceService implements DeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850DeviceService.class);

    @Autowired
    private Iec61850DeviceConnectionService iec61850DeviceConnectionService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.alliander.osgp.adapter.protocol.iec61850.infra.networking.DeviceService
     * #getStatus(com.alliander.osgp.adapter.protocol.iec61850.device.requests.
     * GetStatusDeviceRequest)
     */
    @Override
    public void getStatus(final GetStatusDeviceRequest deviceRequest) {

        // this.ssldDataRepository.findByDeviceIdentification("KAI-001");

        try {

            this.connect(deviceRequest.getIpAddress(), deviceRequest.getDeviceIdentification());

            final ServerModel serverModel = this.iec61850DeviceConnectionService.getServerModel(deviceRequest
                    .getDeviceIdentification());

            // readAllValues() is already performed then connect() does it's
            // job.
            // this.iec61850DeviceConnectionService.readAllValues(deviceRequest.getDeviceIdentification());

            final String xswc1PositionStateObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                    + LogicalNodeAttributeDefinitons.getNodeNameForRelayIndex(1)
                    + LogicalNodeAttributeDefinitons.PROPERTY_POSITION;

            LOGGER.info("xswc1PositionStateObjectReference: {}", xswc1PositionStateObjectReference);

            final FcModelNode switchPositonState = (FcModelNode) serverModel.findModelNode(
                    xswc1PositionStateObjectReference, Fc.ST);

            LOGGER.info("FcModelNode: {}", switchPositonState);

            final BdaBoolean state = (BdaBoolean) switchPositonState.getChild("stVal");
            LOGGER.info("state: {}", state);

            // GET STATUS OLD IMPL

            // Connect to obtain ClientAssociation.
            // final ClientAssociation clientAssociation =
            // this.iec61850Client.connect(
            // deviceRequest.getDeviceIdentification(), ipAddress);
            //
            // // Read the ServerModel, either from the device or from a SCL
            // file.
            // final ServerModel serverModel =
            // this.iec61850Client.readServerModelFromDevice(clientAssociation);
            //
            // // Read all data values from the device.
            // this.iec61850Client.readAllDataValues(clientAssociation);
            //
            // // Read a particular data value.
            // this.iec61850Client.readDataValue(serverModel, "");
            // // Write a particular data value.
            // this.iec61850Client.writeDataValue(clientAssociation,
            // serverModel, "", false);

            // try {
            // Thread.sleep(10000);
            // } catch (final InterruptedException e) {
            // }
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during getStatus", e);
        }
    }

    /**
     * @see DeviceService #setLight(SetLightDeviceRequest)
     */
    @Override
    public void setLight(final SetLightDeviceRequest deviceRequest) {

        try {
            // Connect, get the ServerModel and ClientAssociation.
            this.connect(deviceRequest.getIpAddress(), deviceRequest.getDeviceIdentification());

            final ServerModel serverModel = this.iec61850DeviceConnectionService.getServerModel(deviceRequest
                    .getDeviceIdentification());
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());

            for (final LightValue lightValue : deviceRequest.getLightValuesContainer().getLightValues()) {
                // TODO check which relays have to be switched
                this.switchLightRelay(lightValue.getIndex(), lightValue.isOn(), serverModel, clientAssociation);
            }
        } catch (final Exception e) {
            LOGGER.error("Unexpected excpetion during writeDataValue", e);
        }
    }

    private void switchLightRelay(final int index, final boolean on, final ServerModel serverModel,
            final ClientAssociation clientAssociation) throws ServiceError, IOException {

        final String nodeName = LogicalNodeAttributeDefinitons.getNodeNameForRelayIndex(index);

        final String relayPositionOperationObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE + nodeName
                + LogicalNodeAttributeDefinitons.PROPERTY_POSITION;
        LOGGER.info("xswc1PositionOperationObjectReference: {}", relayPositionOperationObjectReference);

        // Check if the Pos.ctlModel [CF] is enabled. If it is not enabled,
        // the relay can not be operated.
        final FcModelNode posCtlModel = (FcModelNode) serverModel.findModelNode(relayPositionOperationObjectReference,
                Fc.CF);
        final BdaInt8 masterControlValue = (BdaInt8) posCtlModel.getChild("ctlModel");
        if (masterControlValue.getValue() == 0) {
            LOGGER.info("masterControlValue is false");
            // Set the value to true.
            masterControlValue.setValue((byte) 1);
            clientAssociation.setDataValues(posCtlModel);
            LOGGER.info("set masterControlValue to 1 to enable switching");
        }

        final FcModelNode switchPositionOperation = (FcModelNode) serverModel.findModelNode(
                relayPositionOperationObjectReference, Fc.CO);
        final ModelNode operate = switchPositionOperation.getChild("Oper");
        final BdaBoolean position = (BdaBoolean) operate.getChild("ctlVal");

        position.setValue(on);
        clientAssociation.setDataValues((FcModelNode) operate);
    }

    private void connect(final String ipAddress, final String deviceIdentification) {
        this.iec61850DeviceConnectionService.connect(ipAddress, deviceIdentification);
    }

}
