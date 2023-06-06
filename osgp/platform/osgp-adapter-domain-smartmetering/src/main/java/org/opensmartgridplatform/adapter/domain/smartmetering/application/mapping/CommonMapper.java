// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.CosemObisCodeConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.FaultResponseConverter;
import org.springframework.stereotype.Component;

@Component(value = "CommmonMapper")
public class CommonMapper extends ConfigurableMapper {

  @Override
  public final void configure(final MapperFactory mapperFactory) {
    mapperFactory.getConverterFactory().registerConverter(new FaultResponseConverter());
    mapperFactory.getConverterFactory().registerConverter(new CosemObisCodeConverter());
  }
}
