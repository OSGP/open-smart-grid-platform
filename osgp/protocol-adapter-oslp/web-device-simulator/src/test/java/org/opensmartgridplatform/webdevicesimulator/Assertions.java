/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator;

import org.opensmartgridplatform.oslp.OslpEnvelope;

public class Assertions extends org.assertj.core.api.Assertions {

    public static OslpEnvelopeAssert assertThat(final OslpEnvelope oslpEnvelope) {
        return new OslpEnvelopeAssert(oslpEnvelope);
    }
}
