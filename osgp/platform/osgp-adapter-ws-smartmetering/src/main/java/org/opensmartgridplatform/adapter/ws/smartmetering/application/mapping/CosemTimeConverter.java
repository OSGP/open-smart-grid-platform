/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemTime;

public class CosemTimeConverter extends CustomConverter<byte[], CosemTime> {

  @Override
  public CosemTime convert(
      final byte[] source,
      final Type<? extends CosemTime> destinationType,
      final MappingContext context) {
    final int hour = source[0] & 0xFF;
    final int minute = source[1] & 0xFF;
    final int second = source[2] & 0xFF;
    final int hundredths = source[3] & 0xFF;

    return new CosemTime(hour, minute, second, hundredths);
  }
}
