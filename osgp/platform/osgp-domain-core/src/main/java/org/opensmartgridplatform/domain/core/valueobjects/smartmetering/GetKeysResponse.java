// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
