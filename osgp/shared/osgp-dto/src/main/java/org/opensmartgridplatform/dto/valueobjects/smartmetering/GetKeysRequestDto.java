// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.List;

public class GetKeysRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = 490478142616065020L;

  private final List<SecretTypeDto> secretTypes;

  public GetKeysRequestDto(final List<SecretTypeDto> secretTypes) {
    this.secretTypes = secretTypes;
  }

  public List<SecretTypeDto> getSecretTypes() {
    return this.secretTypes;
  }
}
