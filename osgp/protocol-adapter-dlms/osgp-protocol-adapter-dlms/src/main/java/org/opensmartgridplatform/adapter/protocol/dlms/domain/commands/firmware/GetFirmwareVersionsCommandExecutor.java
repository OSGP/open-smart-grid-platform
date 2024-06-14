// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware;

import static java.util.Arrays.asList;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_FIRMWARE_IDENTIFIER;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.COMMUNICATION_MODULE_ACTIVE_FIRMWARE_IDENTIFIER;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_DRIVER_ACTIVE_FIRMWARE_IDENTIFIER;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MODULE_ACTIVE_FIRMWARE_IDENTIFIER;

import java.util.ArrayList;
import java.util.List;
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
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FirmwareVersionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetFirmwareVersionQueryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetFirmwareVersionRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetFirmwareVersionsCommandExecutor
    extends AbstractCommandExecutor<GetFirmwareVersionQueryDto, List<FirmwareVersionDto>> {

  private static final List<FirmwareModuleType> FIRMWARE_MODULE_TYPES =
      asList(
          FirmwareModuleType.ACTIVE_FIRMWARE,
          FirmwareModuleType.MODULE_ACTIVE,
          FirmwareModuleType.COMMUNICATION,
          FirmwareModuleType.M_BUS_DRIVER_ACTIVE);

  private final DlmsHelper dlmsHelper;
  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  @Autowired
  public GetFirmwareVersionsCommandExecutor(
      final DlmsHelper dlmsHelper, final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(GetFirmwareVersionRequestDto.class);
    this.dlmsHelper = dlmsHelper;
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  @Override
  public GetFirmwareVersionQueryDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {
    this.checkActionRequestType(bundleInput);
    return new GetFirmwareVersionQueryDto();
  }

  @Override
  public ActionResponseDto asBundleResponse(final List<FirmwareVersionDto> executionResult)
      throws ProtocolAdapterException {
    return new FirmwareVersionResponseDto(executionResult);
  }

  @Override
  public List<FirmwareVersionDto> execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetFirmwareVersionQueryDto queryDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    if (queryDto != null && queryDto.isMbusQuery()) {
      throw new IllegalArgumentException(
          "GetFirmwareVersion called with query object for Gas meter.");
    }

    final List<AttributeAddress> attributeAddresses =
        this.objectConfigServiceHelper.findDefaultAttributeAddressesIgnoringMissingTypes(
            Protocol.forDevice(device),
            List.of(
                ACTIVE_FIRMWARE_IDENTIFIER,
                MODULE_ACTIVE_FIRMWARE_IDENTIFIER,
                COMMUNICATION_MODULE_ACTIVE_FIRMWARE_IDENTIFIER,
                MBUS_DRIVER_ACTIVE_FIRMWARE_IDENTIFIER));

    if (attributeAddresses.isEmpty()) {
      throw new NotSupportedByProtocolException(
          String.format(
              "No address found for firmware version in protocol %s",
              Protocol.forDevice(device).getName()));
    }

    return this.getFirmwareVersions(
        conn, device, attributeAddresses.toArray(AttributeAddress[]::new));
  }

  private List<FirmwareVersionDto> getFirmwareVersions(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final AttributeAddress[] attributes)
      throws ProtocolAdapterException {
    conn.getDlmsMessageListener()
        .setDescription(
            "GetFirmwareVersions, retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(attributes));

    final List<GetResult> results =
        this.dlmsHelper.getAndCheck(conn, device, "retrieve firmware versions", attributes);

    final List<FirmwareVersionDto> firmwareVersionDtos = new ArrayList<>();
    for (int i = 0; i < attributes.length; i++) {
      final FirmwareModuleType firmwareModuleType = FIRMWARE_MODULE_TYPES.get(i);
      final String description = firmwareModuleType.getDescription();
      final String version =
          this.dlmsHelper.readString(results.get(i).getResultData(), description);
      firmwareVersionDtos.add(new FirmwareVersionDto(firmwareModuleType, version));
    }
    return firmwareVersionDtos;
  }
}
