/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import org.junit.Assert;
import org.junit.Test;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;

public class ObisCodeValuesTest {

    @Test
    public void testObisCodeValues() {
        final AdhocMapper mapper = new AdhocMapper();

        final ObisCodeValues obisCodeValues = new ObisCodeValues();
        obisCodeValues.setA((short) 1);
        obisCodeValues.setB((short) 2);
        obisCodeValues.setC((short) 3);
        obisCodeValues.setD((short) 234);
        obisCodeValues.setE((short) 5);
        obisCodeValues.setF((short) 255);

        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues obisCodeValues2 = mapper.map(
                obisCodeValues, org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues.class);

        Assert.assertEquals((byte) 1, obisCodeValues2.getA());
        Assert.assertEquals((byte) 2, obisCodeValues2.getB());
        Assert.assertEquals((byte) 3, obisCodeValues2.getC());
        Assert.assertEquals((byte) -22, obisCodeValues2.getD());
        Assert.assertEquals((byte) 5, obisCodeValues2.getE());
        Assert.assertEquals((byte) -1, obisCodeValues2.getF());
    }
    
    @Test
    public void testObisCodeValues2() {
        final AdhocMapper mapper = new AdhocMapper();

        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues obisCodeValues1 =
                new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues((byte)1, (byte)2, (byte)3, (byte)-22, (byte)5, (byte)-1);

        final ObisCodeValues obisCodeValues2 = mapper.map(
                obisCodeValues1, ObisCodeValues.class);

        Assert.assertEquals((short) 1, obisCodeValues2.getA());
        Assert.assertEquals((short) 2, obisCodeValues2.getB());
        Assert.assertEquals((short) 3, obisCodeValues2.getC());
        Assert.assertEquals((short) 234, obisCodeValues2.getD());
        Assert.assertEquals((short) 5, obisCodeValues2.getE());
        Assert.assertEquals((short) 255, obisCodeValues2.getF());
    }    
}
