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

import com.alliander.osgp.adapter.protocol.iec61850.application.mapping.Iec61850pMapper;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceMessageStatus;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponseHandler;
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
    private Iec61850pMapper mapper;

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

    // =================
    // PRIVATE METHODS =
    // =================

    private void switchLightRelay(final int index, final boolean on, final ServerModel serverModel,
            final ClientAssociation clientAssociation) throws ProtocolAdapterException {

        // Commands don't return anything, so returnType is Void
        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {

                final String nodeName = LogicalNodeAttributeDefinitons.getNodeNameForRelayIndex(index);

                final String relayPositionOperationObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + nodeName + LogicalNodeAttributeDefinitons.PROPERTY_POSITION;
                LOGGER.info("xswc1PositionOperationObjectReference: {}", relayPositionOperationObjectReference);

                // Check if the Pos.ctlModel [CF] is enabled. If it is not
                // enabled, the relay can not be operated.
                final FcModelNode posCtlModel = (FcModelNode) serverModel.findModelNode(
                        relayPositionOperationObjectReference, Fc.CF);
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
                        + LogicalNodeAttributeDefinitons.PROPERTY_NODE_CSLC_PREFIX
                        + LogicalNodeAttributeDefinitons.PROPERTY_REG_CONFIGURATION;

                LOGGER.info("regObjectReference: {}", regObjectReference);

                final FcModelNode regConfiguration = (FcModelNode) serverModel.findModelNode(regObjectReference, Fc.CF);

                final BdaVisibleString serverAddress = (BdaVisibleString) regConfiguration.getChild("svrAddr");

                configuration.setOspgIpAddress(new String(serverAddress.getValue()));

                // getting the ip configuration values

                LOGGER.info("Reading the ip configuration values");

                final String ipcfObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + LogicalNodeAttributeDefinitons.PROPERTY_NODE_CSLC_PREFIX
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
                        + LogicalNodeAttributeDefinitons.PROPERTY_NODE_CSLC_PREFIX
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
                        + LogicalNodeAttributeDefinitons.PROPERTY_NODE_CSLC_PREFIX
                        + LogicalNodeAttributeDefinitons.PROPERTY_CLOCK;

                LOGGER.info("clockObjectReference: {}", clockObjectReference);

                final FcModelNode clockConfiguration = (FcModelNode) serverModel.findModelNode(clockObjectReference,
                        Fc.CF);

                final BdaInt16 communicationTimeout = (BdaInt16) clockConfiguration.getChild("syncPer");
                final BdaBoolean automaticSummerTimingEnabled = (BdaBoolean) clockConfiguration.getChild("enbDst");
                final BdaVisibleString summerTimeDetails = (BdaVisibleString) clockConfiguration.getChild("dstBegT");
                final BdaVisibleString winterTimeDetails = (BdaVisibleString) clockConfiguration.getChild("dstEndT");

                configuration.setCommunicationTimeout((int) communicationTimeout.getValue());
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
}
