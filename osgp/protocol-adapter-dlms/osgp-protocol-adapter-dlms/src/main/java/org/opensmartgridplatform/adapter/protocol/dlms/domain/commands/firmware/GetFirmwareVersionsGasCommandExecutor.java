// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware;

import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_DEVICE_CONFIG_SIMPLE_VERSION_INFO;

import java.util.List;
import java.util.Optional;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionGasDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FirmwareVersionGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetFirmwareVersionGasRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetFirmwareVersionQueryDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetFirmwareVersionsGasCommandExecutor
    extends AbstractCommandExecutor<GetFirmwareVersionQueryDto, FirmwareVersionGasDto> {

  private final DlmsHelper dlmsHelper;
  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  @Autowired
  public GetFirmwareVersionsGasCommandExecutor(
      final DlmsHelper dlmsHelper, final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(GetFirmwareVersionGasRequestDto.class);
    this.dlmsHelper = dlmsHelper;
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  @Override
  public GetFirmwareVersionQueryDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    final GetFirmwareVersionGasRequestDto getFirmwareVersionGasRequestDto =
        (GetFirmwareVersionGasRequestDto) bundleInput;
    return new GetFirmwareVersionQueryDto(
        getFirmwareVersionGasRequestDto.getChannel(),
        getFirmwareVersionGasRequestDto.getMbusDeviceIdentification());
  }

  @Override
  public ActionResponseDto asBundleResponse(final FirmwareVersionGasDto executionResult)
      throws ProtocolAdapterException {
    return new FirmwareVersionGasResponseDto(executionResult);
  }

  @Override
  public FirmwareVersionGasDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetFirmwareVersionQueryDto queryDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException, FunctionalException {

    if (queryDto == null || !queryDto.isMbusQuery()) {
      throw new IllegalArgumentException(
          "GetFirmwareVersion called without query object for Gas meter.");
    }

    final Optional<AttributeAddress> optionalAttributeAddress =
        this.objectConfigServiceHelper.findOptionalDefaultAttributeAddress(
            Protocol.forDevice(device),
            MBUS_DEVICE_CONFIG_SIMPLE_VERSION_INFO,
            queryDto.getChannel().getChannelNumber());

    if (optionalAttributeAddress.isEmpty()) {
      throw new NotSupportedByProtocolException("Simple Version Info not supported by protocol");
    }

    final String versionAsHexString =
        this.getSimpleVersionInfoAsHexString(conn, device, optionalAttributeAddress.get());
    return new FirmwareVersionGasDto(
        FirmwareModuleType.SIMPLE_VERSION_INFO,
        versionAsHexString,
        queryDto.getMbusDeviceIdentification());
  }

  private String getSimpleVersionInfoAsHexString(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final AttributeAddress attributeAddress)
      throws ProtocolAdapterException {

    conn.getDlmsMessageListener()
        .setDescription(
            "GetFirmwareVersionGas, retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddress));

    final List<GetResult> results =
        this.dlmsHelper.getAndCheck(conn, device, "retrieve firmware versions", attributeAddress);

    return this.dlmsHelper.readHexString(
        results.get(0).getResultData(), FirmwareModuleType.SIMPLE_VERSION_INFO.getDescription());
  }
}
