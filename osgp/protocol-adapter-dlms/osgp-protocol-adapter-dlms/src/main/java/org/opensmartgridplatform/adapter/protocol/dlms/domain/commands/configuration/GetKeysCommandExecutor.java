/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import java.util.Arrays;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.KeyDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SecretTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetKeysCommandExecutor
    extends AbstractCommandExecutor<GetKeysRequestDto, GetKeysResponseDto> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetKeysCommandExecutor.class);

  @Autowired
  public GetKeysCommandExecutor() {
    super(GetKeysRequestDto.class);
  }

  @Override
  public GetKeysRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {
    /*
     * GetKeys is not allowed to run in a bundle
     */
    return null;
  }

  @Override
  public GetKeysResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetKeysRequestDto getKeysRequestDto)
      throws ProtocolAdapterException {

    // TODO: Get keys from Security Management

    return new GetKeysResponseDto(
        Arrays.asList(
            new KeyDto(SecretTypeDto.E_METER_AUTHENTICATION_KEY, new byte[] {10, 11, 12}),
            new KeyDto(SecretTypeDto.G_METER_ENCRYPTION_KEY, new byte[] {20, 21, 22})));
  }
}
