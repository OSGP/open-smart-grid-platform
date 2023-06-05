// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import javax.annotation.PostConstruct;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "coreDeviceInstallationMapper")
public class DeviceInstallationMapper extends ConfigurableMapper {

  @Autowired private SsldRepository ssldRepository;

  @Autowired private WritableDeviceModelRepository writableDeviceModelRepository;

  public DeviceInstallationMapper() {
    super(false);
  }

  @PostConstruct
  public void initialize() {
    this.init();
  }

  @Override
  public void configure(final MapperFactory mapperFactory) {
    mapperFactory
        .getConverterFactory()
        .registerConverter(
            new WsInstallationDeviceToDeviceConverter(
                this.ssldRepository, this.writableDeviceModelRepository));
    mapperFactory
        .getConverterFactory()
        .registerConverter(new WsInstallationDeviceToSsldConverter());
    mapperFactory
        .getConverterFactory()
        .registerConverter(new WsInstallationLmdToLmdConverter(this.writableDeviceModelRepository));
  }
}
