/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import org.openmuc.jdlms.MethodResultCode;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.BundleService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security.SetKeyOnGMeterCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GMeterInfoDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestDataDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Executor that sets the M-Bus User key for an M-Bus device on a given channel on a gateway device.
 *
 * <p>This executor delegates meter communication to the {@link SetKeyOnGMeterCommandExecutor} for
 * which the actual M-Bus device (with its M-Bus master key) needs to be known ahead of execution.
 *
 * <p>This is implemented as a command executor in order to be able to link it to a {@link
 * SetMbusUserKeyByChannelRequestDataDto} from a bundle, as there does not appear to be a simple way
 * to use the {@link SetKeyOnGMeterCommandExecutor} from the {@link BundleService} for both the
 * {@link GMeterInfoDto} and the {@link SetMbusUserKeyByChannelRequestDataDto} (where in the latter
 * case the M-Bus device has to be retrieved by the channel and the gateway device, while in the
 * former case it can be looked up by device identification).
 */
@Component()
public class SetMbusUserKeyByChannelCommandExecutor
    extends AbstractCommandExecutor<GMeterInfoDto, MethodResultCode> {

  @Autowired private ConfigurationService configurationService;

  @Autowired private SetKeyOnGMeterCommandExecutor setKeyOnGMeterCommandExecutor;

  public SetMbusUserKeyByChannelCommandExecutor() {
    super(SetMbusUserKeyByChannelRequestDataDto.class);
  }

  @Override
  public ActionResponseDto executeBundleAction(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ActionRequestDto actionRequestDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.checkActionRequestType(actionRequestDto);
    final SetMbusUserKeyByChannelRequestDataDto setMbusUserKeyByChannelRequestData =
        (SetMbusUserKeyByChannelRequestDataDto) actionRequestDto;
    final GMeterInfoDto gMeterInfo =
        this.configurationService.getMbusKeyExchangeData(
            conn, device, setMbusUserKeyByChannelRequestData, messageMetadata);
    final MethodResultCode executionResult =
        this.execute(conn, device, gMeterInfo, messageMetadata);
    return this.asBundleResponse(executionResult);
  }

  @Override
  public ActionResponseDto asBundleResponse(final MethodResultCode executionResult)
      throws ProtocolAdapterException {
    this.checkMethodResultCode(executionResult);
    return new ActionResponseDto("Setting M-Bus User key by channel was successful");
  }

  @Override
  public MethodResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GMeterInfoDto gMeterInfo,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException, FunctionalException {
    return this.setKeyOnGMeterCommandExecutor.execute(conn, device, gMeterInfo, messageMetadata);
  }
}
