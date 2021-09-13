/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import java.io.IOException;
import java.util.BitSet;
import java.util.Set;
import java.util.TreeSet;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
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
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class SetAlarmNotificationsCommandExecutor
    extends AbstractCommandExecutor<AlarmNotificationsDto, AccessResultCode> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SetAlarmNotificationsCommandExecutor.class);
  private static final int NUMBER_OF_BITS_IN_ALARM_FILTER = 32;

  private final AlarmHelperService alarmHelperService = new AlarmHelperService();
  private final DlmsObjectConfigService dlmsObjectConfigService;

  @Autowired
  public SetAlarmNotificationsCommandExecutor(
      final DlmsObjectConfigService dlmsObjectConfigService) {
    super(SetAlarmNotificationsRequestDto.class);

    this.dlmsObjectConfigService = dlmsObjectConfigService;
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
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {

    this.checkAccessResultCode(executionResult);

    return new ActionResponseDto("Set alarm notifications was successful");
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final AlarmNotificationsDto alarmNotifications,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    try {
      final AttributeAddress alarmFilterValue =
          this.dlmsObjectConfigService.getAttributeAddress(
              device, DlmsObjectType.ALARM_FILTER, null);

      final AlarmNotificationsDto alarmNotificationsOnDevice =
          this.retrieveCurrentAlarmNotifications(conn, alarmFilterValue);

      LOGGER.info(
          "Alarm Filter on device before setting notifications: {}", alarmNotificationsOnDevice);

      final long alarmFilterLongValueOnDevice =
          this.alarmFilterLongValue(alarmNotificationsOnDevice);
      final long updatedAlarmFilterLongValue =
          this.calculateAlarmFilterLongValue(alarmNotificationsOnDevice, alarmNotifications);

      if (alarmFilterLongValueOnDevice == updatedAlarmFilterLongValue) {
        return AccessResultCode.SUCCESS;
      }

      LOGGER.info("Modified Alarm Filter long value for device: {}", updatedAlarmFilterLongValue);

      return this.writeUpdatedAlarmNotifications(
          conn, updatedAlarmFilterLongValue, alarmFilterValue);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }

  private AlarmNotificationsDto retrieveCurrentAlarmNotifications(
      final DlmsConnectionManager conn, final AttributeAddress alarmFilterValue)
      throws IOException, ProtocolAdapterException {
    conn.getDlmsMessageListener()
        .setDescription(
            "SetAlarmNotifications retrieve current value, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(alarmFilterValue));

    LOGGER.info(
        "Retrieving current alarm filter by issuing get request for for address: {}",
        alarmFilterValue);
    final GetResult getResult = conn.getConnection().get(alarmFilterValue);

    if (getResult == null) {
      throw new ProtocolAdapterException(
          "No GetResult received while retrieving current alarm filter.");
    }

    return this.alarmNotifications(getResult.getResultData());
  }

  private AccessResultCode writeUpdatedAlarmNotifications(
      final DlmsConnectionManager conn,
      final long alarmFilterLongValue,
      final AttributeAddress alarmFilterValue)
      throws IOException {
    final DataObject value = DataObject.newUInteger32Data(alarmFilterLongValue);

    final SetParameter setParameter = new SetParameter(alarmFilterValue, value);

    conn.getDlmsMessageListener()
        .setDescription(
            "SetAlarmNotifications write updated value "
                + alarmFilterLongValue
                + ", set attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(alarmFilterValue));

    return conn.getConnection().set(setParameter);
  }

  private AlarmNotificationsDto alarmNotifications(final DataObject alarmFilter)
      throws ProtocolAdapterException {

    if (alarmFilter == null) {
      throw new ProtocolAdapterException("DataObject expected to contain an alarm filter is null.");
    }

    if (!alarmFilter.isNumber()) {
      throw new ProtocolAdapterException(
          "DataObject isNumber is expected to be true for alarm notifications.");
    }

    if (!(alarmFilter.getValue() instanceof Number)) {
      throw new ProtocolAdapterException(
          "Value in DataObject is not a java.lang.Number: "
              + alarmFilter.getValue().getClass().getName());
    }

    final Number alarmFilterValue = alarmFilter.getValue();
    return this.alarmNotifications(alarmFilterValue.longValue());
  }

  private long calculateAlarmFilterLongValue(
      final AlarmNotificationsDto alarmNotificationsOnDevice,
      final AlarmNotificationsDto alarmNotificationsToSet) {

    /*
     * Create a new (modifiable) set of alarm notifications, based on the
     * notifications to set.
     *
     * Next, add all notifications on the device. These will only really be
     * added to the new set of notifications if it did not contain a
     * notification for the alarm type for which the notification is added.
     *
     * This works because of the specification of addAll for the set,
     * claiming elements will only be added if not already present, and the
     * definition of equals on the AlarmNotification, ensuring only a single
     * setting per AlarmType.
     */

    final Set<AlarmNotificationDto> notificationsToSet =
        new TreeSet<>(alarmNotificationsToSet.getAlarmNotificationsSet());

    notificationsToSet.addAll(alarmNotificationsOnDevice.getAlarmNotificationsSet());

    return this.alarmFilterLongValue(new AlarmNotificationsDto(notificationsToSet));
  }

  private AlarmNotificationsDto alarmNotifications(final long alarmFilterLongValue) {

    final BitSet bitSet = BitSet.valueOf(new long[] {alarmFilterLongValue});
    final Set<AlarmNotificationDto> notifications = new TreeSet<>();

    final AlarmTypeDto[] alarmTypes = AlarmTypeDto.values();
    for (final AlarmTypeDto alarmType : alarmTypes) {
      final boolean enabled =
          bitSet.get(this.alarmHelperService.getAlarmRegisterBitIndexPerAlarmType().get(alarmType));
      notifications.add(new AlarmNotificationDto(alarmType, enabled));
    }

    return new AlarmNotificationsDto(notifications);
  }

  private long alarmFilterLongValue(final AlarmNotificationsDto alarmNotifications) {

    final BitSet bitSet = new BitSet(NUMBER_OF_BITS_IN_ALARM_FILTER);
    for (final AlarmNotificationDto alarmNotification :
        alarmNotifications.getAlarmNotificationsSet()) {
      bitSet.set(
          this.alarmHelperService
              .getAlarmRegisterBitIndexPerAlarmType()
              .get(alarmNotification.getAlarmType()),
          alarmNotification.isEnabled());
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
