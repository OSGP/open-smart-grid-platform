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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DlmsProfile {
  private String profile;
  private String version;
  private String description;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private ParentProfile inherit;

  private List<ProfileProperty> properties;
  private List<CosemObject> objects;

  public Map<DlmsObjectType, CosemObject> objectMap;

  public void createMap() {
    this.objectMap = new EnumMap<>(DlmsObjectType.class);
    this.objects.forEach(
        cosemObject ->
            this.objectMap.put(DlmsObjectType.fromValue(cosemObject.getTag()), cosemObject));
  }

  public String getProfileWithVersion() {
    return this.profile + " " + this.version;
  }
}
