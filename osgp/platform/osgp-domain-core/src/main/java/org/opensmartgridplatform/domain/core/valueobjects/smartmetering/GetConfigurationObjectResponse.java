/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
