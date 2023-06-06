// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import lombok.Getter;

@Getter
public class KeyDto extends ActionResponseDto {

  private final SecretTypeDto secretType;

  private final byte[] secret;

  private static final long serialVersionUID = 7943014080700810139L;

  public KeyDto(final SecretTypeDto secretType, final byte[] secret) {
    this.secretType = secretType;
    this.secret = secret;
  }
}
