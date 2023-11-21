// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmRegisterResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ReadAlarmRegisterDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ReadAlarmRegisterRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReadAlarmRegisterCommandExecutor
    extends AbstractCommandExecutor<ReadAlarmRegisterRequestDto, AlarmRegisterResponseDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ReadAlarmRegisterCommandExecutor.class);

  final ObjectConfigServiceHelper objectConfigServiceHelper;

  private final AlarmHelperService alarmHelperService;

  public ReadAlarmRegisterCommandExecutor(
      final ObjectConfigServiceHelper objectConfigServiceHelper, final AlarmHelperService alarmHelperService) {
    super(ReadAlarmRegisterDataDto.class);
    this.objectConfigServiceHelper = objectConfigServiceHelper;
    this.alarmHelperService = alarmHelperService;
  }

  @Override
  public ReadAlarmRegisterRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    /*
     * ReadAlarmRegisterDataDto does not have a deviceIdentification. Since
     * the device identification is not used by the executor anyway, it is
     * given the value "not relevant", as long as the XSD for the WS input
     * still specifies a device identification for the non-bundled call.
     */
    return new ReadAlarmRegisterRequestDto("not relevant");
  }

  @Override
  public AlarmRegisterResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ReadAlarmRegisterRequestDto object,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    Set<AlarmTypeDto> alarmList = new HashSet<>();

    final Protocol protocol = Protocol.forDevice(device);

    final Optional<AttributeAddress> alarmRegister1AttributeAddress =
        this.objectConfigServiceHelper.findOptionalDefaultAttributeAddress(
            protocol, DlmsObjectType.ALARM_REGISTER_1);

    if (alarmRegister1AttributeAddress.isPresent()) {
      alarmList =
          this.readAlarmRegister(
              conn, alarmRegister1AttributeAddress.get(), DlmsObjectType.ALARM_REGISTER_1);
    }

    final Optional<AttributeAddress> alarmRegister2AttributeAddress =
        this.objectConfigServiceHelper.findOptionalDefaultAttributeAddress(
            protocol, DlmsObjectType.ALARM_REGISTER_2);

    if (alarmRegister2AttributeAddress.isPresent()) {
      alarmList.addAll(
          this.readAlarmRegister(
              conn, alarmRegister2AttributeAddress.get(), DlmsObjectType.ALARM_REGISTER_2));
    }

    return new AlarmRegisterResponseDto(alarmList);
  }

  private Set<AlarmTypeDto> readAlarmRegister(
      final DlmsConnectionManager conn,
      final AttributeAddress alarmRegisterAttributeAddress,
      final DlmsObjectType alarmRegister)
      throws ProtocolAdapterException {

    final GetResult resultAlarmRegister =
        this.executeForAlarmRegister(conn, alarmRegisterAttributeAddress);

    if (resultAlarmRegister == null) {
      throw new ProtocolAdapterException(
          "No GetResult received while retrieving alarm register: " + alarmRegister.name());
    }

    if (resultAlarmRegister.getResultCode() != AccessResultCode.SUCCESS) {
      throw new ProtocolAdapterException(
          "AccessResultCode for retrieving alarm register was not SUCCESS: "
              + resultAlarmRegister.getResultCode());
    }

    return this.convertToAlarmTypes(alarmRegister, resultAlarmRegister);
  }

  private Set<AlarmTypeDto> convertToAlarmTypes(
      final DlmsObjectType dlmsObjectType, final GetResult getResult)
      throws ProtocolAdapterException {
    final DataObject resultData = getResult.getResultData();
    if (resultData != null && resultData.isNumber()) {
      return this.alarmHelperService.toAlarmTypes(
          dlmsObjectType, getResult.getResultData().getValue());
    } else {
      LOGGER.error("Result: {} --> {}", getResult.getResultCode(), getResult.getResultData());
      throw new ProtocolAdapterException("Invalid register value received from the meter.");
    }
  }

  private GetResult executeForAlarmRegister(
      final DlmsConnectionManager conn, final AttributeAddress alarmRegisterAttributeAddress) {

    conn.getDlmsMessageListener()
        .setDescription(
            "ReadAlarmRegister, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(alarmRegisterAttributeAddress));

    try {
      return conn.getConnection().get(alarmRegisterAttributeAddress);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }
}
