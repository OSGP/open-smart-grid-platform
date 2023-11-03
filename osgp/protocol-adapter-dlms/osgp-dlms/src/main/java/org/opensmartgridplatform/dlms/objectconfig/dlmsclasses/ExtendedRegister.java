// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig.dlmsclasses;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.MeterType;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;

@Getter
@NoArgsConstructor
public class ExtendedRegister extends Register {

  public ExtendedRegister(
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

  @Override
  public ExtendedRegister copy() {
    return new ExtendedRegister(
        this.tag,
        this.description,
        this.classId,
        this.version,
        this.obis,
        this.group,
        this.note,
        new ArrayList<>(this.meterTypes),
        this.properties == null || this.properties.isEmpty()
            ? null
            : new EnumMap<>(this.properties),
        this.attributes.stream().map(Attribute::copy).toList());
  }

  @Override
  public ExtendedRegister copyWithNewObis(final String newObis) {
    final ExtendedRegister newExtendedRegister = this.copy();
    newExtendedRegister.obis = newObis;
    return newExtendedRegister;
  }

  @Override
  public ExtendedRegister copyWithNewAttribute(final Attribute newAttribute) {
    final ExtendedRegister newExtendedRegister = this.copy();

    // Remove attribute with same id as newAttribute
    final List<Attribute> copiedAttributes =
        newExtendedRegister.attributes.stream()
            .filter(attr -> attr.getId() != newAttribute.getId())
            .collect(Collectors.toList());
    copiedAttributes.add(newAttribute);

    // Add new attribute
    newExtendedRegister.attributes = copiedAttributes;

    return newExtendedRegister;
  }
}
