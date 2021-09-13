/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
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
import org.opensmartgridplatform.dlms.interfaceclass.method.MBusClientMethod;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearMBusStatusOnAllChannelsRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClearMBusStatusOnAllChannelsCommandExecutor
    extends AbstractCommandExecutor<ClearMBusStatusOnAllChannelsRequestDto, AccessResultCode> {

  private static final int[] CHANNELS = {1, 2, 3, 4};

  final DlmsObjectConfigService dlmsObjectConfigService;

  @Autowired
  public ClearMBusStatusOnAllChannelsCommandExecutor(
      final DlmsObjectConfigService dlmsObjectConfigService) {
    super(ClearMBusStatusOnAllChannelsRequestDto.class);
    this.dlmsObjectConfigService = dlmsObjectConfigService;
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {

    this.checkAccessResultCode(executionResult);

    return new ActionResponseDto("Clear M-Bus status on all channels was successful");
  }

  @Override
  public ClearMBusStatusOnAllChannelsRequestDto fromBundleRequestInput(
      final ActionRequestDto bundleInput) throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    return (ClearMBusStatusOnAllChannelsRequestDto) bundleInput;
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ClearMBusStatusOnAllChannelsRequestDto requestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    try {
      for (final int channel : CHANNELS) {
        this.clearStatusMaskForChannel(conn, channel, device);
      }
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }

    return AccessResultCode.SUCCESS;
  }

  private void clearStatusMaskForChannel(
      final DlmsConnectionManager conn, final int channel, final DlmsDevice device)
      throws IOException, ProtocolAdapterException {

    final AttributeAddress readMBusStatusAttributeAddress =
        this.dlmsObjectConfigService.getAttributeAddress(
            device, DlmsObjectType.READ_MBUS_STATUS, channel);
    final AttributeAddress clearMBusStatusAttributeAddress =
        this.dlmsObjectConfigService.getAttributeAddress(
            device, DlmsObjectType.CLEAR_MBUS_STATUS, channel);
    final AttributeAddress clientSetupMbus =
        this.dlmsObjectConfigService.getAttributeAddress(
            device, DlmsObjectType.CLIENT_SETUP_MBUS, channel);

    final long statusMask = this.readStatus(conn, channel, readMBusStatusAttributeAddress);

    final AccessResultCode resultCode =
        this.setClearStatusMask(statusMask, conn, channel, clearMBusStatusAttributeAddress);

    if (resultCode != AccessResultCode.SUCCESS) {
      throw new ProtocolAdapterException(
          "Unable to set clear status mask for M-Bus channel "
              + channel
              + ", AccessResultCode="
              + resultCode
              + ".");
    }

    final MethodResult methodResult = this.resetAlarm(conn, channel, clientSetupMbus);

    if (methodResult.getResultCode() != MethodResultCode.SUCCESS) {
      throw new ProtocolAdapterException(
          "Call for RESET_ALARM was unsuccessful for M-Bus channel "
              + channel
              + ", MethodResultCode="
              + methodResult.getResultCode()
              + ".");
    }
  }

  private long readStatus(
      final DlmsConnectionManager conn,
      final Integer channel,
      final AttributeAddress attributeAddress)
      throws IOException, ProtocolAdapterException {

    conn.getDlmsMessageListener()
        .setDescription(
            "ClearMBusStatusOnAllChannels-readStatus for channel"
                + channel
                + " - read status"
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddress));

    log.info(
        "Reading status for M-Bus channel {} with attributeAddress: {}.",
        channel,
        attributeAddress);

    final GetResult result = conn.getConnection().get(attributeAddress);

    if (result == null) {
      throw new ProtocolAdapterException(
          "No GetResult received while reading status for M-Bus channel " + channel + ".");
    }

    return this.parseStatusFilter(result.getResultData(), channel);
  }

  private AccessResultCode setClearStatusMask(
      final long statusMask,
      final DlmsConnectionManager conn,
      final Integer channel,
      final AttributeAddress attributeAddress)
      throws IOException {

    final SetParameter parameter =
        new SetParameter(attributeAddress, DataObject.newUInteger32Data(statusMask));

    conn.getDlmsMessageListener()
        .setDescription(
            "ClearMBusStatusOnAllChannels-setClearStatusMask for channel"
                + channel
                + " - writing status mask "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddress));

    log.info(
        "Writing clear status mask {} for M-Bus channel {} with attributeAddress: {}.",
        statusMask,
        channel,
        attributeAddress);
    return conn.getConnection().set(parameter);
  }

  private MethodResult resetAlarm(
      final DlmsConnectionManager conn, final int channel, final AttributeAddress clientSetupMbus)
      throws IOException {
    final MBusClientMethod method = MBusClientMethod.RESET_ALARM;
    final MethodParameter methodParameter =
        new MethodParameter(
            method.getInterfaceClass().id(),
            clientSetupMbus.getInstanceId(),
            method.getMethodId(),
            DataObject.newInteger8Data((byte) 0));

    conn.getDlmsMessageListener()
        .setDescription(
            "ClearMBusStatusOnAllChannels-resetAlarm for channel"
                + channel
                + " - calling client setup: "
                + JdlmsObjectToStringUtil.describeMethod(methodParameter));

    log.info(
        "Calling method RESET_ALARM for channel {} with methodParam: {}.",
        channel,
        methodParameter);
    return conn.getConnection().action(methodParameter);
  }

  private Long parseStatusFilter(final DataObject statusMask, final int channel)
      throws ProtocolAdapterException {

    if (statusMask == null) {
      throw new ProtocolAdapterException(
          "DataObject expected to contain a status mask for M-Bus channel "
              + channel
              + " is null.");
    }

    if (!statusMask.isNumber()) {
      throw new ProtocolAdapterException(
          "DataObject isNumber is expected to be true for status mask of M-Bus channel "
              + channel
              + ".");
    }

    if (!(statusMask.getValue() instanceof Number)) {
      throw new ProtocolAdapterException(
          "Value in DataObject is not a java.lang.Number: "
              + statusMask.getValue().getClass().getName()
              + " in M-Bus channel "
              + channel
              + ".");
    }

    final Number maskValue = statusMask.getValue();
    return maskValue.longValue();
  }
}
