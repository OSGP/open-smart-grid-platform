// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.testutil;

import java.util.Date;

/** Creates instances, for testing purposes only. */
public class DateBuilder {
  private static int counter = 0;

  public Date build() {
    counter += 1;
    return new Date(24L * 60 * 60 * 1000 * counter);
  }
}
