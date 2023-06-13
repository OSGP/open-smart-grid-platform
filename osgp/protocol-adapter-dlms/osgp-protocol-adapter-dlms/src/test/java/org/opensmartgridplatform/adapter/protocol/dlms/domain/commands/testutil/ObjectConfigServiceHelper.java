// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.MeterType;

public class ObjectConfigServiceHelper {
  private static final int ATTRIBUTE_ID_SCALER_UNIT = 3;

  public static CosemObject createObject(
      final int classId,
      final String obis,
      final String tag,
      final String scalerUnitValue,
      final boolean polyphase) {
    final CosemObject object = new CosemObject();
    object.setClassId(classId);
    object.setObis(obis);
    object.setTag(tag);
    if (scalerUnitValue != null) {
      object.setAttributes(createScalerUnitAttributeList(scalerUnitValue));
    }
    object.setMeterTypes(getMeterTypes(polyphase));

    return object;
  }

  private static List<Attribute> createScalerUnitAttributeList(final String value) {
    final Attribute scalerUnitAttribute = new Attribute();
    scalerUnitAttribute.setId(ATTRIBUTE_ID_SCALER_UNIT);
    scalerUnitAttribute.setValue(value);
    return Collections.singletonList(scalerUnitAttribute);
  }

  private static List<MeterType> getMeterTypes(final boolean polyphase) {
    if (polyphase) {
      return Collections.singletonList(MeterType.PP);
    } else {
      return Arrays.asList(MeterType.PP, MeterType.SP);
    }
  }
}
