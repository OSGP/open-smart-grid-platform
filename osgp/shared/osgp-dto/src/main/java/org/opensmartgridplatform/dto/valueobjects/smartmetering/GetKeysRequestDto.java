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

import java.util.List;

public class GetKeysRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = 490478142616065020L;

  private final List<SecretTypeDto> secretTypes;

  public GetKeysRequestDto(final List<SecretTypeDto> secretTypes) {
    this.secretTypes = secretTypes;
  }

  public List<SecretTypeDto> getsecretTypes() {
    return this.secretTypes;
  }
}
