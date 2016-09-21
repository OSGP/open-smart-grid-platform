/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openmuc.openiec61850.BdaBoolean;
import org.openmuc.openiec61850.BdaInt8;
import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.Fc;
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
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.SsldDeviceService;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.requests.GetPowerUsageHistoryDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.requests.SetConfigurationDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.requests.SetEventNotificationsDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.requests.SetLightDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.requests.SetScheduleDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.requests.SetTransitionDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.requests.UpdateDeviceSslCertificationDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.requests.UpdateFirmwareDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses.EmptyDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses.GetConfigurationDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses.GetFirmwareVersionDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses.GetPowerUsageHistoryDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses.GetStatusDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.DaylightSavingTimeTransition;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.DaylightSavingTimeTransition.DstTransitionFormat;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.EventType;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.ScheduleEntry;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.ScheduleWeekday;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.TriggerType;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ConnectionFailureException;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850ClientAssociation;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Connection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.Function;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.IED;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting.Iec61850ClientBaseEventListener;
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
import com.alliander.osgp.dto.valueobjects.EventNotificationTypeDto;
import com.alliander.osgp.dto.valueobjects.FirmwareModuleType;
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
public class Iec61850SsldDeviceService implements SsldDeviceService {

    private static final DateTimeZone TIME_ZONE_AMSTERDAM = DateTimeZone.forID("Europe/Amsterdam");

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850SsldDeviceService.class);

    @Autowired
    private Iec61850DeviceConnectionService iec61850DeviceConnectionService;

    @Autowired
    private SsldDataService ssldDataService;

    @Autowired
    private Iec61850Client iec61850Client;

    @Autowired
    private Iec61850Mapper mapper;

    // Timeout between the SetLight and getStatus during the device selftest
    @Autowired
    private int selftestTimeout;

    // The value used to indicate that the time on or time off of a schedule
    // entry is unused.
    private static final int DEFAULT_SCHEDULE_VALUE = -1;
    // The number of schedule entries available for a relay.
    private static final int MAX_NUMBER_OF_SCHEDULE_ENTRIES = 64;

    private static final int SWITCH_TYPE_TARIFF = 0;
    private static final int SWITCH_TYPE_LIGHT = 1;

    @Override
    public void getStatus(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler) {
        try {
            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());

            // Getting the ssld for the device outputsettings
            final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());

            // Getting the data with retries
            final DeviceStatusDto deviceStatus = this.getStatusFromDevice(
                    new DeviceConnection(new Iec61850Connection(new Iec61850ClientAssociation(clientAssociation, null),
                            serverModel), deviceRequest.getDeviceIdentification(), IED.FLEX_OVL), ssld);

            final GetStatusDeviceResponse deviceResponse = new GetStatusDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), deviceStatus);

            deviceResponseHandler.handleResponse(deviceResponse);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during getStatus", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
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
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during getPowerUsageHistory", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
        }
    }

    @Override
    public void setLight(final SetLightDeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler) {
        try {
            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());

            for (final LightValueDto lightValue : deviceRequest.getLightValuesContainer().getLightValues()) {

                final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());

                // for index 0, only devices LIGHT RelayTypes have to be
                // switched
                boolean switchAllLightRelays = false;
                if (lightValue == null) {
                    switchAllLightRelays = true;
                } else if (lightValue.getIndex() == null) {
                    switchAllLightRelays = true;
                } else if (lightValue.getIndex() == 0) {
                    switchAllLightRelays = true;
                }

                LOGGER.info("switchAllLightRelays: {}", switchAllLightRelays);

                if (switchAllLightRelays) {
                    for (final DeviceOutputSetting deviceOutputSetting : this.ssldDataService.findByRelayType(ssld,
                            RelayType.LIGHT)) {
                        this.switchLightRelay(deviceOutputSetting.getInternalId(), lightValue.isOn(),
                                new DeviceConnection(new Iec61850Connection(new Iec61850ClientAssociation(
                                        clientAssociation, null), serverModel),
                                        deviceRequest.getDeviceIdentification(), IED.FLEX_OVL));
                    }
                } else {

                    final DeviceOutputSetting deviceOutputSetting = this.ssldDataService
                            .getDeviceOutputSettingForExternalIndex(ssld, lightValue.getIndex());

                    if (deviceOutputSetting != null) {

                        // You can only switch LIGHT relays that are used
                        this.checkRelay(deviceOutputSetting.getRelayType(), RelayType.LIGHT,
                                deviceOutputSetting.getInternalId());

                        this.switchLightRelay(deviceOutputSetting.getInternalId(), lightValue.isOn(),
                                new DeviceConnection(new Iec61850Connection(new Iec61850ClientAssociation(
                                        clientAssociation, null), serverModel),
                                        deviceRequest.getDeviceIdentification(), IED.FLEX_OVL));
                    }

                }
            }
        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during writeDataValue", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        }

        final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK);

        deviceResponseHandler.handleResponse(deviceResponse);
        this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
    }

    @Override
    public void setConfiguration(final SetConfigurationDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler) {
        try {
            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());

            final ConfigurationDto configuration = deviceRequest.getConfiguration();

            // ignoring required, unused fields daliconfiguration, meterType,
            // shortTermHistoryIntervalMinutes, preferredLinkType,
            // longTermHistoryInterval and longTermHistoryIntervalType
            this.setConfigurationOnDevice(new DeviceConnection(new Iec61850Connection(new Iec61850ClientAssociation(
                    clientAssociation, null), serverModel), deviceRequest.getDeviceIdentification(), IED.FLEX_OVL),
                    configuration);
        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during writeDataValue", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        }

        final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK);

        deviceResponseHandler.handleResponse(deviceResponse);
        this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
    }

    @Override
    public void getConfiguration(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler) {
        try {
            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);

            // Getting the ssld for the device outputsettings
            final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());

            final ConfigurationDto configuration = this.getConfigurationFromDevice(new DeviceConnection(
                    new Iec61850Connection(null, serverModel), deviceRequest.getDeviceIdentification(), IED.FLEX_OVL),
                    ssld);

            final GetConfigurationDeviceResponse response = new GetConfigurationDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK, configuration);

            deviceResponseHandler.handleResponse(response);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during writeDataValue", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        }
    }

    @Override
    public void setReboot(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler) {
        try {
            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());
            this.rebootDevice(new DeviceConnection(new Iec61850Connection(new Iec61850ClientAssociation(
                    clientAssociation, null), serverModel), deviceRequest.getDeviceIdentification(), IED.FLEX_OVL));

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK);
            deviceResponseHandler.handleResponse(deviceResponse);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during writeDataValue", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        }
    }

    @Override
    public void runSelfTest(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final boolean startOfTest) {
        // Assuming all goes well
        final DeviceMessageStatus status = DeviceMessageStatus.OK;

        try {
            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);
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
                this.switchLightRelay(deviceOutputSetting.getInternalId(), startOfTest, new DeviceConnection(
                        new Iec61850Connection(new Iec61850ClientAssociation(clientAssociation, null), serverModel),
                        deviceRequest.getDeviceIdentification(), IED.FLEX_OVL));
            }

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
                    deviceRequest.getDeviceIdentification(), IED.FLEX_OVL, LogicalDevice.LIGHTING);

            // Getting the status
            final DeviceStatusDto deviceStatus = this.getStatusFromDevice(
                    new DeviceConnection(new Iec61850Connection(new Iec61850ClientAssociation(clientAssociation, null),
                            serverModel), deviceRequest.getDeviceIdentification(), IED.FLEX_OVL), ssld);

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
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during writeDataValue", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        }

        final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), status);

        deviceResponseHandler.handleResponse(deviceResponse);
        this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
    }

    @Override
    public void setSchedule(final SetScheduleDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler) {
        try {
            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());

            final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());

            this.setScheduleOnDevice(new DeviceConnection(new Iec61850Connection(new Iec61850ClientAssociation(
                    clientAssociation, null), serverModel), deviceRequest.getDeviceIdentification(), IED.FLEX_OVL),
                    deviceRequest.getRelayType(), deviceRequest.getScheduleMessageDataContainer().getScheduleList(),
                    ssld);

        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during writeDataValue", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        }

        final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK);
        deviceResponseHandler.handleResponse(deviceResponse);
        this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
    }

    @Override
    public void getFirmwareVersion(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler) {
        try {
            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());

            // Getting the data with retries
            final List<FirmwareVersionDto> firmwareVersions = this.getFirmwareVersionFromDevice(new DeviceConnection(
                    new Iec61850Connection(new Iec61850ClientAssociation(clientAssociation, null), serverModel),
                    deviceRequest.getDeviceIdentification(), IED.FLEX_OVL));

            final GetFirmwareVersionDeviceResponse deviceResponse = new GetFirmwareVersionDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), firmwareVersions);

            deviceResponseHandler.handleResponse(deviceResponse);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during getFirmwareVersion", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
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

            this.transitionDevice(new DeviceConnection(new Iec61850Connection(new Iec61850ClientAssociation(
                    clientAssociation, null), serverModel), deviceRequest.getDeviceIdentification(), IED.FLEX_OVL),
                    deviceRequest.getTransitionTypeContainer());

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK);
            deviceResponseHandler.handleResponse(deviceResponse);

            // Enabling device reporting. This is placed here because this is
            // called twice a day.
            this.enableReportingOnDevice(new DeviceConnection(new Iec61850Connection(iec61850ClientAssociation,
                    serverModel), deviceRequest.getDeviceIdentification(), IED.FLEX_OVL), deviceRequest
                    .getDeviceIdentification());
            // Don't disconnect here! The device should be able to send reports.
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Iec61850SsldDeviceService.this.iec61850DeviceConnectionService.disconnect(deviceRequest
                            .getDeviceIdentification());
                }
            }, 5000);
        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during writeDataValue", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
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

            this.pushFirmwareToDevice(new DeviceConnection(new Iec61850Connection(new Iec61850ClientAssociation(
                    clientAssociation, null), serverModel), deviceRequest.getDeviceIdentification(), IED.FLEX_OVL),
                    serverModel, clientAssociation,
                    deviceRequest.getFirmwareDomain().concat(deviceRequest.getFirmwareUrl()));

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK);
            deviceResponseHandler.handleResponse(deviceResponse);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during writeDataValue", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
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

            this.pushSslCertificateToDevice(new DeviceConnection(new Iec61850Connection(new Iec61850ClientAssociation(
                    clientAssociation, null), serverModel), deviceRequest.getDeviceIdentification(), IED.FLEX_OVL),
                    deviceRequest.getCertification());

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK);
            deviceResponseHandler.handleResponse(deviceResponse);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during writeDataValue", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        }
    }

    @Override
    public void setEventNotifications(final SetEventNotificationsDeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler) {
        LOGGER.info("Called setEventNotifications, doing nothing for now and returning OK");

        final List<EventNotificationTypeDto> eventNotifications = deviceRequest.getEventNotificationsContainer()
                .getEventNotifications();
        final String filter = EventType.getEventTypeFilterMaskForNotificationTypes(eventNotifications);

        try {
            final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest);
            final ClientAssociation clientAssociation = this.iec61850DeviceConnectionService
                    .getClientAssociation(deviceRequest.getDeviceIdentification());

            this.setEventNotificationFilterOnDevice(
                    new DeviceConnection(new Iec61850Connection(new Iec61850ClientAssociation(clientAssociation, null),
                            serverModel), deviceRequest.getDeviceIdentification(), IED.FLEX_OVL), filter);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.OK);
            deviceResponseHandler.handleResponse(deviceResponse);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
        } catch (final ConnectionFailureException se) {
            LOGGER.error("Could not connect to device after all retries", se);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(se, deviceResponse, true);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during setEventNotificationFilterOnDevice", e);

            final EmptyDeviceResponse deviceResponse = new EmptyDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), DeviceMessageStatus.FAILURE);

            deviceResponseHandler.handleException(e, deviceResponse, false);
            this.iec61850DeviceConnectionService.disconnect(deviceRequest.getDeviceIdentification());
            return;
        }
    }

    // ======================================
    // PRIVATE DEVICE COMMUNICATION METHODS =
    // ======================================

    private ServerModel connectAndRetrieveServerModel(final DeviceRequest deviceRequest)
            throws ProtocolAdapterException {
        this.iec61850DeviceConnectionService.connect(deviceRequest.getIpAddress(),
                deviceRequest.getDeviceIdentification(), IED.FLEX_OVL, LogicalDevice.LIGHTING);
        return this.iec61850DeviceConnectionService.getServerModel(deviceRequest.getDeviceIdentification());
    }

    private void switchLightRelay(final int index, final boolean on, final DeviceConnection deviceConnection)
            throws ProtocolAdapterException {
        // Commands don't return anything, so returnType is Void
        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {

                final LogicalNode logicalNode = LogicalNode.getSwitchComponentByIndex(index);
                LOGGER.info("logicalNode: {}", logicalNode);

                // Check if CfSt.enbOper [CF] is set to true. If it is not
                // set to true, the relay can not be operated.
                final NodeContainer masterControl = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                        logicalNode, DataAttribute.MASTER_CONTROL, Fc.CF);
                LOGGER.info("masterControl: {}", masterControl);

                final BdaBoolean enbOper = masterControl.getBoolean(SubDataAttribute.ENABLE_OPERATION);
                if (enbOper.getValue()) {
                    LOGGER.info("masterControl.enbOper is true, switching of relay is enabled");
                } else {
                    LOGGER.info("masterControl.enbOper is false, switching of relay is disabled");
                    // Set the value to true.
                    enbOper.setValue(true);
                    masterControl.write();

                    LOGGER.info("set masterControl.enbOper to true to enable switching");
                }

                // Switch the relay using Pos.Oper.ctlVal [CO].
                final NodeContainer position = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING, logicalNode,
                        DataAttribute.POSITION, Fc.CO);
                LOGGER.info("position: {}", position);

                final NodeContainer operation = position.getChild(SubDataAttribute.OPERATION);
                LOGGER.info("operation: {}", operation);

                final BdaBoolean controlValue = operation.getBoolean(SubDataAttribute.CONTROL_VALUE);
                LOGGER.info("controlValue: {}", controlValue);

                LOGGER.info(String.format("Switching relay %d %s", index, on ? "on" : "off"));
                controlValue.setValue(on);
                operation.write();

                Thread.sleep(10000);

                // return null == Void
                return null;
            }
        };

        this.iec61850Client.sendCommandWithRetry(function);
    }

    private DeviceStatusDto getStatusFromDevice(final DeviceConnection deviceConnection, final Ssld ssld)
            throws ProtocolAdapterException {
        // getting the light relay values
        final List<LightValueDto> lightValues = new ArrayList<>();

        for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {
            final LogicalNode logicalNode = LogicalNode.getSwitchComponentByIndex(deviceOutputSetting.getInternalId());
            final NodeContainer position = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING, logicalNode,
                    DataAttribute.POSITION, Fc.ST);
            this.iec61850Client.readNodeDataValues(deviceConnection.getConnection().getIec61850ClientAssociation()
                    .getClientAssociation(), position.getFcmodelNode());
            final BdaBoolean state = position.getBoolean(SubDataAttribute.STATE);
            final boolean on = state.getValue();
            lightValues.add(new LightValueDto(deviceOutputSetting.getExternalId(), on, null));

            LOGGER.info(String.format("Got status of relay %d => %s", deviceOutputSetting.getInternalId(), on ? "on"
                    : "off"));
        }

        final NodeContainer eventBuffer = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.EVENT_BUFFER, Fc.CF);
        this.iec61850Client.readNodeDataValues(deviceConnection.getConnection().getIec61850ClientAssociation()
                .getClientAssociation(), eventBuffer.getFcmodelNode());
        final String filter = eventBuffer.getString(SubDataAttribute.EVENT_BUFFER_FILTER);
        LOGGER.info("Got EvnBuf.enbEvnType filter {}", filter);

        final Set<EventNotificationTypeDto> notificationTypes = EventType.getNotificationTypesForFilter(filter);
        int eventNotificationsMask = 0;
        for (final EventNotificationTypeDto notificationType : notificationTypes) {
            eventNotificationsMask |= notificationType.getValue();
        }

        final NodeContainer softwareConfiguration = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.SOFTWARE_CONFIGURATION, Fc.CF);
        this.iec61850Client.readNodeDataValues(deviceConnection.getConnection().getIec61850ClientAssociation()
                .getClientAssociation(), softwareConfiguration.getFcmodelNode());
        String lightTypeValue = softwareConfiguration.getString(SubDataAttribute.LIGHT_TYPE);
        // Fix for Kaifa bug KI-31
        if (lightTypeValue == null || lightTypeValue.isEmpty()) {
            lightTypeValue = "RELAY";
        }
        final LightTypeDto lightType = LightTypeDto.valueOf(lightTypeValue);

        /*
         * The preferredLinkType and actualLinkType are hardcoded to
         * LinkTypeDto.ETHERNET, other link types do not apply to the device
         * type in use.
         */
        return new DeviceStatusDto(lightValues, LinkTypeDto.ETHERNET, LinkTypeDto.ETHERNET, lightType,
                eventNotificationsMask);
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
            final List<PowerUsageDataDto> powerUsageData = Iec61850SsldDeviceService.this
                    .getPowerUsageHistoryDataFromRelay(new DeviceConnection(new Iec61850Connection(null, serverModel),
                            deviceIdentification, IED.FLEX_OVL), deviceIdentification, timePeriod, deviceOutputSetting);
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

    private ConfigurationDto getConfigurationFromDevice(final DeviceConnection deviceConnection, final Ssld ssld)
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
            this.checkRelayType(deviceOutputSetting, deviceConnection);
            relayMaps.add(Iec61850SsldDeviceService.this.mapper.map(deviceOutputSetting, RelayMapDto.class));
        }

        final RelayConfigurationDto relayConfiguration = new RelayConfigurationDto(relayMaps);

        // PSLD specific => just sending null so it'll be ignored
        final DaliConfigurationDto daliConfiguration = null;

        // getting the software configuration values
        LOGGER.info("Reading the software configuration values");
        final NodeContainer softwareConfiguration = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.SOFTWARE_CONFIGURATION, Fc.CF);

        String lightTypeValue = softwareConfiguration.getString(SubDataAttribute.LIGHT_TYPE);
        // Fix for Kaifa bug KI-31
        if (lightTypeValue == null || lightTypeValue.isEmpty()) {
            lightTypeValue = "RELAY";
        }
        final LightTypeDto lightType = LightTypeDto.valueOf(lightTypeValue);
        final short astroGateSunRiseOffset = softwareConfiguration.getShort(SubDataAttribute.ASTRONOMIC_SUNRISE_OFFSET)
                .getValue();
        final short astroGateSunSetOffset = softwareConfiguration.getShort(SubDataAttribute.ASTRONOMIC_SUNSET_OFFSET)
                .getValue();

        final ConfigurationDto configuration = new ConfigurationDto(lightType, daliConfiguration, relayConfiguration,
                shortTermHistoryIntervalMinutes, preferredLinkType, meterType, longTermHistoryInterval,
                longTermHistoryIntervalType);

        // getting the registration configuration values
        LOGGER.info("Reading the registration configuration values");
        final NodeContainer registration = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.REGISTRATION, Fc.CF);

        final String serverAddress = registration.getString(SubDataAttribute.SERVER_ADDRESS);
        final int serverPort = registration.getInteger(SubDataAttribute.SERVER_PORT).getValue();

        configuration.setOspgIpAddress(serverAddress);
        configuration.setOsgpPortNumber(serverPort);

        // getting the IP configuration values
        LOGGER.info("Reading the IP configuration values");
        final NodeContainer ipConfiguration = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.IP_CONFIGURATION, Fc.CF);

        final String deviceFixedIpAddress = ipConfiguration.getString(SubDataAttribute.IP_ADDRESS);
        final String deviceFixedIpNetmask = ipConfiguration.getString(SubDataAttribute.NETMASK);
        final String deviceFixedIpGateway = ipConfiguration.getString(SubDataAttribute.GATEWAY);
        final boolean isDhcpEnabled = ipConfiguration.getBoolean(SubDataAttribute.ENABLE_DHCP).getValue();

        final DeviceFixedIpDto deviceFixedIp = new DeviceFixedIpDto(deviceFixedIpAddress, deviceFixedIpNetmask,
                deviceFixedIpGateway);

        configuration.setDeviceFixedIp(deviceFixedIp);
        configuration.setDhcpEnabled(isDhcpEnabled);

        // setting the software configuration values
        configuration.setAstroGateSunRiseOffset((int) astroGateSunRiseOffset);
        configuration.setAstroGateSunSetOffset((int) astroGateSunSetOffset);

        // getting the clock configuration values
        LOGGER.info("Reading the clock configuration values");
        final NodeContainer clock = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.CLOCK, Fc.CF);

        final int timeSyncFrequency = clock.getUnsignedShort(SubDataAttribute.TIME_SYNC_FREQUENCY).getValue();
        final boolean automaticSummerTimingEnabled = clock.getBoolean(SubDataAttribute.AUTOMATIC_SUMMER_TIMING_ENABLED)
                .getValue();
        final String summerTimeDetails = clock.getString(SubDataAttribute.SUMMER_TIME_DETAILS);
        final String winterTimeDetails = clock.getString(SubDataAttribute.WINTER_TIME_DETAILS);

        configuration.setTimeSyncFrequency(timeSyncFrequency);
        configuration.setAutomaticSummerTimingEnabled(automaticSummerTimingEnabled);
        configuration.setSummerTimeDetails(new DaylightSavingTimeTransition(TIME_ZONE_AMSTERDAM, summerTimeDetails)
                .getDateTimeForNextTransition().toDateTime(DateTimeZone.UTC));
        configuration.setWinterTimeDetails(new DaylightSavingTimeTransition(TIME_ZONE_AMSTERDAM, winterTimeDetails)
                .getDateTimeForNextTransition().toDateTime(DateTimeZone.UTC));

        return configuration;
    }

    private void checkRelayType(final DeviceOutputSetting deviceOutputSetting, final DeviceConnection deviceConnection)
            throws ProtocolAdapterException {
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

        final LogicalNode logicalNode = LogicalNode.getSwitchComponentByIndex(deviceOutputSetting.getInternalId());
        final NodeContainer switchType = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING, logicalNode,
                DataAttribute.SWITCH_TYPE, Fc.ST);

        final int switchTypeValue = switchType.getByte(SubDataAttribute.STATE).getValue();
        if (expectedSwType != switchTypeValue) {
            throw new ProtocolAdapterException("DeviceOutputSetting (internal index = "
                    + deviceOutputSetting.getInternalId()
                    + ", external index = "
                    + deviceOutputSetting.getExternalId()
                    + ") has a RelayType ("
                    + registeredRelayType
                    + ") that does not match the SwType on the device: "
                    + (switchTypeValue == SWITCH_TYPE_TARIFF ? "Tariff switch (0)"
                            : (switchTypeValue == SWITCH_TYPE_LIGHT ? "Light switch (1)" : "Unknown value: "
                                    + switchTypeValue)));
        }
    }

    private void setConfigurationOnDevice(final DeviceConnection deviceConnection, final ConfigurationDto configuration)
            throws ProtocolAdapterException {
        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {

                if (configuration.getRelayConfiguration() != null
                        && configuration.getRelayConfiguration().getRelayMap() != null) {

                    final List<RelayMapDto> relayMaps = configuration.getRelayConfiguration().getRelayMap();
                    for (final RelayMapDto relayMap : relayMaps) {
                        final Integer internalIndex = relayMap.getAddress();
                        final RelayTypeDto relayType = relayMap.getRelayType();

                        final LogicalNode logicalNode = LogicalNode.getSwitchComponentByIndex(internalIndex);
                        final NodeContainer switchType = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                                logicalNode, DataAttribute.SWITCH_TYPE, Fc.CO);

                        final NodeContainer operation = switchType.getChild(SubDataAttribute.OPERATION);
                        final BdaInt8 ctlVal = operation.getByte(SubDataAttribute.CONTROL_VALUE);

                        final byte switchTypeValue = (byte) (RelayTypeDto.LIGHT.equals(relayType) ? SWITCH_TYPE_LIGHT
                                : SWITCH_TYPE_TARIFF);
                        LOGGER.info("Updating Switch for internal index {} to {} ({})", internalIndex, switchTypeValue,
                                relayType);

                        ctlVal.setValue(switchTypeValue);
                        operation.write();
                    }
                }

                // checking to see if all register values are null, so that we
                // don't read the values for no reason
                if (!(configuration.getOspgIpAddress() == null && configuration.getOsgpPortNumber() == null)) {

                    final NodeContainer registration = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                            LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.REGISTRATION, Fc.CF);

                    if (configuration.getOspgIpAddress() != null) {
                        LOGGER.info("Updating OspgIpAddress to {}", configuration.getOspgIpAddress());
                        registration.writeString(SubDataAttribute.SERVER_ADDRESS, configuration.getOspgIpAddress());
                    }

                    if (configuration.getOsgpPortNumber() != null) {
                        LOGGER.info("Updating OsgpPortNumber to {}", configuration.getOsgpPortNumber());
                        registration.writeInteger(SubDataAttribute.SERVER_PORT, configuration.getOsgpPortNumber());
                    }

                }

                // checking to see if all software config values are null, so
                // that we don't read the values for no reason
                if (!(configuration.getAstroGateSunRiseOffset() == null
                        && configuration.getAstroGateSunSetOffset() == null && configuration.getLightType() == null)) {

                    final NodeContainer softwareConfiguration = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                            LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.SOFTWARE_CONFIGURATION, Fc.CF);

                    if (configuration.getAstroGateSunRiseOffset() != null) {
                        LOGGER.info("Updating AstroGateSunRiseOffset to {}", configuration.getAstroGateSunRiseOffset());
                        softwareConfiguration.writeShort(SubDataAttribute.ASTRONOMIC_SUNRISE_OFFSET, configuration
                                .getAstroGateSunRiseOffset().shortValue());
                    }

                    if (configuration.getAstroGateSunSetOffset() != null) {
                        LOGGER.info("Updating AstroGateSunSetOffset to {}", configuration.getAstroGateSunSetOffset());
                        softwareConfiguration.writeShort(SubDataAttribute.ASTRONOMIC_SUNSET_OFFSET, configuration
                                .getAstroGateSunSetOffset().shortValue());
                    }

                    if (configuration.getLightType() != null) {
                        LOGGER.info("Updating LightType to {}", configuration.getLightType());
                        softwareConfiguration.writeString(SubDataAttribute.LIGHT_TYPE, configuration.getLightType()
                                .name());
                    }
                }

                // checking to see if all register values are null, so that we
                // don't read the values for no reason
                if (!(configuration.getTimeSyncFrequency() == null
                        && configuration.isAutomaticSummerTimingEnabled() == null
                        && configuration.getSummerTimeDetails() == null && configuration.getWinterTimeDetails() == null)) {

                    final NodeContainer clock = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                            LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.CLOCK, Fc.CF);

                    if (configuration.getTimeSyncFrequency() != null) {
                        LOGGER.info("Updating TimeSyncFrequency to {}", configuration.getTimeSyncFrequency());
                        clock.writeUnsignedShort(SubDataAttribute.TIME_SYNC_FREQUENCY,
                                configuration.getTimeSyncFrequency());
                    }

                    if (configuration.isAutomaticSummerTimingEnabled() != null) {
                        LOGGER.info("Updating AutomaticSummerTimingEnabled to {}",
                                configuration.isAutomaticSummerTimingEnabled());
                        clock.writeBoolean(SubDataAttribute.AUTOMATIC_SUMMER_TIMING_ENABLED,
                                configuration.isAutomaticSummerTimingEnabled());
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
                        LOGGER.info("Updating DstBeginTime to {} based on SummerTimeDetails {}", mwdValueForBeginOfDst,
                                summerTimeDetails);
                        clock.writeString(SubDataAttribute.SUMMER_TIME_DETAILS, mwdValueForBeginOfDst);
                    }
                    if (winterTimeDetails != null) {

                        final String mwdValueForEndOfDst = DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                                winterTimeDetails, dstFormatMwd).getTransition();
                        LOGGER.info("Updating DstEndTime to {} based on WinterTimeDetails {}", mwdValueForEndOfDst,
                                winterTimeDetails);
                        clock.writeString(SubDataAttribute.WINTER_TIME_DETAILS, mwdValueForEndOfDst);
                    }
                }

                // checking to see if all network values are null, so that we
                // don't read the values for no reason
                if (!(configuration.isDhcpEnabled() == null && configuration.getDeviceFixedIp() == null)) {

                    final NodeContainer ipConfiguration = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                            LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.IP_CONFIGURATION, Fc.CF);

                    if (configuration.isDhcpEnabled() != null) {
                        LOGGER.info("Updating DhcpEnabled to {}", configuration.isDhcpEnabled());
                        ipConfiguration.writeBoolean(SubDataAttribute.ENABLE_DHCP, configuration.isDhcpEnabled());
                    }

                    // All values in DeviceFixedIpDto are non-nullable, so no
                    // nullchecks are needed.
                    final DeviceFixedIpDto deviceFixedIp = configuration.getDeviceFixedIp();

                    LOGGER.info("Updating deviceFixedIpAddress to {}", configuration.getDeviceFixedIp().getIpAddress());
                    ipConfiguration.writeString(SubDataAttribute.IP_ADDRESS, deviceFixedIp.getIpAddress());

                    LOGGER.info("Updating deviceFixedIpNetmask to {}", configuration.getDeviceFixedIp().getNetMask());
                    ipConfiguration.writeString(SubDataAttribute.NETMASK, deviceFixedIp.getNetMask());

                    LOGGER.info("Updating deviceFixIpGateway to {}", configuration.getDeviceFixedIp().getGateWay());
                    ipConfiguration.writeString(SubDataAttribute.GATEWAY, deviceFixedIp.getGateWay());
                }

                return null;
            }
        };

        this.iec61850Client.sendCommandWithRetry(function);
    }

    private void rebootDevice(final DeviceConnection deviceConnection) throws ProtocolAdapterException {
        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {
                final NodeContainer rebootOperationNode = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                        LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.REBOOT_OPERATION, Fc.CO);
                LOGGER.info("device: {}, rebootOperationNode: {}", deviceConnection.getDeviceIdentification(),
                        rebootOperationNode);

                final NodeContainer oper = rebootOperationNode.getChild(SubDataAttribute.OPERATION);
                LOGGER.info("device: {}, oper: {}", deviceConnection.getDeviceIdentification(), oper);

                final BdaBoolean ctlVal = oper.getBoolean(SubDataAttribute.CONTROL_VALUE);
                LOGGER.info("device: {}, ctlVal: {}", deviceConnection.getDeviceIdentification(), ctlVal);

                ctlVal.setValue(true);
                LOGGER.info("device: {}, set ctlVal to true in order to reboot the device",
                        deviceConnection.getDeviceIdentification());
                oper.write();
                return null;
            }
        };

        this.iec61850Client.sendCommandWithRetry(function);
    }

    private void setScheduleOnDevice(final DeviceConnection deviceConnection, final RelayTypeDto relayType,
            final List<ScheduleDto> scheduleList, final Ssld ssld) throws ProtocolAdapterException, FunctionalException {
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

                    final LogicalNode logicalNode = LogicalNode.getSwitchComponentByIndex(relayIndex);
                    final NodeContainer schedule = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING, logicalNode,
                            DataAttribute.SCHEDULE, Fc.CF);

                    // Clear existing schedule by disabling schedule entries.
                    for (int i = 0; i < MAX_NUMBER_OF_SCHEDULE_ENTRIES; i++) {

                        LOGGER.info("Disabling schedule entry {} of {} for relay {} before setting new {} schedule",
                                i + 1, MAX_NUMBER_OF_SCHEDULE_ENTRIES, relayIndex, tariffOrLight);

                        final String scheduleEntryName = SubDataAttribute.SCHEDULE_ENTRY.getDescription() + (i + 1);
                        final NodeContainer scheduleNode = schedule.getChild(scheduleEntryName);

                        final BdaBoolean enabled = scheduleNode.getBoolean(SubDataAttribute.SCHEDULE_ENABLE);
                        if (enabled.getValue()) {
                            scheduleNode.writeBoolean(SubDataAttribute.SCHEDULE_ENABLE, false);
                        }
                    }

                    for (int i = 0; i < numberOfScheduleEntries; i++) {

                        LOGGER.info("Writing {} schedule entry {} for relay {}", tariffOrLight, i + 1, relayIndex);

                        final ScheduleEntry scheduleEntry = scheduleEntries.get(i);

                        final String scheduleEntryName = SubDataAttribute.SCHEDULE_ENTRY.getDescription() + (i + 1);
                        final NodeContainer scheduleNode = schedule.getChild(scheduleEntryName);

                        final BdaBoolean enabled = scheduleNode.getBoolean(SubDataAttribute.SCHEDULE_ENABLE);
                        if (enabled.getValue() != scheduleEntry.isEnabled()) {
                            scheduleNode.writeBoolean(SubDataAttribute.SCHEDULE_ENABLE, scheduleEntry.isEnabled());
                        }

                        final Integer day = scheduleNode.getInteger(SubDataAttribute.SCHEDULE_DAY).getValue();
                        if (day != scheduleEntry.getDay()) {
                            scheduleNode.writeInteger(SubDataAttribute.SCHEDULE_DAY, scheduleEntry.getDay());
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

                        final Integer timeOn = scheduleNode.getInteger(SubDataAttribute.SCHEDULE_TIME_ON).getValue();
                        if (timeOn != timeOnValue) {
                            scheduleNode.writeInteger(SubDataAttribute.SCHEDULE_TIME_ON, timeOnValue);
                        }

                        final Byte timeOnActionTime = scheduleNode.getByte(SubDataAttribute.SCHEDULE_TIME_ON_TYPE)
                                .getValue();
                        if (timeOnActionTime != timeOnTypeValue) {
                            scheduleNode.writeByte(SubDataAttribute.SCHEDULE_TIME_ON_TYPE, timeOnTypeValue);
                        }

                        final Integer timeOff = scheduleNode.getInteger(SubDataAttribute.SCHEDULE_TIME_OFF).getValue();
                        if (timeOff != timeOffValue) {
                            scheduleNode.writeInteger(SubDataAttribute.SCHEDULE_TIME_OFF, timeOffValue);
                        }

                        final Byte timeOffActionTime = scheduleNode.getByte(SubDataAttribute.SCHEDULE_TIME_OFF_TYPE)
                                .getValue();
                        if (timeOffActionTime != timeOffTypeValue) {
                            scheduleNode.writeByte(SubDataAttribute.SCHEDULE_TIME_OFF_TYPE, timeOffTypeValue);
                        }

                        final Integer minimumTimeOn = scheduleNode.getUnsignedShort(SubDataAttribute.MINIMUM_TIME_ON)
                                .getValue();
                        final Integer newMinimumTimeOn = scheduleEntry.getMinimumLightsOn() / 60;
                        if (minimumTimeOn != newMinimumTimeOn) {
                            scheduleNode.writeUnsignedShort(SubDataAttribute.MINIMUM_TIME_ON, newMinimumTimeOn);
                        }

                        final Integer triggerMinutesBefore = scheduleNode.getUnsignedShort(
                                SubDataAttribute.SCHEDULE_TRIGGER_MINUTES_BEFORE).getValue();
                        if (triggerMinutesBefore != scheduleEntry.getTriggerWindowMinutesBefore()) {
                            scheduleNode.writeUnsignedShort(SubDataAttribute.SCHEDULE_TRIGGER_MINUTES_BEFORE,
                                    scheduleEntry.getTriggerWindowMinutesBefore());
                        }

                        final Integer triggerMinutesAfter = scheduleNode.getUnsignedShort(
                                SubDataAttribute.SCHEDULE_TRIGGER_MINUTES_AFTER).getValue();
                        if (triggerMinutesAfter != scheduleEntry.getTriggerWindowMinutesAfter()) {
                            scheduleNode.writeUnsignedShort(SubDataAttribute.SCHEDULE_TRIGGER_MINUTES_AFTER,
                                    scheduleEntry.getTriggerWindowMinutesAfter());
                        }
                    }

                    return null;
                }
            };

            this.iec61850Client.sendCommandWithRetry(function);
        }
    }

    private List<FirmwareVersionDto> getFirmwareVersionFromDevice(final DeviceConnection connection)
            throws ProtocolAdapterException {
        final List<FirmwareVersionDto> output = new ArrayList<>();

        // Getting the functional firmware version
        LOGGER.info("Reading the functional firmware version");

        final NodeContainer functionalFirmwareNode = connection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.FUNCTIONAL_FIRMWARE, Fc.ST);

        final String functionalFirmwareVersion = functionalFirmwareNode.getString(SubDataAttribute.CURRENT_VERSION);

        // Adding it to the list
        output.add(new FirmwareVersionDto(FirmwareModuleType.FUNCTIONAL, functionalFirmwareVersion));

        // Getting the security firmware version
        LOGGER.info("Reading the security firmware version");

        final NodeContainer securityFirmwareNode = connection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.SECURITY_FIRMWARE, Fc.ST);

        final String securityFirmwareVersion = securityFirmwareNode.getString(SubDataAttribute.CURRENT_VERSION);

        // Adding it to the list
        output.add(new FirmwareVersionDto(FirmwareModuleType.SECURITY, securityFirmwareVersion));

        return output;
    }

    private void transitionDevice(final DeviceConnection deviceConnection,
            final TransitionMessageDataContainerDto transitionMessageDataContainer) throws ProtocolAdapterException {
        final TransitionTypeDto transitionType = transitionMessageDataContainer.getTransitionType();
        LOGGER.info("device: {}, transition: {}", deviceConnection.getDeviceIdentification(), transitionType);
        final boolean controlValueForTransition = transitionType.equals(TransitionTypeDto.DAY_NIGHT);

        final DateTime dateTime = transitionMessageDataContainer.getDateTime();
        if (dateTime != null) {
            LOGGER.warn("device: {}, setting date/time {} for transition {} not supported",
                    deviceConnection.getDeviceIdentification(), dateTime, transitionType);
        }

        final Function<Void> function = new Function<Void>() {
            @Override
            public Void apply() throws Exception {

                final NodeContainer sensorNode = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                        LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.SENSOR, Fc.CO);
                LOGGER.info("device: {}, sensorNode: {}", deviceConnection.getDeviceIdentification(), sensorNode);

                final NodeContainer oper = sensorNode.getChild(SubDataAttribute.OPERATION);
                LOGGER.info("device: {}, oper: {}", deviceConnection.getDeviceIdentification(), oper);

                final BdaBoolean ctlVal = oper.getBoolean(SubDataAttribute.CONTROL_VALUE);
                LOGGER.info("device: {}, ctlVal: {}", deviceConnection.getDeviceIdentification(), ctlVal);

                ctlVal.setValue(controlValueForTransition);
                LOGGER.info("device: {}, set ctlVal to {} in order to transition the device",
                        deviceConnection.getDeviceIdentification(), controlValueForTransition);

                oper.write();
                return null;
            }
        };

        this.iec61850Client.sendCommandWithRetry(function);
    }

    private void pushFirmwareToDevice(final DeviceConnection connection, final ServerModel serverModel,
            final ClientAssociation clientAssociation, final String fullUrl) throws ProtocolAdapterException,
            FunctionalException {
        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {
                // Getting the functional firmware version
                LOGGER.info("Reading the functional firmware version");
                final NodeContainer functionalFirmwareNode = connection.getFcModelNode(LogicalDevice.LIGHTING,
                        LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.FUNCTIONAL_FIRMWARE, Fc.CF);

                LOGGER.info("Updating the firmware download url");
                functionalFirmwareNode.writeString(SubDataAttribute.URL, fullUrl);

                // creating a Date one minute from now
                final Date oneMinuteFromNow = Iec61850SsldDeviceService.this.getLocalTimeForDevice(connection)
                        .plusMinutes(1).toDate();

                LOGGER.info("Updating the firmware download start time");
                functionalFirmwareNode.writeDate(SubDataAttribute.START_TIME, oneMinuteFromNow);

                return null;
            }
        };

        this.iec61850Client.sendCommandWithRetry(function);
    }

    private void pushSslCertificateToDevice(final DeviceConnection deviceConnection,
            final CertificationDto certification) throws ProtocolAdapterException, FunctionalException {
        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {

                LOGGER.info("Reading the certificate authority url");
                final NodeContainer sslConfiguration = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                        LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.CERTIFICATE_AUTHORITY_REPLACE, Fc.CF);

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
                sslConfiguration.writeString(SubDataAttribute.URL, fullUrl);

                final Date oneMinuteFromNow = Iec61850SsldDeviceService.this.getLocalTimeForDevice(deviceConnection)
                        .plusMinutes(1).toDate();

                LOGGER.info("Updating the certificate download start time to {}", oneMinuteFromNow);
                sslConfiguration.writeDate(SubDataAttribute.START_TIME, oneMinuteFromNow);

                return null;
            }
        };

        this.iec61850Client.sendCommandWithRetry(function);
    }

    private void setEventNotificationFilterOnDevice(final DeviceConnection deviceConnection, final String filter)
            throws ProtocolAdapterException, FunctionalException {
        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {

                LOGGER.info("Setting the event notification filter");

                final NodeContainer eventBufferConfiguration = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                        LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.EVENT_BUFFER, Fc.CF);

                LOGGER.info("Updating the enabled EventType filter to {}", filter);
                eventBufferConfiguration.writeString(SubDataAttribute.EVENT_BUFFER_FILTER, filter);

                return null;
            }
        };

        this.iec61850Client.sendCommandWithRetry(function);
    }

    public void enableReportingOnDevice(final DeviceConnection deviceConnection, final String deviceIdentification)
            throws ServiceError {
        final NodeContainer reporting = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.LOGICAL_NODE_ZERO, DataAttribute.REPORTING, Fc.BR);

        final Iec61850ClientBaseEventListener reportListener = deviceConnection.getConnection()
                .getIec61850ClientAssociation().getReportListener();

        final Integer sqNum = reporting.getUnsignedShort(SubDataAttribute.SEQUENCE_NUMBER).getValue();
        reportListener.setSqNum(sqNum);
        reporting.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
        LOGGER.info("Allowing device {} to send events", deviceIdentification);
    }

    // ========================
    // PRIVATE HELPER METHODS =
    // ========================

    /*
     * Checks to see if the relay has the correct type, throws an exception when
     * that't not the case
     */
    private void checkRelay(final RelayType actual, final RelayType expected, final Integer internalAddress)
            throws FunctionalException {
        if (!actual.equals(expected)) {
            if (RelayType.LIGHT.equals(expected)) {
                LOGGER.error("Relay with internal address: {} is not configured as light relay", internalAddress);
                throw new FunctionalException(FunctionalExceptionType.ACTION_NOT_ALLOWED_FOR_LIGHT_RELAY,
                        ComponentType.PROTOCOL_IEC61850);
            } else {
                LOGGER.error("Relay with internal address: {} is not configured as tariff relay", internalAddress);
                throw new FunctionalException(FunctionalExceptionType.ACTION_NOT_ALLOWED_FOR_TARIFF_RELAY,
                        ComponentType.PROTOCOL_IEC61850);
            }
        }
    }

    /**
     * Check specific for schedule setting.
     */
    private void checkRelayForSchedules(final RelayType actual, final RelayType expected, final Integer internalAddress)
            throws FunctionalException {
        // First check the special case.
        if (expected.equals(RelayType.TARIFF) && actual.equals(RelayType.TARIFF_REVERSED)) {
            return;
        }
        this.checkRelay(actual, expected, internalAddress);
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
     * Convert a time String to a short value.
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
                    final List<DeviceOutputSetting> settings = Iec61850SsldDeviceService.this.ssldDataService
                            .findByRelayType(ssld, relayType);

                    for (final DeviceOutputSetting deviceOutputSetting : settings) {
                        indexes.add(deviceOutputSetting.getInternalId());
                    }
                } else {
                    // index != 0, adding just the one index to the list
                    indexes.add(Iec61850SsldDeviceService.this.ssldDataService.convertToInternalIndex(ssld,
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
                        Iec61850SsldDeviceService.this.checkRelayForSchedules(
                                Iec61850SsldDeviceService.this.ssldDataService.getDeviceOutputSettingForInternalIndex(
                                        ssld, internalIndex).getRelayType(), relayType, internalIndex);

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
    private DateTime getLocalTimeForDevice(final DeviceConnection deviceConnection) {
        LOGGER.info("Converting local time to the device's local time");

        // Get the Clock node.
        final NodeContainer clock = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.CLOCK, Fc.CF);
        LOGGER.info("Trying to read automatic summer-timing enabled...");
        final boolean automaticSummerTimingEnabled = clock.getBoolean(SubDataAttribute.AUTOMATIC_SUMMER_TIMING_ENABLED)
                .getValue();
        LOGGER.info("Value of automaticSummerTimingEnabled: {}", automaticSummerTimingEnabled);

        int daylightSavingTime = 0;

        if (automaticSummerTimingEnabled) {
            LOGGER.info("Automatic summer-timing is enabled, trying to read the begin and end of summer time...");
            final String summerTimeDetails = clock.getString(SubDataAttribute.SUMMER_TIME_DETAILS);
            final String winterTimeDetails = clock.getString(SubDataAttribute.WINTER_TIME_DETAILS);
            LOGGER.info("Value of summerTimeDetails: {}", summerTimeDetails);
            LOGGER.info("Value of winterTimeDetails: {}", winterTimeDetails);

            final DstTransitionFormat dstTransitionFormat = DstTransitionFormat.DAY_OF_WEEK_OF_MONTH;
            final DateTime now = DateTime.now();
            final int year = now.getYear();
            final DateTime summerTime = dstTransitionFormat.getDateTime(TIME_ZONE_AMSTERDAM, summerTimeDetails, year);
            final DateTime winterTime = dstTransitionFormat.getDateTime(TIME_ZONE_AMSTERDAM, winterTimeDetails, year);
            LOGGER.info("Value of summerTime: {}", summerTime.toString());
            LOGGER.info("Value of winterTime: {}", winterTime.toString());

            if (now.isAfter(summerTime) && now.isBefore(winterTime)) {
                LOGGER.info("It is summer time, trying to read summer time deviation...");
                // Get the DST deviation from the data-attribute dvt from the
                // Clock which is in minutes.
                final Short deviation = clock.getShort(SubDataAttribute.DAYLIGHT_SAVING_TIME).getValue();
                LOGGER.info("Value of deviation: {} minutes", deviation);
                daylightSavingTime = deviation / 60;
            } else {
                LOGGER.info("It is winter time.");
            }
        }

        LOGGER.info("Trying to read timezone...");
        final Short timezone = clock.getShort(SubDataAttribute.TIME_ZONE).getValue();
        LOGGER.info("Value of timezone: {} minutes", timezone);

        // Default value for time zone offset is 60, that
        // means 60 minutes / 60 = 1 hour.
        final int offset = timezone / 60;
        final DateTime dateTime = DateTime.now().plusHours(offset + daylightSavingTime);
        LOGGER.info("Value of dateTime: {}", dateTime);

        return dateTime;
    }

    private List<PowerUsageDataDto> getPowerUsageHistoryDataFromRelay(final DeviceConnection deviceConnection,
            final String deviceIdentification, final TimePeriodDto timePeriod,
            final DeviceOutputSetting deviceOutputSetting) throws TechnicalException {
        final List<PowerUsageDataDto> powerUsageHistoryDataFromRelay = new ArrayList<>();

        final int relayIndex = deviceOutputSetting.getExternalId();

        final LogicalNode logicalNode = LogicalNode.getSwitchComponentByIndex(deviceOutputSetting.getInternalId());
        final NodeContainer onIntervalBuffer = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING, logicalNode,
                DataAttribute.SWITCH_ON_INTERVAL_BUFFER, Fc.ST);

        final Short lastIndex = onIntervalBuffer.getUnsignedByte(SubDataAttribute.LAST_INDEX).getValue();

        /*-
         * Last index is the last index written in the 60-entry buffer.
         * When the last buffer entry is written, the next entry will be placed
         * at the first position in the buffer (cyclical).
         * To preserve the order of entries written in the response, iteration
         * starts with the next index (oldest entry) and loops from there.
         */
        final int numberOfEntries = 60;
        final int idxOldest = (lastIndex + 1) % numberOfEntries;

        for (int i = 0; i < numberOfEntries; i++) {
            final int bufferIndex = (idxOldest + i) % numberOfEntries;
            final NodeContainer indexedItvNode = onIntervalBuffer.getChild(SubDataAttribute.INTERVAL.getDescription()
                    + (bufferIndex + 1));
            LOGGER.info("device: {}, itv{}: {}", deviceIdentification, bufferIndex + 1, indexedItvNode);

            final Integer itvNode = indexedItvNode.getInteger(SubDataAttribute.INTERVAL).getValue();
            LOGGER.info("device: {}, itv{}.itv: {}", deviceIdentification, bufferIndex + 1, itvNode);

            final DateTime date = new DateTime(indexedItvNode.getDate(SubDataAttribute.DAY));
            LOGGER.info("device: {}, itv{}.day: {}", deviceIdentification, bufferIndex + 1, date);

            final int totalMinutesOnForDate = itvNode;
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
