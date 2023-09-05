// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearAlarmRegisterRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClearAlarmRegisterCommandExecutor
    extends AbstractCommandExecutor<ClearAlarmRegisterRequestDto, AccessResultCode> {

  final ObjectConfigServiceHelper objectConfigServiceHelper;

  private static final int ALARM_CODE = 0;
  static final int ALARM_REGISTER_ATTRIBUTE_ID = 2;

  public ClearAlarmRegisterCommandExecutor(
      final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(ClearAlarmRegisterRequestDto.class);
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {

    this.checkAccessResultCode(executionResult);

    return new ActionResponseDto("Clear alarm register was successful");
  }

  @Override
  public ClearAlarmRegisterRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    return (ClearAlarmRegisterRequestDto) bundleInput;
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ClearAlarmRegisterRequestDto clearAlarmRegisterRequestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    // Clear alarm 1
    final Optional<AccessResultCode> optionalResultCodeAlarm1 =
        this.clearAlarmRegister(conn, device, DlmsObjectType.ALARM_REGISTER_1);
    if (optionalResultCodeAlarm1.isEmpty()) {
      throw new ProtocolAdapterException("Unable to find alarm register 1.");
    }
    final AccessResultCode resultCodeAlarmRegister1 = optionalResultCodeAlarm1.get();
    if (resultCodeAlarmRegister1 != AccessResultCode.SUCCESS) {
      return resultCodeAlarmRegister1;
    }

    // Clear alarm 2
    final Optional<AccessResultCode> optionalResultCodeAlarm2 =
        this.clearAlarmRegister(conn, device, DlmsObjectType.ALARM_REGISTER_2);
    if (optionalResultCodeAlarm2.isEmpty()) {
      return resultCodeAlarmRegister1;
    }
    final AccessResultCode resultCodeAlarmRegister2 = optionalResultCodeAlarm2.get();
    if (resultCodeAlarmRegister2 != AccessResultCode.SUCCESS) {
      return resultCodeAlarmRegister2;
    }

    // Clear alarm 3
    final Optional<AccessResultCode> optionalResultCodeAlarm3 =
        this.clearAlarmRegister(conn, device, DlmsObjectType.ALARM_REGISTER_3);
    return optionalResultCodeAlarm3.orElse(resultCodeAlarmRegister2);
  }

  private Optional<AccessResultCode> clearAlarmRegister(
      final DlmsConnectionManager conn, final DlmsDevice device, final DlmsObjectType objectType)
      throws ProtocolAdapterException {
    log.debug("clearAlarmRegister {}", objectType);
    final Optional<AttributeAddress> optAlarmRegisterAttributeAddress =
        this.objectConfigServiceHelper.findOptionalAttributeAddress(
            Protocol.forDevice(device), objectType, ALARM_REGISTER_ATTRIBUTE_ID);

    if (optAlarmRegisterAttributeAddress.isEmpty()) {
      return Optional.empty();
    } else {
      final AccessResultCode resultCodeAlarmRegister =
          this.executeForAlarmRegister(conn, optAlarmRegisterAttributeAddress.get());

      if (resultCodeAlarmRegister != null) {
        return Optional.of(resultCodeAlarmRegister);
      } else {
        throw new ProtocolAdapterException(
            "Error occurred for clear alarm register: ." + objectType.name());
      }
    }
  }

  public AccessResultCode executeForAlarmRegister(
      final DlmsConnectionManager conn, final AttributeAddress alarmRegisterAttributeAddress) {

    log.info(
        "Clear alarm register {}",
        JdlmsObjectToStringUtil.describeAttributes(alarmRegisterAttributeAddress));

    final DataObject data = DataObject.newUInteger32Data(ALARM_CODE);
    final SetParameter setParameter = new SetParameter(alarmRegisterAttributeAddress, data);

    conn.getDlmsMessageListener()
        .setDescription(
            "ClearAlarmRegister, with alarm code = "
                + ALARM_CODE
                + JdlmsObjectToStringUtil.describeAttributes(alarmRegisterAttributeAddress));

    try {
      return conn.getConnection().set(setParameter);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }
}
