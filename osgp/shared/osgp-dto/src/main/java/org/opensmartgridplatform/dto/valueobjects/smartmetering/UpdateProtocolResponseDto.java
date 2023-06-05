// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProtocolResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = 173773882762590354L;

  private final String protocol;
  private final String protocolVersion;
  private final String protocolVariant;
}
