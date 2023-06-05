// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import java.util.Set;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.openmuc.jdlms.datatypes.CosemDateFormat;
import org.openmuc.jdlms.datatypes.CosemDateTime.ClockStatus;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;

public class CosemDateTimeConverter
    extends BidirectionalConverter<CosemDateTimeDto, org.openmuc.jdlms.datatypes.CosemDateTime> {

  @Override
  public org.openmuc.jdlms.datatypes.CosemDateTime convertTo(
      final CosemDateTimeDto source,
      final Type<org.openmuc.jdlms.datatypes.CosemDateTime> destinationType,
      final MappingContext context) {

    final CosemTimeDto time = source.getTime();
    final CosemDateDto date = source.getDate();

    final Set<ClockStatus> clockStatus =
        ClockStatus.clockStatusFrom((byte) source.getClockStatus().getStatus());

    return new org.openmuc.jdlms.datatypes.CosemDateTime(
        date.getYear(),
        date.getMonth(),
        date.getDayOfMonth(),
        date.getDayOfWeek(),
        time.getHour(),
        time.getMinute(),
        time.getSecond(),
        time.getHundredths(),
        source.getDeviation(),
        clockStatus.toArray(new ClockStatus[clockStatus.size()]));
  }

  @Override
  public CosemDateTimeDto convertFrom(
      final org.openmuc.jdlms.datatypes.CosemDateTime source,
      final Type<CosemDateTimeDto> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final int year = source.get(CosemDateFormat.Field.YEAR);
    final int month = source.get(CosemDateFormat.Field.MONTH);
    final int dayOfMonth = source.get(CosemDateFormat.Field.DAY_OF_MONTH);
    final int dayOfWeek = source.get(CosemDateFormat.Field.DAY_OF_WEEK);
    final org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto date =
        new org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto(
            year, month, dayOfMonth, dayOfWeek);

    final int hour = source.get(CosemDateFormat.Field.HOUR);
    final int minute = source.get(CosemDateFormat.Field.MINUTE);
    final int second = source.get(CosemDateFormat.Field.SECOND);
    final int hundredths = source.get(CosemDateFormat.Field.HUNDREDTHS);
    final org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto time =
        new org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto(
            hour, minute, second, hundredths);

    final int deviation = source.get(CosemDateFormat.Field.DEVIATION);

    final int clockStatusValue = source.get(CosemDateFormat.Field.CLOCK_STATUS);
    final org.opensmartgridplatform.dto.valueobjects.smartmetering.ClockStatusDto clockStatus =
        new org.opensmartgridplatform.dto.valueobjects.smartmetering.ClockStatusDto(
            clockStatusValue);

    return new org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto(
        date, time, deviation, clockStatus);
  }
}
