/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetPushSetupUdpRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TransportServiceTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetPushSetupUdpCommandExecutor
    extends SetPushSetupCommandExecutor<SetPushSetupUdpRequestDto, AccessResultCode> {

  public SetPushSetupUdpCommandExecutor(final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(SetPushSetupUdpRequestDto.class, objectConfigServiceHelper);
  }

  @Override
  public SetPushSetupUdpRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    return (SetPushSetupUdpRequestDto) bundleInput;
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {

    this.checkAccessResultCode(executionResult);

    return new ActionResponseDto("Setting push setup UDP was successful");
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetPushSetupUdpRequestDto pushSetupSms,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final SetParameter setParameterCommunicationWindow =
        this.getSetParameterCommunicationWindow(device);

    final AccessResultCode resultCode =
        this.doSetRequest(
            "PushSetupUdp, communication window", conn, setParameterCommunicationWindow);

    if (resultCode != null) {
      return resultCode;
    } else {
      throw new ProtocolAdapterException("Error setting push setup udp.");
    }
  }

  private SetParameter getSetParameterCommunicationWindow(final DlmsDevice device)
      throws ProtocolAdapterException {
    final AttributeAddress communicationWindowAddress =
        this.getCommunicationWindowAddress(
            Protocol.forDevice(device), DlmsObjectType.PUSH_SETUP_UDP);
    final DataObject value = DataObject.newArrayData(Collections.emptyList());
    return new SetParameter(communicationWindowAddress, value);
  }

  @Override
  protected TransportServiceTypeDto getTransportServiceType() {
    return TransportServiceTypeDto.TCP;
  }
}
