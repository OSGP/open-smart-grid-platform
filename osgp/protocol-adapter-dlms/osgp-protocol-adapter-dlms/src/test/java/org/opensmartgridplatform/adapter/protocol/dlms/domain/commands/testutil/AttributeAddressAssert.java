// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.List;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.CosemDateFormat;
import org.openmuc.jdlms.datatypes.DataObject;

public class AttributeAddressAssert {

  public static void is(final AttributeAddress actual, final AttributeAddress expected) {
    assertThat(actual.getClassId()).isEqualTo(expected.getClassId());
    assertThat(actual.getInstanceId()).isEqualTo(expected.getInstanceId());
    assertThat(actual.getId()).isEqualTo(expected.getId());

    if (actual.getAccessSelection() != null || expected.getAccessSelection() != null) {
      assertAccessSelectionIs(actual.getAccessSelection(), expected.getAccessSelection());
    }
  }

  private static void assertAccessSelectionIs(
      final SelectiveAccessDescription actual, final SelectiveAccessDescription expected) {
    if (actual == null || expected == null) {
      fail("AccessSelection not equal. Actual: " + actual + ", expected: " + expected);
    }

    assertThat(actual.getAccessSelector()).isEqualTo(expected.getAccessSelector());

    if (actual.getAccessParameter() != null || expected.getAccessParameter() != null) {
      assertThatDataObjectIs(actual.getAccessParameter(), expected.getAccessParameter());
    }
  }

  private static void assertThatDataObjectIs(final DataObject actual, final DataObject expected) {
    assertThat(actual.getType()).isEqualTo(expected.getType());

    if (actual.isComplex() && actual.getType() != DataObject.Type.COMPACT_ARRAY) {
      assertThatArrayIs(actual, expected);
    } else if (actual.isNumber()) {
      final Number actualNumber = actual.getValue();
      final Number expectedNumber = actual.getValue();
      assertThat(actualNumber).isEqualTo(expectedNumber);
    } else if (actual.getType() == DataObject.Type.OCTET_STRING) {
      final byte[] actualOctetString = actual.getValue();
      final byte[] expectedOctetString = expected.getValue();
      assertThat(actualOctetString).isEqualTo(expectedOctetString);
    } else if (actual.isCosemDateFormat()) {
      assertThatDateIs(actual, expected);
    } else {
      fail("AssertThat for type " + actual.getType() + " not available");
    }
  }

  private static void assertThatArrayIs(final DataObject actual, final DataObject expected) {
    final List<DataObject> actualList = actual.getValue();
    final List<DataObject> expectedList = expected.getValue();

    assertThat(actualList.size()).isEqualTo(expectedList.size());

    int index = 0;
    for (final DataObject actualObject : actualList) {
      assertThatDataObjectIs(actualObject, expectedList.get(index));
      index++;
    }
  }

  private static void assertThatDateIs(final DataObject actual, final DataObject expected) {
    final CosemDateFormat actualDate = actual.getValue();
    final CosemDateFormat expectedDate = expected.getValue();

    for (final CosemDateFormat.Field field : CosemDateFormat.Field.values()) {
      final var a = actualDate.get(field);
      final var b = expectedDate.get(field);
      assertThat(actualDate.get(field)).isEqualTo(expectedDate.get(field));
    }
  }
}
