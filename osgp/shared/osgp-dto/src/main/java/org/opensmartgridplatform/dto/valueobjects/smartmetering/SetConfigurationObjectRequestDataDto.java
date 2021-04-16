/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetConfigurationObjectRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = -381163520662276868L;

  private final ConfigurationObjectDto configurationObject;

  public SetConfigurationObjectRequestDataDto(final ConfigurationObjectDto configurationObject) {
    this.configurationObject = configurationObject;
  }

  public ConfigurationObjectDto getConfigurationObject() {
    return this.configurationObject;
  }
}
