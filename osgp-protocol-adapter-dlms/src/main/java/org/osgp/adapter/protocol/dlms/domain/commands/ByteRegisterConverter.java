package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ByteRegisterConverter<T extends Enum<T>> {

    private final Map<T, Integer> registerBitInderPerType;
    private final Map<Integer, T> typePerRegisterBitIndex;

    private final int numberOfBitsInRegister;

    public ByteRegisterConverter(final Map<T, Integer> registerBitInderPerType, final int numberOfBitsInRegister) {
        this.numberOfBitsInRegister = numberOfBitsInRegister;
        this.registerBitInderPerType = Collections.unmodifiableMap(registerBitInderPerType);
        this.typePerRegisterBitIndex = this.createFlippedMap(this.registerBitInderPerType);
    }

    /**
     * Flips the key and value of the map, and returns it.
     *
     * @return Flipped map.
     */
    private Map<Integer, T> createFlippedMap(final Map<T, Integer> map) {
        final HashMap<Integer, T> tempReversed = new HashMap<>();
        for (final Entry<T, Integer> val : map.entrySet()) {
            tempReversed.put(val.getValue(), val.getKey());
        }

        return Collections.unmodifiableMap(tempReversed);
    }

    /**
     * Returns the position of the bit value for the given T, in the byte
     * register space.
     *
     * @param T
     *            T
     * @return position of the bit holding the alarm type value.
     */
    public Integer toBitPosition(final T T) {
        return this.registerBitInderPerType.get(T);
    }

    /**
     * Create a set of alarm types representing the active bits in the register
     * value.
     *
     * @param registerValue
     *            Value of the register.
     * @return List of active types.
     */
    public Set<T> toTypes(final Long registerValue) {
        final Set<T> Types = new HashSet<>();

        final BitSet bitSet = BitSet.valueOf(new long[] { registerValue });
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
            Types.add(this.typePerRegisterBitIndex.get(i));
        }

        return Types;
    }

    /**
     * Calculate the long value for the given set of Types
     *
     * @param Types
     *            Set of types
     * @return Long value.
     */
    public Long toLongValue(final Set<T> types) {
        final BitSet bitSet = new BitSet(this.numberOfBitsInRegister);
        for (final T T : types) {
            bitSet.set(this.toBitPosition(T), true);
        }

        return bitSet.toLongArray()[0];
    }
}
