// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.converters;

import java.nio.charset.StandardCharsets;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.apache.commons.codec.binary.Base64;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

public class LmdConverter
    extends BidirectionalConverter<
        org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice,
        LightMeasurementDevice> {

  @Override
  public LightMeasurementDevice convertTo(
      final org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice source,
      final Type<LightMeasurementDevice> destinationType,
      final MappingContext mappingContext) {
    final LightMeasurementDevice lmd = new LightMeasurementDevice();
    final String deviceIdentification = source.getDeviceIdentification();
    lmd.setDeviceUid(
        Base64.encodeBase64String(deviceIdentification.getBytes(StandardCharsets.US_ASCII)));
    lmd.setDeviceIdentification(deviceIdentification);
    final Address containerAddress = source.getContainerAddress();
    if (containerAddress != null) {
      lmd.setContainerPostalCode(containerAddress.getPostalCode());
      lmd.setContainerCity(containerAddress.getCity());
      lmd.setContainerStreet(containerAddress.getStreet());
      if (containerAddress.getNumber() != null) {
        lmd.setContainerNumber(containerAddress.getNumber().toString());
      }
    }
    final GpsCoordinates gpsCoordinates = source.getGpsCoordinates();
    if (gpsCoordinates != null) {
      lmd.setGpsLatitude(gpsCoordinates.getLatitude());
      lmd.setGpsLongitude(gpsCoordinates.getLongitude());
    }
    lmd.setDeviceType(source.getDeviceType());
    lmd.setActivated(source.isActivated());
    lmd.setDescription(source.getDescription());
    lmd.setCode(source.getCode());
    lmd.setColor(source.getColor());
    lmd.setDigitalInput(source.getDigitalInput());
    return lmd;
  }

  @Override
  public org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice convertFrom(
      final LightMeasurementDevice source,
      final Type<org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice>
          destinationType,
      final MappingContext mappingContext) {
    final String deviceIdentification = source.getDeviceIdentification();
    final Integer containerNumber =
        source.getContainerNumber() == null ? null : Integer.valueOf(source.getContainerNumber());
    final Address containerAddress =
        new Address(
            source.getContainerCity(),
            source.getContainerPostalCode(),
            source.getContainerStreet(),
            containerNumber,
            null,
            null);
    final GpsCoordinates gpsCoordinates =
        new GpsCoordinates(source.getGpsLatitude(), source.getGpsLongitude());
    final org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice lmd =
        new org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice(
            deviceIdentification, null, containerAddress, gpsCoordinates, null);
    lmd.updateRegistrationData(null, source.getDeviceType());
    lmd.setDescription(source.getDescription());
    lmd.setCode(source.getCode());
    lmd.setColor(source.getColor());
    lmd.setDigitalInput(source.getDigitalInput());
    return lmd;
  }
}
