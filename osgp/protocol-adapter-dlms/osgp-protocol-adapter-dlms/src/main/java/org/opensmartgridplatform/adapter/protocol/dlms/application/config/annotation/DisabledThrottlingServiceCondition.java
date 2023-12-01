// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.config.annotation;

public class DisabledThrottlingServiceCondition extends ThrottlingTypeCondition {
  public DisabledThrottlingServiceCondition() {
    super("disabled");
  }
}
