package org.opensmartgridplatform.domain.smartmetering.config;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class Attribute {
  public int id;
  public String description;
  public String datatype;
  public String valuetype;
  public String value;
  public String access;
}
