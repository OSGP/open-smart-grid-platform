// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class GetThdFingerprintResponse extends ActionResponse implements Serializable {

  private static final long serialVersionUID = 6582902371132344574L;

  private final ThdFingerprint thdFingerprint;

  public GetThdFingerprintResponse(final ThdFingerprint thdFingerprint) {
    this.thdFingerprint = thdFingerprint;
  }

  public ThdFingerprint getThdFingerprint() {
    return this.thdFingerprint;
  }
}
