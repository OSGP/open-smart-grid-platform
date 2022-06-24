package org.opensmartgridplatform.domain.smartmetering.config;

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
