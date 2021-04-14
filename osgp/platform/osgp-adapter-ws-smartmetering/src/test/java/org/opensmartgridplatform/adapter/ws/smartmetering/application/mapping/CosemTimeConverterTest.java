/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemTime;

public class CosemTimeConverterTest {

  private static final byte BYTE_FOR_HOUR_OF_DAY = 10;

  private static final byte BYTE_FOR_MINUTE_OF_HOUR = 34;
  private static final byte BYTE_FOR_SECOND_OF_MINUTE = 35;
  private static final byte BYTE_FOR_HUNDREDS_0F_SECONDS = 10;
  private static final byte[] COSEMTIME_BYTE_ARRAY = {
    BYTE_FOR_HOUR_OF_DAY,
    BYTE_FOR_MINUTE_OF_HOUR,
    BYTE_FOR_SECOND_OF_MINUTE,
    BYTE_FOR_HUNDREDS_0F_SECONDS
  };
  private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

  /** Tests if mapping a byte[] to a CosemTime object succeeds. */
  @Test
  public void testToCosemTimeMapping() {

    // register the CosemTimeConverter, since the test converts to a
    // CosemTime object.
    this.mapperFactory.getConverterFactory().registerConverter(new CosemTimeConverter());

    // actual mapping
    final CosemTime cosemTime =
        this.mapperFactory.getMapperFacade().map(COSEMTIME_BYTE_ARRAY, CosemTime.class);

    // check mapping
    assertThat(cosemTime).isNotNull();
    assertThat(cosemTime.getHour()).isEqualTo(BYTE_FOR_HOUR_OF_DAY);
    assertThat(cosemTime.getMinute()).isEqualTo(BYTE_FOR_MINUTE_OF_HOUR);
    assertThat(cosemTime.getSecond()).isEqualTo(BYTE_FOR_SECOND_OF_MINUTE);
    assertThat(cosemTime.getHundredths()).isEqualTo(BYTE_FOR_HUNDREDS_0F_SECONDS);
  }
}
