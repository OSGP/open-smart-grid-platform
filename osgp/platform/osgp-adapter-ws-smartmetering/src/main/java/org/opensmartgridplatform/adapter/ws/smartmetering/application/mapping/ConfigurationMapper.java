// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ConfigureDefinableLoadProfileRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.AlarmNotifications;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.DefinableLoadProfileConfigurationData;
import org.springframework.stereotype.Component;

@Component(value = "configurationMapper")
public class ConfigurationMapper extends ConfigurableMapper {

  private static final String CAPTURE_OBJECTS = "captureObjects";
  private static final String CAPTURE_OBJECTS_CAPTURE_OBJECT = "captureObjects.captureObject";

  @Override
  public void configure(final MapperFactory mapperFactory) {

    // This converter is necessary because of the SeasonsType object in
    // ActivityCalendarType.
    mapperFactory.getConverterFactory().registerConverter(new ActivityCalendarConverter());

    // This classMap replaces the AlarmNotificationsConverter, is needed
    // because of different field names.
    mapperFactory
        .classMap(
            AlarmNotifications.class,
            org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmNotifications
                .class)
        .field("alarmNotification", "alarmNotificationsSet")
        .byDefault()
        .register();
    mapperFactory.getConverterFactory().registerConverter(new AlarmNotificationsConverter());

    mapperFactory
        .classMap(
            org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ConfigurationFlags
                .class,
            org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .ConfigurationFlags.class)
        .field("flags", "configurationFlag")
        .byDefault()
        .register();

    mapperFactory
        .classMap(
            ConfigureDefinableLoadProfileRequest.class,
            org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                .DefinableLoadProfileConfigurationData.class)
        .fieldAToB(CAPTURE_OBJECTS_CAPTURE_OBJECT, CAPTURE_OBJECTS)
        .fieldBToA(CAPTURE_OBJECTS, CAPTURE_OBJECTS_CAPTURE_OBJECT)
        .byDefault()
        .register();
    mapperFactory
        .classMap(
            DefinableLoadProfileConfigurationData.class,
            org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                .DefinableLoadProfileConfigurationData.class)
        .fieldAToB(CAPTURE_OBJECTS_CAPTURE_OBJECT, CAPTURE_OBJECTS)
        .fieldBToA(CAPTURE_OBJECTS, CAPTURE_OBJECTS_CAPTURE_OBJECT)
        .byDefault()
        .register();
    mapperFactory.getConverterFactory().registerConverter(new ObisCodeValuesConverter());

    // These converters are needed for PushSetup
    mapperFactory.getConverterFactory().registerConverter(new PushSetupAlarmConverter());
    mapperFactory.getConverterFactory().registerConverter(new ObisCodeConverter());
    mapperFactory.getConverterFactory().registerConverter(new PushObjectConverter());
    mapperFactory.getConverterFactory().registerConverter(new PushSetupLastGaspConverter());
    mapperFactory.getConverterFactory().registerConverter(new PushSetupSmsConverter());

    mapperFactory.getConverterFactory().registerConverter(new SetKeysRequestConverter());

    // These converters are necessary to enable correct mapping of dates and
    // times.
    mapperFactory.getConverterFactory().registerConverter(new CosemDateTimeConverter());
    mapperFactory.getConverterFactory().registerConverter(new CosemTimeConverter());
    mapperFactory.getConverterFactory().registerConverter(new CosemDateConverter());
    mapperFactory.getConverterFactory().registerConverter(new XsdDateTimeToLongConverter());

    mapperFactory.getConverterFactory().registerConverter(new FirmwareVersionConverter());
    mapperFactory.getConverterFactory().registerConverter(new FirmwareVersionGasConverter());
  }
}
