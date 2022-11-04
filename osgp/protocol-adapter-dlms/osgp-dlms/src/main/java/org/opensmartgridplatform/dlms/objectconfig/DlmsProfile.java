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
import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DlmsProfile {
  public String profile;
  public String version;
  public String description;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public ParentProfile inherit;

  public List<ProfileProperty> properties;
  public List<CosemObject> objects;
}
