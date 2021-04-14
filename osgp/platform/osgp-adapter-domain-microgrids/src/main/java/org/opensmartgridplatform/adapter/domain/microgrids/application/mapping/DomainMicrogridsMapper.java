/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.microgrids.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class DomainMicrogridsMapper extends ConfigurableMapper {

  @Override
  protected void configure(final MapperFactory factory) {
    factory.getConverterFactory().registerConverter(new PassThroughConverter(DateTime.class));
    factory.getConverterFactory().registerConverter(new GetDataRequestConverter());
    factory.getConverterFactory().registerConverter(new SystemFilterConverter());
    factory.getConverterFactory().registerConverter(new SetDataRequestConverter());
    factory.getConverterFactory().registerConverter(new SetDataSystemIdentifierConverter());
    factory.getConverterFactory().registerConverter(new ProfileConverter());
  }
}
