// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping;

import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.List;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.dto.valueobjects.RelayMatrixDto;
import org.opensmartgridplatform.oslp.Oslp;

public class RelayMatrixConverter extends BidirectionalConverter<RelayMatrixDto, Oslp.RelayMatrix> {

  @Override
  public org.opensmartgridplatform.oslp.Oslp.RelayMatrix convertTo(
      final RelayMatrixDto source,
      final Type<org.opensmartgridplatform.oslp.Oslp.RelayMatrix> destinationType,
      final MappingContext context) {

    final ByteString masterRelayIndex =
        this.mapperFacade.map(source.getMasterRelayIndex(), ByteString.class);
    final boolean isMasterRelayOn = source.isMasterRelayOn();
    final ByteString indicesOfControlledRelaysOn =
        this.convertListOfIntegersToByteString(source.getIndicesOfControlledRelaysOn());
    final ByteString indicesOfControlledRelaysOff =
        this.convertListOfIntegersToByteString(source.getIndicesOfControlledRelaysOff());

    return Oslp.RelayMatrix.newBuilder()
        .setMasterRelayIndex(masterRelayIndex)
        .setMasterRelayOn(isMasterRelayOn)
        .setIndicesOfControlledRelaysOn(indicesOfControlledRelaysOn)
        .setIndicesOfControlledRelaysOff(indicesOfControlledRelaysOff)
        .build();
  }

  @Override
  public RelayMatrixDto convertFrom(
      final org.opensmartgridplatform.oslp.Oslp.RelayMatrix source,
      final Type<RelayMatrixDto> destinationType,
      final MappingContext context) {

    final Integer masterRelayIndex =
        this.mapperFacade.map(source.getMasterRelayIndex(), Integer.class);
    final boolean isMasterRelayOn = source.getMasterRelayOn();
    final List<Integer> indicesOfControlledRelaysOn =
        this.convertByteStringToListOfIntegers(source.getIndicesOfControlledRelaysOn());
    final List<Integer> indicesOfControlledRelaysOff =
        this.convertByteStringToListOfIntegers(source.getIndicesOfControlledRelaysOff());

    final RelayMatrixDto relayMatrix = new RelayMatrixDto(masterRelayIndex, isMasterRelayOn);
    relayMatrix.setIndicesOfControlledRelaysOn(indicesOfControlledRelaysOn);
    relayMatrix.setIndicesOfControlledRelaysOff(indicesOfControlledRelaysOff);
    return relayMatrix;
  }

  private ByteString convertListOfIntegersToByteString(final List<Integer> list) {
    final List<ByteString> byteStrings = new ArrayList<>();
    for (final Integer integer : list) {
      final ByteString byteString = this.mapperFacade.map(integer, ByteString.class);
      byteStrings.add(byteString);
    }
    return ByteString.copyFrom(byteStrings);
  }

  private List<Integer> convertByteStringToListOfIntegers(final ByteString byteString) {
    final List<Integer> integers = new ArrayList<>();
    for (final byte index : byteString.toByteArray()) {
      integers.add((int) index);
    }
    return integers;
  }
}
