//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SeasonProfileDto;

public class SeasonProfileConverter extends CustomConverter<SeasonProfileDto, DataObject> {

  @Override
  public DataObject convert(
      final SeasonProfileDto source,
      final Type<? extends DataObject> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final List<DataObject> seasonElements = new ArrayList<>();

    final DataObject seasonProfileNameObject =
        DataObject.newOctetStringData(
            new BigInteger(source.getSeasonProfileName(), 10).toByteArray());
    seasonElements.add(seasonProfileNameObject);

    final DataObject seasonStartObject =
        DataObject.newOctetStringData(
            this.mapperFacade.map(source.getSeasonStart(), CosemDateTime.class).encode());
    seasonElements.add(seasonStartObject);

    final DataObject seasonWeekProfileNameObject =
        DataObject.newOctetStringData(
            new BigInteger(source.getWeekProfile().getWeekProfileName(), 10).toByteArray());
    seasonElements.add(seasonWeekProfileNameObject);

    return DataObject.newStructureData(seasonElements);
  }
}
