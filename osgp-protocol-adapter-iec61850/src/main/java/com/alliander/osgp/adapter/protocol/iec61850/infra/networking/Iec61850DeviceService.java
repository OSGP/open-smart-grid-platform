/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import java.io.IOException;
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
import org.openmuc.openiec61850.ServiceError;
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
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetEventNotificationsDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetLightDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetScheduleDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetTransitionDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.UpdateDeviceSslCertificationDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.UpdateFirmwareDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.EmptyDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.GetConfigurationDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.GetFirmwareVersionDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.GetPowerUsageHistoryDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.responses.GetStatusDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.DaylightSavingTimeTransition;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.ScheduleEntry;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.ScheduleWeekday;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.TriggerType;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ConnectionFailureException;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.Function;
import com.alliander.osgp.core.db.api.iec61850.application.services.SsldDataService;
import com.alliander.osgp.core.db.api.iec61850.entities.DeviceOutputSetting;
import com.alliander.osgp.core.db.api.iec61850.entities.Ssld;
import com.alliander.osgp.core.db.api.iec61850valueobjects.RelayType;
import com.alliander.osgp.dto.valueobjects.ActionTimeTypeDto;
import com.alliander.osgp.dto.valueobjects.CertificationDto;
import com.alliander.osgp.dto.valueobjects.ConfigurationDto;
import com.alliander.osgp.dto.valueobjects.DaliConfigurationDto;
import com.alliander.osgp.dto.valueobjects.DeviceFixedIpDto;
import com.alliander.osgp.dto.valueobjects.DeviceStatusDto;
import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;
import com.alliander.osgp.dto.valueobjects.HistoryTermTypeDto;
import com.alliander.osgp.dto.valueobjects.LightTypeDto;
import com.alliander.osgp.dto.valueobjects.LightValueDto;
import com.alliander.osgp.dto.valueobjects.LinkTypeDto;
import com.alliander.osgp.dto.valueobjects.LongTermIntervalTypeDto;
import com.alliander.osgp.dto.valueobjects.MeterTypeDto;
import com.alliander.osgp.dto.valueobjects.PowerUsageDataDto;
import com.alliander.osgp.dto.valueobjects.PowerUsageHistoryMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.RelayConfigurationDto;
import com.alliander.osgp.dto.valueobjects.RelayDataDto;
import com.alliander.osgp.dto.valueobjects.RelayMapDto;
import com.alliander.osgp.dto.valueobjects.RelayTypeDto;
import com.alliander.osgp.dto.valueobjects.ScheduleDto;
import com.alliander.osgp.dto.valueobjects.SsldDataDto;
import com.alliander.osgp.dto.valueobjects.TimePeriodDto;
import com.alliander.osgp.dto.valueobjects.TransitionMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.TransitionTypeDto;
import com.alliander.osgp.dto.valueobjects.WeekDayTypeDto;
import com.alliander.osgp.dto.valueobjects.WindowTypeDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

@Component
public class Iec61850DeviceService implements DeviceService {

