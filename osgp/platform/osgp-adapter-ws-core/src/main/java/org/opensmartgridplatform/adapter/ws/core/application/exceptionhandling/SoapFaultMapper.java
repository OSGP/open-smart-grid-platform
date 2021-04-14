/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.exceptionhandling;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

public class SoapFaultMapper extends ConfigurableMapper {
  @Override
  protected void configure(final MapperFactory factory) {
    factory.getConverterFactory().registerConverter(new FunctionalExceptionConverter());
    factory.getConverterFactory().registerConverter(new TechnicalExceptionConverter());
  }
}
