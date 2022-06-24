package org.opensmartgridplatform.domain.smartmetering.config;

import lombok.Data;

@Data
public class Attribute {
  public int id;
  public String description;
  public String datatype;
  public String valuetype;
  public String value;
  public String access;
}
