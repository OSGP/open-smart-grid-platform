/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ByteRegisterConverterTest {

  private final Map<TestType, Integer> registerBitIndexPerType = this.createMap();

  private final ByteRegisterConverter<TestType> converter =
      new ByteRegisterConverter<>(this.registerBitIndexPerType, 8);

  private enum TestType {
    TYPE_0,
    TYPE_1,
    TYPE_2,
    TYPE_3,
    TYPE_4,
    TYPE_5,
    TYPE_6,
    TYPE_7
  }

  private Map<TestType, Integer> createMap() {
    final Map<TestType, Integer> registerBitIndexPerType = new HashMap<>();

    registerBitIndexPerType.put(TestType.TYPE_0, 0);
    registerBitIndexPerType.put(TestType.TYPE_1, 1);
    registerBitIndexPerType.put(TestType.TYPE_2, 2);
    registerBitIndexPerType.put(TestType.TYPE_3, 3);
    registerBitIndexPerType.put(TestType.TYPE_4, 4);
    registerBitIndexPerType.put(TestType.TYPE_5, 5);
    registerBitIndexPerType.put(TestType.TYPE_6, 6);
    registerBitIndexPerType.put(TestType.TYPE_7, 7);

    return registerBitIndexPerType;
  }

  @Test
  void testToBitPosition() {
    assertThat(this.converter.toBitPosition(TestType.TYPE_0)).isZero();
    assertThat(this.converter.toBitPosition(TestType.TYPE_1)).isEqualTo(1);
    assertThat(this.converter.toBitPosition(TestType.TYPE_2)).isEqualTo(2);
    assertThat(this.converter.toBitPosition(TestType.TYPE_3)).isEqualTo(3);
    assertThat(this.converter.toBitPosition(TestType.TYPE_4)).isEqualTo(4);
    assertThat(this.converter.toBitPosition(TestType.TYPE_5)).isEqualTo(5);
    assertThat(this.converter.toBitPosition(TestType.TYPE_6)).isEqualTo(6);
    assertThat(this.converter.toBitPosition(TestType.TYPE_7)).isEqualTo(7);
  }

  @Test
  void testToTypes() {
    assertThat(this.converter.toTypes(0x00L)).isEmpty();
    assertThat(this.converter.toTypes(0x01L)).containsExactlyInAnyOrder(TestType.TYPE_0);
    assertThat(this.converter.toTypes(0x02L)).containsExactlyInAnyOrder(TestType.TYPE_1);
    assertThat(this.converter.toTypes(0x03L))
        .containsExactlyInAnyOrder(TestType.TYPE_0, TestType.TYPE_1);
    assertThat(this.converter.toTypes(0x80L)).containsExactlyInAnyOrder(TestType.TYPE_7);
    assertThat(this.converter.toTypes(0xC0L))
        .containsExactlyInAnyOrder(TestType.TYPE_7, TestType.TYPE_6);
    assertThat(this.converter.toTypes(0xFFL))
        .containsExactlyInAnyOrder(
            TestType.TYPE_0,
            TestType.TYPE_1,
            TestType.TYPE_2,
            TestType.TYPE_3,
            TestType.TYPE_4,
            TestType.TYPE_5,
            TestType.TYPE_6,
            TestType.TYPE_7);
  }

  @Test
  void testToLongValue() {
    //    assertThat(this.converter.toLongValue(Collections.emptySet())).isEqualTo(0x00L);
    assertThat(this.converter.toLongValue(this.createSet(TestType.TYPE_0))).isEqualTo(0x01L);
    assertThat(this.converter.toLongValue(this.createSet(TestType.TYPE_1))).isEqualTo(0x02L);
    assertThat(this.converter.toLongValue(this.createSet(TestType.TYPE_0, TestType.TYPE_1)))
        .isEqualTo(0x03L);
    assertThat(this.converter.toLongValue(this.createSet(TestType.TYPE_7))).isEqualTo(0x80L);
    assertThat(this.converter.toLongValue(this.createSet(TestType.TYPE_7, TestType.TYPE_6)))
        .isEqualTo(0xC0L);
    assertThat(
            this.converter.toLongValue(
                this.createSet(
                    TestType.TYPE_0,
                    TestType.TYPE_1,
                    TestType.TYPE_2,
                    TestType.TYPE_3,
                    TestType.TYPE_4,
                    TestType.TYPE_5,
                    TestType.TYPE_6,
                    TestType.TYPE_7)))
        .isEqualTo(0xFFL);
  }

  private Set<TestType> createSet(final TestType... types) {
    final Set<TestType> setWithTestTypes = new HashSet<>();

    Collections.addAll(setWithTestTypes, types);

    return setWithTestTypes;
  }
}
