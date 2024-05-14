// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute;

class GetPushSetupCommandExecutorTest {

  @Test
  void test() {
    final List<AttributeAddress> actualAddresses =
        this.createExpectedAttributeAddresses(new ObisCode(1, 1, 1, 1, 1, 1));
    final List<Integer> actualAttributeIds =
        actualAddresses.stream().map(AttributeAddress::getId).toList();
    final List<Integer> expectedAttributeIds =
        Arrays.stream(PushSetupAttribute.values())
            .filter(a -> !a.attributeName().equals(PushSetupAttribute.LOGICAL_NAME.attributeName()))
            .map(PushSetupAttribute::attributeId)
            .toList();
    assertThat(actualAttributeIds).isEqualTo(expectedAttributeIds);
  }

  protected static List<AttributeAddress> createExpectedAttributeAddresses(
      final ObisCode obisCode) {
    final List<AttributeAddress> expectedAttributeAddresses = new ArrayList<>();
    expectedAttributeAddresses.add(
        new AttributeAddress(
            InterfaceClass.PUSH_SETUP.id(),
            obisCode,
            PushSetupAttribute.PUSH_OBJECT_LIST.attributeId()));
    expectedAttributeAddresses.add(
        new AttributeAddress(
            InterfaceClass.PUSH_SETUP.id(),
            obisCode,
            PushSetupAttribute.SEND_DESTINATION_AND_METHOD.attributeId()));
    expectedAttributeAddresses.add(
        new AttributeAddress(
            InterfaceClass.PUSH_SETUP.id(),
            obisCode,
            PushSetupAttribute.COMMUNICATION_WINDOW.attributeId()));
    expectedAttributeAddresses.add(
        new AttributeAddress(
            InterfaceClass.PUSH_SETUP.id(),
            obisCode,
            PushSetupAttribute.RANDOMISATION_START_INTERVAL.attributeId()));
    expectedAttributeAddresses.add(
        new AttributeAddress(
            InterfaceClass.PUSH_SETUP.id(),
            obisCode,
            PushSetupAttribute.NUMBER_OF_RETRIES.attributeId()));
    expectedAttributeAddresses.add(
        new AttributeAddress(
            InterfaceClass.PUSH_SETUP.id(),
            obisCode,
            PushSetupAttribute.REPETITION_DELAY.attributeId()));
    return expectedAttributeAddresses;
  }

  protected GetResultImpl unsigned(final int value, final AccessResultCode resultCode) {
    return new GetResultImpl(DataObject.newUInteger16Data(value), resultCode);
  }

  protected GetResultImpl longUnsigned(final long value, final AccessResultCode resultCode) {
    return new GetResultImpl(DataObject.newUInteger32Data(value), resultCode);
  }
}
