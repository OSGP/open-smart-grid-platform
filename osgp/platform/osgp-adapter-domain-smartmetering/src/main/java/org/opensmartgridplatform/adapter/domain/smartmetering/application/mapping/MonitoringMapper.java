// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.converter.builtin.DateAndTimeConverters.DateToXmlGregorianCalendarConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.ActualPowerQualityDtoConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.DlmsMeterValueConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.GetPowerQualityProfileDtoConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.MeterReadsResponseItemDtoConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.PeriodicMeterReadsResponseItemDtoConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.PowerQualityValueConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.ProfileEntryValueConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.ReadAlarmRegisterDataConverter;
import org.springframework.stereotype.Component;

@Component(value = "monitoringMapper")
public class MonitoringMapper extends ConfigurableMapper {

  @Override
  public final void configure(final MapperFactory mapperFactory) {
    // This converter must be used: a multiplier might be needed when
    // mapping between DlmsMeterValue and OsgpMeterValue. Thus mapping must
    // never be attempted without using this converter!
    final ConverterFactory converterFactory = mapperFactory.getConverterFactory();
    converterFactory.registerConverter(new DlmsMeterValueConverter());
    converterFactory.registerConverter(new ReadAlarmRegisterDataConverter());
    converterFactory.registerConverter(new ProfileEntryValueConverter());
    converterFactory.registerConverter(new DateToXmlGregorianCalendarConverter());
    converterFactory.registerConverter(
        new PeriodicMeterReadsResponseItemDtoConverter(mapperFactory));
    converterFactory.registerConverter(new MeterReadsResponseItemDtoConverter(mapperFactory));
    converterFactory.registerConverter(new GetPowerQualityProfileDtoConverter(mapperFactory));
    converterFactory.registerConverter(new ActualPowerQualityDtoConverter(mapperFactory));
    converterFactory.registerConverter(new PowerQualityValueConverter());
  }
}
