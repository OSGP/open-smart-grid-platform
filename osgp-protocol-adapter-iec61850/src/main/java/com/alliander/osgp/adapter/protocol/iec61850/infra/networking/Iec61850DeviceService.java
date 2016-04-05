/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.openiec61850.BdaBoolean;
import org.openmuc.openiec61850.BdaInt16;
import org.openmuc.openiec61850.BdaInt32;
import org.openmuc.openiec61850.BdaInt8;
import org.openmuc.openiec61850.BdaVisibleString;
import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.FcModelNode;
import org.openmuc.openiec61850.ModelNode;
import org.openmuc.openiec61850.ServerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.application.mapping.Iec61850Mapper;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceMessageStatus;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetConfigurationDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetLightDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.EmptyDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.GetConfigurationDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.GetStatusDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.DeviceRelayType;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ConnectionFailureException;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.InvalidConfigurationException;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.Function;
import com.alliander.osgp.core.db.api.iec61850.application.services.SsldDataService;
import com.alliander.osgp.core.db.api.iec61850.entities.DeviceOutputSetting;
import com.alliander.osgp.core.db.api.iec61850.entities.Ssld;
import com.alliander.osgp.core.db.api.iec61850valueobjects.RelayType;
import com.alliander.osgp.dto.valueobjects.Configuration;
import com.alliander.osgp.dto.valueobjects.DaliConfiguration;
import com.alliander.osgp.dto.valueobjects.DeviceStatus;
import com.alliander.osgp.dto.valueobjects.LightType;
import com.alliander.osgp.dto.valueobjects.LightValue;
import com.alliander.osgp.dto.valueobjects.LinkType;
import com.alliander.osgp.dto.valueobjects.LongTermIntervalType;
import com.alliander.osgp.dto.valueobjects.MeterType;
import com.alliander.osgp.dto.valueobjects.RelayConfiguration;
import com.alliander.osgp.dto.valueobjects.RelayMap;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

