/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.oslp;

import java.io.Serializable;

public class SignedOslpEnvelopeDto implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 2598572730380822536L;

    /**
     * The signed envelope which can be sent to a device.
     */
    private OslpEnvelope oslpEnvelope;

    /**
     * The DTO which was sent to the signing server.
     */
    private UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto;

    public SignedOslpEnvelopeDto(final OslpEnvelope oslpEnvelope, final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto) {
        this.oslpEnvelope = oslpEnvelope;
        this.unsignedOslpEnvelopeDto = unsignedOslpEnvelopeDto;
    }

    public OslpEnvelope getOslpEnvelope() {
        return this.oslpEnvelope;
    }

    public UnsignedOslpEnvelopeDto getUnsignedOslpEnvelopeDto() {
        return this.unsignedOslpEnvelopeDto;
    }
}
