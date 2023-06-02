//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public enum LightTypeDto implements Serializable {
  RELAY,
  ONE_TO_TEN_VOLT,
  ONE_TO_TEN_VOLT_REVERSE,
  ONE_TO_TWENTY_FOUR_VOLT,
  DALI
}