@Component
public class Iec61850DeviceService implements DeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850DeviceService.class);

    @Autowired
    private Iec61850DeviceConnectionService iec61850DeviceConnectionService;

    @Autowired
    private SsldDataService ssldDataService;

    @Autowired
    private Iec61850Client iec61850Client;

    @Autowired
    private Iec61850Mapper mapper;

    // 30 seconds
    private static final int SELF_TEST_TIMEOUT = 30_000;

    /**
     * @see DeviceService#getStatus(GetStatusDeviceRequest,
     *      DeviceResponseHandler)
     */
    @Override
    public void getStatus(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler) {

        try {

            this.iec61850DeviceConnectionService.connect(deviceRequest.getIpAddress(),
                    deviceRequest.getDeviceIdentification());
            final ServerModel serverModel = this.iec61850DeviceConnectionService.getServerModel(deviceRequest
                    .getDeviceIdentification());

            // Getting the ssld for the device outputsettings
            final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());

            // Getting the data with retries
            final DeviceStatus deviceStatus = this.getStatusFromDevice(serverModel, ssld);

            final GetStatusDeviceResponse deviceResponse = new GetStatusDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), deviceStatus);

            deviceResponseHandler.handleResponse(deviceResponse);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during getStatus", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, true);
        }
    }

    /**
     * @see DeviceService#setLight(SetLightDeviceRequest)
     */
    @Override
    public void setLight(final SetLightDeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler) {

        try {

            // Connect, get the ServerModel final and ClientAssociation.
            this.iec61850DeviceConnectionService.connect(deviceRequest.getIpAddress(),
                    deviceRequest.getDeviceIdentification());

            final ServerModel serverModel = this.iec61850DeviceConnectionService.getServerModel(deviceRequest
                    .getDeviceIdentification());
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());

            for (final LightValue lightValue : deviceRequest.getLightValuesContainer().getLightValues()) {

                final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());

                // for index 0, only devices LIGHT RelaytTypes have to be
                // switched
                if (lightValue.getIndex() == 0) {
                    for (final DeviceOutputSetting deviceOutputSetting : ssld.findByRelayType(RelayType.LIGHT)) {
                        this.switchLightRelay(deviceOutputSetting.getInternalId(), lightValue.isOn(), serverModel,
                                clientAssociation);
                    }
                } else {

                    final DeviceOutputSetting deviceOutputSetting = ssld.getDeviceOutputSettingForIndex(lightValue
                            .getIndex());

                    // You can only switch LIGHT relays that are used
                    if (deviceOutputSetting == null || !RelayType.LIGHT.equals(deviceOutputSetting.getRelayType())) {
                        throw new FunctionalException(FunctionalExceptionType.LIGHT_SWITCHING_NOT_ALLOWED_FOR_RELAY,
                                ComponentType.PROTOCOL_IEC61850);
                    }

                    this.switchLightRelay(deviceOutputSetting.getInternalId(), lightValue.isOn(), serverModel,
                            clientAssociation);
                }
            }
        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during writeDataValue", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            return;
        }

        final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK);

        deviceResponseHandler.handleResponse(deviceResponse);
    }

    /**
     * @see DeviceService#setLight(SetLightDeviceRequest)
     */
    @Override
    public void setConfiguration(final SetConfigurationDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler) {

        try {
            // Connect, get the ServerModel final and ClientAssociation.
            this.iec61850DeviceConnectionService.connect(deviceRequest.getIpAddress(),
                    deviceRequest.getDeviceIdentification());

            final ServerModel serverModel = this.iec61850DeviceConnectionService.getServerModel(deviceRequest
                    .getDeviceIdentification());
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());

            final Configuration configuration = deviceRequest.getConfiguration();

            // ignoring required, unused fields daliconfiguration, meterType,
            // shortTermHistoryIntervalMinutes, preferredLinkType,
            // longTermHistoryInterval and longTermHistoryIntervalType

            final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());

            this.setConfigurationOnDevice(serverModel, clientAssociation, ssld, configuration);

        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during writeDataValue", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            return;
        }

        final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK);

        deviceResponseHandler.handleResponse(deviceResponse);
    }

    @Override
    public void getConfiguration(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler) {

        try {

            this.iec61850DeviceConnectionService.connect(deviceRequest.getIpAddress(),
                    deviceRequest.getDeviceIdentification());
            final ServerModel serverModel = this.iec61850DeviceConnectionService.getServerModel(deviceRequest
                    .getDeviceIdentification());

            // Getting the ssld for the device outputsettings
            final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());

            final Configuration configuration = this.getConfigurationFromDevice(serverModel, ssld);

            final GetConfigurationDeviceResponse response = new GetConfigurationDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK, configuration);

            deviceResponseHandler.handleResponse(response);

        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during writeDataValue", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            return;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.alliander.osgp.adapter.protocol.iec61850.infra.networking.DeviceService
     * #
     * setReboot(com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest
     * ,
     * com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponseHandler
     * )
     */
    @Override
    public void setReboot(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler) {

        try {
            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());
            this.rebootDevice(serverModel, clientAssociation, deviceRequest.getDeviceIdentification());

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK);
            deviceResponseHandler.handleResponse(deviceResponse);
        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during writeDataValue", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            return;
        }
    }

    @Override
    public void runSelfTest(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final boolean startOfTest) {

        // Assuming all goes well
        final DeviceMessageStatus status = DeviceMessageStatus.OK;

        try {

            // Connect, get the ServerModel final and ClientAssociation.
            this.iec61850DeviceConnectionService.connect(deviceRequest.getIpAddress(),
                    deviceRequest.getDeviceIdentification());

            ServerModel serverModel = this.iec61850DeviceConnectionService.getServerModel(deviceRequest
                    .getDeviceIdentification());
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());

            final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());

            // This list will contain the external indexes of all light relays.
            // It's used to interpret the deviceStatus data later on
            final List<Integer> lightRelays = new ArrayList<>();

            LOGGER.info("Turning all lights relays {}", startOfTest ? "on" : "off");

            // Turning all light relays on or off, depending on the value of
            // startOfTest
            for (final DeviceOutputSetting deviceOutputSetting : ssld.findByRelayType(RelayType.LIGHT)) {
                lightRelays.add(deviceOutputSetting.getExternalId());
                this.switchLightRelay(deviceOutputSetting.getInternalId(), startOfTest, serverModel, clientAssociation);
            }

            // Disconnect from the device.
            clientAssociation.disconnect();

            // Sleep and wait
            try {
                LOGGER.info("Waiting {} seconds before getting the device status", SELF_TEST_TIMEOUT / 1000);
                Thread.sleep(SELF_TEST_TIMEOUT);
            } catch (final InterruptedException e) {
                LOGGER.error("An error occured during the device selftest timeout.", e);
                throw new TechnicalException(ComponentType.PROTOCOL_IEC61850,
                        "An error occured during the device selftest timeout.");
            }

            // Reconnecting to the device
            this.iec61850DeviceConnectionService.connect(deviceRequest.getIpAddress(),
                    deviceRequest.getDeviceIdentification());

            // Refreshing the servermodel
            serverModel = this.iec61850DeviceConnectionService.getServerModel(deviceRequest.getDeviceIdentification());

            // Getting the status
            final DeviceStatus deviceStatus = this.getStatusFromDevice(serverModel, ssld);

            LOGGER.info("Fetching and checking the devicestatus");

            // Checking to see if all light relays have the correct state
            for (final LightValue lightValue : deviceStatus.getLightValues()) {
                if (lightRelays.contains(lightValue.getIndex()) && lightValue.isOn() != startOfTest) {
                    // One the the light relays is not in the correct state,
                    // request failed
                    throw new ProtocolAdapterException("not all relays are ".concat(startOfTest ? "on" : "off"));
                }
            }

            LOGGER.info("All lights relays are {}, returning OK", startOfTest ? "on" : "off");

        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);

            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during writeDataValue", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            return;
        }

        final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), status);

        deviceResponseHandler.handleResponse(deviceResponse);

    }

    // =================
    // PRIVATE METHODS =
    // =================

    private ServerModel connectAndRetrieveServerModel(final DeviceRequest deviceRequest)
            throws ProtocolAdapterException {
        this.iec61850DeviceConnectionService.connect(deviceRequest.getIpAddress(),
                deviceRequest.getDeviceIdentification());
        return this.iec61850DeviceConnectionService.getServerModel(deviceRequest.getDeviceIdentification());
    }

    private void switchLightRelay(final int index, final boolean on, final ServerModel serverModel,
            final ClientAssociation clientAssociation) throws ProtocolAdapterException {

        // Commands don't return anything, so returnType is Void
        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {

                final String nodeName = LogicalNodeAttributeDefinitons.getNodeNameForRelayIndex(index);

                // Check if CfSt.enbOper [CF] is set to true. If it is not
                // set to true, the relay can not be operated.
                final String masterControlObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE + nodeName
                        + LogicalNodeAttributeDefinitons.PROPERTY_MASTER_CONTROL;
                LOGGER.info("masterControlObjectReference: {}", masterControlObjectReference);

                final FcModelNode cfSt = Iec61850DeviceService.this.getNode(serverModel, masterControlObjectReference,
                        Fc.CF);
                final BdaBoolean enbOper = (BdaBoolean) Iec61850DeviceService.this.getChildOfNodeWithConstraint(cfSt,
                        LogicalNodeAttributeDefinitons.PROPERTY_MASTER_CONTROL_ATTRIBUTE_ENABLE_OPERATION, Fc.CF);
                if (enbOper.getValue()) {
                    LOGGER.info("masterControlValue is true, switching of relay is enabled");
                } else {
                    LOGGER.info("masterControlValue is false, switching of relay is disabled");
                    // Set the value to true.
                    enbOper.setValue(true);
                    clientAssociation.setDataValues(enbOper);
                    LOGGER.info("set masterControlValue to true to enable switching");
                }

                // Switch the relay using Pos.Oper.ctlVal [CO].
                final String relayPositionOperationObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + nodeName + LogicalNodeAttributeDefinitons.PROPERTY_POSITION;
                LOGGER.info("relayPositionOperationObjectReference: {}", relayPositionOperationObjectReference);

                final FcModelNode switchPositionOperation = (FcModelNode) serverModel.findModelNode(
                        relayPositionOperationObjectReference, Fc.CO);
                LOGGER.info("switchPositionOperation: {}", switchPositionOperation);
                final ModelNode operate = switchPositionOperation
                        .getChild(LogicalNodeAttributeDefinitons.PROPERTY_POSITION_ATTRIBUTE_OPER);
                LOGGER.info("operate: {}", operate);
                final BdaBoolean position = (BdaBoolean) operate
                        .getChild(LogicalNodeAttributeDefinitons.PROPERTY_POSITION_ATTRIBUTE_OPER_CONTROL_VALUE);
                LOGGER.info("position: {}", position);

                LOGGER.info(String.format("Switching relay %d %s", index, on ? "on" : "off"));

                position.setValue(on);
                clientAssociation.setDataValues((FcModelNode) operate);

                // return null == Void
                return null;
            }
        };

        this.iec61850Client.sendCommandWithRetry(function);
    }

    private DeviceStatus getStatusFromDevice(final ServerModel serverModel, final Ssld ssld)
            throws ProtocolAdapterException {

        // creating the Function that will be retried, if necessary
        final Function<DeviceStatus> function = new Function<DeviceStatus>() {

            @Override
            public DeviceStatus apply() throws Exception {
                // getting the light relay values

                final List<LightValue> lightValues = new ArrayList<>();

                for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {

                    final String relayPositionOperationObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                            + LogicalNodeAttributeDefinitons.getNodeNameForRelayIndex(deviceOutputSetting
                                    .getInternalId()) + LogicalNodeAttributeDefinitons.PROPERTY_POSITION;

                    LOGGER.info("relayPositionOperationObjectReference: {}", relayPositionOperationObjectReference);

                    final FcModelNode switchPositonState = (FcModelNode) serverModel.findModelNode(
                            relayPositionOperationObjectReference, Fc.ST);

                    LOGGER.info("FcModelNode: {}", switchPositonState);

                    final BdaBoolean state = (BdaBoolean) switchPositonState.getChild("stVal");

                    final boolean on = state.getValue();
                    lightValues.add(new LightValue(deviceOutputSetting.getExternalId(), on, null));

                    LOGGER.info(String.format("Got status of relay %d => %s", deviceOutputSetting.getInternalId(),
                            on ? "on" : "off"));
                }

                // TODO caution: the referredLinkType and actualLinkType are
                // hardcoded
                // TODO eventNotificationsMask, the kaifa device will have a
                // 1-9
                // value that will have to be mapped to our
                // eventNotificationsMask
                // TODO uncomment the LightRelay code

                // Getting the LightType
                // final String softwareConfigurationObjectReference =
                // LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                // +
                // LogicalNodeAttributeDefinitons.PROPERTY_NODE_CSLC_PREFIX
                // +
                // LogicalNodeAttributeDefinitons.PROPERTY_SOFTWARE_CONFIGURATION;
                //
                // final FcModelNode test = (FcModelNode)
                // serverModel.findModelNode(softwareConfigurationObjectReference,
                // Fc.CF);
                //
                // final BdaVisibleString value = (BdaVisibleString)
                // test.getChild("LT");
                // LightType.valueOf(new String(value.getValue()));

                return new DeviceStatus(lightValues, LinkType.ETHERNET, LinkType.ETHERNET, LightType.RELAY, 0);
            }
        };

        return this.iec61850Client.sendCommandWithRetry(function);

    }

    private Configuration getConfigurationFromDevice(final ServerModel serverModel, final Ssld ssld)
            throws ProtocolAdapterException {

        // Keeping the hardcoded values and values that aren't fetched from the
        // device out of the Function

        // Hardcoded (not supported)
        final MeterType meterType = MeterType.AUX;
        // Hardcoded (not supported)
        final Integer shortTermHistoryIntervalMinutes = 15;
        // Hardcoded (not supported)
        final LinkType preferredLinkType = LinkType.ETHERNET;
        // Hardcoded (not supported)
        final Integer longTermHistoryInterval = 1;
        // Hardcoded (not supported)
        final LongTermIntervalType longTermHistoryIntervalType = LongTermIntervalType.DAYS;

        // creating the Function that will be retried, if necessary
        final Function<Configuration> function = new Function<Configuration>() {

            @Override
            public Configuration apply() throws Exception {

                final List<RelayMap> relayMaps = new ArrayList<>();

                for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {

                    // TODO uncomment this code when XSWC1.SwType.stVal
                    // functions properly. Exception handling hasn't been worked
                    // out for this flow yet!
                    // Iec61850DeviceService.this.checkRelayTypes(deviceOutputSetting,
                    // serverModel);

                    relayMaps.add(Iec61850DeviceService.this.mapper.map(deviceOutputSetting, RelayMap.class));
                }

                final RelayConfiguration relayConfiguration = new RelayConfiguration(relayMaps);

                // PSLD specific => just sending null so it'll be ignored
                final DaliConfiguration daliConfiguration = null;

                // TODO Lighttype is hardcoded, but it will be the same code as
                // in getStatusFromDevice, so I won't copy it here for now
                final LightType lightType = LightType.RELAY;

                final Configuration configuration = new Configuration(lightType, daliConfiguration, relayConfiguration,
                        shortTermHistoryIntervalMinutes, preferredLinkType, meterType, longTermHistoryInterval,
                        longTermHistoryIntervalType);

                // getting the reg configuration values

                LOGGER.info("Reading the registration configuration values");

                final String regObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                        + LogicalNodeAttributeDefinitons.PROPERTY_REG_CONFIGURATION;

                LOGGER.info("regObjectReference: {}", regObjectReference);

                final FcModelNode regConfiguration = (FcModelNode) serverModel.findModelNode(regObjectReference, Fc.CF);

                final BdaVisibleString serverAddress = (BdaVisibleString) regConfiguration.getChild("svrAddr");
                final BdaInt32 serverPort = (BdaInt32) regConfiguration.getChild("svrPort");

                configuration.setOspgIpAddress(new String(serverAddress.getValue()));
                configuration.setOsgpPortNumber(serverPort.getValue());

                // getting the IP configuration values

                LOGGER.info("Reading the IP configuration values");

                final String ipcfObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                        + LogicalNodeAttributeDefinitons.PROPERTY_IP_CONFIGURATION;

                LOGGER.info("ipcfObjectReference: {}", ipcfObjectReference);

                final FcModelNode ipConfiguration = (FcModelNode) serverModel.findModelNode(ipcfObjectReference, Fc.CF);

                final BdaVisibleString deviceFixIpValue = (BdaVisibleString) ipConfiguration.getChild("ipAddr");
                final BdaBoolean dhcpEnabled = (BdaBoolean) ipConfiguration.getChild("enbDHCP");

                configuration.setDeviceFixIpValue(new String(deviceFixIpValue.getValue()));
                configuration.setDhcpEnabled(dhcpEnabled.getValue());

                // getting the software configuration values

                LOGGER.info("Reading the software configuration values");

                final String swcfObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                        + LogicalNodeAttributeDefinitons.PROPERTY_SOFTWARE_CONFIGURATION;

                LOGGER.info("swcfObjectReference: {}", swcfObjectReference);

                final FcModelNode softwareConfiguration = (FcModelNode) serverModel.findModelNode(swcfObjectReference,
                        Fc.CF);

                final BdaInt16 astroGateSunRiseOffset = (BdaInt16) softwareConfiguration.getChild("osRise");
                final BdaInt16 astroGateSunSetOffset = (BdaInt16) softwareConfiguration.getChild("osSet");

                configuration.setAstroGateSunRiseOffset((int) astroGateSunRiseOffset.getValue());
                configuration.setAstroGateSunSetOffset((int) astroGateSunSetOffset.getValue());

                // getting the clock configuration values

                LOGGER.info("Reading the clock configuration values");

                final String clockObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                        + LogicalNodeAttributeDefinitons.PROPERTY_CLOCK;

                LOGGER.info("clockObjectReference: {}", clockObjectReference);

                final FcModelNode clockConfiguration = (FcModelNode) serverModel.findModelNode(clockObjectReference,
                        Fc.CF);

                final BdaInt16 timeSyncFrequency = (BdaInt16) clockConfiguration.getChild("syncPer");
                final BdaBoolean automaticSummerTimingEnabled = (BdaBoolean) clockConfiguration.getChild("enbDst");
                final BdaVisibleString summerTimeDetails = (BdaVisibleString) clockConfiguration.getChild("dstBegT");
                final BdaVisibleString winterTimeDetails = (BdaVisibleString) clockConfiguration.getChild("dstEndT");

                configuration.setTimeSyncFrequency((int) timeSyncFrequency.getValue());
                configuration.setAutomaticSummerTimingEnabled(automaticSummerTimingEnabled.getValue());
                // TODO hardcoded current time for now
                configuration.setSummerTimeDetails(new DateTime());
                // TODO hardcoded current time for now
                configuration.setWinterTimeDetails(new DateTime());

                return configuration;

            }
        };

        return this.iec61850Client.sendCommandWithRetry(function);

    }

    private void setConfigurationOnDevice(final ServerModel serverModel, final ClientAssociation clientAssociation,
            final Ssld ssld, final Configuration configuration) throws ProtocolAdapterException {

        // creating the Function that will be retried, if necessary
        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {

                // TODO add these once the date formatting is sorted out. (Maybe
                // an invalid format causes issues?)
                // summerTimeDetails --> CSLC.Clock.dstBegT
                // winterTimeDetails --> CSLC.Clock.dstEndT

                // TODO set lightType once it's writable
                // lightType --> CSLC.SWCf.LT

                // TODO set relayTypes once they are writable
                // relayMap.getRelayType() --> XSWC{1-4}.post.stVal

                // checking to see if all register values are null, so that we
                // don't read the values for no reason
                if (!(configuration.getOspgIpAddress() == null && configuration.getOsgpPortNumber() == null)) {

                    // Create the object reference string
                    final String regObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                            + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                            + LogicalNodeAttributeDefinitons.PROPERTY_REG_CONFIGURATION;
                    LOGGER.info("regObjectReference: {}", regObjectReference);

                    // Get the node using configuration functional constraint.
                    final FcModelNode cslcLogicalNodeRegAttribute = Iec61850DeviceService.this.getNode(serverModel,
                            regObjectReference, Fc.CF);

                    if (configuration.getOspgIpAddress() != null) {

                        // Get a data attribute using configuration functional
                        // constraint.
                        final BdaVisibleString serverAddress = (BdaVisibleString) Iec61850DeviceService.this
                                .getChildOfNodeWithConstraint(cslcLogicalNodeRegAttribute,
                                        LogicalNodeAttributeDefinitons.PROPERTY_REG_ATTRIBUTE_OSGP_IP_ADDRESS, Fc.CF);

                        LOGGER.info("Updating OspgIpAddress to {}", configuration.getOspgIpAddress());

                        // Get the value and send the value to the device.
                        serverAddress.setValue(configuration.getOspgIpAddress());
                        clientAssociation.setDataValues(serverAddress);

                    }

                    if (configuration.getOsgpPortNumber() != null) {

                        final BdaInt32 serverPort = (BdaInt32) Iec61850DeviceService.this.getChildOfNodeWithConstraint(
                                cslcLogicalNodeRegAttribute,
                                LogicalNodeAttributeDefinitons.PROPERTY_REG_ATTRIBUTE_SERVER_PORT, Fc.CF);

                        LOGGER.info("Updating OsgpPortNumber to {}", configuration.getOsgpPortNumber());

                        // Get the value and send the value to the device.
                        serverPort.setValue(configuration.getOsgpPortNumber());
                        clientAssociation.setDataValues(serverPort);
                    }

                }

                // checking to see if all software config values are null, so
                // that we don't read the values for no reason
                if (!(configuration.getAstroGateSunRiseOffset() == null && configuration.getAstroGateSunSetOffset() == null)) {

                    final String swcfObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                            + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                            + LogicalNodeAttributeDefinitons.PROPERTY_SOFTWARE_CONFIGURATION;

                    final FcModelNode softwareConfiguration = Iec61850DeviceService.this.getNode(serverModel,
                            swcfObjectReference, Fc.CF);

                    if (configuration.getAstroGateSunRiseOffset() != null) {

                        final BdaInt16 astroGateSunRiseOffset = (BdaInt16) Iec61850DeviceService.this
                                .getChildOfNodeWithConstraint(softwareConfiguration,
                                        LogicalNodeAttributeDefinitons.PROPERTY_POSITION_OFFSET_SUNRISE, Fc.CF);

                        LOGGER.info("Updating AstroGateSunRiseOffset to {}", configuration.getAstroGateSunRiseOffset());

                        // Get the value and send the value to the device.
                        astroGateSunRiseOffset.setValue(configuration.getAstroGateSunRiseOffset().shortValue());
                        clientAssociation.setDataValues(astroGateSunRiseOffset);
                    }

                    if (configuration.getAstroGateSunSetOffset() != null) {

                        final BdaInt16 astroGateSunSetOffset = (BdaInt16) Iec61850DeviceService.this
                                .getChildOfNodeWithConstraint(softwareConfiguration,
                                        LogicalNodeAttributeDefinitons.PROPERTY_POSITION_OFFSET_SUNSET, Fc.CF);

                        LOGGER.info("Updating AstroGateSunSetOffset to {}", configuration.getAstroGateSunSetOffset());

                        // Get the value and send the value to the device.
                        astroGateSunSetOffset.setValue(configuration.getAstroGateSunSetOffset().shortValue());
                        clientAssociation.setDataValues(astroGateSunSetOffset);
                    }

                }

                // checking to see if all register values are null, so that we
                // don't read the values for no reason
                if (!(configuration.getTimeSyncFrequency() == null && configuration.isAutomaticSummerTimingEnabled() == null)) {

                    final String clockObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                            + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                            + LogicalNodeAttributeDefinitons.PROPERTY_CLOCK;

                    final FcModelNode clockConfiguration = Iec61850DeviceService.this.getNode(serverModel,
                            clockObjectReference, Fc.CF);

                    if (configuration.getTimeSyncFrequency() != null) {

                        final BdaInt16 timeSyncFrequency = (BdaInt16) Iec61850DeviceService.this
                                .getChildOfNodeWithConstraint(clockConfiguration,
                                        LogicalNodeAttributeDefinitons.PROPERTY_POSITION_SYNC_PERIOD, Fc.CF);

                        LOGGER.info("Updating TimeSyncFrequency to {}", configuration.getTimeSyncFrequency());

                        // Get the value and send the value to the device.
                        timeSyncFrequency.setValue(configuration.getTimeSyncFrequency().shortValue());
                        clientAssociation.setDataValues(timeSyncFrequency);
                    }

                    if (configuration.isAutomaticSummerTimingEnabled() != null) {

                        final BdaBoolean automaticSummerTimingEnabled = (BdaBoolean) Iec61850DeviceService.this
                                .getChildOfNodeWithConstraint(clockConfiguration,
                                        LogicalNodeAttributeDefinitons.PROPERTY_POSITION_DAYLIGHT_SAVING_ENABLED, Fc.CF);

                        LOGGER.info("Updating AutomaticSummerTimingEnabled to {}",
                                configuration.isAutomaticSummerTimingEnabled());

                        // Get the value and send the value to the device.
                        automaticSummerTimingEnabled.setValue(configuration.isAutomaticSummerTimingEnabled());
                        clientAssociation.setDataValues(automaticSummerTimingEnabled);
                    }

                }

                // checking to see if all network values are null, so that we
                // don't read the values for no reason
                if (!(configuration.isDhcpEnabled() == null && configuration.getDeviceFixIpValue() == null)) {

                    final String networkObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                            + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                            + LogicalNodeAttributeDefinitons.PROPERTY_IP_CONFIGURATION;

                    final FcModelNode networkConfiguration = Iec61850DeviceService.this.getNode(serverModel,
                            networkObjectReference, Fc.CF);

                    if (configuration.isDhcpEnabled() != null) {

                        final BdaBoolean dhcpEnabled = (BdaBoolean) Iec61850DeviceService.this
                                .getChildOfNodeWithConstraint(networkConfiguration,
                                        LogicalNodeAttributeDefinitons.PROPERTY_POSITION_DHCP_ENABLED, Fc.CF);

                        LOGGER.info("Updating DhcpEnabled to {}", configuration.isDhcpEnabled());

                        // Get the value and send the value to the device.
                        dhcpEnabled.setValue(configuration.isDhcpEnabled());
                        clientAssociation.setDataValues(dhcpEnabled);
                    }

                    if (configuration.getDeviceFixIpValue() != null) {

                        final BdaVisibleString deviceFixIpValue = (BdaVisibleString) Iec61850DeviceService.this
                                .getChildOfNodeWithConstraint(networkConfiguration,
                                        LogicalNodeAttributeDefinitons.PROPERTY_POSITION_FIXED_IP, Fc.CF);

                        LOGGER.info("Updating DeviceFixIpValue to {}", configuration.getDeviceFixIpValue());

                        // Get the value and send the value to the device.
                        deviceFixIpValue.setValue(configuration.getDeviceFixIpValue());
                        clientAssociation.setDataValues(deviceFixIpValue);
                    }
                }

                // Disconnect from the device.
                clientAssociation.disconnect();

                return null;
            }
        };

        this.iec61850Client.sendCommandWithRetry(function);

    }

    private void rebootDevice(final ServerModel serverModel, final ClientAssociation clientAssociation,
            final String deviceIdentification) throws ProtocolAdapterException {

        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {
                final String rbOperObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                        + LogicalNodeAttributeDefinitons.PROPERTY_RB_OPER;
                LOGGER.info("device: {}, rbOperObjectReference: {}", deviceIdentification, rbOperObjectReference);

                final FcModelNode rebootConfiguration = (FcModelNode) serverModel.findModelNode(rbOperObjectReference,
                        Fc.CO);
                LOGGER.info("device: {}, rebootConfiguration: {}", deviceIdentification, rebootConfiguration);

                final FcModelNode oper = (FcModelNode) rebootConfiguration.getChild(
                        LogicalNodeAttributeDefinitons.PROPERTY_RB_OPER_ATTRIBUTE_OPER, Fc.CO);
                LOGGER.info("device: {}, oper: {}", deviceIdentification, oper);

                final BdaBoolean ctlVal = (BdaBoolean) oper.getChild(
                        LogicalNodeAttributeDefinitons.PROPERTY_RB_OPER_ATTRIBUTE_CONTROL, Fc.CO);
                LOGGER.info("device: {}, ctlVal: {}", deviceIdentification, ctlVal);

                ctlVal.setValue(true);
                LOGGER.info("device: {}, set ctlVal to true in order to reboot the device", deviceIdentification);

                clientAssociation.setDataValues(oper);
                return null;
            }
        };

        this.iec61850Client.sendCommandWithRetry(function);
    }

    // This code will be used in the future
    private void checkRelayTypes(final DeviceOutputSetting deviceOutputSetting, final ServerModel serverModel)
            throws InvalidConfigurationException {

        final String relaySwitchTypeObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                + LogicalNodeAttributeDefinitons.getNodeNameForRelayIndex(deviceOutputSetting.getInternalId())
                + LogicalNodeAttributeDefinitons.PROPERTY_SWITCH_TYPE;

        LOGGER.info("relaySwitchTypeObjectReference: {}", relaySwitchTypeObjectReference);

        final FcModelNode switchTypeState = (FcModelNode) serverModel.findModelNode(relaySwitchTypeObjectReference,
                Fc.ST);
        final BdaInt8 state = (BdaInt8) switchTypeState.getChild("stVal");

        if (DeviceRelayType.getByIndex(state.getValue()).name() != deviceOutputSetting.getRelayType().name()) {
            // Inconsistent configuration, throwing exception
            throw new InvalidConfigurationException(String.format(
                    "RelayType of relay %d, {%s} is nconsisntent with the device output settings {%s}",
                    deviceOutputSetting.getExternalId(), DeviceRelayType.getByIndex(state.getValue()),
                    deviceOutputSetting.getRelayType()));
        }
    }

    /*
     * Returns an FcModelNode, or throws an exception if the returned node is
     * null.
     */
    private FcModelNode getNode(final ServerModel serverModel, final String objectReference,
            final Fc functionalConstraint) {

        final FcModelNode output = (FcModelNode) serverModel.findModelNode(objectReference, Fc.CF);
        if (output == null) {
            LOGGER.info("{} is null", objectReference);
            // TODO exceptionHandling
        }

        return output;

    }

    /*
     * Returns the child of a node, or throws an exception if the returned child
     * is null.
     */
    private ModelNode getChildOfNode(final FcModelNode modelNode, final String attribute) {

        final ModelNode output = modelNode.getChild(attribute);
        if (output == null) {
            LOGGER.info("{} is null", attribute);
            // TODO exceptionHandling
        }

        return output;
    }

    /*
     * Returns the child of a node, using a given {@link Fc} or throws an
     * exception if the returned child is null.
     */
    private ModelNode getChildOfNodeWithConstraint(final FcModelNode modelNode, final String attribute,
            final Fc functionalConstraint) {

        final ModelNode output = modelNode.getChild(attribute, functionalConstraint);
        if (output == null) {
            LOGGER.info("{} is null", attribute);
            // TODO exceptionHandling
        }

        return output;
    }
}
