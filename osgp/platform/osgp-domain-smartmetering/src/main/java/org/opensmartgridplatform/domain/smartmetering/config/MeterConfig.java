package org.opensmartgridplatform.domain.smartmetering.config;

import java.util.ArrayList;

public class MeterConfig {
  public String profile;
  public String version;
  public String description;
  public ArrayList<Setting> settings;
  public ArrayList<CosumObject> cosumObjects;
}
