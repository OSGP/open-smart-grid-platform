// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig.dlmsclasses;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.MeterType;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;

@Getter
@NoArgsConstructor
public class Data extends CosemObject {

  public Data(
      final String tag,
      final String description,
      final int classId,
      final int version,
      final String obis,
      final String group,
      final String note,
      final List<MeterType> meterTypes,
      final Map<ObjectProperty, Object> properties,
      final List<Attribute> attributes) {
    super(
        tag, description, classId, version, obis, group, note, meterTypes, properties, attributes);
  }
}
