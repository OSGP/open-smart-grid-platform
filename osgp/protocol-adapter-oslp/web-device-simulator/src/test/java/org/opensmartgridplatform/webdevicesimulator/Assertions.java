//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.webdevicesimulator;

import org.opensmartgridplatform.oslp.OslpEnvelope;

public class Assertions extends org.assertj.core.api.Assertions {

  public static OslpEnvelopeAssert assertThat(final OslpEnvelope oslpEnvelope) {
    return new OslpEnvelopeAssert(oslpEnvelope);
  }
}
