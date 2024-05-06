package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import java.util.ArrayList;
import java.util.List;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute;

class GetPushSetupCommandExecutorTest {

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
