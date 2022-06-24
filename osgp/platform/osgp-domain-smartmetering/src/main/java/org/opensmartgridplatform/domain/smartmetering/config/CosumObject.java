package org.opensmartgridplatform.domain.smartmetering.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import lombok.Data;

@Data
public class CosumObject {
  public String tag;
  public String description;

  @JsonProperty("class-id")
  public int classId;

  public int version;
  public String obis;
  public String group;
  public ArrayList<String> meterTypes;
  public ArrayList<Attribute> attributes;
}
