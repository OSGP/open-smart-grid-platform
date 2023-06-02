//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.WeekProfileDto;

public class WeekProfileConverter extends CustomConverter<WeekProfileDto, DataObject> {

  @Override
  public DataObject convert(
      final WeekProfileDto source,
      final Type<? extends DataObject> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final List<DataObject> weekElements = new ArrayList<>();

    weekElements.add(
        DataObject.newOctetStringData(
            new BigInteger(source.getWeekProfileName(), 10).toByteArray()));
    weekElements.add(DataObject.newUInteger8Data(source.getMonday().getDayId().shortValue()));
    weekElements.add(DataObject.newUInteger8Data(source.getTuesday().getDayId().shortValue()));
    weekElements.add(DataObject.newUInteger8Data(source.getWednesday().getDayId().shortValue()));
    weekElements.add(DataObject.newUInteger8Data(source.getThursday().getDayId().shortValue()));
    weekElements.add(DataObject.newUInteger8Data(source.getFriday().getDayId().shortValue()));
    weekElements.add(DataObject.newUInteger8Data(source.getSaturday().getDayId().shortValue()));
    weekElements.add(DataObject.newUInteger8Data(source.getSunday().getDayId().shortValue()));

    return DataObject.newStructureData(weekElements);
  }
}
