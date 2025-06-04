// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.google.protobuf.GeneratedMessage;
import org.apache.commons.codec.binary.Base64;
import org.assertj.core.api.AbstractAssert;
import org.opensmartgridplatform.oslp.LegacyOslpEnvelope;

public class OslpEnvelopeAssert extends AbstractAssert<OslpEnvelopeAssert, LegacyOslpEnvelope> {

  public OslpEnvelopeAssert(final LegacyOslpEnvelope actual) {
    super(actual, OslpEnvelopeAssert.class);
  }

  public OslpEnvelopeAssert hasDeviceId(final byte[] expectedDeviceId) {
    this.isNotNull();
    assertArrayEquals(
        expectedDeviceId,
        this.actual.getDeviceId(),
        String.format(
            "Expected message to have deviceId %s, but found: %s",
            Base64.encodeBase64String(expectedDeviceId),
            Base64.encodeBase64String(this.actual.getDeviceId())));
    return this;
  }

  public OslpEnvelopeAssert hasMessageWithName(final String expectedMessageName) {
    this.isNotNull();
    final String actualMessageName = this.messageName(this.actual);
    if (expectedMessageName == null || !expectedMessageName.equals(actualMessageName)) {
      this.failWithMessage(
          "Expected OslpEnvelope to have a payload message containing a %s; found a %s",
          expectedMessageName, actualMessageName);
    }
    return this;
  }

  private String messageName(final LegacyOslpEnvelope legacyOslpEnvelope) {
    if (legacyOslpEnvelope == null) {
      return null;
    }
    return legacyOslpEnvelope.getPayloadMessage().getAllFields().entrySet().stream()
        .filter(entry -> entry.getValue() instanceof GeneratedMessage)
        .map(entry -> entry.getKey().getName())
        .findFirst()
        .orElse(null);
  }
}
