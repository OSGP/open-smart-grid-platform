// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.da.application.exceptionhandling;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

public class SoapFaultMapper extends ConfigurableMapper {
  @Override
  protected void configure(final MapperFactory factory) {
    factory.getConverterFactory().registerConverter(new FunctionalExceptionConverter());
    factory.getConverterFactory().registerConverter(new TechnicalExceptionConverter());
    factory.getConverterFactory().registerConverter(new ConnectionFailureExceptionConverter());
  }
}
