// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.PushSetupMapper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupSmsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetPushSetupSmsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TransportServiceTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class SetPushSetupSmsCommandExecutor
    extends SetPushSetupCommandExecutor<PushSetupSmsDto, AccessResultCode> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SetPushSetupSmsCommandExecutor.class);

  private final PushSetupMapper pushSetupMapper;

  public SetPushSetupSmsCommandExecutor(
      final PushSetupMapper pushSetupMapper,
      final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(SetPushSetupSmsRequestDto.class, objectConfigServiceHelper);
    this.pushSetupMapper = pushSetupMapper;
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

    final SetParameter setParameterSendDestinationAndMethod =
        this.getSetParameter(pushSetupSms, device);

    final AccessResultCode resultCode =
        this.doSetRequest(
            "PushSetupSms, Send destination and method",
            conn,
            setParameterSendDestinationAndMethod);

    if (resultCode != null) {
      return resultCode;
    } else {
      throw new ProtocolAdapterException("Error setting Sms push setup data.");
    }
  }

  private SetParameter getSetParameter(final PushSetupSmsDto pushSetupSms, final DlmsDevice device)
      throws ProtocolAdapterException {

    final AttributeAddress sendDestinationAndMethodAddress =
        this.getSendDestinationAndMethodAddress(
            Protocol.forDevice(device), DlmsObjectType.PUSH_SETUP_SMS);

    this.checkPushSetupSms(pushSetupSms);

    final DataObject value =
        this.pushSetupMapper.map(
            this.getUpdatedSendDestinationAndMethod(
                pushSetupSms.getSendDestinationAndMethod(), device),
            DataObject.class);
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

  @Override
  protected TransportServiceTypeDto getTransportServiceType() {
    return TransportServiceTypeDto.TCP;
  }
}
