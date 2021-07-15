/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class GetAllAttributeValuesResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -936146933525914836L;

  private String attributeValuesData;

  public GetAllAttributeValuesResponseDto() {
    // Default constructor
  }

  public GetAllAttributeValuesResponseDto(final String attributeValuesData) {
    this.attributeValuesData = attributeValuesData;
  }

  public String getAttributeValuesData() {
    return this.attributeValuesData;
  }
}
