package org.opensmartgridplatform.domain.smartmetering.config;

import java.util.ArrayList;
import lombok.Data;

@Data
public class MeterConfig {
  public String profile;
  public String version;
  public String description;
  public ArrayList<Setting> settings;
  public ArrayList<CosemObject> cosemObjects;
}
