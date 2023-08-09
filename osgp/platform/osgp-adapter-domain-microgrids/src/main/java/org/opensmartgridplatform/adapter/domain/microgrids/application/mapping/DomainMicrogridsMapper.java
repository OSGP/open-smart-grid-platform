// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.microgrids.application.mapping;

import java.time.ZonedDateTime;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

@Component
public class DomainMicrogridsMapper extends ConfigurableMapper {

  @Override
  protected void configure(final MapperFactory factory) {
    factory.getConverterFactory().registerConverter(new PassThroughConverter(ZonedDateTime.class));
    factory.getConverterFactory().registerConverter(new GetDataRequestConverter());
    factory.getConverterFactory().registerConverter(new SystemFilterConverter());
    factory.getConverterFactory().registerConverter(new SetDataRequestConverter());
    factory.getConverterFactory().registerConverter(new SetDataSystemIdentifierConverter());
    factory.getConverterFactory().registerConverter(new ProfileConverter());
  }
}
