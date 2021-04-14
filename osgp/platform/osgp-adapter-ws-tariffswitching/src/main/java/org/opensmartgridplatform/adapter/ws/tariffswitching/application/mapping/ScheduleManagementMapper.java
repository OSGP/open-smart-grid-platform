/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.tariffswitching.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.ws.tariffswitching.application.mapping.ws.TariffScheduleToScheduleConverter;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import org.springframework.stereotype.Component;

@Component(value = "tariffSwitchingScheduleManagementMapper")
public class ScheduleManagementMapper extends ConfigurableMapper {

  @Override
  public void configure(final MapperFactory mapperFactory) {

    mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToDateTimeConverter());
    mapperFactory.getConverterFactory().registerConverter(new TariffScheduleToScheduleConverter());
  }
}
