// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import javax.annotation.PostConstruct;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareVersion;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableFirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "coreFirmwareManagementMapper")
public class FirmwareManagementMapper extends ConfigurableMapper {

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private WritableFirmwareFileRepository firmwareFileRepository;

  public FirmwareManagementMapper() {
    // Setting auto init to false, to make sure the repo
    super(false);
  }

  @PostConstruct
  public void initialize() {
    this.init();
  }

  @Override
  public void configure(final MapperFactory mapperFactory) {

    mapperFactory.getConverterFactory().registerConverter(new FirmwareConverter());
    mapperFactory.getConverterFactory().registerConverter(new FirmwareModuleDataConverter());

    mapperFactory
        .getConverterFactory()
        .registerConverter(
            new DeviceFirmwareConverter(this.deviceRepository, this.firmwareFileRepository));
    mapperFactory
        .classMap(
            FirmwareVersion.class,
            org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion.class)
        .byDefault()
        .register();

    mapperFactory.registerClassMap(
        mapperFactory
            .classMap(
                org.opensmartgridplatform.domain.core.entities.DeviceModel.class,
                org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.DeviceModel
                    .class)
            .field("manufacturer.code", "manufacturer")
            .byDefault()
            .toClassMap());

    mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToDateTimeConverter());
  }
}
