// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.EventsConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.FindEventsRequestDataConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.OutagesConverter;
import org.springframework.stereotype.Component;

@Component(value = "managementMapper")
public class ManagementMapper extends ConfigurableMapper {
  @Override
  public void configure(final MapperFactory mapperFactory) {

    // These converters are needed. Otherwise mapping will lead to
    // strange exceptions when mapping DateTime fields
    mapperFactory.getConverterFactory().registerConverter(new FindEventsRequestDataConverter());
    mapperFactory.getConverterFactory().registerConverter(new EventsConverter());
    mapperFactory.getConverterFactory().registerConverter(new OutagesConverter());
  }
}
