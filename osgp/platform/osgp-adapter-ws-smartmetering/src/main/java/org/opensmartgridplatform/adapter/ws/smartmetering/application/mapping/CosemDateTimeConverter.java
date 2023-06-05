// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.nio.ByteBuffer;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClockStatus;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDate;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDateTime;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemTime;

public class CosemDateTimeConverter extends CustomConverter<byte[], CosemDateTime> {

  @Override
  public CosemDateTime convert(
      final byte[] source,
      final Type<? extends CosemDateTime> destinationType,
      final MappingContext context) {
    final ByteBuffer bb = ByteBuffer.wrap(source);

    final int year = bb.getShort() & 0xFFFF;
    final int month = bb.get() & 0xFF;
    final int dayOfMonth = bb.get() & 0xFF;
    final int dayOfWeek = bb.get() & 0xFF;
    final int hour = bb.get() & 0xFF;
    final int minute = bb.get() & 0xFF;
    final int second = bb.get() & 0xFF;
    final int hundredths = bb.get() & 0xFF;
    final int deviation = bb.getShort();

    final ClockStatus clockStatus = new ClockStatus(bb.get());
    final CosemTime time = new CosemTime(hour, minute, second, hundredths);
    final CosemDate date = new CosemDate(year, month, dayOfMonth, dayOfWeek);

    return new CosemDateTime(date, time, deviation, clockStatus);
  }
}
