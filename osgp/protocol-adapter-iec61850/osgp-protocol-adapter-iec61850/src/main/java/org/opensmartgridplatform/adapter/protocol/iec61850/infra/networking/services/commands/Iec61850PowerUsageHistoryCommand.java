/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.openiec61850.Fc;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Function;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.services.DeviceMessageLoggingService;
import org.opensmartgridplatform.core.db.api.iec61850.entities.DeviceOutputSetting;
import org.opensmartgridplatform.dto.valueobjects.HistoryTermTypeDto;
import org.opensmartgridplatform.dto.valueobjects.MeterTypeDto;
import org.opensmartgridplatform.dto.valueobjects.PowerUsageDataDto;
import org.opensmartgridplatform.dto.valueobjects.PowerUsageHistoryMessageDataContainerDto;
import org.opensmartgridplatform.dto.valueobjects.RelayDataDto;
import org.opensmartgridplatform.dto.valueobjects.SsldDataDto;
import org.opensmartgridplatform.dto.valueobjects.TimePeriodDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850PowerUsageHistoryCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850PowerUsageHistoryCommand.class);

    private DeviceMessageLoggingService loggingService;

    public Iec61850PowerUsageHistoryCommand(final DeviceMessageLoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public List<PowerUsageDataDto> getPowerUsageHistoryDataFromDevice(final Iec61850Client iec61850Client,
            final DeviceConnection deviceConnection,
            final PowerUsageHistoryMessageDataContainerDto powerUsageHistoryContainer,
            final List<DeviceOutputSetting> deviceOutputSettingsLightRelays) throws ProtocolAdapterException {
        final Function<List<PowerUsageDataDto>> function = new Function<List<PowerUsageDataDto>>() {

            @Override
            public List<PowerUsageDataDto> apply(final DeviceMessageLog deviceMessageLog)
                    throws ProtocolAdapterException {
                final HistoryTermTypeDto historyTermType = powerUsageHistoryContainer.getHistoryTermType();
                if (historyTermType != null) {
                    LOGGER.info("device: {}, ignoring HistoryTermType ({}) determining power usage history",
                            deviceConnection.getDeviceIdentification(), historyTermType);
                }
                final TimePeriodDto timePeriod = powerUsageHistoryContainer.getTimePeriod();

                final List<PowerUsageDataDto> powerUsageHistoryData = new ArrayList<>();
                for (final DeviceOutputSetting deviceOutputSetting : deviceOutputSettingsLightRelays) {
                    final List<PowerUsageDataDto> powerUsageData = Iec61850PowerUsageHistoryCommand.this
                            .getPowerUsageHistoryDataFromRelay(iec61850Client, deviceConnection, timePeriod,
                                    deviceOutputSetting, deviceMessageLog);
                    powerUsageHistoryData.addAll(powerUsageData);
                }

                Iec61850PowerUsageHistoryCommand.this.loggingService.logMessage(deviceMessageLog,
                        deviceConnection.getDeviceIdentification(), deviceConnection.getOrganisationIdentification(),
                        false);

                /*
                 * This way of gathering leads to PowerUsageData elements per
                 * relay. If it is necessary to only include one PowerUsageData
                 * element for the device, where data for the different relays
                 * is combined in the SsldData.relayData some sort of merge
                 * needs to be performed.
                 *
                 * This can either be a rework of the list currently returned,
                 * or it can be a list constructed based on an altered return
                 * type from getPowerUsageHistoryDataFromRelay (for instance a
                 * Map of Date to a Map of Relay Index to Total Lighting
                 * Minutes).
                 */
                return powerUsageHistoryData;
            }
        };

        return iec61850Client.sendCommandWithRetry(function, "GetPowerUsageHistory",
                deviceConnection.getDeviceIdentification());
    }

    private List<PowerUsageDataDto> getPowerUsageHistoryDataFromRelay(final Iec61850Client iec61850Client,
            final DeviceConnection deviceConnection, final TimePeriodDto timePeriod,
            final DeviceOutputSetting deviceOutputSetting, final DeviceMessageLog deviceMessageLog)
            throws NodeException {
        final List<PowerUsageDataDto> powerUsageHistoryDataFromRelay = new ArrayList<>();

        final int relayIndex = deviceOutputSetting.getExternalId();

        final LogicalNode logicalNode = LogicalNode.getSwitchComponentByIndex(deviceOutputSetting.getInternalId());
        final NodeContainer onIntervalBuffer = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING, logicalNode,
                DataAttribute.SWITCH_ON_INTERVAL_BUFFER, Fc.ST);
        iec61850Client.readNodeDataValues(deviceConnection.getConnection().getClientAssociation(),
                onIntervalBuffer.getFcmodelNode());

        final Short lastIndex = onIntervalBuffer.getUnsignedByte(SubDataAttribute.LAST_INDEX).getValue();

        deviceMessageLog.addVariable(LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.SWITCH_ON_INTERVAL_BUFFER,
                Fc.ST, SubDataAttribute.LAST_INDEX, lastIndex.toString());

        /*
         * Last index is the last index written in the 60-entry buffer. When the
         * last buffer entry is written, the next entry will be placed at the
         * first position in the buffer (cyclically). To preserve the order of
         * entries written in the response, iteration starts with the next index
         * (oldest entry) and loops from there.
         */
        final int numberOfEntries = 60;
        final int idxOldest = (lastIndex + 1) % numberOfEntries;

        for (int i = 0; i < numberOfEntries; i++) {
            final int bufferIndex = (idxOldest + i) % numberOfEntries;
            final NodeContainer indexedItvNode = onIntervalBuffer
                    .getChild(SubDataAttribute.INTERVAL.getDescription() + (bufferIndex + 1));
            LOGGER.info("device: {}, itv{}: {}", deviceConnection.getDeviceIdentification(), bufferIndex + 1,
                    indexedItvNode);

            final Integer itvNode = indexedItvNode.getInteger(SubDataAttribute.INTERVAL).getValue();
            LOGGER.info("device: {}, itv{}.itv: {}", deviceConnection.getDeviceIdentification(), bufferIndex + 1,
                    itvNode);

            deviceMessageLog.addVariable(LogicalNode.STREET_LIGHT_CONFIGURATION,
                    DataAttribute.SWITCH_ON_INTERVAL_BUFFER, Fc.ST,
                    SubDataAttribute.INTERVAL.getDescription().concat(Integer.toString(bufferIndex + 1)),
                    SubDataAttribute.INTERVAL, itvNode.toString());

            final DateTime date = new DateTime(indexedItvNode.getDate(SubDataAttribute.DAY));
            LOGGER.info("device: {}, itv{}.day: {}", deviceConnection.getDeviceIdentification(), bufferIndex + 1, date);

            deviceMessageLog.addVariable(LogicalNode.STREET_LIGHT_CONFIGURATION,
                    DataAttribute.SWITCH_ON_INTERVAL_BUFFER, Fc.ST,
                    SubDataAttribute.INTERVAL.getDescription().concat(Integer.toString(bufferIndex + 1)),
                    SubDataAttribute.DAY, date.toString("yyyy-MM-dd"));

            final int totalMinutesOnForDate = itvNode;
            final boolean includeEntryInResponse = this.timePeriodContainsDateTime(timePeriod, date,
                    deviceConnection.getDeviceIdentification(), relayIndex, bufferIndex);
            if (!includeEntryInResponse) {
                continue;
            }

            // MeterType.AUX hard-coded (not supported).
            final PowerUsageDataDto powerUsageData = new PowerUsageDataDto(date, MeterTypeDto.AUX, 0, 0);
            final List<RelayDataDto> relayDataList = new ArrayList<>();
            final RelayDataDto relayData = new RelayDataDto(relayIndex, totalMinutesOnForDate);
            relayDataList.add(relayData);
            final SsldDataDto ssldData = SsldDataDto.newBuilder()
                    .withActualCurrent1(0)
                    .withActualCurrent2(0)
                    .withActualCurrent3(0)
                    .withActualPower1(0)
                    .withActualPower2(0)
                    .withActualPower3(0)
                    .withAveragePowerFactor1(0)
                    .withAveragePowerFactor2(0)
                    .withAveragePowerFactor3(0)
                    .withRelayData(relayDataList)
                    .build();
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
