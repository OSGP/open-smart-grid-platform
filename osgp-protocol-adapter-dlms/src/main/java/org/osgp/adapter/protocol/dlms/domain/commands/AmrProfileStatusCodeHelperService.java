/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlag;

@Service("amrProfileStatusCodeHelperService")
public class AmrProfileStatusCodeHelperService {
    private static final int NUMBER_OF_BITS_IN_REGISTER = 8;

    private static final ByteRegisterConverter<AmrProfileStatusCodeFlag> BYTE_REGISTER_CONVERTER;

    static {
        final EnumMap<AmrProfileStatusCodeFlag, Integer> map = new EnumMap<>(AmrProfileStatusCodeFlag.class);

        map.put(AmrProfileStatusCodeFlag.CRITICAL_ERROR, 0);
        map.put(AmrProfileStatusCodeFlag.CLOCK_INVALID, 1);
        map.put(AmrProfileStatusCodeFlag.DATA_NOT_VALID, 2);
        map.put(AmrProfileStatusCodeFlag.DAYLIGHT_SAVING, 3);
        map.put(AmrProfileStatusCodeFlag.CLOCK_ADJUSTED, 5);
        map.put(AmrProfileStatusCodeFlag.POWER_DOWN, 7);

        BYTE_REGISTER_CONVERTER = new ByteRegisterConverter<AmrProfileStatusCodeFlag>(Collections.unmodifiableMap(map),
                NUMBER_OF_BITS_IN_REGISTER);
    }

    public Integer toBitPosition(final AmrProfileStatusCodeFlag amrProfileStatus) {
        return BYTE_REGISTER_CONVERTER.toBitPosition(amrProfileStatus);
    }

    public Set<AmrProfileStatusCodeFlag> toAmrProfileStatusCodeFlags(final Number registerValue) {
        return BYTE_REGISTER_CONVERTER.toTypes(registerValue.longValue());
    }

    public Short toValue(final Set<AmrProfileStatusCodeFlag> amrProfileStatusCodeFlags) {
        return BYTE_REGISTER_CONVERTER.toLongValue(amrProfileStatusCodeFlags).shortValue();
    }
}
