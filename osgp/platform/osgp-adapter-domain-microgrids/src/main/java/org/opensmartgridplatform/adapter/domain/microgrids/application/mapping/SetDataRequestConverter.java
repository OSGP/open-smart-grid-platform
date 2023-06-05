// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
