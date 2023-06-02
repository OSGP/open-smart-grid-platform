//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
