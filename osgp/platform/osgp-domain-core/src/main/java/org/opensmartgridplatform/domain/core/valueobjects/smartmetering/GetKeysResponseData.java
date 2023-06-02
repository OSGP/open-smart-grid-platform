//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class GetKeysResponseData extends ActionResponse implements Serializable {

  private static final long serialVersionUID = 2909496927015589991L;

  private final SecretType secretType;
  private final byte[] secretValue;

  public GetKeysResponseData(final SecretType secretType, final byte[] secretValue) {
    this.secretType = secretType;
    this.secretValue = secretValue;
  }

  public SecretType getSecretType() {
    return this.secretType;
  }

  public byte[] getSecretValue() {
    return this.secretValue;
  }
}
