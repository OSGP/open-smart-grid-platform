// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.application.mapping;

import java.time.ZonedDateTime;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

@Component
public class Iec61850Mapper extends ConfigurableMapper {

  @Override
  protected void configure(final MapperFactory factory) {
    factory.getConverterFactory().registerConverter(new DeviceOutputSettingToRelayMapConverter());
    factory.getConverterFactory().registerConverter(new PassThroughConverter(ZonedDateTime.class));
  }
}
