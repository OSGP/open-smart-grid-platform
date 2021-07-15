/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetPushSetupAlarmRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class SetPushSetupAlarmCommandExecutor
    extends SetPushSetupCommandExecutor<PushSetupAlarmDto, AccessResultCode> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SetPushSetupAlarmCommandExecutor.class);
  private static final ObisCode OBIS_CODE = new ObisCode("0.1.25.9.0.255");

  public SetPushSetupAlarmCommandExecutor() {
    super(SetPushSetupAlarmRequestDto.class);
  }

  @Override
  public PushSetupAlarmDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);
    final SetPushSetupAlarmRequestDto setPushSetupAlarmRequestDto =
        (SetPushSetupAlarmRequestDto) bundleInput;

    return setPushSetupAlarmRequestDto.getPushSetupAlarm();
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {

    this.checkAccessResultCode(executionResult);

    return new ActionResponseDto("Setting push setup alarm was successful");
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final PushSetupAlarmDto pushSetupAlarm)
      throws ProtocolAdapterException {

    final SetParameter setParameterSendDestinationAndMethod = this.getSetParameter(pushSetupAlarm);

    final AccessResultCode resultCode =
        this.getAccessResultSetSendDestinationAndMethod(
            "PushSetupAlarm", conn, OBIS_CODE, setParameterSendDestinationAndMethod);

    if (resultCode != null) {
      return resultCode;
    } else {
      throw new ProtocolAdapterException("Error setting Alarm push setup data.");
    }
  }

  private SetParameter getSetParameter(final PushSetupAlarmDto pushSetupAlarm)
      throws ProtocolAdapterException {

    this.checkPushSetupAlarm(pushSetupAlarm);

    final AttributeAddress sendDestinationAndMethodAddress =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD);
    final DataObject value =
        this.buildSendDestinationAndMethodObject(pushSetupAlarm.getSendDestinationAndMethod());
    return new SetParameter(sendDestinationAndMethodAddress, value);
  }

  private void checkPushSetupAlarm(final PushSetupAlarmDto pushSetupAlarm)
      throws ProtocolAdapterException {
    if (!pushSetupAlarm.hasSendDestinationAndMethod()) {
      LOGGER.error("Send Destination and Method of the Push Setup Alarm is expected to be set.");
      throw new ProtocolAdapterException(
          "Error setting Alarm push setup data. No destination and method data");
    }

    if (pushSetupAlarm.hasPushObjectList()) {
      LOGGER.warn(
          "Setting Push Object List of Push Setup Alarm not implemented: {}",
          pushSetupAlarm.getPushObjectList());
    }

    if (pushSetupAlarm.hasCommunicationWindow()) {
      LOGGER.warn(
          "Setting Communication Window of Push Setup Alarm not implemented: {}",
          pushSetupAlarm.getCommunicationWindow());
    }
    if (pushSetupAlarm.hasRandomisationStartInterval()) {
      LOGGER.warn(
          "Setting Randomisation Start Interval of Push Setup Alarm not implemented: {}",
          pushSetupAlarm.getRandomisationStartInterval());
    }
    if (pushSetupAlarm.hasNumberOfRetries()) {
      LOGGER.warn(
          "Setting Number of Retries of Push Setup Alarm not implemented: {}",
          pushSetupAlarm.getNumberOfRetries());
    }
    if (pushSetupAlarm.hasRepetitionDelay()) {
      LOGGER.warn(
          "Setting Repetition Delay of Push Setup Alarm not implemented: {}",
          pushSetupAlarm.getRepetitionDelay());
    }
  }
}
