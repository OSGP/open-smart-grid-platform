//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public class GetAllAttributeValuesResponse extends ActionResponse {

  private static final long serialVersionUID = 8942300410552414718L;

  private final String attributeValuesData;

  public GetAllAttributeValuesResponse(final String attributeValuesData) {
    this.attributeValuesData = attributeValuesData;
  }

  public String getAttributeValuesData() {
    return this.attributeValuesData;
  }
}
