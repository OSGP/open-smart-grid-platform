/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import java.util.List;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetPushSetupAlarmRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class SetPushSetupAlarmCommandExecutor
    extends SetPushSetupCommandExecutor<PushSetupAlarmDto, AccessResultCode> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SetPushSetupAlarmCommandExecutor.class);
  private static final ObisCode OBIS_CODE = new ObisCode("0.1.25.9.0.255");

  private final DlmsHelper dlmsHelper;

  public SetPushSetupAlarmCommandExecutor(final DlmsHelper dlmsHelper) {
    super(SetPushSetupAlarmRequestDto.class);
    this.dlmsHelper = dlmsHelper;
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
      final PushSetupAlarmDto pushSetupAlarm,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    this.checkPushSetupAlarm(pushSetupAlarm);

    AccessResultCode resultCode = null;

    if (pushSetupAlarm.hasSendDestinationAndMethod()) {
      resultCode = this.setSendDestinationAndMethod(conn, pushSetupAlarm);

      if (resultCode != AccessResultCode.SUCCESS) {
        return resultCode;
      }
    }

    if (pushSetupAlarm.hasPushObjectList()) {
      resultCode = this.setPushObjectList(conn, pushSetupAlarm);

      return resultCode;
    }

    if (resultCode == null) {
      throw new ProtocolAdapterException(
          "SetPushSetupAlarmCommandExecutor called without any valid option set in request.");
    } else {
      return resultCode;
    }
  }

  private AccessResultCode setSendDestinationAndMethod(
      final DlmsConnectionManager conn, final PushSetupAlarmDto pushSetupAlarm)
      throws ProtocolAdapterException {
    final SetParameter setParameterSendDestinationAndMethod =
        this.getSetParameterSendDestinationAndMethod(pushSetupAlarm);
    LOGGER.info(
        "Setting Send destination and method of Push Setup Alarm: {}",
        pushSetupAlarm.getPushObjectList());

    final AccessResultCode resultCode =
        this.doSetRequest(
            "PushSetupAlarm, Send destination and method",
            conn,
            OBIS_CODE,
            setParameterSendDestinationAndMethod);

    if (resultCode != null) {
      return resultCode;
    } else {
      throw new ProtocolAdapterException(
          "Error setting Alarm push setup data (destination and method.");
    }
  }

  private SetParameter getSetParameterSendDestinationAndMethod(
      final PushSetupAlarmDto pushSetupAlarm) {

    final AttributeAddress sendDestinationAndMethodAddress =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD);
    final DataObject value =
        this.buildSendDestinationAndMethodObject(pushSetupAlarm.getSendDestinationAndMethod());

    return new SetParameter(sendDestinationAndMethodAddress, value);
  }

  private AccessResultCode setPushObjectList(
      final DlmsConnectionManager conn, final PushSetupAlarmDto pushSetupAlarm)
      throws ProtocolAdapterException {
    LOGGER.info(
        "Setting Push Object List of Push Setup Alarm: {}", pushSetupAlarm.getPushObjectList());

    // Before setting the push object list, verify if the objects in the list are really present in
    // the meter
    this.verifyPushObjects(pushSetupAlarm.getPushObjectList(), conn);

    final SetParameter setParameterPushObjectList =
        this.getSetParameterPushObjectList(pushSetupAlarm);

    final AccessResultCode resultCode =
        this.doSetRequest(
            "PushSetupAlarm, push object list", conn, OBIS_CODE, setParameterPushObjectList);

    if (resultCode != null) {
      return resultCode;
    } else {
      throw new ProtocolAdapterException("Error setting Alarm push setup data (push object list).");
    }
  }

  private void verifyPushObjects(
      final List<CosemObjectDefinitionDto> pushObjects, final DlmsConnectionManager conn)
      throws ProtocolAdapterException {
    for (final CosemObjectDefinitionDto pushObject : pushObjects) {
      this.verifyPushObject(pushObject, conn);
    }
  }

  private void verifyPushObject(
      final CosemObjectDefinitionDto pushObject, final DlmsConnectionManager conn)
      throws ProtocolAdapterException {
    final int dataIndex = pushObject.getDataIndex();
    if (dataIndex != 0) {
      throw new ProtocolAdapterException(
          "PushObject contains non-zero data index: "
              + dataIndex
              + ". Using data index is not implemented.");
    }

    final ObisCode obisCode = new ObisCode(pushObject.getLogicalName().toByteArray());

    final AttributeAddress attributeAddress =
        new AttributeAddress(pushObject.getClassId(), obisCode, pushObject.getAttributeIndex());

    try {
      this.dlmsHelper.getAttributeValue(conn, attributeAddress);
    } catch (final FunctionalException e) {
      throw new ProtocolAdapterException(
          "Verification of push object failed. Object "
              + obisCode.asHexCodeString()
              + " could not be retrieved using a get request.",
          e);
    }
  }

  private SetParameter getSetParameterPushObjectList(final PushSetupAlarmDto pushSetupAlarm) {

    final AttributeAddress pushObjectListAddress =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_PUSH_OBJECT_LIST);
    final DataObject value = this.buildPushObjectListObject(pushSetupAlarm.getPushObjectList());

    return new SetParameter(pushObjectListAddress, value);
  }

  private void checkPushSetupAlarm(final PushSetupAlarmDto pushSetupAlarm) {
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
