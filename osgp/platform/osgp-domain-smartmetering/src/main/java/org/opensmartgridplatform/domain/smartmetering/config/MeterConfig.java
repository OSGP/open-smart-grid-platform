package org.opensmartgridplatform.domain.smartmetering.config;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@Builder
public class MeterConfig {
  public String profile;
  public String version;
  public String description;
  public List<Setting> settings;
  public List<CosemObject> cosemObjects;
}
