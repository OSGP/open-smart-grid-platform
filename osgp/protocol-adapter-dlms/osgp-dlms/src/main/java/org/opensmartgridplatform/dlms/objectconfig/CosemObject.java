/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package org.opensmartgridplatform.dlms.objectconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class CosemObject {
  public String tag;
  public String description;

  @JsonProperty("class-id")
  public int classId;

  public int version;
  public String obis;
  public String group;
  public List<String> meterTypes;
  public Map<ObjectProperty, Object> properties;
  public List<Attribute> attributes;

  public Attribute getAttribute(final int id) {
    return this.attributes.stream()
        .filter(attribute -> attribute.id == id)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Attribute " + id + " not found"));
  }

  public Object getProperty(final ObjectProperty objectProperty) {
    if (this.properties == null) {
      return null;
    }
    return this.properties.getOrDefault(objectProperty, null);
  }
}
