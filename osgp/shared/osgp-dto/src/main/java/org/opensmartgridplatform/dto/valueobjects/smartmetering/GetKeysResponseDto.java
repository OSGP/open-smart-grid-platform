//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.List;

public class GetKeysResponseDto extends ActionResponseDto {

  private final List<KeyDto> keys;

  private static final long serialVersionUID = 4319141619048777092L;

  public GetKeysResponseDto(final List<KeyDto> keys) {
    this.keys = keys;
  }

  public List<KeyDto> getKeys() {
    return this.keys;
  }
}
