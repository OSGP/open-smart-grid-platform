// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SynchronizeTimeRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = 4501989071785153393L;

  private final String timeZone;
}
