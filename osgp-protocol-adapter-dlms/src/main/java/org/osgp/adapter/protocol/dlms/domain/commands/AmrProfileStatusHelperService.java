package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatus;

@Service("amrProfileStatusHelperService")
public class AmrProfileStatusHelperService {
    private static final int NUMBER_OF_BITS_IN_REGISTER = 8;

    private static final ByteRegisterConverter<AmrProfileStatus> BYTE_REGISTER_CONVERTER;

    static {
        final EnumMap<AmrProfileStatus, Integer> map = new EnumMap<>(AmrProfileStatus.class);

        map.put(AmrProfileStatus.CRITICAL_ERROR, 0);
        map.put(AmrProfileStatus.CLOCK_INVALID, 1);
        map.put(AmrProfileStatus.DATA_NOT_VALID, 2);
        map.put(AmrProfileStatus.DAYLIGHT_SAVING, 3);
        map.put(AmrProfileStatus.CLOCK_ADJUSTED, 5);
        map.put(AmrProfileStatus.POWER_DOWN, 7);

        // TODO: Dependency injection of this instance?
        BYTE_REGISTER_CONVERTER = new ByteRegisterConverter<AmrProfileStatus>(Collections.unmodifiableMap(map),
                NUMBER_OF_BITS_IN_REGISTER);
    }

    public Integer toBitPosition(final AmrProfileStatus amrProfileStatus) {
        return BYTE_REGISTER_CONVERTER.toBitPosition(amrProfileStatus);
    }

    public Set<AmrProfileStatus> toAmrProfileStatusses(final Number registerValue) {
        return BYTE_REGISTER_CONVERTER.toTypes(registerValue.longValue());
    }

    public Short toValue(final Set<AmrProfileStatus> amrProfileStatusses) {
        return BYTE_REGISTER_CONVERTER.toLongValue(amrProfileStatusses).shortValue();
    }
}
