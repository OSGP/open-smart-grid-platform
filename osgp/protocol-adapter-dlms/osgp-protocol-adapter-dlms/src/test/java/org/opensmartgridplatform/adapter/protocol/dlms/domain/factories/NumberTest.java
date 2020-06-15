package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import java.nio.ByteBuffer;

import com.google.common.primitives.UnsignedInteger;
import org.junit.jupiter.api.Test;

/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
public class NumberTest {

    @Test
    public void testThis() {

        UnsignedInteger i = UnsignedInteger.valueOf(3598612623L);

        System.out.println("I == "+i);

        System.out.println("I as int == "+i.intValue());

        int j = (int) (i.longValue() >>1 );


        System.out.println("J == "+j);
    }

    public static byte[] getUnsignedInt(long value) {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putInt((int) value);
        return bytes;
    }
}
