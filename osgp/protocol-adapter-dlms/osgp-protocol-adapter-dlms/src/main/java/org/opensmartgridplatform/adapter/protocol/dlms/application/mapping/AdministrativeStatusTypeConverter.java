// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;

public class AdministrativeStatusTypeConverter
    extends BidirectionalConverter<AdministrativeStatusTypeDto, Integer> {

  private static final Map<Integer, AdministrativeStatusTypeDto> administrativeStatusMap;
  private static final Map<AdministrativeStatusTypeDto, Integer> administrativeStatusMapReversed;

  static {
    final Map<Integer, AdministrativeStatusTypeDto> map = new HashMap<>();

    map.put(0, AdministrativeStatusTypeDto.UNDEFINED);
    map.put(1, AdministrativeStatusTypeDto.OFF);
    map.put(2, AdministrativeStatusTypeDto.ON);

    administrativeStatusMap = Collections.unmodifiableMap(map);
    administrativeStatusMapReversed =
        AdministrativeStatusTypeConverter.createFlippedMap(administrativeStatusMap);
  }

  /**
   * Flips the key and value of the map, and returns it.
   *
   * @return Flipped map.
   */
  private static Map<AdministrativeStatusTypeDto, Integer> createFlippedMap(
      final Map<Integer, AdministrativeStatusTypeDto> map) {
    final HashMap<AdministrativeStatusTypeDto, Integer> tempReversed = new HashMap<>();
    for (final Entry<Integer, AdministrativeStatusTypeDto> val : map.entrySet()) {
      tempReversed.put(val.getValue(), val.getKey());
    }

    return Collections.unmodifiableMap(tempReversed);
  }

  @Override
  public Integer convertTo(
      final AdministrativeStatusTypeDto source,
      final Type<Integer> destinationType,
      final MappingContext context) {
    return administrativeStatusMapReversed.get(source);
  }

  @Override
  public AdministrativeStatusTypeDto convertFrom(
      final Integer source,
      final Type<AdministrativeStatusTypeDto> destinationType,
      final MappingContext context) {
    return administrativeStatusMap.get(source);
  }
}
