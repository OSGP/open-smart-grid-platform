/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.microgrids.application.mapping;

import java.util.List;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.microgrids.valueobjects.SetDataRequest;
import org.opensmartgridplatform.domain.microgrids.valueobjects.SetDataSystemIdentifier;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetDataRequestDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetDataSystemIdentifierDto;

public class SetDataRequestConverter
    extends BidirectionalConverter<SetDataRequest, SetDataRequestDto> {

  @Override
  public SetDataRequestDto convertTo(
      final SetDataRequest source,
      final Type<SetDataRequestDto> destinationType,
      final MappingContext context) {
    final List<SetDataSystemIdentifierDto> setDataSystemIdentifiers =
        this.mapperFacade.mapAsList(
            source.getSetDataSystemIdentifiers(), SetDataSystemIdentifierDto.class);

    return new SetDataRequestDto(setDataSystemIdentifiers);
  }

  @Override
  public SetDataRequest convertFrom(
      final SetDataRequestDto source,
      final Type<SetDataRequest> destinationType,
      final MappingContext context) {
    final List<SetDataSystemIdentifier> setDataSystemIdentifiers =
        this.mapperFacade.mapAsList(
            source.getSetDataSystemIdentifiers(), SetDataSystemIdentifier.class);

    return new SetDataRequest(setDataSystemIdentifiers);
  }
}
