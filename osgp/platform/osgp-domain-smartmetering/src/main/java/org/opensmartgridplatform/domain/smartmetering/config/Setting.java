package org.opensmartgridplatform.domain.smartmetering.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Setting {
  @JsonProperty("firmware update type")
  public String firmwareUpdateType;
}