    private static final DateTimeZone TIME_ZONE_AMSTERDAM = DateTimeZone.forID("Europe/Amsterdam");

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850DeviceService.class);

    @Autowired
    private Iec61850DeviceConnectionService iec61850DeviceConnectionService;

    @Autowired
    private SsldDataService ssldDataService;

    @Autowired
    private Iec61850Client iec61850Client;

    @Autowired
    private Iec61850Mapper mapper;

    // Timeout between the SetLight and getStatus during the device selftest
    @Resource
    private int selftestTimeout;

    // The value used to indicate that the time on or time off of a schedule
    // entry is unused.
    private static final int DEFAULT_SCHEDULE_VALUE = -1;
    // The number of schedule entries available for a relay.
    private static final int MAX_NUMBER_OF_SCHEDULE_ENTRIES = 64;

    // Used to keep the firmware version apart in the FirmwareVersionDto objects
    // of getFirmwareVersion
    private static final String FUNCTIONAL_FIRMWARE_TYPE_DESCRIPTION = "Functional firmware version";
    private static final String SECURITY_FIRMWARE_TYPE_DESCRIPTION = "Security firmware version";

    private static final int SWITCH_TYPE_TARIFF = 0;
    private static final int SWITCH_TYPE_LIGHT = 1;

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
            final DeviceStatusDto deviceStatus = this.getStatusFromDevice(serverModel, ssld);

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

            final List<PowerUsageDataDto> powerUsageHistoryData = this.getPowerUsageHistoryDataFromDevice(serverModel,
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

            for (final LightValueDto lightValue : deviceRequest.getLightValuesContainer().getLightValues()) {

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

            final ConfigurationDto configuration = deviceRequest.getConfiguration();

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

            final ConfigurationDto configuration = this.getConfigurationFromDevice(serverModel, ssld);

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
            final DeviceStatusDto deviceStatus = this.getStatusFromDevice(serverModel, ssld);

            LOGGER.info("Fetching and checking the devicestatus");

            // Checking to see if all light relays have the correct state
            for (final LightValueDto lightValue : deviceStatus.getLightValues()) {
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

            this.setScheduleOnDevice(serverModel, clientAssociation, deviceRequest.getRelayType(), deviceRequest
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
            final Iec61850ClientAssociation iec61850ClientAssociation = this.iec61850DeviceConnectionService
                    .getIec61850ClientAssociation(deviceRequest.getDeviceIdentification());
            final ClientAssociation clientAssociation = iec61850ClientAssociation.getClientAssociation();

            this.transitionDevice(serverModel, clientAssociation, deviceRequest.getDeviceIdentification(),
                    deviceRequest.getTransitionTypeContainer());

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK);
            deviceResponseHandler.handleResponse(deviceResponse);

            // Enabling device reporting. This is placed here because this is
            // called twice a day.
            this.enableReportingOnDevice(serverModel, iec61850ClientAssociation,
                    deviceRequest.getDeviceIdentification());

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

    @Override
    public void updateDeviceSslCertification(final UpdateDeviceSslCertificationDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler) {

        try {

            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());

            this.pushSslCertificateToDevice(serverModel, clientAssociation, deviceRequest.getCertification());

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
    public void setEventNotifications(final SetEventNotificationsDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler) {

        LOGGER.info("Called setEventNotifications, doing nothing for now and returning OK");

        final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK);
        deviceResponseHandler.handleResponse(deviceResponse);
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

    private DeviceStatusDto getStatusFromDevice(final ServerModel serverModel, final Ssld ssld)
            throws ProtocolAdapterException {

        // getting the light relay values
        final List<LightValueDto> lightValues = new ArrayList<>();

        for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {

            final String relayPositionOperationObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                    + LogicalNodeAttributeDefinitons.getNodeNameForRelayIndex(deviceOutputSetting.getInternalId())
                    + LogicalNodeAttributeDefinitons.PROPERTY_POSITION;

            LOGGER.info("relayPositionOperationObjectReference: {}", relayPositionOperationObjectReference);

            final FcModelNode switchPositonState = (FcModelNode) serverModel.findModelNode(
                    relayPositionOperationObjectReference, Fc.ST);

            LOGGER.info("FcModelNode: {}", switchPositonState);

            final BdaBoolean state = (BdaBoolean) switchPositonState.getChild("stVal");

            final boolean on = state.getValue();
            lightValues.add(new LightValueDto(deviceOutputSetting.getExternalId(), on, null));

            LOGGER.info(String.format("Got status of relay %d => %s", deviceOutputSetting.getInternalId(), on ? "on"
                    : "off"));
        }

        // TODO caution: the referredLinkType and actualLinkType are
        // hardcoded
        // TODO eventNotificationsMask, the kaifa device will have a
        // 1-9
        // value that will have to be mapped to our
        // eventNotificationsMask

        // Getting the LightType
        final String softwareConfigurationObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                + LogicalNodeAttributeDefinitons.PROPERTY_SOFTWARE_CONFIGURATION;

        final FcModelNode softwareConfiguration = (FcModelNode) serverModel.findModelNode(
                softwareConfigurationObjectReference, Fc.CF);

        final BdaVisibleString lightTypeValue = (BdaVisibleString) softwareConfiguration
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_SOFTWARE_CONFIG_LIGHT_TYPE);
        final LightTypeDto lightType = LightTypeDto.valueOf(lightTypeValue.getStringValue());

        return new DeviceStatusDto(lightValues, LinkTypeDto.ETHERNET, LinkTypeDto.ETHERNET, lightType, 0);

    }

    private List<PowerUsageDataDto> getPowerUsageHistoryDataFromDevice(final ServerModel serverModel,
            final String deviceIdentification,
            final PowerUsageHistoryMessageDataContainerDto powerUsageHistoryContainer,
            final List<DeviceOutputSetting> deviceOutputSettingsLightRelays) throws ProtocolAdapterException,
            TechnicalException {

        final HistoryTermTypeDto historyTermType = powerUsageHistoryContainer.getHistoryTermType();
        if (historyTermType != null) {
            LOGGER.info("device: {}, ignoring HistoryTermType ({}) determining power usage history",
                    deviceIdentification, historyTermType);
        }
        final TimePeriodDto timePeriod = powerUsageHistoryContainer.getTimePeriod();

        final List<PowerUsageDataDto> powerUsageHistoryData = new ArrayList<>();
        for (final DeviceOutputSetting deviceOutputSetting : deviceOutputSettingsLightRelays) {
            final List<PowerUsageDataDto> powerUsageData = Iec61850DeviceService.this
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

    private ConfigurationDto getConfigurationFromDevice(final ServerModel serverModel, final Ssld ssld)
            throws ProtocolAdapterException {

        // Keeping the hardcoded values and values that aren't fetched from the
        // device out of the Function

        // Hardcoded (not supported)
        final MeterTypeDto meterType = MeterTypeDto.AUX;
        // Hardcoded (not supported)
        final Integer shortTermHistoryIntervalMinutes = 15;
        // Hardcoded (not supported)
        final LinkTypeDto preferredLinkType = LinkTypeDto.ETHERNET;
        // Hardcoded (not supported)
        final Integer longTermHistoryInterval = 1;
        // Hardcoded (not supported)
        final LongTermIntervalTypeDto longTermHistoryIntervalType = LongTermIntervalTypeDto.DAYS;

        final List<RelayMapDto> relayMaps = new ArrayList<>();

        for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {
            this.checkRelayType(deviceOutputSetting, serverModel);
            relayMaps.add(Iec61850DeviceService.this.mapper.map(deviceOutputSetting, RelayMapDto.class));
        }

        final RelayConfigurationDto relayConfiguration = new RelayConfigurationDto(relayMaps);

        // PSLD specific => just sending null so it'll be ignored
        final DaliConfigurationDto daliConfiguration = null;

        LOGGER.info("Reading the software configuration values");

        // Getting the LightType
        final String softwareConfigurationObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                + LogicalNodeAttributeDefinitons.PROPERTY_SOFTWARE_CONFIGURATION;

        final FcModelNode softwareConfiguration = (FcModelNode) serverModel.findModelNode(
                softwareConfigurationObjectReference, Fc.CF);

        final BdaVisibleString lightTypeValue = (BdaVisibleString) softwareConfiguration
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_SOFTWARE_CONFIG_LIGHT_TYPE);
        final LightTypeDto lightType = LightTypeDto.valueOf(lightTypeValue.getStringValue());

        final BdaInt16 astroGateSunRiseOffset = (BdaInt16) softwareConfiguration
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_POSITION_OFFSET_SUNRISE);
        final BdaInt16 astroGateSunSetOffset = (BdaInt16) softwareConfiguration
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_POSITION_OFFSET_SUNSET);

        final ConfigurationDto configuration = new ConfigurationDto(lightType, daliConfiguration, relayConfiguration,
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

        final BdaVisibleString deviceFixedIpAddress = (BdaVisibleString) ipConfiguration
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_POSITION_FIXED_IP_ADDRESS);
        final BdaVisibleString deviceFixedIpNetMask = (BdaVisibleString) ipConfiguration
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_POSITION_FIXED_IP_NETMASK);
        final BdaVisibleString deviceFixedIpGateway = (BdaVisibleString) ipConfiguration
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_POSITION_FIXED_IP_GATEWAY);
        final BdaBoolean dhcpEnabled = (BdaBoolean) ipConfiguration.getChild("enbDHCP");

        final DeviceFixedIpDto deviceFixedIp = new DeviceFixedIpDto(deviceFixedIpAddress.getStringValue(),
                deviceFixedIpNetMask.getStringValue(), deviceFixedIpGateway.getStringValue());

        configuration.setDeviceFixedIp(deviceFixedIp);
        configuration.setDhcpEnabled(dhcpEnabled.getValue());

        // setting the software configuration values

        configuration.setAstroGateSunRiseOffset((int) astroGateSunRiseOffset.getValue());
        configuration.setAstroGateSunSetOffset((int) astroGateSunSetOffset.getValue());

        // getting the clock configuration values

        LOGGER.info("Reading the clock configuration values");

        final String clockObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC + LogicalNodeAttributeDefinitons.PROPERTY_CLOCK;

        LOGGER.info("clockObjectReference: {}", clockObjectReference);

        final FcModelNode clockConfiguration = (FcModelNode) serverModel.findModelNode(clockObjectReference, Fc.CF);

        final BdaInt16U timeSyncFrequency = (BdaInt16U) clockConfiguration.getChild("syncPer");
        final BdaBoolean automaticSummerTimingEnabled = (BdaBoolean) clockConfiguration
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_POSITION_DAYLIGHT_SAVING_ENABLED);
        final BdaVisibleString summerTimeDetails = (BdaVisibleString) clockConfiguration.getChild("dstBegT");
        final BdaVisibleString winterTimeDetails = (BdaVisibleString) clockConfiguration.getChild("dstEndT");


        configuration.setTimeSyncFrequency(timeSyncFrequency.getValue());
        configuration.setAutomaticSummerTimingEnabled(automaticSummerTimingEnabled.getValue());
        configuration.setSummerTimeDetails(new DaylightSavingTimeTransition(TIME_ZONE_AMSTERDAM, summerTimeDetails
                .getStringValue()).getDateTimeForNextTransition().toDateTime(DateTimeZone.UTC));
        configuration.setWinterTimeDetails(new DaylightSavingTimeTransition(TIME_ZONE_AMSTERDAM, winterTimeDetails
                .getStringValue()).getDateTimeForNextTransition().toDateTime(DateTimeZone.UTC));

        return configuration;

    }

    private void checkRelayType(final DeviceOutputSetting deviceOutputSetting, final ServerModel serverModel) throws ProtocolAdapterException {

        final RelayType registeredRelayType = deviceOutputSetting.getRelayType();

        final int expectedSwType;
        if (RelayType.LIGHT.equals(registeredRelayType)) {
            expectedSwType = SWITCH_TYPE_LIGHT;
        } else if (RelayType.TARIFF.equals(registeredRelayType)
                || RelayType.TARIFF_REVERSED.equals(registeredRelayType)) {
            expectedSwType = SWITCH_TYPE_TARIFF;
        } else {
            throw new ProtocolAdapterException("DeviceOutputSetting (internal index = "
                    + deviceOutputSetting.getInternalId() + ", external index = " + deviceOutputSetting.getExternalId()
                    + ") does not have a known RelayType: " + registeredRelayType);
        }

        final String switchTypeObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                + LogicalNodeAttributeDefinitons.getNodeNameForRelayIndex(deviceOutputSetting.getInternalId())
                + LogicalNodeAttributeDefinitons.PROPERTY_SWITCH_TYPE;

        final FcModelNode switchTypeStatus = this.getNode(serverModel, switchTypeObjectReference, Fc.ST);

        final BdaInt8 swTypeValue = (BdaInt8) switchTypeStatus
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_SW_TYPE_ATTRIBUTE_VALUE);

        final int actualSwType = swTypeValue.getValue();
        if (expectedSwType != actualSwType) {
            throw new ProtocolAdapterException("DeviceOutputSetting (internal index = "
                    + deviceOutputSetting.getInternalId()
                    + ", external index = "
                    + deviceOutputSetting.getExternalId()
                    + ") has a RelayType ("
                    + registeredRelayType
                    + ") that does not match the SwType on the device: "
                    + (actualSwType == SWITCH_TYPE_TARIFF ? "Tariff switch (0)"
                            : (actualSwType == SWITCH_TYPE_LIGHT ? "Light switch (1)" : "Unknown value: "
                                    + actualSwType)));
        }
    }

    private void setConfigurationOnDevice(final ServerModel serverModel, final ClientAssociation clientAssociation,
            final Ssld ssld, final ConfigurationDto configuration) throws ProtocolAdapterException {

        // creating the Function that will be retried, if necessary
        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {

                if (configuration.getRelayConfiguration() != null
                        && configuration.getRelayConfiguration().getRelayMap() != null) {

                    final List<RelayMapDto> relayMaps = configuration.getRelayConfiguration().getRelayMap();
                    for (final RelayMapDto relayMap : relayMaps) {
                        final Integer internalIndex = relayMap.getAddress();
                        final RelayTypeDto relayType = relayMap.getRelayType();

                        final String switchTypeObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                                + LogicalNodeAttributeDefinitons.getNodeNameForRelayIndex(internalIndex)
                                + LogicalNodeAttributeDefinitons.PROPERTY_SWITCH_TYPE;

                        final FcModelNode switchTypeControl = Iec61850DeviceService.this.getNode(serverModel,
                                switchTypeObjectReference, Fc.CO);

                        final FcModelNode oper = (FcModelNode) switchTypeControl.getChild(
                                LogicalNodeAttributeDefinitons.PROPERTY_SWITCH_TYPE_ATTRIBUTE_OPER, Fc.CO);

                        final BdaInt8 ctlVal = (BdaInt8) oper.getChild(
                                LogicalNodeAttributeDefinitons.PROPERTY_SWITCH_TYPE_OPER_ATTRIBUTE_CONTROL, Fc.CO);

                        final byte switchTypeValue = (byte) (RelayTypeDto.LIGHT.equals(relayType) ? SWITCH_TYPE_LIGHT
                                : SWITCH_TYPE_TARIFF);
                        LOGGER.info("Updating Switch for internal index {} to {} ({})", internalIndex, switchTypeValue,
                                relayType);

                        ctlVal.setValue(switchTypeValue);
                        clientAssociation.setDataValues(oper);
                    }
                }

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
                if (!(configuration.getTimeSyncFrequency() == null
                        && configuration.isAutomaticSummerTimingEnabled() == null
                        && configuration.getSummerTimeDetails() == null && configuration.getWinterTimeDetails() == null)) {

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

                    /*
                     * Perform some effort to create dstBegT/dstEndt information
                     * based on provided DateTime values. This will work in a
                     * number of cases, but to be able to do this accurately in
                     * an international context, DST transition times will
                     * probably have to be based on information about the
                     * timezone the device is operating in, instead of a
                     * particular DateTime provided by the caller without
                     * further information.
                     */
                    final DaylightSavingTimeTransition.DstTransitionFormat dstFormatMwd = DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH;
                    final DateTime summerTimeDetails = configuration.getSummerTimeDetails();
                    final DateTime winterTimeDetails = configuration.getWinterTimeDetails();
                    if (summerTimeDetails != null) {

                        final String mwdValueForBeginOfDst = DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                                summerTimeDetails, dstFormatMwd).getTransition();

                        final BdaVisibleString dstBegT = (BdaVisibleString) clockConfiguration.getChild("dstBegT");

                        LOGGER.info("Updating DstBeginTime to {} based on SummerTimeDetails {}", mwdValueForBeginOfDst,
                                summerTimeDetails);

                        dstBegT.setValue(mwdValueForBeginOfDst);
                        clientAssociation.setDataValues(dstBegT);
                    }
                    if (winterTimeDetails != null) {

                        final String mwdValueForEndOfDst = DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                                winterTimeDetails, dstFormatMwd).getTransition();

                        final BdaVisibleString dstEndT = (BdaVisibleString) clockConfiguration.getChild("dstEndT");

                        LOGGER.info("Updating DstEndTime to {} based on WinterTimeDetails {}", mwdValueForEndOfDst,
                                winterTimeDetails);

                        dstEndT.setValue(mwdValueForEndOfDst);
                        clientAssociation.setDataValues(dstEndT);
                    }
                }

                // checking to see if all network values are null, so that we
                // don't read the values for no reason
                if (!(configuration.isDhcpEnabled() == null && configuration.getDeviceFixedIp() == null)) {

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

                    // All values in DeviceFixedIpDto are non-nullable, so no
                    // nullchecks are needed.
                    final DeviceFixedIpDto deviceFixedIp = configuration.getDeviceFixedIp();

                    final BdaVisibleString deviceFixedIpAddress = (BdaVisibleString) Iec61850DeviceService.this
                            .getChildOfNodeWithConstraint(networkConfiguration,
                                    LogicalNodeAttributeDefinitons.PROPERTY_POSITION_FIXED_IP_ADDRESS, Fc.CF);

                    final BdaVisibleString deviceFixedIpNetmask = (BdaVisibleString) Iec61850DeviceService.this
                            .getChildOfNodeWithConstraint(networkConfiguration,
                                    LogicalNodeAttributeDefinitons.PROPERTY_POSITION_FIXED_IP_NETMASK, Fc.CF);

                    final BdaVisibleString deviceFixedIpGateway = (BdaVisibleString) Iec61850DeviceService.this
                            .getChildOfNodeWithConstraint(networkConfiguration,
                                    LogicalNodeAttributeDefinitons.PROPERTY_POSITION_FIXED_IP_GATEWAY, Fc.CF);

                    LOGGER.info("Updating deviceFixedIpAddress to {}", configuration.getDeviceFixedIp().getIpAddress());
                    // Set the value and send the value to the device.
                    deviceFixedIpAddress.setValue(deviceFixedIp.getIpAddress());
                    clientAssociation.setDataValues(deviceFixedIpAddress);

                    LOGGER.info("Updating deviceFixedIpNetmask to {}", configuration.getDeviceFixedIp().getNetMask());
                    // Set the value and send the value to the device.
                    deviceFixedIpNetmask.setValue(deviceFixedIp.getNetMask());
                    clientAssociation.setDataValues(deviceFixedIpNetmask);

                    LOGGER.info("Updating deviceFixIpGateway to {}", configuration.getDeviceFixedIp().getGateWay());
                    // Set the value and send the value to the device.
                    deviceFixedIpGateway.setValue(deviceFixedIp.getGateWay());
                    clientAssociation.setDataValues(deviceFixedIpGateway);
                }

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

    private void setScheduleOnDevice(final ServerModel serverModel, final ClientAssociation clientAssociation,
            final RelayTypeDto relayType, final List<ScheduleDto> scheduleList, final Ssld ssld)
                    throws ProtocolAdapterException, FunctionalException {

        final String tariffOrLight = relayType.equals(RelayTypeDto.LIGHT) ? "light" : "tariff";

        // Creating a list of all Schedule entries, grouped by relay index
        final Map<Integer, List<ScheduleEntry>> relaySchedulesEntries = this.createScheduleEntries(scheduleList, ssld,
                relayType);

        for (final Integer relayIndex : relaySchedulesEntries.keySet()) {

            final Function<Void> function = new Function<Void>() {

                @Override
                public Void apply() throws Exception {

                    final List<ScheduleEntry> scheduleEntries = relaySchedulesEntries.get(relayIndex);
                    final int numberOfScheduleEntries = scheduleEntries.size();

                    if (numberOfScheduleEntries > MAX_NUMBER_OF_SCHEDULE_ENTRIES) {
                        throw new ProtocolAdapterException("Received " + numberOfScheduleEntries + " " + tariffOrLight
                                + " schedule entries for relay " + relayIndex + " for device "
                                + ssld.getDeviceIdentification() + ". Setting more than "
                                + MAX_NUMBER_OF_SCHEDULE_ENTRIES + " is not possible.");
                    }

                    final String scheduleObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                            + LogicalNodeAttributeDefinitons.getNodeNameForRelayIndex(relayIndex)
                            + LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE;

                    final FcModelNode scheduleConfiguration = Iec61850DeviceService.this.getNode(serverModel,
                            scheduleObjectReference, Fc.CF);

                    // Clear existing schedule by disabling schedule entries.
                    for (int i = 0; i < MAX_NUMBER_OF_SCHEDULE_ENTRIES; i++) {

                        LOGGER.info("Disabling schedule entry {} of {} for relay {} before setting new {} schedule",
                                i + 1, MAX_NUMBER_OF_SCHEDULE_ENTRIES, relayIndex, tariffOrLight);

                        final ConstructedDataAttribute scheduleNode = (ConstructedDataAttribute) Iec61850DeviceService.this
                                .getChildOfNodeWithConstraint(scheduleConfiguration,
                                        LogicalNodeAttributeDefinitons.getSchedulePropertyNameForIndex(i + 1), Fc.CF);

                        final BdaBoolean enabled = (BdaBoolean) Iec61850DeviceService.this.getChildOfNode(scheduleNode,
                                LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_ENABLE);
                        if (enabled.getValue()) {
                            enabled.setValue(false);
                            clientAssociation.setDataValues(enabled);
                        }
                    }

                    for (int i = 0; i < numberOfScheduleEntries; i++) {

                        LOGGER.info("Writing {} schedule entry {} for relay {}", tariffOrLight, i + 1, relayIndex);

                        final ScheduleEntry scheduleEntry = scheduleEntries.get(i);

                        final ConstructedDataAttribute scheduleNode = (ConstructedDataAttribute) Iec61850DeviceService.this
                                .getChildOfNodeWithConstraint(scheduleConfiguration,
                                        LogicalNodeAttributeDefinitons.getSchedulePropertyNameForIndex(i + 1), Fc.CF);

                        final BdaBoolean enabled = (BdaBoolean) Iec61850DeviceService.this.getChildOfNode(scheduleNode,
                                LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_ENABLE);
                        if (enabled.getValue() != scheduleEntry.isEnabled()) {
                            enabled.setValue(scheduleEntry.isEnabled());
                            clientAssociation.setDataValues(enabled);
                        }

                        final BdaInt32 day = (BdaInt32) Iec61850DeviceService.this.getChildOfNode(scheduleNode,
                                LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_DAY);
                        if (day.getValue() != scheduleEntry.getDay()) {
                            day.setValue(scheduleEntry.getDay());
                            clientAssociation.setDataValues(day);
                        }

                        /*
                         * A schedule entry on the platform is about switching
                         * on a certain time, or on a certain trigger. The
                         * schedule entries on the device are about a period
                         * with a time on and a time off. To bridge these
                         * different approaches, either the on or the off values
                         * on the device are set to a certain default to
                         * indicate they are not relevant to the schedule entry.
                         */

                        int timeOnValue = DEFAULT_SCHEDULE_VALUE;
                        byte timeOnTypeValue = DEFAULT_SCHEDULE_VALUE;
                        int timeOffValue = DEFAULT_SCHEDULE_VALUE;
                        byte timeOffTypeValue = DEFAULT_SCHEDULE_VALUE;

                        if (scheduleEntry.isOn()) {
                            timeOnValue = scheduleEntry.getTime();
                            timeOnTypeValue = (byte) scheduleEntry.getTriggerType().getIndex();

                        } else {
                            timeOffValue = scheduleEntry.getTime();
                            timeOffTypeValue = (byte) scheduleEntry.getTriggerType().getIndex();
                        }

                        final BdaInt32 timeOn = (BdaInt32) Iec61850DeviceService.this.getChildOfNode(scheduleNode,
                                LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_TIME_ON);
                        if (timeOn.getValue() != timeOnValue) {
                            timeOn.setValue(timeOnValue);
                            clientAssociation.setDataValues(timeOn);
                        }

                        final BdaInt8 timeOnActionTime = (BdaInt8) Iec61850DeviceService.this.getChildOfNode(
                                scheduleNode, LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_TIME_ON_TYPE);
                        if (timeOnActionTime.getValue() != timeOnTypeValue) {
                            timeOnActionTime.setValue(timeOnTypeValue);
                            clientAssociation.setDataValues(timeOnActionTime);
                        }

                        final BdaInt32 timeOff = (BdaInt32) Iec61850DeviceService.this.getChildOfNode(scheduleNode,
                                LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_TIME_OFF);
                        if (timeOff.getValue() != timeOffValue) {
                            timeOff.setValue(timeOffValue);
                            clientAssociation.setDataValues(timeOff);
                        }

                        final BdaInt8 timeOffActionTime = (BdaInt8) Iec61850DeviceService.this.getChildOfNode(
                                scheduleNode, LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_TIME_OFF_TYPE);
                        if (timeOffActionTime.getValue() != timeOffTypeValue) {
                            timeOffActionTime.setValue(timeOffTypeValue);
                            clientAssociation.setDataValues(timeOffActionTime);
                        }

                        final BdaInt16U minimumTimeOn = (BdaInt16U) Iec61850DeviceService.this.getChildOfNode(
                                scheduleNode, LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_MINIMUM_TIME_ON);
                        if (minimumTimeOn.getValue() != scheduleEntry.getMinimumLightsOn()) {
                            minimumTimeOn.setValue(scheduleEntry.getMinimumLightsOn());
                            clientAssociation.setDataValues(minimumTimeOn);
                        }

                        final BdaInt16U triggerMinutesBefore = (BdaInt16U) Iec61850DeviceService.this.getChildOfNode(
                                scheduleNode, LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_TRIGGER_MINUTES_BEFORE);
                        if (triggerMinutesBefore.getValue() != scheduleEntry.getTriggerWindowMinutesBefore()) {
                            triggerMinutesBefore.setValue(scheduleEntry.getTriggerWindowMinutesBefore());
                            clientAssociation.setDataValues(triggerMinutesBefore);
                        }

                        final BdaInt16U triggerMinutesAfter = (BdaInt16U) Iec61850DeviceService.this.getChildOfNode(
                                scheduleNode, LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_TRIGGER_MINUTES_AFTER);
                        if (triggerMinutesAfter.getValue() != scheduleEntry.getTriggerWindowMinutesAfter()) {
                            triggerMinutesAfter.setValue(scheduleEntry.getTriggerWindowMinutesAfter());
                            clientAssociation.setDataValues(triggerMinutesAfter);
                        }
                    }

                    return null;
                }
            };

            this.iec61850Client.sendCommandWithRetry(function);

        }

    }

    private List<FirmwareVersionDto> getFirmwareVersionFromDevice(final ServerModel serverModel)
            throws ProtocolAdapterException {

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

    private void transitionDevice(final ServerModel serverModel, final ClientAssociation clientAssociation,
            final String deviceIdentification, final TransitionMessageDataContainerDto transitionMessageDataContainer)
                    throws ProtocolAdapterException {

        final TransitionTypeDto transitionType = transitionMessageDataContainer.getTransitionType();
        LOGGER.info("device: {}, transition: {}", deviceIdentification, transitionType);
        final boolean controlValueForTransition = transitionType.equals(TransitionTypeDto.DAY_NIGHT);

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
                        .getChild(LogicalNodeAttributeDefinitons.PROPERTY_DOWNLOAD_URL);

                final BdaTimestamp functionalFirmwareDownloadStartTime = (BdaTimestamp) functionalFirmwareConfiguration
                        .getChild(LogicalNodeAttributeDefinitons.PROPERTY_DOWNLOAD_START_TIME);

                LOGGER.info("Updating the firmware download url to {}", fullUrl);

                functionalFirmwareDownloadUrl.setValue(fullUrl);
                clientAssociation.setDataValues(functionalFirmwareDownloadUrl);

                final Date oneMinuteFromNow = Iec61850DeviceService.this.getLocalTimeForDevice(serverModel)
                        .plusMinutes(1).toDate();

                LOGGER.info("Updating the firmware download start time to {}", oneMinuteFromNow);

                functionalFirmwareDownloadStartTime.setDate(oneMinuteFromNow);
                clientAssociation.setDataValues(functionalFirmwareDownloadStartTime);

                return null;
            }
        };

        this.iec61850Client.sendCommandWithRetry(function);

    }

    private void pushSslCertificateToDevice(final ServerModel serverModel, final ClientAssociation clientAssociation,
            final CertificationDto certification) throws ProtocolAdapterException, FunctionalException {

        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {

                LOGGER.info("Reading the certificate authority url");

                final String updateSslConfigurationObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                        + LogicalNodeAttributeDefinitons.LOGICAL_NODE_CSLC
                        + LogicalNodeAttributeDefinitons.PROPERTY_CERTIFICATE_AUTHORITY_REPLACE;

                final FcModelNode certificateConfiguration = Iec61850DeviceService.this.getNode(serverModel,
                        updateSslConfigurationObjectReference, Fc.CF);

                final BdaVisibleString certificateDownloadUrl = (BdaVisibleString) certificateConfiguration
                        .getChild(LogicalNodeAttributeDefinitons.PROPERTY_DOWNLOAD_URL);

                final BdaTimestamp certificateUrlDownloadStartTime = (BdaTimestamp) certificateConfiguration
                        .getChild(LogicalNodeAttributeDefinitons.PROPERTY_DOWNLOAD_START_TIME);

                // removing trailing and leading slashes (if present) from the
                // domain and the url
                String adjustedDomain = certification.getCertificateDomain();
                if (adjustedDomain.endsWith("/")) {
                    adjustedDomain = adjustedDomain.substring(0, adjustedDomain.length() - 1);
                }

                String adjustedUrl = certification.getCertificateUrl();
                if (adjustedUrl.startsWith("/")) {
                    adjustedUrl = adjustedUrl.substring(1, adjustedUrl.length());
                }

                final String fullUrl = adjustedDomain.concat("/").concat(adjustedUrl);

                LOGGER.info("Updating the certificate download url to {}", fullUrl);

                certificateDownloadUrl.setValue(fullUrl);
                clientAssociation.setDataValues(certificateDownloadUrl);

                final Date oneMinuteFromNow = Iec61850DeviceService.this.getLocalTimeForDevice(serverModel)
                        .plusMinutes(1).toDate();

                LOGGER.info("Updating the certificate download start time to {}", oneMinuteFromNow);

                certificateUrlDownloadStartTime.setDate(oneMinuteFromNow);
                clientAssociation.setDataValues(certificateUrlDownloadStartTime);

                return null;
            }
        };

        this.iec61850Client.sendCommandWithRetry(function);

    }

    private void enableReportingOnDevice(final ServerModel serverModel,
            final Iec61850ClientAssociation iec61850ClientAssociation, final String deviceIdentification)
                    throws ServiceError, IOException {

        final ClientAssociation clientAssociation = iec61850ClientAssociation.getClientAssociation();

        final String eventReportingDataObjectReference = LogicalNodeAttributeDefinitons.LOGICAL_DEVICE
                + LogicalNodeAttributeDefinitons.LOGICAL_NODE_LLN0 + LogicalNodeAttributeDefinitons.PROPERTY_REPORTING;

        final FcModelNode eventReportingDataNode = (FcModelNode) serverModel.findModelNode(
                eventReportingDataObjectReference, Fc.BR);

        final Iec61850ClientEventListener reportListener = iec61850ClientAssociation.getReportListener();
        final BdaInt16U sqNum = (BdaInt16U) eventReportingDataNode
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_SEQUENCE_NUMBER);
        if (sqNum == null) {
            LOGGER.warn("Child {} of {} is null. No SqNum available for filtering incoming event reports.",
                    LogicalNodeAttributeDefinitons.PROPERTY_SEQUENCE_NUMBER, eventReportingDataNode);
        } else {
            reportListener.setSqNum(sqNum.getValue());
        }

        final BdaBoolean enableReporting = (BdaBoolean) eventReportingDataNode
                .getChild(LogicalNodeAttributeDefinitons.PROPERTY_ENABLE_REPORTING);

        LOGGER.info("Allowing device {} to send events", deviceIdentification);
        enableReporting.setValue(true);
        clientAssociation.setDataValues(enableReporting);
    }

    // ========================
    // PRIVATE HELPER METHODS =
    // ========================

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

    private ScheduleEntry convertToScheduleEntry(final ScheduleDto schedule, final LightValueDto lightValue)
            throws ProtocolAdapterException {

        final ScheduleEntry.Builder builder = new ScheduleEntry.Builder();
        try {
            if (schedule.getTime() != null) {
                builder.time(this.convertTime(schedule.getTime()));
            }
            final WindowTypeDto triggerWindow = schedule.getTriggerWindow();
            if (triggerWindow != null) {
                if (triggerWindow.getMinutesBefore() > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("Schedule TriggerWindow minutesBefore must not be greater than "
                            + Integer.MAX_VALUE);
                }
                if (triggerWindow.getMinutesAfter() > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("Schedule TriggerWindow minutesAfter must not be greater than "
                            + Integer.MAX_VALUE);
                }
                builder.triggerWindowMinutesBefore((int) triggerWindow.getMinutesBefore());
                builder.triggerWindowMinutesAfter((int) triggerWindow.getMinutesAfter());
            }
            builder.triggerType(this.extractTriggerType(schedule));
            builder.enabled(schedule.getIsEnabled() == null ? true : schedule.getIsEnabled());
            final WeekDayTypeDto weekDay = schedule.getWeekDay();
            if (WeekDayTypeDto.ABSOLUTEDAY.equals(weekDay)) {
                final DateTime specialDay = schedule.getStartDay();
                if (specialDay == null) {
                    throw new IllegalArgumentException(
                            "Schedule startDay must not be null when weekDay equals ABSOLUTEDAY");
                }
                builder.specialDay(specialDay);
            } else {
                builder.weekday(ScheduleWeekday.valueOf(schedule.getWeekDay().name()));
            }
            builder.on(lightValue.isOn());
            if (schedule.getMinimumLightsOn() != null) {
                builder.minimumLightsOn(schedule.getMinimumLightsOn());
            }
            return builder.build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new ProtocolAdapterException("Error converting ScheduleDto and LightValueDto into a ScheduleEntry: "
                    + e.getMessage(), e);
        }
    }

    /**
     *
     * @param time
     *            a time String in the format hh:mm:ss.SSS, hh:mm:ss or hh:mm.
     * @return the short value formed by parsing the digits of hhmm from the
     *         given time.
     * @throws ProtocolAdapterException
     *             if time is {@code null} or not of the format specified.
     */
    private short convertTime(final String time) throws ProtocolAdapterException {

        if (time == null || !time.matches("\\d\\d:\\d\\d(:\\d\\d)?\\.?\\d*")) {
            throw new ProtocolAdapterException("Schedule time (" + time
                    + ") is not formatted as hh:mm, hh:mm:ss or hh:mm:ss.SSS");
        }
        return Short.parseShort(time.replace(":", "").substring(0, 4));
    }

    private TriggerType extractTriggerType(final ScheduleDto schedule) {
        final TriggerType triggerType;
        if (ActionTimeTypeDto.ABSOLUTETIME.equals(schedule.getActionTime())) {
            triggerType = TriggerType.FIX;
        } else if (com.alliander.osgp.dto.valueobjects.TriggerTypeDto.ASTRONOMICAL.equals(schedule.getTriggerType())) {
            triggerType = TriggerType.AUTONOME;
        } else {
            triggerType = TriggerType.SENSOR;
        }
        return triggerType;
    }

    /*
     * returns a map of schedule entries, grouped by the internal index
     */
    private Map<Integer, List<ScheduleEntry>> createScheduleEntries(final List<ScheduleDto> scheduleList,
            final Ssld ssld, final RelayTypeDto relayTypeDto) throws FunctionalException {

        final Map<Integer, List<ScheduleEntry>> relaySchedulesEntries = new HashMap<>();

        final RelayType relayType = RelayType.valueOf(relayTypeDto.name());

        for (final ScheduleDto schedule : scheduleList) {
            for (final LightValueDto lightValue : schedule.getLightValue()) {

                final List<Integer> indexes = new ArrayList<>();

                if (lightValue.getIndex() == 0
                        && (RelayType.TARIFF.equals(relayType) || RelayType.TARIFF_REVERSED.equals(relayType))) {

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

                ScheduleEntry scheduleEntry;
                try {
                    scheduleEntry = this.convertToScheduleEntry(schedule, lightValue);
                } catch (final ProtocolAdapterException e) {
                    throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                            ComponentType.PROTOCOL_IEC61850, e);
                }

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

    private List<PowerUsageDataDto> getPowerUsageHistoryDataFromRelay(final ServerModel serverModel,
            final String deviceIdentification, final TimePeriodDto timePeriod,
            final DeviceOutputSetting deviceOutputSetting) throws TechnicalException {
        final List<PowerUsageDataDto> powerUsageHistoryDataFromRelay = new ArrayList<>();

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

            final boolean includeEntryInResponse = this.timePeriodContainsDateTime(timePeriod, date,
                    deviceIdentification, relayIndex, bufferIndex);
            if (!includeEntryInResponse) {
                continue;
            }

            // MeterType.AUX hardcoded (not supported)
            final PowerUsageDataDto powerUsageData = new PowerUsageDataDto(date, MeterTypeDto.AUX, 0, 0);
            final List<RelayDataDto> relayDataList = new ArrayList<>();
            final RelayDataDto relayData = new RelayDataDto(relayIndex, totalMinutesOnForDate);
            relayDataList.add(relayData);
            final SsldDataDto ssldData = new SsldDataDto(0, 0, 0, 0, 0, 0, 0, 0, 0, relayDataList);
            powerUsageData.setSsldData(ssldData);
            powerUsageHistoryDataFromRelay.add(powerUsageData);
        }

        return powerUsageHistoryDataFromRelay;
    }

    private boolean timePeriodContainsDateTime(final TimePeriodDto timePeriod, final DateTime date,
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

}
