// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearAlarmRegisterRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClearAlarmRegisterCommandExecutor
    extends AbstractCommandExecutor<ClearAlarmRegisterRequestDto, AccessResultCode> {

  private final ObjectConfigServiceHelper objectConfigServiceHelper;
  private static final int ALARM_CODE_NO_ALARM = 0;
  private static final List<DlmsObjectType> ALARM_REGISTERS =
      List.of(
          DlmsObjectType.ALARM_REGISTER_1,
          DlmsObjectType.ALARM_REGISTER_2,
          DlmsObjectType.ALARM_REGISTER_3);

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

    AccessResultCode previousResult = null;
    for (final DlmsObjectType alarmType : ALARM_REGISTERS) {
      final Optional<AccessResultCode> result =
          this.clearSingleAlarmRegister(conn, device, alarmType);

      if (result.isEmpty()) {
        if (alarmType == DlmsObjectType.ALARM_REGISTER_1) {
          throw new ProtocolAdapterException("Unable to find alarm register 1.");
        }
        return previousResult;
      }
      previousResult = result.get();
      if (result.get() != AccessResultCode.SUCCESS) {
        return result.get();
      }
    }
    return AccessResultCode.SUCCESS;
  }

  private Optional<AccessResultCode> clearSingleAlarmRegister(
      final DlmsConnectionManager conn, final DlmsDevice device, final DlmsObjectType alarmType)
      throws ProtocolAdapterException {

    final Optional<AttributeAddress> optAlarmRegisterAttributeAddress =
        this.objectConfigServiceHelper.findOptionalDefaultAttributeAddress(
            Protocol.forDevice(device), alarmType);

    if (optAlarmRegisterAttributeAddress.isEmpty()) {
      return Optional.empty();
    }

    if (!this.alarmIsPresent(conn, optAlarmRegisterAttributeAddress.get())) {
      return Optional.of(AccessResultCode.SUCCESS);
    }

    final AccessResultCode result =
        this.executeClearForAlarmRegister(conn, optAlarmRegisterAttributeAddress.get());

    if (result != null) {
      return Optional.of(result);
    } else {
      throw new ProtocolAdapterException(
          "Error occurred for clear alarm register: " + alarmType.name());
    }
  }

  private AccessResultCode executeClearForAlarmRegister(
      final DlmsConnectionManager conn, final AttributeAddress alarmRegisterAttributeAddress) {

    log.info(
        "Clear alarm register {}",
        JdlmsObjectToStringUtil.describeAttributes(alarmRegisterAttributeAddress));

    final DataObject data = DataObject.newUInteger32Data(ALARM_CODE_NO_ALARM);
    final SetParameter setParameter = new SetParameter(alarmRegisterAttributeAddress, data);

    conn.getDlmsMessageListener()
        .setDescription(
            "ClearAlarmRegister, with alarm code = "
                + ALARM_CODE_NO_ALARM
                + JdlmsObjectToStringUtil.describeAttributes(alarmRegisterAttributeAddress));

    try {
      return conn.getConnection().set(setParameter);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }

  private boolean alarmIsPresent(
      final DlmsConnectionManager conn, final AttributeAddress alarmRegisterAttributeAddress) {

    conn.getDlmsMessageListener()
        .setDescription(
            "ReadAlarmRegister, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(alarmRegisterAttributeAddress));

    try {
      final GetResult getResult = conn.getConnection().get(alarmRegisterAttributeAddress);
      return getResult != null
          && getResult.getResultCode() == AccessResultCode.SUCCESS
          && ((Number) getResult.getResultData().getValue()).intValue() != ALARM_CODE_NO_ALARM;

    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }
}
