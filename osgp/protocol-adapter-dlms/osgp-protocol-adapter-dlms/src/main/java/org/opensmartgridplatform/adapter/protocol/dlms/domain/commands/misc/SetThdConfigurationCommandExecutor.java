// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FindEventsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetThdConfigurationRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ThdConfigurationDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component()
public class SetThdConfigurationCommandExecutor
    extends AbstractCommandExecutor<SetThdConfigurationRequestDto, AccessResultCode> {

  private final DlmsHelper dlmsHelper;
  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  @Autowired
  public SetThdConfigurationCommandExecutor(
      final DlmsHelper dlmsHelper, final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(FindEventsRequestDto.class);
    this.dlmsHelper = dlmsHelper;
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode resultCode)
      throws ProtocolAdapterException {
    return new ActionResponseDto("THD Configuration was successful");
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetThdConfigurationRequestDto setThdConfigRequest,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final Protocol protocol = Protocol.forDevice(device);

    final AttributeAddress addressThreshold =
        this.getAttributeAddress(DlmsObjectType.THD_VALUE_THRESHOLD, protocol);
    final AttributeAddress addressHysteresis =
        this.getAttributeAddress(DlmsObjectType.THD_VALUE_HYSTERESIS, protocol);
    final AttributeAddress addressMinDurationNormalToOver =
        this.getAttributeAddress(DlmsObjectType.THD_MIN_DURATION_NORMAL_TO_OVER, protocol);
    final AttributeAddress addressMinDurationOverToNormal =
        this.getAttributeAddress(DlmsObjectType.THD_MIN_DURATION_OVER_TO_NORMAL, protocol);
    final AttributeAddress addressTimeThreshold =
        this.getAttributeAddress(DlmsObjectType.THD_TIME_THRESHOLD, protocol);

    conn.getDlmsMessageListener()
        .setDescription(
            "THD configuration, set attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(
                    addressThreshold,
                    addressHysteresis,
                    addressMinDurationNormalToOver,
                    addressMinDurationOverToNormal,
                    addressTimeThreshold));

    final ThdConfigurationDto thdConfig = setThdConfigRequest.getThdConfiguration();

    final SetParameter setParamThreshold =
        this.getSetParameterLong(addressThreshold, (int) thdConfig.getThdValueThreshold());
    final SetParameter setParamHysteresis =
        this.getSetParameterLong(addressHysteresis, (int) thdConfig.getThdValueHysteresis());
    final SetParameter setParamMinDurationNormalToOver =
        this.getSetParameterDoubleLong(
            addressMinDurationNormalToOver, thdConfig.getThdMinDurationNormalToOver());
    final SetParameter setParamMinDurationOverToNormal =
        this.getSetParameterDoubleLong(
            addressMinDurationOverToNormal, thdConfig.getThdMinDurationOverToNormal());
    final SetParameter setParamTimeThreshold =
        this.getSetParameterDoubleLong(addressTimeThreshold, thdConfig.getThdTimeThreshold());

    final List<AccessResultCode> resultCodes =
        this.dlmsHelper.setWithList(
            conn,
            device,
            List.of(
                setParamThreshold,
                setParamHysteresis,
                setParamMinDurationNormalToOver,
                setParamMinDurationOverToNormal,
                setParamTimeThreshold));

    if (resultCodes.isEmpty()) {
      throw new ProtocolAdapterException("No resultCodes received while configuring THD");
    }

    if (!resultCodes.stream().allMatch(code -> code.equals(AccessResultCode.SUCCESS))) {
      log.debug("Result of THD configuration is {}", resultCodes);
      throw new ProtocolAdapterException("THD configuration resulted in: " + resultCodes);
    } else {
      return AccessResultCode.SUCCESS;
    }
  }

  private AttributeAddress getAttributeAddress(
      final DlmsObjectType dlmsObjectType, final Protocol protocol)
      throws NotSupportedByProtocolException {
    return this.objectConfigServiceHelper
        .findOptionalDefaultAttributeAddress(protocol, dlmsObjectType)
        .orElseThrow(
            () ->
                new NotSupportedByProtocolException(
                    String.format(
                        "No address found for %s in protocol %s %s",
                        dlmsObjectType.name(), protocol.getName(), protocol.getVersion())));
  }

  private SetParameter getSetParameterLong(final AttributeAddress address, final int value) {
    // long-unsigned ==> unsigned16
    final DataObject dataObject = DataObject.newUInteger16Data(value);
    return new SetParameter(address, dataObject);
  }

  private SetParameter getSetParameterDoubleLong(final AttributeAddress address, final long value) {
    // double-long-unsigned ==> unsigned32
    final DataObject dataObject = DataObject.newUInteger32Data(value);
    return new SetParameter(address, dataObject);
  }
}
