// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig.dlmsclasses;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.MeterType;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;
import org.opensmartgridplatform.dlms.objectconfig.TypeBasedValue;
import org.opensmartgridplatform.dlms.objectconfig.ValueType;

@Getter
@NoArgsConstructor
public class Register extends Data {

  public Register(
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

  public boolean needsScalerUnitFromMeter(final String configLookupType) {
    final Attribute scalerUnitAttribute =
        this.getAttribute(RegisterAttribute.SCALER_UNIT.attributeId());

    if (scalerUnitAttribute == null
        || scalerUnitAttribute.getValue() == null
        || scalerUnitAttribute.getValue().isEmpty()) {
      return true;
    }

    if (scalerUnitAttribute.getValuetype() == ValueType.DYNAMIC) {
      return true;
    }

    if ("BASED_ON_TYPE".equals(scalerUnitAttribute.getValue())) {
      final List<TypeBasedValue> typeBasedValues = scalerUnitAttribute.getValues();

      return typeBasedValues.stream().noneMatch(tbv -> tbv.getTypes().contains(configLookupType));
    }

    return false;
  }

  public String getScalerUnit(final String configLookupType) throws ObjectConfigException {
    final Attribute scalerUnitAttribute =
        this.getAttribute(RegisterAttribute.SCALER_UNIT.attributeId());

    if ("BASED_ON_TYPE".equals(scalerUnitAttribute.getValue())) {
      final List<TypeBasedValue> typeBasedValues = scalerUnitAttribute.getValues();

      final Optional<TypeBasedValue> typeBasedValueOptional =
          typeBasedValues.stream()
              .filter(tbv -> tbv.getTypes().contains(configLookupType))
              .findFirst();
      if (typeBasedValueOptional.isPresent()) {
        return typeBasedValueOptional.get().getValue();
      } else {
        throw new ObjectConfigException("No scaler unit found for " + configLookupType);
      }
    } else {
      return scalerUnitAttribute.getValue();
    }
  }
}
