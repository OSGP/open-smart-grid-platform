/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IdentificationNumberTest {

    String last8Digits = "12049260";
    Long identification = 302289504L;

    @Test
    public void testIdentificationNumberFromIdentification() {
        final IdentificationNumber idNumber = IdentificationNumber.fromIdentification(this.identification);
        assertEquals("last 8 digits", this.last8Digits, idNumber.getLast8Digits());
    }

    @Test
    public void testIdentificationNumberFromLast8Digits() {
        final IdentificationNumber idNumber = IdentificationNumber.fromLast8Digits(this.last8Digits);
        assertEquals("identification number", this.identification, idNumber.getIdentificationNumber());
    }
}
