package org.osgp.adapter.protocol.dlms.application.mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusType;

public class AdministrativeStatusTypeConverter extends BidirectionalConverter<AdministrativeStatusType, Integer> {

    private static final Map<Integer, AdministrativeStatusType> administrativeStatusMap;
    private static final Map<AdministrativeStatusType, Integer> administrativeStatusMapReversed;

    static {
        final Map<Integer, AdministrativeStatusType> map = new HashMap<>();

        map.put(0, AdministrativeStatusType.UNDEFINED);
        map.put(1, AdministrativeStatusType.OFF);
        map.put(2, AdministrativeStatusType.ON);

        administrativeStatusMap = Collections.unmodifiableMap(map);
        administrativeStatusMapReversed = AdministrativeStatusTypeConverter.createFlippedMap(administrativeStatusMap);
    }

    /**
     * Flips the key and value of the map, and returns it.
     *
     * @return Flipped map.
     */
    private static Map<AdministrativeStatusType, Integer> createFlippedMap(
            final Map<Integer, AdministrativeStatusType> map) {
        final HashMap<AdministrativeStatusType, Integer> tempReversed = new HashMap<>();
        for (final Entry<Integer, AdministrativeStatusType> val : map.entrySet()) {
            tempReversed.put(val.getValue(), val.getKey());
        }

        return Collections.unmodifiableMap(tempReversed);
    }

    @Override
    public Integer convertTo(final AdministrativeStatusType source, final Type<Integer> destinationType) {
        return administrativeStatusMapReversed.get(source);
    }

    @Override
    public AdministrativeStatusType convertFrom(final Integer source,
            final Type<AdministrativeStatusType> destinationType) {
        return administrativeStatusMap.get(source);
    }

}
