// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

@Component
public class DomainPublicLightingMapper extends ConfigurableMapper {

  @Override
  protected void configure(final MapperFactory factory) {
    final ConverterFactory converterFactory = factory.getConverterFactory();

    converterFactory.registerConverter(new LightTypeConverter());
    converterFactory.registerConverter(new LinkTypeConverter());
    converterFactory.registerConverter(new ScheduleConverter());
    converterFactory.registerConverter(new ScheduleEntryConverter());
  }
}
