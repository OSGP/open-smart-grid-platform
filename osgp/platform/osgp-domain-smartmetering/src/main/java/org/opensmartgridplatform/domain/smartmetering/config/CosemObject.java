package org.opensmartgridplatform.domain.smartmetering.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class CosemObject {
  public String tag;
  public String description;

  @JsonProperty("class-id")
  public int classId;

  public int version;
  public String obis;
  public String group;
  public ArrayList<String> meterTypes;
  public ArrayList<Attribute> attributes;

  public Attribute getAttribute(final int id) {
    return this.attributes.stream()
        .filter(attribute -> attribute.id == id)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Attribute " + id + " not found"));
  }
}
