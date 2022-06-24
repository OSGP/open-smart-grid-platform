package org.opensmartgridplatform.domain.smartmetering.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class Setting {
  @JsonProperty("firmware update type")
  public String firmwareUpdateType;
}
