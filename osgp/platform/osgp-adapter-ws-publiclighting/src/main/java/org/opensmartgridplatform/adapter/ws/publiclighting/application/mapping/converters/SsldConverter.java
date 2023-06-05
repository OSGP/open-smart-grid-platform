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
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

public class SsldConverter
    extends BidirectionalConverter<org.opensmartgridplatform.domain.core.entities.Ssld, Ssld> {

  @Override
  public Ssld convertTo(
      final org.opensmartgridplatform.domain.core.entities.Ssld source,
      final Type<Ssld> destinationType,
      final MappingContext mappingContext) {
    final Ssld ssld = new Ssld();
    final String deviceIdentification = source.getDeviceIdentification();
    ssld.setDeviceUid(
        Base64.encodeBase64String(deviceIdentification.getBytes(StandardCharsets.US_ASCII)));
    ssld.setDeviceIdentification(deviceIdentification);
    final Address containerAddress = source.getContainerAddress();
    if (containerAddress != null) {
      ssld.setContainerPostalCode(containerAddress.getPostalCode());
      ssld.setContainerCity(containerAddress.getCity());
      ssld.setContainerStreet(containerAddress.getStreet());
      if (containerAddress.getNumber() != null) {
        ssld.setContainerNumber(containerAddress.getNumber().toString());
      }
    }
    final GpsCoordinates gpsCoordinates = source.getGpsCoordinates();
    if (gpsCoordinates != null) {
      ssld.setGpsLatitude(gpsCoordinates.getLatitude());
      ssld.setGpsLongitude(gpsCoordinates.getLongitude());
    }
    ssld.setDeviceType(source.getDeviceType());
    ssld.setActivated(source.isActivated());
    ssld.setHasSchedule(source.getHasSchedule());
    ssld.setPublicKeyPresent(source.isPublicKeyPresent());
    return ssld;
  }

  @Override
  public org.opensmartgridplatform.domain.core.entities.Ssld convertFrom(
      final Ssld source,
      final Type<org.opensmartgridplatform.domain.core.entities.Ssld> destinationType,
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
    final org.opensmartgridplatform.domain.core.entities.Ssld ssld =
        new org.opensmartgridplatform.domain.core.entities.Ssld(
            deviceIdentification, null, containerAddress, gpsCoordinates, null);
    ssld.updateRegistrationData(null, source.getDeviceType());
    ssld.setHasSchedule(source.isHasSchedule());
    ssld.setPublicKeyPresent(source.isPublicKeyPresent());
    return ssld;
  }
}
