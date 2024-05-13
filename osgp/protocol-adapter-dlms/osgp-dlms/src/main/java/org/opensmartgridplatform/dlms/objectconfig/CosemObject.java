// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.Data;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.ExtendedRegister;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.ProfileGeneric;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.Register;

@Getter
@NoArgsConstructor
@JsonTypeInfo(use = Id.NAME, property = "class-id", visible = true, defaultImpl = CosemObject.class)
@JsonSubTypes({
  @JsonSubTypes.Type(value = Data.class, name = "1"),
  @JsonSubTypes.Type(value = Register.class, name = "3"),
  @JsonSubTypes.Type(value = ExtendedRegister.class, name = "4"),
  @JsonSubTypes.Type(value = ProfileGeneric.class, name = "7"),
})
public class CosemObject {
  protected String tag;
  protected String description;

  @JsonProperty("class-id")
  protected int classId;

  protected int version;
  protected String obis;
  protected String group;
  protected String note;
  protected List<MeterType> meterTypes;
  protected Map<ObjectProperty, Object> properties;
  protected List<Attribute> attributes;

  public CosemObject(
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
    this.tag = tag;
    this.description = description;
    this.classId = classId;
    this.version = version;
    this.obis = obis;
    this.group = group;
    this.note = note;
    this.meterTypes = meterTypes;
    this.properties = properties;
    this.attributes = attributes;
  }

  public CosemObject copy() {
    return new CosemObject(
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

  public CosemObject copyWithNewObis(final String newObis) {
    final CosemObject newCosemObject = this.copy();
    newCosemObject.obis = newObis;
    return newCosemObject;
  }

  public CosemObject copyWithNewAttribute(final Attribute newAttribute) {
    final CosemObject newCosemObject = this.copy();

    // Remove attribute with same id as newAttribute
    final List<Attribute> copiedAttributes =
        newCosemObject.attributes.stream()
            .filter(attr -> attr.getId() != newAttribute.getId())
            .collect(Collectors.toList());
    copiedAttributes.add(newAttribute);

    // Add new attribute
    newCosemObject.attributes = copiedAttributes;

    return newCosemObject;
  }

  public Attribute getAttribute(final int id) {
    return this.attributes.stream()
        .filter(attribute -> attribute.getId() == id)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Attribute " + id + " not found"));
  }

  public boolean hasAttribute(final int id) {
    if (this.attributes == null) {
      return false;
    }
    return this.attributes.stream().anyMatch(attribute -> attribute.getId() == id);
  }

  public Object getProperty(final ObjectProperty objectProperty) {
    if (this.properties == null) {
      return null;
    }
    return this.properties.getOrDefault(objectProperty, null);
  }

  @SuppressWarnings("unchecked")
  public List<String> getListProperty(final ObjectProperty objectProperty) {
    if (this.properties != null) {
      return (List<String>) this.properties.getOrDefault(objectProperty, null);
    }

    return Collections.emptyList();
  }

  @JsonIgnore
  public int getChannel() throws ObjectConfigException {
    final String[] obisParts = this.getObisInParts();
    if (obisParts[1].equals("x")) {
      throw new ObjectConfigException("Can't get channel from " + this.obis);
    }
    return Integer.parseInt(obisParts[1]);
  }

  public boolean hasWildcardChannel() throws ObjectConfigException {
    final String[] obisParts = this.getObisInParts();
    return obisParts[1].equals("x");
  }

  private String[] getObisInParts() throws ObjectConfigException {
    final String[] obisParts = this.obis.split("\\.");
    if (obisParts.length != 6) {
      throw new ObjectConfigException("Invalid obiscode " + this.obis);
    }
    return obisParts;
  }
}
