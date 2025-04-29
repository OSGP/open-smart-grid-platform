// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.AdhocService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.method.MBusClientMethod;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearMBusStatusOnAllChannelsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelShortEquipmentIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ScanMbusChannelsResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClearMBusStatusOnAllChannelsCommandExecutor
    extends AbstractCommandExecutor<ClearMBusStatusOnAllChannelsRequestDto, AccessResultCode> {

  final ObjectConfigServiceHelper config;
  private final AdhocService adhocService;

  public ClearMBusStatusOnAllChannelsCommandExecutor(
      final ObjectConfigServiceHelper objectConfigServiceHelper, final AdhocService adhocService) {
    super(ClearMBusStatusOnAllChannelsRequestDto.class);
    this.config = objectConfigServiceHelper;
    this.adhocService = adhocService;
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
      final ScanMbusChannelsResponseDto scanMbusChannelsResponseDto =
          this.adhocService.scanMbusChannels(conn, device, messageMetadata);

      final List<String> exceptions = new ArrayList<>();

      scanMbusChannelsResponseDto.getChannelShortIds().stream()
          .filter(this::isNotEmptyChannel)
          .map(MbusChannelShortEquipmentIdentifierDto::getChannel)
          .forEach(
              channel -> {
                try {
                  this.clearStatusMaskForChannel(conn, channel, device);
                } catch (final Exception e) {
                  exceptions.add(e.getMessage());
                }
              });

      if (!exceptions.isEmpty()) {
        throw new ProtocolAdapterException(String.join(", ", exceptions));
      }

    } catch (final Exception e) {
      throw new ProtocolAdapterException(e.getMessage());
    }

    return AccessResultCode.SUCCESS;
  }

  private void clearStatusMaskForChannel(
      final DlmsConnectionManager conn, final int channel, final DlmsDevice device)
      throws IOException, ProtocolAdapterException {

    final Protocol protocol = Protocol.forDevice(device);

    final AttributeAddress readMBusStatusAttributeAddress =
        this.config.findDefaultAttributeAddress(
            device, protocol, DlmsObjectType.READ_MBUS_STATUS, channel);

    final AttributeAddress clearMBusStatusAttributeAddress =
        this.config.findDefaultAttributeAddress(
            device, protocol, DlmsObjectType.CLEAR_MBUS_STATUS, channel);

    final AttributeAddress clientSetupMbus =
        this.config.findDefaultAttributeAddress(
            device, protocol, DlmsObjectType.MBUS_CLIENT_SETUP, channel);

    final long statusMask = this.readStatus(conn, channel, readMBusStatusAttributeAddress);
    if (statusMask == 0L) {
      return;
    }

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

    final MethodResult methodResult =
        this.resetAlarm(conn, channel, clientSetupMbus.getInstanceId());

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
      final DlmsConnectionManager conn, final int channel, final ObisCode obisCode)
      throws IOException {
    final MBusClientMethod method = MBusClientMethod.RESET_ALARM;
    final MethodParameter methodParameter =
        new MethodParameter(
            method.getInterfaceClass().id(),
            obisCode,
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

  private boolean isNotEmptyChannel(final MbusChannelShortEquipmentIdentifierDto dto) {
    return !"00000000".equals(dto.getShortId().getIdentificationNumber());
  }
}
