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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.GetRequestParameter;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.RequestParameterFactory;
import org.openmuc.jdlms.SetRequestParameter;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AlarmHelperService alarmHelperService;

    @Override
    public AccessResultCode execute(final ClientConnection conn, final AlarmNotifications alarmNotifications)
            throws IOException, ProtocolAdapterException {

        final AlarmNotifications alarmNotificationsOnDevice = this.retrieveCurrentAlarmNotifications(conn);

        LOGGER.info("Alarm Filter on device before setting notifications: {}", alarmNotificationsOnDevice);

        final long alarmFilterLongValue = this.calculateAlarmFilterLongValue(alarmNotificationsOnDevice,
                alarmNotifications);

        LOGGER.info("Modified Alarm Filter long value for device: {}", alarmFilterLongValue);

        return this.writeUpdatedAlarmNotifications(conn, alarmFilterLongValue);
    }

    public AlarmNotifications retrieveCurrentAlarmNotifications(final ClientConnection conn) throws IOException,
            ProtocolAdapterException {

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        final GetRequestParameter getRequestParameter = factory.createGetRequestParameter();

        LOGGER.info(
                "Retrieving current alarm filter by issuing get request for class id: {}, obis code: {}, attribute id: {}",
                getRequestParameter.classId(), getRequestParameter.obisCode(), getRequestParameter.attributeId());
        final List<GetResult> getResultList = conn.get(getRequestParameter);

        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving current alarm filter.");
        }

        if (getResultList.size() > 1) {
            throw new ProtocolAdapterException("Expected 1 GetResult while retrieving current alarm filter, got "
                    + getResultList.size());
        }

        final AlarmNotifications alarmNotificationsOnDevice = this
                .alarmNotifications(getResultList.get(0).resultData());

        return alarmNotificationsOnDevice;
    }

    public AccessResultCode writeUpdatedAlarmNotifications(final ClientConnection conn, final long alarmFilterLongValue)
            throws IOException {

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        final DataObject obj = DataObject.newUInteger32Data(alarmFilterLongValue);

        final SetRequestParameter request = factory.createSetRequestParameter(obj);

        return conn.set(request).get(0);
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
            final boolean enabled = bitSet.get(this.alarmHelperService.toBitPosition(alarmType));
            notifications.add(new AlarmNotification(alarmType, enabled));
        }

        return new AlarmNotifications(notifications);
    }

    public long alarmFilterLongValue(final AlarmNotifications alarmNotifications) {
        final Set<AlarmType> alarmTypes = new HashSet<>();

        // Group all active alarm types.
        final Set<AlarmNotification> notifications = alarmNotifications.getAlarmNotifications();
        for (final AlarmNotification alarmNotification : notifications) {
            if (alarmNotification.isEnabled()) {
                alarmTypes.add(alarmNotification.getAlarmType());
            }
        }

        return this.alarmHelperService.toLongValue(alarmTypes);
    }
}
