// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ByteRegisterConverterTest {

  private final ByteRegisterConverter<TestType> converter =
      new ByteRegisterConverter<>(this.createMap(), 16);

  private enum TestType {
    TYPE_0(0),
    TYPE_1(1),
    TYPE_2(2),
    TYPE_3(3),
    TYPE_4(4),
    TYPE_5(5),
    TYPE_6(6),
    TYPE_7(7),
    TYPE_8(8),
    TYPE_9(9),
    TYPE_10(10),
    TYPE_11(11),
    TYPE_12(12),
    TYPE_13(13),
    TYPE_14(14),
    TYPE_15(15);

    final Integer index;

    TestType(final Integer index) {
      this.index = index;
    }

    long getValue() {
      return (long) Math.pow(2, this.index);
    }
  }

  private Map<TestType, Integer> createMap() {
    return Arrays.stream(TestType.values())
        .collect(Collectors.toMap(item -> item, item -> item.index));
  }

  @ParameterizedTest
  @EnumSource(TestType.class)
  void testToBitPosition(final TestType type) {
    assertThat(this.converter.toBitPosition(type)).isEqualTo(type.index);
  }

  @ParameterizedTest
  @EnumSource(TestType.class)
  void testToTypesWithSingleBitSet(final TestType type) {
    assertThat(this.converter.toTypes(type.getValue())).containsExactly(type);
  }

  @Test
  void testToTypesWithNoBitsSet() {
    assertThat(this.converter.toTypes(0x0000L)).isEmpty();
  }

  @Test
  void testToTypesWithMultipleBitsSet() {
    assertThat(this.converter.toTypes(0x0003L))
        .containsExactlyInAnyOrder(TestType.TYPE_0, TestType.TYPE_1);
    assertThat(this.converter.toTypes(0x00C0L))
        .containsExactlyInAnyOrder(TestType.TYPE_6, TestType.TYPE_7);
    assertThat(this.converter.toTypes(0x01FFL))
        .containsExactlyInAnyOrder(
            TestType.TYPE_0,
            TestType.TYPE_1,
            TestType.TYPE_2,
            TestType.TYPE_3,
            TestType.TYPE_4,
            TestType.TYPE_5,
            TestType.TYPE_6,
            TestType.TYPE_7,
            TestType.TYPE_8);
    assertThat(this.converter.toTypes(0x0300L))
        .containsExactlyInAnyOrder(TestType.TYPE_8, TestType.TYPE_9);
    assertThat(this.converter.toTypes(0xC000L))
        .containsExactlyInAnyOrder(TestType.TYPE_14, TestType.TYPE_15);
  }

  @Test
  void testToTypesWithUnknownBitSet() {
    assertThat(this.converter.toTypes(0x10000L)).containsNull();
  }

  @ParameterizedTest
  @EnumSource(TestType.class)
  void testToLongValueWithSingleAlarmSet(final TestType type) {
    assertThat(this.converter.toLongValue(this.createSet(type))).isEqualTo(type.getValue());
  }

  @Test
  void testToLongValueWithNoAlarmsSet() {
    assertThat(this.converter.toLongValue(Collections.emptySet())).isEqualTo(0x0000L);
  }

  @Test
  void testToLongValueWithMultipleAlarmsSet() {
    assertThat(this.converter.toLongValue(this.createSet(TestType.TYPE_0, TestType.TYPE_1)))
        .isEqualTo(0x0003L);
    assertThat(this.converter.toLongValue(this.createSet(TestType.TYPE_6, TestType.TYPE_7)))
        .isEqualTo(0x00C0L);
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
                    TestType.TYPE_7,
                    TestType.TYPE_8)))
        .isEqualTo(0x01FFL);
    assertThat(this.converter.toLongValue(this.createSet(TestType.TYPE_8, TestType.TYPE_9)))
        .isEqualTo(0x0300L);
    assertThat(this.converter.toLongValue(this.createSet(TestType.TYPE_14, TestType.TYPE_15)))
        .isEqualTo(0xC000L);
  }

  private Set<TestType> createSet(final TestType... types) {
    final Set<TestType> setWithTestTypes = new HashSet<>();

    Collections.addAll(setWithTestTypes, types);

    return setWithTestTypes;
  }
}
