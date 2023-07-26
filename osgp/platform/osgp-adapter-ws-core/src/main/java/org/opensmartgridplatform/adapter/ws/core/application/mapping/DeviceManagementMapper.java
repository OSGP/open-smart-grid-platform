// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import java.util.Objects;
import javax.annotation.PostConstruct;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.ws.core.application.mapping.ws.EventTypeConverter;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToInstantConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "coreDeviceManagementMapper")
public class DeviceManagementMapper extends ConfigurableMapper {

  @Autowired private SsldRepository ssldRepository;

  public DeviceManagementMapper() {
    super(false);
  }

  protected DeviceManagementMapper(final SsldRepository ssldRepository) {
    super(false);
    this.ssldRepository = ssldRepository;
  }

  @PostConstruct
  public void initialize() {
    this.init();
  }

  @Override
  public void configure(final MapperFactory mapperFactory) {
    mapperFactory.registerClassMap(
        mapperFactory
            .classMap(
                org.opensmartgridplatform.domain.core.entities.Device.class,
                org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device.class)
            .field("networkAddress", "networkAddress")
            .byDefault()
            .toClassMap());

    mapperFactory.registerClassMap(
        mapperFactory
            .classMap(
                org.opensmartgridplatform.domain.core.entities.Event.class,
                org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Event.class)
            .field("dateTime", "timestamp")
            .byDefault()
            .toClassMap());

    mapperFactory.registerClassMap(
        mapperFactory
            .classMap(
                org.opensmartgridplatform.domain.core.entities.ScheduledTaskWithoutData.class,
                org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.ScheduledTask
                    .class)
            .byDefault()
            .toClassMap());

    mapperFactory.registerClassMap(
        mapperFactory
            .classMap(
                org.opensmartgridplatform.domain.core.entities.ScheduledTaskWithoutData.class,
                org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.ScheduledTask
                    .class)
            .byDefault()
            .toClassMap());

    final Mapper<
            org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdatedDevice, Ssld>
        updatedDeviceToSsldMapper = new UpdatedDeviceToSsldMapper();
    mapperFactory
        .classMap(
            org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdatedDevice.class,
            Ssld.class)
        .exclude("outputSettings")
        .exclude("gpsLatitude")
        .exclude("gpsLongitude")
        .byDefault()
        .customize(updatedDeviceToSsldMapper)
        .register();

    mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToDateTimeConverter());
    mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToInstantConverter());
    mapperFactory.getConverterFactory().registerConverter(new EventTypeConverter());
    mapperFactory.getConverterFactory().registerConverter(new SmartMeterConverter());
    mapperFactory.getConverterFactory().registerConverter(new DeviceConverter());
    mapperFactory.getConverterFactory().registerConverter(new RtuDeviceConverter());
    mapperFactory.getConverterFactory().registerConverter(new LightMeasurementDeviceConverter());
    mapperFactory.getConverterFactory().registerConverter(new SsldConverter(this.ssldRepository));
  }

  @Override
  public int hashCode() {
    return super.hashCode() + Objects.hashCode(this.ssldRepository);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }

    return super.equals(obj)
        && Objects.equals(this.ssldRepository, ((DeviceManagementMapper) obj).ssldRepository);
  }
}
