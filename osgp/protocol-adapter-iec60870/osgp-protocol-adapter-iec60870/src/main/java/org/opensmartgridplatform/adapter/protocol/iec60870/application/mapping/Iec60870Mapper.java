/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping;

import java.util.TimeZone;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.informationelements.IeQualityConverter;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.informationelements.IeShortFloatConverter;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.informationelements.IeSinglePointWithQualityConverter;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.informationelements.IeTime56Converter;

public class Iec60870Mapper extends ConfigurableMapper {

  private final TimeZone timeZone;

  public Iec60870Mapper(final TimeZone timeZone) {
    super(false);
    this.timeZone = timeZone;
    this.init();
  }

  @Override
  protected void configure(final MapperFactory factory) {
    factory.getConverterFactory().registerConverter(new IeShortFloatConverter());
    factory.getConverterFactory().registerConverter(new IeQualityConverter());
    factory.getConverterFactory().registerConverter(new IeSinglePointWithQualityConverter());
    factory.getConverterFactory().registerConverter(new IeTime56Converter(this.timeZone));
    factory.getConverterFactory().registerConverter(new Iec60870InformationObjectConverter());
    factory.getConverterFactory().registerConverter(new Iec60870AsduConverter());
  }
}
