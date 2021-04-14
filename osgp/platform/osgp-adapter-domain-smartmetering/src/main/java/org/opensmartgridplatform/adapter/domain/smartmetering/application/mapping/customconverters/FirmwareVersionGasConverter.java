/**
 * Copyright 2021 Alliander N.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionGasDto;

public class FirmwareVersionGasConverter
    extends CustomConverter<FirmwareVersionGasDto, FirmwareVersion> {

  @Override
  public FirmwareVersion convert(
      final FirmwareVersionGasDto source,
      final Type<? extends FirmwareVersion> destinationType,
      final MappingContext context) {

    if (source == null) {
      return null;
    }

    return new FirmwareVersion(
        org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleType.valueOf(
            source.getFirmwareModuleType().name()),
        source.getVersion());
  }
}
