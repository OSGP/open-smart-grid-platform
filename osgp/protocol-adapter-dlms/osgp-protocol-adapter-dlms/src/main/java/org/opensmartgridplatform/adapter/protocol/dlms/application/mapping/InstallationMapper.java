//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

@Component(value = "installationMapper")
public class InstallationMapper extends ConfigurableMapper {

  @Override
  public void configure(final MapperFactory mapperFactory) {
    mapperFactory.getConverterFactory().registerConverter(new DeviceConverter());
  }
}
