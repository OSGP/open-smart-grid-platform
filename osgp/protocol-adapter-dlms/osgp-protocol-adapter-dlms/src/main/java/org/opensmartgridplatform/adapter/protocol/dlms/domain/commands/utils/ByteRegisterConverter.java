//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ByteRegisterConverter<T extends Enum<T>> {

  private final Map<T, Integer> registerBitIndexPerType;
  private final Map<Integer, T> typePerRegisterBitIndex;

  private final int numberOfBitsInRegister;

  public ByteRegisterConverter(
      final Map<T, Integer> registerBitIndexPerType, final int numberOfBitsInRegister) {
    this.numberOfBitsInRegister = numberOfBitsInRegister;
    this.registerBitIndexPerType = Collections.unmodifiableMap(registerBitIndexPerType);
    this.typePerRegisterBitIndex = this.createFlippedMap(this.registerBitIndexPerType);
  }

  /**
   * Flips the key and value of the map, and returns it.
   *
   * @return Flipped map.
   */
  private Map<Integer, T> createFlippedMap(final Map<T, Integer> map) {
    final Map<Integer, T> tempReversed = new HashMap<>();
    for (final Entry<T, Integer> val : map.entrySet()) {
      tempReversed.put(val.getValue(), val.getKey());
    }

    return Collections.unmodifiableMap(tempReversed);
  }

  /**
   * Returns the position of the bit value for the given T, in the byte register space.
   *
   * @param t T
   * @return position of the bit holding the T value.
   */
  public Integer toBitPosition(final T t) {
    return this.registerBitIndexPerType.get(t);
  }

  /**
   * Create a set of T representing the active bits in the register value. Returns empty set if no
   * bits are active.
   *
   * @param registerValue Value of the register.
   * @return List of active types.
   */
  public Set<T> toTypes(final Long registerValue) {
    final Set<T> types = new HashSet<>();

    final BitSet bitSet = BitSet.valueOf(new long[] {registerValue});
    for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
      types.add(this.typePerRegisterBitIndex.get(i));
    }

    return types;
  }

  /**
   * Calculate the long value for the given set of T
   *
   * @param types Set of types
   * @return Long value.
   */
  public Long toLongValue(final Set<T> types) {
    final BitSet bitSet = new BitSet(this.numberOfBitsInRegister);
    for (final T T : types) {
      bitSet.set(this.toBitPosition(T), true);
    }

    long value = 0L;
    for (int i = 0; i < bitSet.length(); ++i) {
      value += bitSet.get(i) ? (1L << i) : 0L;
    }
    return value;
  }
}
