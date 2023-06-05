// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

@Component(value = "installationMapper")
public class InstallationMapper extends ConfigurableMapper {
  @Override
  public void configure(final MapperFactory mapperFactory) {

    // this converter is needed to ensure correct mapping of dates and
    // times.
    mapperFactory.getConverterFactory().registerConverter(new XsdDateTimeToLongConverter());
  }
}
