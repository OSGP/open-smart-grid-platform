/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import org.openmuc.jdlms.AccessResultCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.ProtocolServiceLookup;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDataDto;
import org.springframework.stereotype.Component;

@Component
public class SetConfigurationObjectCommandExecutor
    extends AbstractCommandExecutor<ConfigurationObjectDto, AccessResultCode> {

  private final ProtocolServiceLookup protocolServiceLookup;

  public SetConfigurationObjectCommandExecutor(final ProtocolServiceLookup protocolServiceLookup) {
    super(SetConfigurationObjectRequestDataDto.class);
    this.protocolServiceLookup = protocolServiceLookup;
  }

  @Override
  public ConfigurationObjectDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {
    this.checkActionRequestType(bundleInput);
    final SetConfigurationObjectRequestDataDto dto =
        (SetConfigurationObjectRequestDataDto) bundleInput;
    return dto.getConfigurationObject();
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {
    this.checkAccessResultCode(executionResult);
    return new ActionResponseDto("Set configuration object was successful");
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ConfigurationObjectDto configurationToSet)
      throws ProtocolAdapterException {
    final Protocol protocol = Protocol.forDevice(device);
    final GetConfigurationObjectService getService =
        this.protocolServiceLookup.lookupGetService(protocol);
    final ConfigurationObjectDto configurationOnDevice = getService.getConfigurationObject(conn);
    final SetConfigurationObjectService setService =
        this.protocolServiceLookup.lookupSetService(protocol);
    return setService.setConfigurationObject(conn, configurationToSet, configurationOnDevice);
  }
}
