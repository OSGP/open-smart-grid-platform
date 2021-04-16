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
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

public class ErrorResponseMessageMatcher implements ArgumentMatcher<ResponseMessage> {

  @Override
  public boolean matches(final ResponseMessage argument) {
    return argument.getResult() == ResponseMessageResultType.NOT_OK;
  }
}
