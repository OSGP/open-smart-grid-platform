/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware;

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
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute;
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

  private static final int CLASS_ID = InterfaceClass.EXTENDED_REGISTER.id();
  private static final int ATTRIBUTE_ID = ExtendedRegisterAttribute.VALUE.attributeId();
  public static final String OBIS_CODE_TEMPLATE = "0.%d.24.2.11.255";

  private final DlmsHelper dlmsHelper;

  @Autowired
  public GetFirmwareVersionsGasCommandExecutor(final DlmsHelper dlmsHelper) {
    super(GetFirmwareVersionGasRequestDto.class);
    this.dlmsHelper = dlmsHelper;
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

    if (!Protocol.forDevice(device).isSmr5()) {
      throw new NotSupportedByProtocolException("Simple Version Info not supported by protocol");
    }

    final AttributeAddress attributeAddress =
        new AttributeAddress(
            CLASS_ID,
            new ObisCode(
                String.format(OBIS_CODE_TEMPLATE, queryDto.getChannel().getChannelNumber())),
            ATTRIBUTE_ID);
    final String versionAsHexString =
        this.getSimpleVersionInfoAsHexString(conn, device, attributeAddress);
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
