// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.nio.ByteBuffer;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDate;

public class CosemDateConverter extends CustomConverter<byte[], CosemDate> {

  @Override
  public CosemDate convert(
      final byte[] source,
      final Type<? extends CosemDate> destinationType,
      final MappingContext context) {
    final ByteBuffer bb = ByteBuffer.wrap(source);
    final int year = bb.getShort() & 0xFFFF;
    final int month = bb.get() & 0xFF;
    final int day = bb.get() & 0xFF;
    final int weekDay = bb.get() & 0xFF;
    return new CosemDate(year, month, day, weekDay);
  }
}
