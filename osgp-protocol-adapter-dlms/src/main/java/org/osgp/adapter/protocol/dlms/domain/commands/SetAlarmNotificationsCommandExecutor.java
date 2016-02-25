/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotification;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmType;

@Component()
public class SetAlarmNotificationsCommandExecutor implements CommandExecutor<AlarmNotifications, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetAlarmNotificationsCommandExecutor.class);

    private static final int CLASS_ID = 1;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.97.98.10.255");
    private static final int ATTRIBUTE_ID = 2;

    private static final int NUMBER_OF_BITS_IN_ALARM_FILTER = 32;

    /**
     * Gives the position of the alarm code as indicated by the AlarmType in the
     * bit string representation of the alarm register.
     * <p>
     * A position of 0 means the least significant bit, up to the maximum of 31
     * for the most significant bit. Since the 4 most significant bits in the
     * object are not used according to the DSMR documentation, the practical
     * meaningful most significant bit is bit 27.
     */
    private static final Map<AlarmType, Integer> alarmRegisterBitIndexPerAlarmType;

    static {
        final Map<AlarmType, Integer> map = new EnumMap<>(AlarmType.class);

        // Bits for group: Other Alarms
        map.put(AlarmType.CLOCK_INVALID, 0);
        map.put(AlarmType.REPLACE_BATTERY, 1);
        map.put(AlarmType.POWER_UP, 2);
        // bits 3 to 7 are not used

        // Bits for group: Critical Alarms
        map.put(AlarmType.PROGRAM_MEMORY_ERROR, 8);
        map.put(AlarmType.RAM_ERROR, 9);
        map.put(AlarmType.NV_MEMORY_ERROR, 10);
        map.put(AlarmType.MEASUREMENT_SYSTEM_ERROR, 11);
        map.put(AlarmType.WATCHDOG_ERROR, 12);
        map.put(AlarmType.FRAUD_ATTEMPT, 13);
        // bits 14 and 15 are not used

        // Bits for group: M-Bus Alarms
        map.put(AlarmType.COMMUNICATION_ERROR_M_BUS_CHANNEL_1, 16);
        map.put(AlarmType.COMMUNICATION_ERROR_M_BUS_CHANNEL_2, 17);
        map.put(AlarmType.COMMUNICATION_ERROR_M_BUS_CHANNEL_3, 18);
        map.put(AlarmType.COMMUNICATION_ERROR_M_BUS_CHANNEL_4, 19);
        map.put(AlarmType.FRAUD_ATTEMPT_M_BUS_CHANNEL_1, 20);
        map.put(AlarmType.FRAUD_ATTEMPT_M_BUS_CHANNEL_2, 21);
        map.put(AlarmType.FRAUD_ATTEMPT_M_BUS_CHANNEL_3, 22);
        map.put(AlarmType.FRAUD_ATTEMPT_M_BUS_CHANNEL_4, 23);

        // Bits for group: Reserved
        map.put(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1, 24);
        map.put(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2, 25);
        map.put(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3, 26);
        map.put(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4, 27);
        // bits 28 to 31 are not used

        alarmRegisterBitIndexPerAlarmType = Collections.unmodifiableMap(map);
    }

    @Override
    public AccessResultCode execute(final LnClientConnection conn, final DlmsDevice device,
            final AlarmNotifications alarmNotifications) throws ProtocolAdapterException {

        try {
            final AlarmNotifications alarmNotificationsOnDevice = this.retrieveCurrentAlarmNotifications(conn);

            LOGGER.info("Alarm Filter on device before setting notifications: {}", alarmNotificationsOnDevice);

            final long alarmFilterLongValue = this.calculateAlarmFilterLongValue(alarmNotificationsOnDevice,
                    alarmNotifications);

            LOGGER.info("Modified Alarm Filter long value for device: {}", alarmFilterLongValue);

            return this.writeUpdatedAlarmNotifications(conn, alarmFilterLongValue);
        } catch (IOException | TimeoutException e) {
            throw new ConnectionException(e);
        }
    }

    public AlarmNotifications retrieveCurrentAlarmNotifications(final LnClientConnection conn) throws IOException,
            TimeoutException, ProtocolAdapterException {

        final AttributeAddress alarmFilterValue = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        LOGGER.info(
                "Retrieving current alarm filter by issuing get request for class id: {}, obis code: {}, attribute id: {}",
                CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
        final List<GetResult> getResultList = conn.get(alarmFilterValue);

        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving current alarm filter.");
        }

        if (getResultList.size() > 1) {
            throw new ProtocolAdapterException("Expected 1 GetResult while retrieving current alarm filter, got "
                    + getResultList.size());
        }

        return this.alarmNotifications(getResultList.get(0).resultData());
    }

    public AccessResultCode writeUpdatedAlarmNotifications(final LnClientConnection conn,
            final long alarmFilterLongValue) throws IOException, TimeoutException {

        final AttributeAddress alarmFilterValue = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
        final DataObject value = DataObject.newUInteger32Data(alarmFilterLongValue);

        final SetParameter setParameter = new SetParameter(alarmFilterValue, value);

        return conn.set(setParameter).get(0);
    }

    public AlarmNotifications alarmNotifications(final DataObject alarmFilter) throws ProtocolAdapterException {

        if (alarmFilter == null) {
            throw new ProtocolAdapterException("DataObject expected to contain an alarm filter is null.");
        }

        if (!alarmFilter.isNumber()) {
            throw new ProtocolAdapterException("DataObject isNumber is expected to be true for alarm notifications.");
        }

        if (!(alarmFilter.value() instanceof Number)) {
            throw new ProtocolAdapterException("Value in DataObject is not a java.lang.Number: "
                    + alarmFilter.value().getClass().getName());
        }

        return this.alarmNotifications(((Number) alarmFilter.value()).longValue());

    }

    public long calculateAlarmFilterLongValue(final AlarmNotifications alarmNotificationsOnDevice,
            final AlarmNotifications alarmNotificationsToSet) {

        /*
         * Create a new (modifyable) set of alarm notifications, based on the
         * notifications to set.
         * 
         * Next, add all notifications on the device. These will only really be
         * added to the new set of notifications if it did not contain a
         * notification for the alarm type for which the notification is added.
         * 
         * This works because of the specification of addAll for the set,
         * claiming elements will only be added if not already present, and the
         * defintion of equals on the AlarmNotification, ensuring only a simgle
         * setting per AlarmType.
         */

        final Set<AlarmNotification> notificationsToSet = new TreeSet<>(alarmNotificationsToSet.getAlarmNotifications());

        notificationsToSet.addAll(alarmNotificationsOnDevice.getAlarmNotifications());

        return this.alarmFilterLongValue(new AlarmNotifications(notificationsToSet));
    }

    public AlarmNotifications alarmNotifications(final long alarmFilterLongValue) {

        final BitSet bitSet = BitSet.valueOf(new long[] { alarmFilterLongValue });
        final Set<AlarmNotification> notifications = new TreeSet<>();

        final AlarmType[] alarmTypes = AlarmType.values();
        for (final AlarmType alarmType : alarmTypes) {
            final boolean enabled = bitSet.get(alarmRegisterBitIndexPerAlarmType.get(alarmType));
            notifications.add(new AlarmNotification(alarmType, enabled));
        }

        return new AlarmNotifications(notifications);
    }

    public long alarmFilterLongValue(final AlarmNotifications alarmNotifications) {

        final BitSet bitSet = new BitSet(NUMBER_OF_BITS_IN_ALARM_FILTER);
        final Set<AlarmNotification> notifications = alarmNotifications.getAlarmNotifications();
        for (final AlarmNotification alarmNotification : notifications) {
            bitSet.set(alarmRegisterBitIndexPerAlarmType.get(alarmNotification.getAlarmType()),
                    alarmNotification.isEnabled());
        }

        return bitSet.toLongArray()[0];
    }
}
