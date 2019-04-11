/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmNotificationDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetAlarmNotificationsRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class SetAlarmNotificationsCommandExecutor
        extends AbstractCommandExecutor<AlarmNotificationsDto, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetAlarmNotificationsCommandExecutor.class);

    private static final int CLASS_ID = 1;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.97.98.10.255");
    private static final int ATTRIBUTE_ID = 2;

    private static final int NUMBER_OF_BITS_IN_ALARM_FILTER = 32;

    private final AlarmHelperService alarmHelperService = new AlarmHelperService();

    public SetAlarmNotificationsCommandExecutor() {
        super(SetAlarmNotificationsRequestDto.class);
    }

    @Override
    public AlarmNotificationsDto fromBundleRequestInput(final ActionRequestDto bundleInput)
            throws ProtocolAdapterException {

        this.checkActionRequestType(bundleInput);
        final SetAlarmNotificationsRequestDto setAlarmNotificationsRequestDto =
                (SetAlarmNotificationsRequestDto) bundleInput;

        return setAlarmNotificationsRequestDto.getAlarmNotifications();
    }

    @Override
    public ActionResponseDto asBundleResponse(final AccessResultCode executionResult) throws ProtocolAdapterException {

        this.checkAccessResultCode(executionResult);

        return new ActionResponseDto("Set alarm notifications was successful");
    }

    @Override
    public AccessResultCode execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final AlarmNotificationsDto alarmNotifications) throws ProtocolAdapterException {

        try {
            final AlarmNotificationsDto alarmNotificationsOnDevice = this.retrieveCurrentAlarmNotifications(conn);

            LOGGER.info("Alarm Filter on device before setting notifications: {}", alarmNotificationsOnDevice);

            final long alarmFilterLongValueOnDevice = this.alarmFilterLongValue(alarmNotificationsOnDevice);
            final long updatedAlarmFilterLongValue = this.calculateAlarmFilterLongValue(alarmNotificationsOnDevice,
                    alarmNotifications);

            if (alarmFilterLongValueOnDevice == updatedAlarmFilterLongValue) {
                return AccessResultCode.SUCCESS;
            }

            LOGGER.info("Modified Alarm Filter long value for device: {}", updatedAlarmFilterLongValue);

            return this.writeUpdatedAlarmNotifications(conn, updatedAlarmFilterLongValue);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }

    private AlarmNotificationsDto retrieveCurrentAlarmNotifications(final DlmsConnectionManager conn)
            throws IOException, ProtocolAdapterException {

        final AttributeAddress alarmFilterValue = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        conn.getDlmsMessageListener().setDescription(
                "SetAlarmNotifications retrieve current value, retrieve attribute: "
                        + JdlmsObjectToStringUtil.describeAttributes(alarmFilterValue));

        LOGGER.info("Retrieving current alarm filter by issuing get request for class id: {}, obis code: {}, attribute "
                + "id: {}", CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
        final GetResult getResult = conn.getConnection().get(alarmFilterValue);

        if (getResult == null) {
            throw new ProtocolAdapterException("No GetResult received while retrieving current alarm filter.");
        }

        return this.alarmNotifications(getResult.getResultData());
    }

    private AccessResultCode writeUpdatedAlarmNotifications(final DlmsConnectionManager conn,
            final long alarmFilterLongValue) throws IOException {

        final AttributeAddress alarmFilterValue = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
        final DataObject value = DataObject.newUInteger32Data(alarmFilterLongValue);

        final SetParameter setParameter = new SetParameter(alarmFilterValue, value);

        conn.getDlmsMessageListener().setDescription(
                "SetAlarmNotifications write updated value " + alarmFilterLongValue + ", set attribute: "
                        + JdlmsObjectToStringUtil.describeAttributes(alarmFilterValue));

        return conn.getConnection().set(setParameter);
    }

    private AlarmNotificationsDto alarmNotifications(final DataObject alarmFilter) throws ProtocolAdapterException {

        if (alarmFilter == null) {
            throw new ProtocolAdapterException("DataObject expected to contain an alarm filter is null.");
        }

        if (!alarmFilter.isNumber()) {
            throw new ProtocolAdapterException("DataObject isNumber is expected to be true for alarm notifications.");
        }

        if (!(alarmFilter.getValue() instanceof Number)) {
            throw new ProtocolAdapterException(
                    "Value in DataObject is not a java.lang.Number: " + alarmFilter.getValue().getClass().getName());
        }

        final Number alarmFilterValue = alarmFilter.getValue();
        return this.alarmNotifications(alarmFilterValue.longValue());

    }

    private AlarmNotificationsDto alarmNotifications(final long alarmFilterLongValue) {

        final BitSet bitSet = BitSet.valueOf(new long[] { alarmFilterLongValue });
        final Set<AlarmNotificationDto> notifications = new TreeSet<>();

        final AlarmTypeDto[] alarmTypes = AlarmTypeDto.values();
        for (final AlarmTypeDto alarmType : alarmTypes) {
            final boolean enabled = bitSet.get(this.alarmHelperService.getBitIndexForAlarmType(alarmType));
            notifications.add(new AlarmNotificationDto(alarmType, enabled));
        }

        return new AlarmNotificationsDto(notifications);
    }

    private long calculateAlarmFilterLongValue(final AlarmNotificationsDto existingNotifications,
            final AlarmNotificationsDto newNotifications) {
        AlarmNotificationsDto notifications = new AlarmNotificationsDto(new HashSet<>(
                newNotifications.getAlarmNotificationsSet()));
        // adds only existing notifications with unique AlarmTypeDto
        notifications.getAlarmNotificationsSet().addAll(existingNotifications.getAlarmNotificationsSet());
        return this.alarmFilterLongValue(notifications);
    }

    private long alarmFilterLongValue(final AlarmNotificationsDto alarmNotifications) {
        final BitSet bitSet = new BitSet(NUMBER_OF_BITS_IN_ALARM_FILTER);
        for (final AlarmNotificationDto alarmNotification : alarmNotifications.getAlarmNotificationsSet()) {
            Integer index = this.alarmHelperService.getBitIndexForAlarmType(alarmNotification.getAlarmType());
            bitSet.set(index, alarmNotification.isEnabled());
        }

        /*
         * If no alarmType has isEnabled is true in the request, bitSet stays
         * empty. Value 0 should then be returned because nothing has to be
         * enabled. Then the alarmFilter value to write to the device will be
         * calculated with this input.
         */
        if (bitSet.isEmpty()) {
            return 0L;
        } else {
            return bitSet.toLongArray()[0];
        }
    }
}
