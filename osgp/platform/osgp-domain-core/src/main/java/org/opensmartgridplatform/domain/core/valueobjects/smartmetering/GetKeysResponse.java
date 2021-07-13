/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class GetKeysResponse extends ActionResponse implements Serializable {

  private static final long serialVersionUID = 2156683896582365175L;

  private final List<GetKeysResponseData> keys;

  public GetKeysResponse(final List<GetKeysResponseData> keys) {
    this.keys = keys;
  }

  public List<GetKeysResponseData> getKeys() {
    return this.keys;
  }
}
