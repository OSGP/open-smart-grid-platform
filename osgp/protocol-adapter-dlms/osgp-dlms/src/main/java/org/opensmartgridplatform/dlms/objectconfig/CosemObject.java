// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;

@Data
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
