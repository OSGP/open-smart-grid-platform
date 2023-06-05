// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.DecoupleMbusDeviceByChannelResponseConverter;
import org.springframework.stereotype.Component;

@Component(value = "installationMapper")
public class InstallationMapper extends ConfigurableMapper {

  @Override
  public final void configure(final MapperFactory mapperFactory) {
    final ConverterFactory converterFactory = mapperFactory.getConverterFactory();
    converterFactory.registerConverter(new DecoupleMbusDeviceByChannelResponseConverter());
  }
}
