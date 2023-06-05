// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.testutils.matchers;

import org.mockito.ArgumentMatcher;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;

public class LogItemTypeMatcher implements ArgumentMatcher<LogItem> {

  private String typeId;

  public LogItemTypeMatcher(final String typeId) {
    this.typeId = typeId;
  }

  @Override
  public boolean matches(final LogItem argument) {
    if (argument == null) {
      return false;
    }
    return argument.getMessage().contains(this.typeId);
  }
}
