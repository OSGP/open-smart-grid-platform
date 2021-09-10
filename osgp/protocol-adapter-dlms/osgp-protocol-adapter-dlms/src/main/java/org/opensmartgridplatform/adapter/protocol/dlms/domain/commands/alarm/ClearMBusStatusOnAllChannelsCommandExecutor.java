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
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute;
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

  private static final int CLASS_ID_READ_STATUS = InterfaceClass.EXTENDED_REGISTER.id();
  private static final int ATTR_ID_READ_STATUS = ExtendedRegisterAttribute.VALUE.attributeId();

  private static final AttributeAddress ATTR_READ_STATUS_MBUS_1 =
      new AttributeAddress(CLASS_ID_READ_STATUS, "0.1.24.2.6.255", ATTR_ID_READ_STATUS);
  private static final AttributeAddress ATTR_READ_STATUS_MBUS_2 =
      new AttributeAddress(CLASS_ID_READ_STATUS, "0.2.24.2.6.255", ATTR_ID_READ_STATUS);
  private static final AttributeAddress ATTR_READ_STATUS_MBUS_3 =
      new AttributeAddress(CLASS_ID_READ_STATUS, "0.3.24.2.6.255", ATTR_ID_READ_STATUS);
  private static final AttributeAddress ATTR_READ_STATUS_MBUS_4 =
      new AttributeAddress(CLASS_ID_READ_STATUS, "0.4.24.2.6.255", ATTR_ID_READ_STATUS);

  private static final int CLASS_ID_CLEAR_STATUS = InterfaceClass.DATA.id();
  private static final int ATTR_ID_CLEAR_STATUS = DataAttribute.VALUE.attributeId();

  private static final AttributeAddress ATTR_CLEAR_STATUS_MBUS_1 =
      new AttributeAddress(CLASS_ID_CLEAR_STATUS, "0.1.94.31.10.255", ATTR_ID_CLEAR_STATUS);
  private static final AttributeAddress ATTR_CLEAR_STATUS_MBUS_2 =
      new AttributeAddress(CLASS_ID_CLEAR_STATUS, "0.2.94.31.10.255", ATTR_ID_CLEAR_STATUS);
  private static final AttributeAddress ATTR_CLEAR_STATUS_MBUS_3 =
      new AttributeAddress(CLASS_ID_CLEAR_STATUS, "0.3.94.31.10.255", ATTR_ID_CLEAR_STATUS);
  private static final AttributeAddress ATTR_CLEAR_STATUS_MBUS_4 =
      new AttributeAddress(CLASS_ID_CLEAR_STATUS, "0.4.94.31.10.255", ATTR_ID_CLEAR_STATUS);

  private static final ObisCode OBIS_CODE_CLIENT_SETUP_MBUS_1 = new ObisCode("0.1.24.1.0.255");
  private static final ObisCode OBIS_CODE_CLIENT_SETUP_MBUS_2 = new ObisCode("0.2.24.1.0.255");
  private static final ObisCode OBIS_CODE_CLIENT_SETUP_MBUS_3 = new ObisCode("0.3.24.1.0.255");
  private static final ObisCode OBIS_CODE_CLIENT_SETUP_MBUS_4 = new ObisCode("0.4.24.1.0.255");

  private static final Map<Integer, AttributeAddress> ATTR_READ_STATUS_MAP = new HashMap<>();
  private static final Map<Integer, AttributeAddress> ATTR_SET_STATUS_MAP = new HashMap<>();
  private static final Map<Integer, ObisCode> OBIS_CODE_CLIENT_SETUP_MAP = new HashMap<>();

  static {
    ATTR_READ_STATUS_MAP.put(1, ATTR_READ_STATUS_MBUS_1);
    ATTR_READ_STATUS_MAP.put(2, ATTR_READ_STATUS_MBUS_2);
    ATTR_READ_STATUS_MAP.put(3, ATTR_READ_STATUS_MBUS_3);
    ATTR_READ_STATUS_MAP.put(4, ATTR_READ_STATUS_MBUS_4);

    ATTR_SET_STATUS_MAP.put(1, ATTR_CLEAR_STATUS_MBUS_1);
    ATTR_SET_STATUS_MAP.put(2, ATTR_CLEAR_STATUS_MBUS_2);
    ATTR_SET_STATUS_MAP.put(3, ATTR_CLEAR_STATUS_MBUS_3);
    ATTR_SET_STATUS_MAP.put(4, ATTR_CLEAR_STATUS_MBUS_4);

    OBIS_CODE_CLIENT_SETUP_MAP.put(1, OBIS_CODE_CLIENT_SETUP_MBUS_1);
    OBIS_CODE_CLIENT_SETUP_MAP.put(2, OBIS_CODE_CLIENT_SETUP_MBUS_2);
    OBIS_CODE_CLIENT_SETUP_MAP.put(3, OBIS_CODE_CLIENT_SETUP_MBUS_3);
    OBIS_CODE_CLIENT_SETUP_MAP.put(4, OBIS_CODE_CLIENT_SETUP_MBUS_4);
  }

  private static final int[] CHANNELS = {1, 2, 3, 4};

  @Autowired
  public ClearMBusStatusOnAllChannelsCommandExecutor() {
    super(ClearMBusStatusOnAllChannelsRequestDto.class);
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

    if (Protocol.forDevice(device) != Protocol.SMR_5_1) {
      throw new NotSupportedByProtocolException(
          "ClearMBusStatusOnAllChannels not supported by protocol.");
    }

    try {
      for (final int channel : CHANNELS) {
        this.setClearStatusMask(conn, channel);
      }
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }

    return AccessResultCode.SUCCESS;
  }

  private void setClearStatusMask(final DlmsConnectionManager conn, final int channel)
      throws IOException, ProtocolAdapterException {

    final long statusMask = this.readStatus(conn, channel);

    final AccessResultCode resultCode = this.setClearStatusMask(statusMask, conn, channel);

    if (resultCode != AccessResultCode.SUCCESS) {
      throw new ProtocolAdapterException(
          "Unable to set clear status mask for M-Bus channel "
              + channel
              + ", AccessResultCode="
              + resultCode
              + ".");
    }

    final MethodResult methodResult = this.resetAlarm(conn, channel);

    if (methodResult.getResultCode() != MethodResultCode.SUCCESS) {
      throw new ProtocolAdapterException(
          "Call for RESET_ALARM was unsuccessful for M-Bus channel "
              + channel
              + ", MethodResultCode="
              + methodResult.getResultCode()
              + ".");
    }
  }

  private long readStatus(final DlmsConnectionManager conn, final Integer channel)
      throws IOException, ProtocolAdapterException {
    final AttributeAddress attributeAddress = ATTR_READ_STATUS_MAP.get(channel);

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
      final long statusMask, final DlmsConnectionManager conn, final Integer channel)
      throws IOException {
    final AttributeAddress attributeAddress = ATTR_SET_STATUS_MAP.get(channel);
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

  private MethodResult resetAlarm(final DlmsConnectionManager conn, final int channel)
      throws IOException {
    final MBusClientMethod method = MBusClientMethod.RESET_ALARM;
    final MethodParameter methodParameter =
        new MethodParameter(
            method.getInterfaceClass().id(),
            OBIS_CODE_CLIENT_SETUP_MAP.get(channel),
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
