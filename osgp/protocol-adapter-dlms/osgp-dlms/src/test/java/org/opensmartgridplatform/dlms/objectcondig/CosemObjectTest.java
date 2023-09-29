// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectcondig;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;

@Slf4j
class CosemObjectTest {

  @Test
  void getAttribute() {
    final Attribute attribute1 = new Attribute();
    attribute1.setId(1);
    attribute1.setValue("Value 1");
    final Attribute attribute2 = new Attribute();
    attribute2.setId(2);
    attribute2.setValue("Value 2");

    final CosemObject object = new CosemObject();
    object.setAttributes(List.of(attribute1, attribute2));

    assertThat(object.getAttribute(1)).isEqualTo(attribute1);
    assertThat(object.getAttribute(2)).isEqualTo(attribute2);
    Assertions.assertThrows(IllegalArgumentException.class, () -> object.getAttribute(3));
  }

  @ParameterizedTest
  @CsvSource({"1.1.1.1.1.1,1", "0.2.0.0.0.0,2", "1.4.99.98.10.255,4"})
  void getChannel(final String obis, final int expectedChannel) throws ObjectConfigException {

    final CosemObject object = new CosemObject();
    object.setObis(obis);

    assertThat(object.getChannel()).isEqualTo(expectedChannel);
  }

  @ParameterizedTest
  @ValueSource(strings = {"1.2.3.4.5", "1.2.3.4.5.6.7", "1.x.1.1.1.1.1"})
  void getChannelWithError(final String obis) {

    final CosemObject object = new CosemObject();
    object.setObis(obis);

    Assertions.assertThrows(ObjectConfigException.class, object::getChannel);
  }

  @ParameterizedTest
  @CsvSource({"1.1.1.1.1.1,false", "0.x.0.0.0.0,true", "0.X.0.0.0.0,false"})
  void hasWildcardChannel(final String obis, final boolean result) throws ObjectConfigException {

    final CosemObject object = new CosemObject();
    object.setObis(obis);

    assertThat(object.hasWildcardChannel()).isEqualTo(result);
  }
}
