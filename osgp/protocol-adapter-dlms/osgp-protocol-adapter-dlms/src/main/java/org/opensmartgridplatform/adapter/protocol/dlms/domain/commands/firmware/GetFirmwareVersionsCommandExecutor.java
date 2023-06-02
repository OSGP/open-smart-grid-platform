//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
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

  private static final int CLASS_ID = InterfaceClass.DATA.id();
  private static final int ATTRIBUTE_ID = DataAttribute.VALUE.attributeId();

  private static final ObisCode OBIS_CODE_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.0.0.2.0.255");
  private static final ObisCode OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION =
      new ObisCode("1.1.0.2.0.255");
  private static final ObisCode OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION =
      new ObisCode("1.2.0.2.0.255");
  private static final ObisCode OBIS_CODE_MBUS_DRIVER_ACTIVE_FIRMWARE_VERSION =
      new ObisCode("1.4.0.2.0.255");

  private static final List<FirmwareModuleType> FIRMWARE_MODULE_TYPES =
      asList(
          FirmwareModuleType.ACTIVE_FIRMWARE,
          FirmwareModuleType.MODULE_ACTIVE,
          FirmwareModuleType.COMMUNICATION,
          FirmwareModuleType.M_BUS_DRIVER_ACTIVE);

  private static final List<AttributeAddress> ALL_ATTRIBUTE_ADDRESSES =
      asList(
          new AttributeAddress(CLASS_ID, OBIS_CODE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
          new AttributeAddress(CLASS_ID, OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
          new AttributeAddress(
              CLASS_ID, OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
          new AttributeAddress(
              CLASS_ID, OBIS_CODE_MBUS_DRIVER_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID));

  private static final AttributeAddress[] FOR_DSMR_4_2_2 =
      ALL_ATTRIBUTE_ADDRESSES.subList(0, 3).toArray(new AttributeAddress[3]);
  private static final AttributeAddress[] FOR_SMR_5 =
      ALL_ATTRIBUTE_ADDRESSES.subList(0, 4).toArray(new AttributeAddress[4]);

  private final DlmsHelper dlmsHelper;

  @Autowired
  public GetFirmwareVersionsCommandExecutor(final DlmsHelper dlmsHelper) {
    super(GetFirmwareVersionRequestDto.class);
    this.dlmsHelper = dlmsHelper;
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

    if (Protocol.forDevice(device).isSmr5()) {
      return this.getFirmwareVersions(conn, device, FOR_SMR_5);
    }
    return this.getFirmwareVersions(conn, device, FOR_DSMR_4_2_2);
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
