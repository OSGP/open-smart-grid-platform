// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
