// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;

@Slf4j
class CosemObjectTest {

  @Test
  void getAttribute() {
    final Attribute attribute1 = this.createAttribute(1, "Value 1");
    final Attribute attribute2 = this.createAttribute(2, "Value 2");

    final CosemObject object = this.createCosemObject(List.of(attribute1, attribute2));

    assertThat(object.getAttribute(1)).isEqualTo(attribute1);
    assertThat(object.getAttribute(2)).isEqualTo(attribute2);
    Assertions.assertThrows(IllegalArgumentException.class, () -> object.getAttribute(3));
  }

  @ParameterizedTest
  @CsvSource({"1.1.1.1.1.1,1", "0.2.0.0.0.0,2", "1.4.99.98.10.255,4"})
  void getChannel(final String obis, final int expectedChannel) throws ObjectConfigException {

    final CosemObject object = this.createCosemObject(obis);

    assertThat(object.getChannel()).isEqualTo(expectedChannel);
  }

  @ParameterizedTest
  @ValueSource(strings = {"1.2.3.4.5", "1.2.3.4.5.6.7", "1.x.1.1.1.1.1"})
  void getChannelWithError(final String obis) {

    final CosemObject object = this.createCosemObject(obis);

    Assertions.assertThrows(ObjectConfigException.class, object::getChannel);
  }

  @ParameterizedTest
  @CsvSource({"1.1.1.1.1.1,false", "0.x.0.0.0.0,true", "0.X.0.0.0.0,false"})
  void hasWildcardChannel(final String obis, final boolean result) throws ObjectConfigException {

    final CosemObject object = this.createCosemObject(obis);

    assertThat(object.hasWildcardChannel()).isEqualTo(result);
  }

  private Attribute createAttribute(final int id, final String value) {
    return new Attribute(
        id, "descr", null, DlmsDataType.DONT_CARE, ValueType.DYNAMIC, value, null, AccessType.RW);
  }

  private CosemObject createCosemObject(final List<Attribute> attributes) {
    return new CosemObject(
        "TAG", "descr", 1, 0, "1.2.3", "group", null, List.of(), Map.of(), attributes);
  }

  private CosemObject createCosemObject(final String obis) {
    return new CosemObject(
        "TAG", "descr", 1, 0, obis, "group", null, List.of(), Map.of(), List.of());
  }
}
