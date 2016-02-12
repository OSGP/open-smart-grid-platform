/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlag;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagType;

@Service("configurationObjectHelperService")
public class ConfigurationObjectHelperService {
    /**
     * Gives the position of the flag as indicated by the FlagType in the bit
     * string representation of the Flags register.
     */
    private static final Map<ConfigurationFlagType, Integer> BIT_INDEX_PER_CONFIGURATION_FLAG_TYPE;
    private static final Map<Integer, ConfigurationFlagType> CONFIGURATION_FLAG_TYPE_PER_BIT_INDEX;

    private static final int NUMBER_OF_FLAG_BITS = 16;

    static {
        final Map<ConfigurationFlagType, Integer> map = new EnumMap<>(ConfigurationFlagType.class);

        map.put(ConfigurationFlagType.DISCOVER_ON_OPEN_COVER, 15);
        map.put(ConfigurationFlagType.DISCOVER_ON_POWER_ON, 14);
        map.put(ConfigurationFlagType.DYNAMIC_MBUS_ADDRESS, 13);
        map.put(ConfigurationFlagType.PO_ENABLE, 12);
        map.put(ConfigurationFlagType.HLS_3_ON_P_3_ENABLE, 11);
        map.put(ConfigurationFlagType.HLS_4_ON_P_3_ENABLE, 10);
        map.put(ConfigurationFlagType.HLS_5_ON_P_3_ENABLE, 9);
        map.put(ConfigurationFlagType.HLS_3_ON_PO_ENABLE, 8);
        map.put(ConfigurationFlagType.HLS_4_ON_PO_ENABLE, 7);
        map.put(ConfigurationFlagType.HLS_5_ON_PO_ENABLE, 6);
        // bits 0 to 5 are not used

        BIT_INDEX_PER_CONFIGURATION_FLAG_TYPE = Collections.unmodifiableMap(map);

        // Create a flipped version of the map.
        final Map<Integer, ConfigurationFlagType> tempReversed = new HashMap<>();
        for (final Entry<ConfigurationFlagType, Integer> val : BIT_INDEX_PER_CONFIGURATION_FLAG_TYPE.entrySet()) {
            tempReversed.put(val.getValue(), val.getKey());
        }

        CONFIGURATION_FLAG_TYPE_PER_BIT_INDEX = Collections.unmodifiableMap(tempReversed);
    }

    /**
     * Returns the position of the bit value for the given
     * ConfigurationFlagType, in the 2-byte register space.
     *
     * @param configurationFlagType
     *            ConfigurationFlagType
     * @return position of the bit holding the configuration flag type value.
     */
    public Integer toBitPosition(final ConfigurationFlagType configurationFlagType) {
        return BIT_INDEX_PER_CONFIGURATION_FLAG_TYPE.get(configurationFlagType);
    }

    /**
     * Create a list of unique configuration flag type objects representing the
     * active bits in the register value.
     *
     * @param flagByteArray
     *            The byte array holding the flag bits.
     * @return List of active configuration flag type objects.
     */
    public List<ConfigurationFlag> toConfigurationFlags(final byte[] flagByteArray) {
        final List<ConfigurationFlag> configurationFlags = new ArrayList<>();
        final BitSet bitSet = BitSet
                .valueOf(new long[] { ((flagByteArray[0] & 0xFF) << 8) + (flagByteArray[1] & 0xFF) });
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
            final ConfigurationFlagType configurationFlagType = CONFIGURATION_FLAG_TYPE_PER_BIT_INDEX.get(i);
            configurationFlags.add(new ConfigurationFlag(configurationFlagType, true));
        }
        return configurationFlags;
    }

    /**
     * Calculate the byte array for the given list of ConfigurationFlagType
     * objects
     *
     * @param configurationFlags
     *            List of ConfigurationFlag objects
     * @return byte array with MSB in first element
     */
    public byte[] toByteArray(final List<ConfigurationFlag> configurationFlags) {
        final BitSet bitSet = new BitSet(NUMBER_OF_FLAG_BITS);
        for (final ConfigurationFlag configurationFlag : configurationFlags) {
            if (configurationFlag.isEnabled()) {
                bitSet.set(this.toBitPosition(configurationFlag.getConfigurationFlagType()), true);
            }
        }
        final byte[] byteArray = bitSet.toByteArray();
        // swap 0 and 1
        final byte tmp = byteArray[1];
        byteArray[1] = byteArray[0];
        byteArray[0] = tmp;
        return byteArray;
    }
}
