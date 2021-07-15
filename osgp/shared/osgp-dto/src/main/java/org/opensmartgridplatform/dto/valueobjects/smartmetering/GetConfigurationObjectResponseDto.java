/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class GetConfigurationObjectResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = 4779593744529504288L;

  private final ConfigurationObjectDto configurationObject;

  public GetConfigurationObjectResponseDto(final ConfigurationObjectDto configurationObject) {
    this.configurationObject = configurationObject;
  }

  public ConfigurationObjectDto getConfigurationObject() {
    return this.configurationObject;
  }
}
