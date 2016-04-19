/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openmuc.openiec61850.BdaBoolean;
import org.openmuc.openiec61850.BdaInt16;
import org.openmuc.openiec61850.BdaInt16U;
import org.openmuc.openiec61850.BdaInt32;
import org.openmuc.openiec61850.BdaInt8;
import org.openmuc.openiec61850.BdaInt8U;
import org.openmuc.openiec61850.BdaTimestamp;
import org.openmuc.openiec61850.BdaVisibleString;
import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.ConstructedDataAttribute;
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
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.GetPowerUsageHistoryDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetConfigurationDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetLightDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetScheduleDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetTransitionDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.UpdateFirmwareDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.EmptyDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.GetConfigurationDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.GetFirmwareVersionDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.GetPowerUsageHistoryDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.GetStatusDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.DeviceRelayType;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.ScheduleEntry;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.ScheduleWeekday;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.TriggerType;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ConnectionFailureException;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.InvalidConfigurationException;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.Function;
import com.alliander.osgp.core.db.api.iec61850.application.services.SsldDataService;
import com.alliander.osgp.core.db.api.iec61850.entities.DeviceOutputSetting;
import com.alliander.osgp.core.db.api.iec61850.entities.Ssld;
import com.alliander.osgp.core.db.api.iec61850valueobjects.RelayType;
import com.alliander.osgp.dto.valueobjects.ActionTimeType;
import com.alliander.osgp.dto.valueobjects.Configuration;
import com.alliander.osgp.dto.valueobjects.DaliConfiguration;
import com.alliander.osgp.dto.valueobjects.DeviceStatus;
import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;
import com.alliander.osgp.dto.valueobjects.HistoryTermType;
import com.alliander.osgp.dto.valueobjects.LightType;
import com.alliander.osgp.dto.valueobjects.LightValue;
import com.alliander.osgp.dto.valueobjects.LinkType;
import com.alliander.osgp.dto.valueobjects.LongTermIntervalType;
import com.alliander.osgp.dto.valueobjects.MeterType;
import com.alliander.osgp.dto.valueobjects.PowerUsageData;
import com.alliander.osgp.dto.valueobjects.PowerUsageHistoryMessageDataContainer;
import com.alliander.osgp.dto.valueobjects.RelayConfiguration;
import com.alliander.osgp.dto.valueobjects.RelayData;
import com.alliander.osgp.dto.valueobjects.RelayMap;
import com.alliander.osgp.dto.valueobjects.Schedule;
import com.alliander.osgp.dto.valueobjects.SsldData;
import com.alliander.osgp.dto.valueobjects.TimePeriod;
import com.alliander.osgp.dto.valueobjects.TransitionMessageDataContainer;
import com.alliander.osgp.dto.valueobjects.TransitionType;
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

    @Resource
    private int selftestTimeout;

    private static final int DEFAULT_SCHEDULE_VALUE = -1;

    private static final String FUNCTIONAL_FIRMWARE_TYPE_DESCRIPTION = "Functional firmware version";

    private static final String SECURITY_FIRMWARE_TYPE_DESCRIPTION = "Security firmware version";

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

        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during getStatus", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, true);
        }
    }

    @Override
    public void getPowerUsageHistory(final GetPowerUsageHistoryDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler) {

        try {

            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);

            final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());
            final List<DeviceOutputSetting> deviceOutputSettingsLightRelays = this.ssldDataService.findByRelayType(
                    ssld, RelayType.LIGHT);

            final List<PowerUsageData> powerUsageHistoryData = this.getPowerUsageHistoryDataFromDevice(serverModel,
                    deviceRequest.getDeviceIdentification(), deviceRequest.getPowerUsageHistoryContainer(),
                    deviceOutputSettingsLightRelays);

            final GetPowerUsageHistoryDeviceResponse deviceResponse = new GetPowerUsageHistoryDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK, powerUsageHistoryData);

            deviceResponseHandler.handleResponse(deviceResponse);

        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during getPowerUsageHistory", e);

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
                    for (final DeviceOutputSetting deviceOutputSetting : this.ssldDataService.findByRelayType(ssld,
                            RelayType.LIGHT)) {
                        this.switchLightRelay(deviceOutputSetting.getInternalId(), lightValue.isOn(), serverModel,
                                clientAssociation);
                    }
                } else {

                    final DeviceOutputSetting deviceOutputSetting = this.ssldDataService
                            .getDeviceOutputSettingForExternalIndex(ssld, lightValue.getIndex());

                    if (deviceOutputSetting != null) {

                        // You can only switch LIGHT relays that are used
                        this.checkRelay(deviceOutputSetting.getRelayType(), RelayType.LIGHT);

                        this.switchLightRelay(deviceOutputSetting.getInternalId(), lightValue.isOn(), serverModel,
                                clientAssociation);
                    }

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
            for (final DeviceOutputSetting deviceOutputSetting : this.ssldDataService.findByRelayType(ssld,
                    RelayType.LIGHT)) {
                lightRelays.add(deviceOutputSetting.getExternalId());
                this.switchLightRelay(deviceOutputSetting.getInternalId(), startOfTest, serverModel, clientAssociation);
            }

            // Disconnect from the device.
            clientAssociation.disconnect();

            // Sleep and wait
            try {
                LOGGER.info("Waiting {} seconds before getting the device status", this.selftestTimeout / 1000);
                Thread.sleep(this.selftestTimeout);
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

    @Override
    public void setSchedule(final SetScheduleDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler) {
        try {
            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());

            final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());

            // TODO make this method more generic once the light schedules are
            // implemented
            this.setTariffScheduleOnDevice(serverModel, clientAssociation, deviceRequest
                    .getScheduleMessageDataContainer().getScheduleList(), ssld);

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
    public void getFirmwareVersion(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler) {

        try {
            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);

            // Getting the data with retries
            final List<FirmwareVersionDto> firmwareVersions = this.getFirmwareVersionFromDevice(serverModel);

            final GetFirmwareVersionDeviceResponse deviceResponse = new GetFirmwareVersionDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), firmwareVersions);

            deviceResponseHandler.handleResponse(deviceResponse);

        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during getFirmwareVersion", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, true);
        }

    }

    @Override
    public void setTransition(final SetTransitionDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler) {

        try {
            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());

            this.transitionDevice(serverModel, clientAssociation, deviceRequest.getDeviceIdentification(),
                    deviceRequest.getTransitionTypeContainer());

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
    public void updateFirmware(final UpdateFirmwareDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler) {

        try {

            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());

            this.pushFirmwareToDevice(serverModel, clientAssociation,
                    deviceRequest.getFirmwareDomain().concat(deviceRequest.getFirmwareUrl()));

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

    // ======================================
    // PRIVATE DEVICE COMMUNICATION METHODS =
    // ======================================

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

                // TODO remove this once the Kaifa device can handle the
                // calls again
                LOGGER.warn("Sleeping for 5 seconds before moving on");
                Thread.sleep(5000);

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
                final String softwareConfigurationObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                        + LogicalNodeAttributeDefinitons.PROPERTY_SOFTWARE_CONFIGURATION;

                final FcModelNode softwareConfiguration = (FcModelNode) serverModel.findModelNode(
                        softwareConfigurationObjectReference, Fc.CF);

                final BdaVisibleString lightTypeValue = (BdaVisibleString) softwareConfiguration
                        .getChild(LogicalNodeAttributeDefinitons.PROPERTY_SOFTWARE_CONFIG_LIGHT_TYPE);
                final LightType lightType = LightType.valueOf(lightTypeValue.getStringValue());

                return new DeviceStatus(lightValues, LinkType.ETHERNET, LinkType.ETHERNET, lightType, 0);
            }
        };

        return this.iec61850Client.sendCommandWithRetry(function);

    }

    private List<PowerUsageData> getPowerUsageHistoryDataFromDevice(final ServerModel serverModel,
            final String deviceIdentification, final PowerUsageHistoryMessageDataContainer powerUsageHistoryContainer,
            final List<DeviceOutputSetting> deviceOutputSettingsLightRelays) throws ProtocolAdapterException {

        final HistoryTermType historyTermType = powerUsageHistoryContainer.getHistoryTermType();
        if (historyTermType != null) {
            LOGGER.info("device: {}, ignoring HistoryTermType ({}) determining power usage history",
                    deviceIdentification, historyTermType);
        }
        final TimePeriod timePeriod = powerUsageHistoryContainer.getTimePeriod();

        final Function<List<PowerUsageData>> function = new Function<List<PowerUsageData>>() {

            @Override
            public List<PowerUsageData> apply() throws Exception {
                final List<PowerUsageData> powerUsageHistoryData = new ArrayList<>();
                for (final DeviceOutputSetting deviceOutputSetting : deviceOutputSettingsLightRelays) {
                    final List<PowerUsageData> powerUsageData = Iec61850DeviceService.this
                            .getPowerUsageHistoryDataFromRelay(serverModel, deviceIdentification, timePeriod,
                                    deviceOutputSetting);
                    powerUsageHistoryData.addAll(powerUsageData);
                }
                /*-
                 * This way of gathering leads to PowerUsageData elements per relay.
                 * If it is necessary to only include one PowerUsageData element for
                 * the device, where data for the different relays is combined in
                 * the SsldData.relayData some sort of merge needs to be performed.
                 *
                 * This can either be a rework of the list currently returned, or it
                 * can be a list constructed based on an altered return type from
                 * getPowerUsageHistoryDataFromRelay (for instance a Map of Date to
                 * a Map of Relay Index to Total Lighting Minutes).
                 */
                return powerUsageHistoryData;
            }
        };

        return this.iec61850Client.sendCommandWithRetry(function);
    }

    private List<PowerUsageData> getPowerUsageHistoryDataFromRelay(final ServerModel serverModel,
            final String deviceIdentification, final TimePeriod timePeriod,
            final DeviceOutputSetting deviceOutputSetting) throws TechnicalException {
        final List<PowerUsageData> powerUsageHistoryDataFromRelay = new ArrayList<>();

        final int relayIndex = deviceOutputSetting.getExternalId();

        final String nodeName = LogicalNodeAttributeDefinitons.getNodeNameForRelayIndex(deviceOutputSetting
                .getInternalId());
        final String onIntervalBufferObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE + nodeName
                + LogicalNodeAttributeDefinitons.PROPERTY_SWITCH_ON_ITV_B;
        LOGGER.info("onIntervalBufferObjectReference: {}", onIntervalBufferObjectReference);
        final FcModelNode onItvB = this.getNode(serverModel, onIntervalBufferObjectReference, Fc.ST);
        LOGGER.info("device: {}, onItvB: {}", deviceIdentification, onItvB);

        final ModelNode lastIdx = onItvB
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_SWITCH_ON_ITV_B_ATTRIBUTE_LAST_IDX);
        LOGGER.info("device: {}, lastIdx: {}", deviceIdentification, lastIdx);

        /*-
         * Last index is the last index written in the 60-entry buffer.
         * When the last buffer entry is written, the next entry will be placed
         * at the first position in the buffer (cyclical).
         * To preserve the order of entries written in the response, iteration
         * starts with the next index (oldest entry) and loops from there.
         */
        final int numberOfEntries = 60;
        final int idxOldest = (((BdaInt8U) lastIdx).getValue() + 1) % numberOfEntries;
        for (int i = 0; i < numberOfEntries; i++) {
            final int bufferIndex = (idxOldest + i) % numberOfEntries;
            final ModelNode indexedItvNode = onItvB
                    .getChild(LogicalNodeAttributeDefinitons.PROPERTY_SWITCH_ON_ITV_B_ATTRIBUTE_ITV + (bufferIndex + 1));
            LOGGER.info("device: {}, itv{}: {}", deviceIdentification, bufferIndex + 1, indexedItvNode);
            final ModelNode itvNode = indexedItvNode
                    .getChild(LogicalNodeAttributeDefinitons.PROPERTY_SWITCH_ON_ITV_B_ATTRIBUTE_ITV_ITV);
            LOGGER.info("device: {}, itv{}.itv: {}", deviceIdentification, bufferIndex + 1, itvNode);
            final ModelNode dayNode = indexedItvNode
                    .getChild(LogicalNodeAttributeDefinitons.PROPERTY_SWITCH_ON_ITV_B_ATTRIBUTE_ITV_DAY);
            LOGGER.info("device: {}, itv{}.day: {}", deviceIdentification, bufferIndex + 1, dayNode);

            final DateTime date = new DateTime(((BdaTimestamp) dayNode).getDate());
            final int totalMinutesOnForDate = ((BdaInt32) itvNode).getValue();

            final boolean includeEntryInResponse = this.periodIncludesDateForPowerUsageHistory(timePeriod, date,
                    deviceIdentification, relayIndex, bufferIndex);
            if (!includeEntryInResponse) {
                continue;
            }

            // MeterType.AUX hardcoded (not supported)
            final PowerUsageData powerUsageData = new PowerUsageData(date, MeterType.AUX, 0, 0);
            final List<RelayData> relayDataList = new ArrayList<>();
            final RelayData relayData = new RelayData(relayIndex, totalMinutesOnForDate);
            relayDataList.add(relayData);
            final SsldData ssldData = new SsldData(0, 0, 0, 0, 0, 0, 0, 0, 0, relayDataList);
            powerUsageData.setSsldData(ssldData);
            powerUsageHistoryDataFromRelay.add(powerUsageData);
        }

        return powerUsageHistoryDataFromRelay;
    }

    private boolean periodIncludesDateForPowerUsageHistory(final TimePeriod timePeriod, final DateTime date,
            final String deviceIdentification, final int relayIndex, final int bufferIndex) {
        if (timePeriod == null) {
            LOGGER.info(
                    "device: {}, no TimePeriod determining power usage history for relay {}, include entry for itv{}",
                    deviceIdentification, relayIndex, bufferIndex + 1);
            return true;
        }
        if (date == null) {
            LOGGER.info(
                    "device: {}, TimePeriod ({} - {}), determining power usage history for relay {}, skip entry for itv{}, no date",
                    deviceIdentification, timePeriod.getStartTime(), timePeriod.getEndTime(), relayIndex,
                    bufferIndex + 1);
            return false;
        }
        if (timePeriod.getStartTime() != null && date.isBefore(timePeriod.getStartTime())) {
            LOGGER.info(
                    "device: {}, determining power usage history for relay {}, skip entry for itv{}, date: {} is before start time: {}",
                    deviceIdentification, relayIndex, bufferIndex + 1, date, timePeriod.getStartTime());
            return false;
        }
        if (timePeriod.getEndTime() != null && date.isAfter(timePeriod.getEndTime())) {
            LOGGER.info(
                    "device: {}, determining power usage history for relay {}, skip entry for itv{}, date: {} is after end time: {}",
                    deviceIdentification, relayIndex, bufferIndex + 1, date, timePeriod.getEndTime());
            return false;
        }
        LOGGER.info(
                "device: {}, TimePeriod ({} - {}), determining power usage history for relay {}, include entry for itv{}, date: {}",
                deviceIdentification, timePeriod.getStartTime(), timePeriod.getEndTime(), relayIndex, bufferIndex + 1,
                date);
        return true;
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

                LOGGER.info("Reading the software configuration values");

                // Getting the LightType
                final String softwareConfigurationObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                        + LogicalNodeAttributeDefinitons.PROPERTY_SOFTWARE_CONFIGURATION;

                final FcModelNode softwareConfiguration = (FcModelNode) serverModel.findModelNode(
                        softwareConfigurationObjectReference, Fc.CF);

                final BdaVisibleString lightTypeValue = (BdaVisibleString) softwareConfiguration
                        .getChild(LogicalNodeAttributeDefinitons.PROPERTY_SOFTWARE_CONFIG_LIGHT_TYPE);
                final LightType lightType = LightType.valueOf(lightTypeValue.getStringValue());

                // These will be used later on
                final BdaInt16 astroGateSunRiseOffset = (BdaInt16) softwareConfiguration.getChild("osRise");
                final BdaInt16 astroGateSunSetOffset = (BdaInt16) softwareConfiguration.getChild("osSet");

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

                configuration.setOspgIpAddress(serverAddress.getStringValue());
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

                // setting the software configuration values

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

                final BdaInt16U timeSyncFrequency = (BdaInt16U) clockConfiguration.getChild("syncPer");
                final BdaBoolean automaticSummerTimingEnabled = (BdaBoolean) clockConfiguration
                        .getChild(LogicalNodeAttributeDefinitons.PROPERTY_POSITION_DAYLIGHT_SAVING_ENABLED);
                final BdaVisibleString summerTimeDetails = (BdaVisibleString) clockConfiguration.getChild("dstBegT");
                final BdaVisibleString winterTimeDetails = (BdaVisibleString) clockConfiguration.getChild("dstEndT");

                configuration.setTimeSyncFrequency(timeSyncFrequency.getValue());
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
                if (!(configuration.getAstroGateSunRiseOffset() == null
                        && configuration.getAstroGateSunSetOffset() == null && configuration.getLightType() == null)) {

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

                    if (configuration.getLightType() != null) {

                        final BdaVisibleString lightTypeValue = (BdaVisibleString) softwareConfiguration
                                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_SOFTWARE_CONFIG_LIGHT_TYPE);

                        LOGGER.info("Updating LightType to {}", configuration.getLightType());

                        // Get the value and send the value to the device.
                        lightTypeValue.setValue(configuration.getLightType().name());
                        clientAssociation.setDataValues(lightTypeValue);
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

                        final BdaInt16U timeSyncFrequency = (BdaInt16U) Iec61850DeviceService.this
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

    private void setTariffScheduleOnDevice(final ServerModel serverModel, final ClientAssociation clientAssociation,
            final List<Schedule> scheduleList, final Ssld ssld) throws ProtocolAdapterException, FunctionalException {

        // Creating a list of all Schedule entries, grouped by relay index
        final Map<Integer, List<ScheduleEntry>> relaySchedulesEntries = this.createScheduleEntries(scheduleList, ssld,
                RelayType.TARIFF);

        for (final Integer relayIndex : relaySchedulesEntries.keySet()) {

            final Function<Void> function = new Function<Void>() {

                @Override
                public Void apply() throws Exception {

                    // TODO clear existing schedule. Do this at the end for the
                    // remaining schedules?

                    final String scheduleObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                            + LogicalNodeAttributeDefinitons.getNodeNameForRelayIndex(relayIndex)
                            + LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE;

                    final FcModelNode scheduleConfiguration = Iec61850DeviceService.this.getNode(serverModel,
                            scheduleObjectReference, Fc.CF);

                    for (int i = 0; i < relaySchedulesEntries.get(relayIndex).size(); i++) {

                        LOGGER.info("Writing schedule entry {} for relay {}", i + 1, relayIndex);

                        final ScheduleEntry scheduleEntry = relaySchedulesEntries.get(relayIndex).get(i);

                        final ConstructedDataAttribute scheduleNode = (ConstructedDataAttribute) Iec61850DeviceService.this
                                .getChildOfNodeWithConstraint(scheduleConfiguration,
                                        LogicalNodeAttributeDefinitons.getSchedulePropertyNameForRelayIndex(i + 1),
                                        Fc.CF);

                        // Setting enables
                        final BdaBoolean enabled = (BdaBoolean) Iec61850DeviceService.this.getChildOfNode(scheduleNode,
                                LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_ENABLE);
                        enabled.setValue(scheduleEntry.isEnabled());
                        clientAssociation.setDataValues(enabled);

                        final BdaInt32 day = (BdaInt32) Iec61850DeviceService.this.getChildOfNode(scheduleNode,
                                LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_DAY);
                        day.setValue(scheduleEntry.getWeekday().getIndex());
                        clientAssociation.setDataValues(day);

                        // Setting the default values for all of these
                        int timeOnValue = DEFAULT_SCHEDULE_VALUE;
                        byte timeOnTypeValue = DEFAULT_SCHEDULE_VALUE;
                        int timeOffValue = DEFAULT_SCHEDULE_VALUE;
                        byte timeOffTypeValue = DEFAULT_SCHEDULE_VALUE;

                        // checking to see if the timeOn of timeOff values have
                        // to be filled
                        if (scheduleEntry.isOn()) {
                            timeOnValue = scheduleEntry.getTime();
                            timeOnTypeValue = (byte) scheduleEntry.getTriggerType().getIndex();

                        } else {
                            timeOffValue = scheduleEntry.getTime();
                            timeOffTypeValue = (byte) scheduleEntry.getTriggerType().getIndex();
                        }

                        final BdaInt32 timeOn = (BdaInt32) Iec61850DeviceService.this.getChildOfNode(scheduleNode,
                                LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_TIME_ON);
                        timeOn.setValue(timeOnValue);
                        clientAssociation.setDataValues(timeOn);

                        final BdaInt8 timeOnActionTime = (BdaInt8) Iec61850DeviceService.this.getChildOfNode(
                                scheduleNode, LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_TIME_ON_TYPE);
                        timeOnActionTime.setValue(timeOnTypeValue);
                        clientAssociation.setDataValues(timeOnActionTime);

                        final BdaInt32 timeOff = (BdaInt32) Iec61850DeviceService.this.getChildOfNode(scheduleNode,
                                LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_TIME_OFF);
                        timeOff.setValue(timeOffValue);
                        clientAssociation.setDataValues(timeOff);

                        final BdaInt8 timeOffActionTime = (BdaInt8) Iec61850DeviceService.this.getChildOfNode(
                                scheduleNode, LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_TIME_OFF_TYPE);
                        timeOffActionTime.setValue(timeOffTypeValue);
                        clientAssociation.setDataValues(timeOffActionTime);

                    }

                    return null;
                }
            };

            this.iec61850Client.sendCommandWithRetry(function);

        }

    }

    private List<FirmwareVersionDto> getFirmwareVersionFromDevice(final ServerModel serverModel)
            throws ProtocolAdapterException {

        // creating the function that will be retried, if necessary
        final Function<List<FirmwareVersionDto>> function = new Function<List<FirmwareVersionDto>>() {

            @Override
            public List<FirmwareVersionDto> apply() throws Exception {

                final List<FirmwareVersionDto> output = new ArrayList<>();

                // Getting the functional firmware version
                LOGGER.info("Reading the functional firmware version");

                final String functionalFirmwareConfigurationObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                        + LogicalNodeAttributeDefinitons.PROPERTY_FUNCTIONAL_FIRMWARE_CONFIGURATION;

                final FcModelNode functionalFirmwareConfiguration = (FcModelNode) serverModel.findModelNode(
                        functionalFirmwareConfigurationObjectReference, Fc.ST);

                final BdaVisibleString functionalFirmwareVersion = (BdaVisibleString) functionalFirmwareConfiguration
                        .getChild(LogicalNodeAttributeDefinitons.PROPERTY_FIRMWARE_CONFIG_CURRENT_VERSION);

                // Adding it to the list
                output.add(new FirmwareVersionDto(Iec61850DeviceService.FUNCTIONAL_FIRMWARE_TYPE_DESCRIPTION,
                        functionalFirmwareVersion.getStringValue()));

                // Getting the security firmware version
                LOGGER.info("Reading the security firmware version");

                final String securityFirmwareConfigurationObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                        + LogicalNodeAttributeDefinitons.PROPERTY_SECURITY_FIRMWARE_CONFIGURATION;

                final FcModelNode securityFirmwareConfiguration = (FcModelNode) serverModel.findModelNode(
                        securityFirmwareConfigurationObjectReference, Fc.ST);

                final BdaVisibleString securityFirmwareVersion = (BdaVisibleString) securityFirmwareConfiguration
                        .getChild(LogicalNodeAttributeDefinitons.PROPERTY_FIRMWARE_CONFIG_CURRENT_VERSION);

                // Adding it to the list
                output.add(new FirmwareVersionDto(Iec61850DeviceService.SECURITY_FIRMWARE_TYPE_DESCRIPTION,
                        securityFirmwareVersion.getStringValue()));

                return output;
            }
        };

        return this.iec61850Client.sendCommandWithRetry(function);

    }

    private void transitionDevice(final ServerModel serverModel, final ClientAssociation clientAssociation,
            final String deviceIdentification, final TransitionMessageDataContainer transitionMessageDataContainer)
                    throws ProtocolAdapterException {

        final TransitionType transitionType = transitionMessageDataContainer.getTransitionType();
        LOGGER.info("device: {}, transition: {}", deviceIdentification, transitionType);
        final boolean controlValueForTransition = transitionType.equals(TransitionType.DAY_NIGHT);

        final DateTime dateTime = transitionMessageDataContainer.getDateTime();
        if (dateTime != null) {
            LOGGER.warn("device: {}, setting date/time {} for transition {} not supported", deviceIdentification,
                    dateTime, transitionType);
        }

        final Function<Void> function = new Function<Void>() {
            @Override
            public Void apply() throws Exception {
                final String sensorObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                        + LogicalNodeAttributeDefinitons.PROPERTY_SENSOR;
                LOGGER.info("device: {}, sensorObjectReference: {}", deviceIdentification, sensorObjectReference);

                final FcModelNode sensorConfiguration = (FcModelNode) serverModel.findModelNode(sensorObjectReference,
                        Fc.CO);
                LOGGER.info("device: {}, sensorConfiguration: {}", deviceIdentification, sensorConfiguration);

                final FcModelNode oper = (FcModelNode) sensorConfiguration.getChild(
                        LogicalNodeAttributeDefinitons.PROPERTY_SENSOR_ATTRIBUTE_OPER, Fc.CO);
                LOGGER.info("device: {}, oper: {}", deviceIdentification, oper);

                final BdaBoolean ctlVal = (BdaBoolean) oper.getChild(
                        LogicalNodeAttributeDefinitons.PROPERTY_SENSOR_ATTRIBUTE_CONTROL, Fc.CO);
                LOGGER.info("device: {}, ctlVal: {}", deviceIdentification, ctlVal);

                ctlVal.setValue(controlValueForTransition);
                LOGGER.info("device: {}, set ctlVal to {} means {} message", deviceIdentification,
                        controlValueForTransition, controlValueForTransition ? "Evening" : "Morning");

                clientAssociation.setDataValues(oper);
                return null;
            }
        };

        this.iec61850Client.sendCommandWithRetry(function);
    }

    private void pushFirmwareToDevice(final ServerModel serverModel, final ClientAssociation clientAssociation,
            final String fullUrl) throws ProtocolAdapterException, FunctionalException {

        // creating the function that will be retried, if necessary
        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {

                // Getting the functional firmware version
                LOGGER.info("Reading the functional firmware version");

                final String functionalFirmwareConfigurationObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                        + LogicalNodeAttributeDefinitons.PROPERTY_FUNCTIONAL_FIRMWARE_CONFIGURATION;

                final FcModelNode functionalFirmwareConfiguration = (FcModelNode) serverModel.findModelNode(
                        functionalFirmwareConfigurationObjectReference, Fc.CF);

                final BdaVisibleString functionalFirmwareDownloadUrl = (BdaVisibleString) functionalFirmwareConfiguration
                        .getChild(LogicalNodeAttributeDefinitons.PROPERTY_FIRMWARE_CONFIG_DOWNLOAD_URL);

                final BdaTimestamp functionalFirmwareStartTime = (BdaTimestamp) functionalFirmwareConfiguration
                        .getChild(LogicalNodeAttributeDefinitons.PROPERTY_FIRMWARE_CONFIG_START_TIME);

                LOGGER.info("Updating the firmware download url to {}", fullUrl);

                functionalFirmwareDownloadUrl.setValue(fullUrl);
                clientAssociation.setDataValues(functionalFirmwareDownloadUrl);

                final Date oneMinuteFromNow = Iec61850DeviceService.this.getLocalTimeForDevice(serverModel)
                        .plusMinutes(1).toDate();

                LOGGER.info("Updating the firmware download start time to {}", oneMinuteFromNow);

                functionalFirmwareStartTime.setDate(oneMinuteFromNow);
                clientAssociation.setDataValues(functionalFirmwareStartTime);

                return null;
            }
        };

        this.iec61850Client.sendCommandWithRetry(function);

    }

    // ========================
    // PRIVATE HELPER METHODS =
    // ========================

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

        final FcModelNode output = (FcModelNode) serverModel.findModelNode(objectReference, functionalConstraint);
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

    /*
     * Checks to see if the relay has the correct type, throws an exception when
     * that't not the case
     */
    private void checkRelay(final RelayType actual, final RelayType expected) throws FunctionalException {

        if (!actual.equals(expected)) {
            if (RelayType.LIGHT.equals(expected)) {
                throw new FunctionalException(FunctionalExceptionType.ACTION_NOT_ALLOWED_FOR_LIGHT_RELAY,
                        ComponentType.PROTOCOL_IEC61850);
            } else {
                throw new FunctionalException(FunctionalExceptionType.ACTION_NOT_ALLOWED_FOR_TARIFF_RELAY,
                        ComponentType.PROTOCOL_IEC61850);
            }
        }
    }

    /*
     * Converts a {@link Schedule} to a {@link ScheduleEntry}
     */
    private ScheduleEntry convertToScheduleEntry(final Schedule schedule, final LightValue lightValue) {

        // A time is formatted as hh:mm:ss, the time on the device is formatted
        // as hhmm in int form
        final short time = Short.valueOf(schedule.getTime().replace(":", "").substring(0, 4));

        // TODO what to do when weekday is ABSOLUTEDAY? Special days is not
        // implemented yet
        final ScheduleWeekday weekday = ScheduleWeekday.valueOf(schedule.getWeekDay().name());

        // ActionTime ABSOLUTETIME --> Fix
        // TriggerType LIGHT_TRIGGER & ActionTime SUNRISE or SUNSET --> Sensor
        // TriggerType ASTRONOMICAL --> Autonome
        final TriggerType triggerType;

        if (ActionTimeType.ABSOLUTETIME.equals(schedule.getActionTime())) {
            triggerType = TriggerType.FIX;
        } else if (com.alliander.osgp.dto.valueobjects.TriggerType.ASTRONOMICAL.equals(schedule.getTriggerType())) {
            triggerType = TriggerType.AUTONOME;
        } else {
            triggerType = TriggerType.SENSOR;
        }

        return new ScheduleEntry(schedule.getIsEnabled() == null ? true : schedule.getIsEnabled(), triggerType,
                weekday, time, lightValue.isOn());

    }

    /*
     * returns a map of schedule entries, grouped by the internal index
     */
    private Map<Integer, List<ScheduleEntry>> createScheduleEntries(final List<Schedule> scheduleList, final Ssld ssld,
            final RelayType relayType) throws FunctionalException {

        final Map<Integer, List<ScheduleEntry>> relaySchedulesEntries = new HashMap<>();

        for (final Schedule schedule : scheduleList) {
            for (final LightValue lightValue : schedule.getLightValue()) {

                final List<Integer> indexes = new ArrayList<>();

                if (lightValue.getIndex() == 0 && RelayType.TARIFF.equals(relayType)) {

                    // Index 0 is not allowed for tariff switching
                    throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                            ComponentType.PROTOCOL_IEC61850);

                } else if (lightValue.getIndex() == 0 && RelayType.LIGHT.equals(relayType)) {

                    // index == 0, getting all light relays and adding their
                    // internal indexes to the indexes list
                    final List<DeviceOutputSetting> settings = Iec61850DeviceService.this.ssldDataService
                            .findByRelayType(ssld, relayType);

                    for (final DeviceOutputSetting deviceOutputSetting : settings) {
                        indexes.add(deviceOutputSetting.getInternalId());
                    }
                } else {
                    // index != 0, adding just the one index to the list
                    indexes.add(Iec61850DeviceService.this.ssldDataService.convertToInternalIndex(ssld,
                            lightValue.getIndex()));
                }

                final ScheduleEntry scheduleEntry = this.convertToScheduleEntry(schedule, lightValue);

                for (final Integer internalIndex : indexes) {

                    if (relaySchedulesEntries.containsKey(internalIndex)) {
                        // Internal index already in the Map, adding to the List
                        relaySchedulesEntries.get(internalIndex).add(scheduleEntry);
                    } else {

                        // First time we come across this relay, checking its
                        // type
                        Iec61850DeviceService.this.checkRelay(Iec61850DeviceService.this.ssldDataService
                                .getDeviceOutputSettingForInternalIndex(ssld, internalIndex).getRelayType(), relayType);

                        // Adding it to scheduleEntries
                        final List<ScheduleEntry> scheduleEntries = new ArrayList<>();
                        scheduleEntries.add(scheduleEntry);

                        relaySchedulesEntries.put(internalIndex, scheduleEntries);
                    }
                }
            }
        }

        return relaySchedulesEntries;

    }

    /*
     * Checks the time zone of the device and check to see if daylight saving is
     * in effect and adjusts the current time accordingly
     */
    private DateTime getLocalTimeForDevice(final ServerModel serverModel) {

        LOGGER.info("Converting local time to the device's local time");

        // getting the clock configuration values
        LOGGER.info("Reading the clock configuration values");

        final String clockObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC + LogicalNodeAttributeDefinitons.PROPERTY_CLOCK;

        LOGGER.info("clockObjectReference: {}", clockObjectReference);

        final FcModelNode clockConfiguration = (FcModelNode) serverModel.findModelNode(clockObjectReference, Fc.CF);

        // Checking to see if daylight savings is enabled.
        final BdaBoolean automaticSummerTimingEnabled = (BdaBoolean) clockConfiguration
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_POSITION_DAYLIGHT_SAVING_ENABLED);

        if (automaticSummerTimingEnabled.getValue()) {

            // TODO figure out which time is used when daylight savings is
            // disabled. For example, if you disable it when daylight savings is
            // in effect, does it stay in daylight saving all year round or does
            // it revert to regular time?

            // TODO use these once a better time format is introduced. Check to
            // see if the current date is between the start and end time of the
            // daylight saving. Also check to see if the current time is in
            // daylight savings.

            // final BdaVisibleString summerTimeDetails = (BdaVisibleString)
            // clockConfiguration.getChild("dstBegT");
            // final BdaVisibleString winterTimeDetails = (BdaVisibleString)
            // clockConfiguration.getChild("dstEndT");
        }

        final BdaInt16 timezone = (BdaInt16) clockConfiguration
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_CLOCK_TIME_ZONE);

        // TODO Default value for time zone offset is 60, so I'm assuming that
        // means 60 minutes / 1 hour. Verify this assumption.
        final int offset = timezone.getValue() / 60;

        return DateTime.now().withZone(DateTimeZone.forOffsetHours(offset));
    }
}
