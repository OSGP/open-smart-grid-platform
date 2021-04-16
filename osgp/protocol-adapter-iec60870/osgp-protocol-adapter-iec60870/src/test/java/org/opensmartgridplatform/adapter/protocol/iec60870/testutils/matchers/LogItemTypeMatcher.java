/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
