/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
