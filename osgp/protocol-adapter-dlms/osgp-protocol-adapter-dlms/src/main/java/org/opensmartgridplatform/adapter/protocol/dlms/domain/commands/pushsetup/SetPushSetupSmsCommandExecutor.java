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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupSmsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetPushSetupSmsRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class SetPushSetupSmsCommandExecutor
    extends SetPushSetupCommandExecutor<PushSetupSmsDto, AccessResultCode> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SetPushSetupSmsCommandExecutor.class);
  private static final ObisCode OBIS_CODE = new ObisCode("0.2.25.9.0.255");

  public SetPushSetupSmsCommandExecutor() {
    super(SetPushSetupSmsRequestDto.class);
  }

  @Override
  public PushSetupSmsDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);
    final SetPushSetupSmsRequestDto setPushSetupSmsRequestDto =
        (SetPushSetupSmsRequestDto) bundleInput;

    return setPushSetupSmsRequestDto.getPushSetupSms();
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {

    this.checkAccessResultCode(executionResult);

    return new ActionResponseDto("Setting push setup SMS was successful");
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final PushSetupSmsDto pushSetupSms,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final SetParameter setParameterSendDestinationAndMethod = this.getSetParameter(pushSetupSms);

    final AccessResultCode resultCode =
        this.doSetRequest(
            "PushSetupSms, Send destination and method",
            conn,
            OBIS_CODE,
            setParameterSendDestinationAndMethod);

    if (resultCode != null) {
      return resultCode;
    } else {
      throw new ProtocolAdapterException("Error setting Sms push setup data.");
    }
  }

  private SetParameter getSetParameter(final PushSetupSmsDto pushSetupSms)
      throws ProtocolAdapterException {

    this.checkPushSetupSms(pushSetupSms);

    final AttributeAddress sendDestinationAndMethodAddress =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD);
    final DataObject value =
        this.buildSendDestinationAndMethodObject(pushSetupSms.getSendDestinationAndMethod());
    return new SetParameter(sendDestinationAndMethodAddress, value);
  }

  private void checkPushSetupSms(final PushSetupSmsDto pushSetupSms)
      throws ProtocolAdapterException {
    if (!pushSetupSms.hasSendDestinationAndMethod()) {
      LOGGER.error("Send Destination and Method of the Push Setup Sms is expected to be set.");
      throw new ProtocolAdapterException(
          "Error setting Sms push setup data. No destination and method data");
    }

    if (pushSetupSms.hasPushObjectList()) {
      LOGGER.warn(
          "Setting Push Object List of Push Setup Sms not implemented: {}",
          pushSetupSms.getPushObjectList());
    }

    if (pushSetupSms.hasCommunicationWindow()) {
      LOGGER.warn(
          "Setting Communication Window of Push Setup Sms not implemented: {}",
          pushSetupSms.getCommunicationWindow());
    }
    if (pushSetupSms.hasRandomisationStartInterval()) {
      LOGGER.warn(
          "Setting Randomisation Start Interval of Push Setup Sms not implemented: {}",
          pushSetupSms.getRandomisationStartInterval());
    }
    if (pushSetupSms.hasNumberOfRetries()) {
      LOGGER.warn(
          "Setting Number of Retries of Push Setup Sms not implemented: {}",
          pushSetupSms.getNumberOfRetries());
    }
    if (pushSetupSms.hasRepetitionDelay()) {
      LOGGER.warn(
          "Setting Repetition Delay of Push Setup Sms not implemented: {}",
          pushSetupSms.getRepetitionDelay());
    }
  }
}
