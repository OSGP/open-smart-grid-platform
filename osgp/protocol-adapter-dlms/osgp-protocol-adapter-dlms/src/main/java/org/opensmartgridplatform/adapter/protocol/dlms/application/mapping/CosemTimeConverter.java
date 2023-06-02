//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.openmuc.jdlms.datatypes.CosemDateFormat;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;

public class CosemTimeConverter
    extends BidirectionalConverter<CosemTimeDto, org.openmuc.jdlms.datatypes.CosemTime> {

  @Override
  public org.openmuc.jdlms.datatypes.CosemTime convertTo(
      final CosemTimeDto source,
      final Type<org.openmuc.jdlms.datatypes.CosemTime> destinationType,
      final MappingContext context) {

    return new org.openmuc.jdlms.datatypes.CosemTime(
        source.getHour(), source.getMinute(), source.getSecond(), source.getHundredths());
  }

  @Override
  public CosemTimeDto convertFrom(
      final org.openmuc.jdlms.datatypes.CosemTime source,
      final Type<CosemTimeDto> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final int hour = source.get(CosemDateFormat.Field.HOUR);
    final int minute = source.get(CosemDateFormat.Field.MINUTE);
    final int second = source.get(CosemDateFormat.Field.SECOND);
    final int hundredths = source.get(CosemDateFormat.Field.HUNDREDTHS);
    return new org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto(
        hour, minute, second, hundredths);
  }
}
