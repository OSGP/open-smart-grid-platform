// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import java.util.Objects;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;

class SmartMeterConverter extends BidirectionalConverter<SmartMeter, Device> {

  private final DeviceConverterHelper<SmartMeter> helper =
      new DeviceConverterHelper<>(SmartMeter.class);

  @Override
  public void setMapperFacade(final MapperFacade mapper) {
    super.setMapperFacade(mapper);
    this.helper.setMapperFacade(mapper);
  }

  @Override
  public SmartMeter convertFrom(
      final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device source,
      final Type<SmartMeter> destinationType,
      final MappingContext context) {
    return this.helper.initEntity(source);
  }

  @Override
  public org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device convertTo(
      final SmartMeter source,
      final Type<org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device>
          destinationType,
      final MappingContext context) {
    return this.helper.initJaxb(source);
  }

  @Override
  public int hashCode() {
    return super.hashCode() + Objects.hash(this.helper);
  }

  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj) && Objects.equals(this.helper, ((SmartMeterConverter) obj).helper);
  }
}
