/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public class IdentificationNumberFactoryTest {

    @Test
    public void testFromIdentificationDsmr4() {

        String last8Digits = "12049260";
        Long identification = 302289504L;

        IdentificationNumber identificationNumber = IdentificationNumberFactory.create(
                Protocol.DSMR_4_2_2).fromIdentification(identification);

        assertEquals(last8Digits, identificationNumber.getLast8Digits());
    }

    @Test
    public void testFromLast8DigitsDsmr4() {

        String last8Digits = "12049260";
        Long identification = 302289504L;

        IdentificationNumber identificationNumber = IdentificationNumberFactory.create(
                Protocol.DSMR_4_2_2).fromLast8Digits(last8Digits);

        assertEquals(identification, identificationNumber.getIdentificationNumber());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromInvalidLast8DigitsDsmr4() {

        IdentificationNumberFactory.create(Protocol.DSMR_4_2_2).fromLast8Digits("123A5678");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromInvalidIdentificationDsmr4() {

        IdentificationNumberFactory.create(Protocol.DSMR_4_2_2).fromIdentification(123456789L);
    }

    @Test
    public void testFromIdentificationSmr5() {

        String last8Digits = "12345BEF";
        Long identification = 305421295L;

        IdentificationNumber identificationNumber = IdentificationNumberFactory.create(
                Protocol.SMR_5_0).fromIdentification(identification);

        assertEquals(last8Digits, identificationNumber.getLast8Digits());
    }

    @Test
    public void testFromLast8DigitsSmr5() {

        String last8Digits = "12345BEF";
        Long identification = 305421295L;

        IdentificationNumber identificationNumber = IdentificationNumberFactory.create(
                Protocol.SMR_5_0).fromLast8Digits(last8Digits);

        assertEquals(identification, identificationNumber.getIdentificationNumber());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromInvalidLast8DigitsSmr5() {

        IdentificationNumberFactory.create(Protocol.SMR_5_0).fromLast8Digits("1234S678");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromInvalidIdentificationSmr5() {

        IdentificationNumberFactory.create(Protocol.SMR_5_0).fromIdentification(123456789100L);
    }

    @Test
    public void testFromNullIdentificationSmr51() {

        IdentificationNumber identificationNumber = IdentificationNumberFactory.create(
                Protocol.SMR_5_1).fromIdentification(null);

        assertNull(identificationNumber.getLast8Digits());
    }

    @Test
    public void testFromNullLast8DigitsSmr51() {

        IdentificationNumber identificationNumber = IdentificationNumberFactory.create(
                Protocol.SMR_5_1).fromLast8Digits(null);

        assertNull(identificationNumber.getIdentificationNumber());
    }

}
