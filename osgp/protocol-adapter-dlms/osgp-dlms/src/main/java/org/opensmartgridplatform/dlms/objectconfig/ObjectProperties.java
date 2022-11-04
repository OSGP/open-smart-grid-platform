/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package org.opensmartgridplatform.dlms.objectconfig;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class ObjectProperties {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty("selectable-objects")
  public List<String> selectableObjects;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty("pq-profile")
  public PowerQualityProfile pqProfile;
}
