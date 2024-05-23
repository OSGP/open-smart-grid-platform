// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import lombok.extern.slf4j.Slf4j;
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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupLastGaspDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetPushSetupLastGaspRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TransportServiceTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component()
public class SetPushSetupLastGaspCommandExecutor
    extends SetPushSetupCommandExecutor<PushSetupLastGaspDto, AccessResultCode> {

  private final PushSetupMapper pushSetupMapper;

  public SetPushSetupLastGaspCommandExecutor(
      final PushSetupMapper pushSetupMapper,
      final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(SetPushSetupLastGaspRequestDto.class, objectConfigServiceHelper);
    this.pushSetupMapper = pushSetupMapper;
  }

  @Override
  public PushSetupLastGaspDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);
    final SetPushSetupLastGaspRequestDto setPushSetupLastGaspRequestDto =
        (SetPushSetupLastGaspRequestDto) bundleInput;

    return setPushSetupLastGaspRequestDto.getPushSetupLastGasp();
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {

    this.checkAccessResultCode(executionResult);

    return new ActionResponseDto("Setting push setup LastGasp was successful");
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final PushSetupLastGaspDto pushSetupLastGasp,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final SetParameter setParameterSendDestinationAndMethod =
        this.getSetParameter(pushSetupLastGasp, device);

    final AccessResultCode resultCode =
        this.doSetRequest(
            "PushSetupLastGasp, Send destination and method",
            conn,
            setParameterSendDestinationAndMethod);

    if (resultCode != null) {
      return resultCode;
    } else {
      throw new ProtocolAdapterException("Error setting LastGasp push setup data.");
    }
  }

  private SetParameter getSetParameter(
      final PushSetupLastGaspDto pushSetupLastGasp, final DlmsDevice device)
      throws ProtocolAdapterException {

    this.checkPushSetupLastGasp(pushSetupLastGasp);

    final AttributeAddress sendDestinationAndMethodAddress =
        this.getSendDestinationAndMethodAddress(
            Protocol.forDevice(device), DlmsObjectType.PUSH_SETUP_UDP);

    final DataObject value =
        this.pushSetupMapper.map(
            this.getUpdatedSendDestinationAndMethod(
                pushSetupLastGasp.getSendDestinationAndMethod(), device),
            DataObject.class);
    return new SetParameter(sendDestinationAndMethodAddress, value);
  }

  private void checkPushSetupLastGasp(final PushSetupLastGaspDto pushSetupLastGasp)
      throws ProtocolAdapterException {
    if (!pushSetupLastGasp.hasSendDestinationAndMethod()) {
      log.error("Send Destination and Method of the Push Setup LastGasp is expected to be set.");
      throw new ProtocolAdapterException(
          "Error setting LastGasp push setup data. No destination and method data");
    }

    if (pushSetupLastGasp.hasPushObjectList()) {
      log.warn(
          "Setting Push Object List of Push Setup LastGasp not implemented: {}",
          pushSetupLastGasp.getPushObjectList());
    }

    if (pushSetupLastGasp.hasCommunicationWindow()) {
      log.warn(
          "Setting Communication Window of Push Setup LastGasp not implemented: {}",
          pushSetupLastGasp.getCommunicationWindow());
    }
    if (pushSetupLastGasp.hasRandomisationStartInterval()) {
      log.warn(
          "Setting Randomisation Start Interval of Push Setup LastGasp not implemented: {}",
          pushSetupLastGasp.getRandomisationStartInterval());
    }
    if (pushSetupLastGasp.hasNumberOfRetries()) {
      log.warn(
          "Setting Number of Retries of Push Setup LastGasp not implemented: {}",
          pushSetupLastGasp.getNumberOfRetries());
    }
    if (pushSetupLastGasp.hasRepetitionDelay()) {
      log.warn(
          "Setting Repetition Delay of Push Setup LastGasp not implemented: {}",
          pushSetupLastGasp.getRepetitionDelay());
    }
  }

  @Override
  protected TransportServiceTypeDto getTransportServiceType() {
    return TransportServiceTypeDto.UDP;
  }
}
