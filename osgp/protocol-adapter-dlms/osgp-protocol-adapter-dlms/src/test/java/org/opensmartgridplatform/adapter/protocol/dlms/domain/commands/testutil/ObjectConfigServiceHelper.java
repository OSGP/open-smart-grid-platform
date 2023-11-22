// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.dlms.objectconfig.AccessType;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsDataType;
import org.opensmartgridplatform.dlms.objectconfig.MeterType;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;
import org.opensmartgridplatform.dlms.objectconfig.ValueType;

public class ObjectConfigServiceHelper {

  private static final int ATTRIBUTE_ID_SCALER_UNIT = 3;

  public static CosemObject createObject(
      final int classId,
      final String obis,
      final String tag,
      final String scalerUnitValue,
      final boolean polyphase) {
    return createObject(classId, obis, tag, scalerUnitValue, polyphase, Map.of());
  }

  public static CosemObject createObject(
      final int classId,
      final String obis,
      final String tag,
      final String scalerUnitValue,
      final boolean polyphase,
      final Map<ObjectProperty, Object> properties) {
    return createObject(
        classId,
        obis,
        tag,
        polyphase,
        properties,
        scalerUnitValue != null ? createScalerUnitAttributeList(scalerUnitValue) : List.of());
  }

  public static CosemObject createObject(
      final int classId,
      final String obis,
      final String tag,
      final boolean polyphase,
      final Map<ObjectProperty, Object> properties,
      final List<Attribute> attributes) {
    return new CosemObject(
        tag, "descr", classId, 0, obis, "", null, getMeterTypes(polyphase), properties, attributes);
  }

  private static List<Attribute> createScalerUnitAttributeList(final String value) {
    return List.of(
        new Attribute(
            ATTRIBUTE_ID_SCALER_UNIT,
            "descr",
            null,
            DlmsDataType.DONT_CARE,
            ValueType.FIXED_IN_PROFILE,
            value,
            null,
            AccessType.RW));
  }

  private static List<MeterType> getMeterTypes(final boolean polyphase) {
    if (polyphase) {
      return Collections.singletonList(MeterType.PP);
    } else {
      return Arrays.asList(MeterType.PP, MeterType.SP);
    }
  }

  public static Attribute createAttribute(final int id, final String value) {
    return new Attribute(
        id,
        "descr",
        null,
        DlmsDataType.DONT_CARE,
        ValueType.FIXED_IN_PROFILE,
        value,
        null,
        AccessType.RW);
  }
}
