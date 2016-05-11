/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;

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

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues obisCodeValues2 = mapper.map(
                obisCodeValues, com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues.class);

        Assert.assertEquals((byte) 1, obisCodeValues2.getA());
        Assert.assertEquals((byte) 2, obisCodeValues2.getB());
        Assert.assertEquals((byte) 3, obisCodeValues2.getC());
        Assert.assertEquals((byte) -22, obisCodeValues2.getD());
        Assert.assertEquals((byte) 5, obisCodeValues2.getE());
        Assert.assertEquals((byte) -1, obisCodeValues2.getF());
    }
}
