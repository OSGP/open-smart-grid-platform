/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.core.db.api.iec61850.entities.DeviceOutputSetting;
import org.opensmartgridplatform.dto.valueobjects.RelayMapDto;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;

public class DeviceOutputSettingToRelayMapConverter
    extends BidirectionalConverter<DeviceOutputSetting, RelayMapDto> {

  @Override
  public RelayMapDto convertTo(
      final DeviceOutputSetting source,
      final Type<RelayMapDto> destinationType,
      final MappingContext context) {
    final RelayTypeDto relayType = RelayTypeDto.valueOf(source.getRelayType().name());

    return new RelayMapDto(
        source.getExternalId(), source.getInternalId(), relayType, source.getAlias());
  }

  @Override
  public DeviceOutputSetting convertFrom(
      final RelayMapDto source,
      final Type<DeviceOutputSetting> destinationType,
      final MappingContext context) {

    final org.opensmartgridplatform.core.db.api.iec61850valueobjects.RelayType relayType =
        org.opensmartgridplatform.core.db.api.iec61850valueobjects.RelayType.valueOf(
            source.getRelayType().name());

    return new DeviceOutputSetting(
        source.getAddress(), source.getIndex(), relayType, source.getAlias());
  }
}
