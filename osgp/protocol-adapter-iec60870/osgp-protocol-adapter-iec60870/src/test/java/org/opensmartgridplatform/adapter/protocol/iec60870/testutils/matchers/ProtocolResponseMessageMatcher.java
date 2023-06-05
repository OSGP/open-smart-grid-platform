// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.testutils.matchers;

import java.util.Objects;
import org.mockito.ArgumentMatcher;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;

public class ProtocolResponseMessageMatcher implements ArgumentMatcher<ProtocolResponseMessage> {

  private final ProtocolResponseMessage responseMessage;

  public ProtocolResponseMessageMatcher(final ProtocolResponseMessage responseMessage) {
    this.responseMessage = responseMessage;
  }

  @Override
  public boolean matches(final ProtocolResponseMessage argument) {
    if (!Objects.equals(
        argument.getDeviceIdentification(), this.responseMessage.getDeviceIdentification())) {
      return false;
    }
    if (!Objects.equals(argument.getMessageType(), this.responseMessage.getMessageType())) {
      return false;
    }
    if (!Objects.equals(argument.getDataObject(), this.responseMessage.getDataObject())) {
      return false;
    }
    return Objects.equals(argument.getResult(), this.responseMessage.getResult());
  }
}
