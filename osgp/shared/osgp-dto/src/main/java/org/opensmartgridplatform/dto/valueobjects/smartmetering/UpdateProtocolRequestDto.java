// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProtocolRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = 4303530157368695435L;

  private final String protocol;
  private final String protocolVersion;
  private final String protocolVariant;
}
