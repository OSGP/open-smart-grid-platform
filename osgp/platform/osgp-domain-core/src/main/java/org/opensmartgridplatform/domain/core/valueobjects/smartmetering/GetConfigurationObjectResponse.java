// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class GetConfigurationObjectResponse extends ActionResponse implements Serializable {

  private static final long serialVersionUID = 3279405192677864979L;

  private final ConfigurationObject configurationObject;

  public GetConfigurationObjectResponse(final ConfigurationObject configurationObject) {
    this.configurationObject = configurationObject;
  }

  public ConfigurationObject getConfigurationObject() {
    return this.configurationObject;
  }
}
