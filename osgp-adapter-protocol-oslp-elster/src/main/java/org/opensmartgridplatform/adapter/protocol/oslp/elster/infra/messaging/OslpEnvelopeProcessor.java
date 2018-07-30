/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging;

import org.opensmartgridplatform.oslp.SignedOslpEnvelopeDto;

public interface OslpEnvelopeProcessor {

    void processSignedOslpEnvelope(String deviceIdentification, SignedOslpEnvelopeDto signedOslpEnvelopeDto);

}
