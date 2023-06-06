// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmSchedulerResponse;
import org.springframework.stereotype.Component;

@Component(value = "adhocMapper")
public class AdhocMapper extends ConfigurableMapper {
  @Override
  public void configure(final MapperFactory mapperFactory) {

    // This converter is needed to ensure correct mapping of dates and times
    mapperFactory.getConverterFactory().registerConverter(new XsdDateTimeToLongConverter());
    mapperFactory.getConverterFactory().registerConverter(new CosemObisCodeConverter());
    mapperFactory.getConverterFactory().registerConverter(new ObisCodeValuesConverter());

    mapperFactory
        .classMap(ResponseData.class, TestAlarmSchedulerResponse.class)
        .field("resultType", "result")
        .field("messageData", "description")
        .register();
  }
}
