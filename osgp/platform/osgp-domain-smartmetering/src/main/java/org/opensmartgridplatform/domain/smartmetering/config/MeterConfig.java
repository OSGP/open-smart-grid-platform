package org.opensmartgridplatform.domain.smartmetering.config;

import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MeterConfig {
  public String profile;
  public String version;
  public String description;
  public List<Setting> settings;
  public List<CosemObject> cosemObjects;
}
