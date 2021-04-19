/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import java.util.List;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.stereotype.Component;

@Component
public class ProtocolServiceLookup {

  private final List<ProtocolService> protocolServices;

  public ProtocolServiceLookup(final List<ProtocolService> protocolServices) {
    this.protocolServices = protocolServices;
  }

  public GetConfigurationObjectService lookupGetService(final Protocol protocol)
      throws ProtocolAdapterException {
    return this.lookupProtocolService(GetConfigurationObjectService.class, protocol);
  }

  public SetConfigurationObjectService lookupSetService(final Protocol protocol)
      throws ProtocolAdapterException {
    return this.lookupProtocolService(SetConfigurationObjectService.class, protocol);
  }

  private <T extends ProtocolService> T lookupProtocolService(
      final Class<T> type, final Protocol protocol) throws ProtocolAdapterException {
    return this.protocolServices.stream()
        .filter(type::isInstance)
        .map(type::cast)
        .filter(s -> s.handles(protocol))
        .findAny()
        .orElseThrow(
            () ->
                new ProtocolAdapterException(
                    String.format("Cannot find %s for protocol %s", type, protocol)));
  }
}
