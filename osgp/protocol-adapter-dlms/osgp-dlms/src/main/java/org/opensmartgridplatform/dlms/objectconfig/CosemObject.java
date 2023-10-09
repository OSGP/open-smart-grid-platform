// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.dlmsClasses.ProfileGeneric;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = Id.NAME, property = "class-id", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = CosemObject.class, name = "1"),
  @JsonSubTypes.Type(value = CosemObject.class, name = "3"),
  @JsonSubTypes.Type(value = CosemObject.class, name = "4"),
  @JsonSubTypes.Type(value = ProfileGeneric.class, name = "7"),
  @JsonSubTypes.Type(value = CosemObject.class, name = "8"),
  @JsonSubTypes.Type(value = CosemObject.class, name = "40"),
  @JsonSubTypes.Type(value = CosemObject.class, name = "47"),
  @JsonSubTypes.Type(value = CosemObject.class, name = "72"),
  @JsonSubTypes.Type(value = CosemObject.class, name = "77")
})
public class CosemObject {
  private String tag;
  private String description;

  @JsonProperty("class-id")
  private int classId;

  private int version;
  private String obis;
  private String group;
  private String note;
  private List<MeterType> meterTypes;
  private Map<ObjectProperty, Object> properties;
  private List<Attribute> attributes;

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
