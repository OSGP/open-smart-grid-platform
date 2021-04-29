/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
