// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

@Component(value = "commonMapper")
public class CommonMapper extends ConfigurableMapper {
  @Override
  public void configure(final MapperFactory mapperFactory) {
    // Only default mapping needed at this time
  }
}
