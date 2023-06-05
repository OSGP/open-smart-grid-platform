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
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;

public class LightMeasurementDeviceConverter
    extends BidirectionalConverter<
        LightMeasurementDevice,
        org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device> {

  private final DeviceConverterHelper<LightMeasurementDevice> helper =
      new DeviceConverterHelper<>(LightMeasurementDevice.class);

  @Override
  public void setMapperFacade(final MapperFacade mapper) {
    super.setMapperFacade(mapper);
    this.helper.setMapperFacade(mapper);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ma.glasnost.orika.converter.BidirectionalConverter#convertTo(java.lang
   * .Object, ma.glasnost.orika.metadata.Type,
   * ma.glasnost.orika.MappingContext)
   */
  @Override
  public org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device convertTo(
      final LightMeasurementDevice source,
      final Type<org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device>
          destinationType,
      final MappingContext mappingContext) {
    return this.helper.initJaxb(source);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ma.glasnost.orika.converter.BidirectionalConverter#convertFrom(java.lang
   * .Object, ma.glasnost.orika.metadata.Type,
   * ma.glasnost.orika.MappingContext)
   */
  @Override
  public LightMeasurementDevice convertFrom(
      final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device source,
      final Type<LightMeasurementDevice> destinationType,
      final MappingContext mappingContext) {
    return this.helper.initEntity(source);
  }

  @Override
  public int hashCode() {
    return super.hashCode() + Objects.hash(this.helper);
  }

  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }
}
