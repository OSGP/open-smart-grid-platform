/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmuc.jdlms.ObisCode;

import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;

public class ObisCodeValuesTest {

    @Ignore
    @Test
    public void testObisCodeValues() {
        final ObisCodeValuesDto obisCodeValues = new ObisCodeValuesDto((byte) 1, (byte) 2, (byte) 3, (byte) 234,
                (byte) 5, (byte) 255);
        final ObisCode obisCode = new ObisCode(obisCodeValues.getA(), obisCodeValues.getB(), obisCodeValues.getC(),
                obisCodeValues.getD(), obisCodeValues.getE(), obisCodeValues.getF());

        Assert.assertEquals((byte) 1, obisCode.bytes()[0]);
        Assert.assertEquals((byte) 2, obisCode.bytes()[1]);
        Assert.assertEquals((byte) 3, obisCode.bytes()[2]);
        Assert.assertEquals((byte) 234, obisCode.bytes()[3]);
        Assert.assertEquals((byte) 5, obisCode.bytes()[4]);
        Assert.assertEquals((byte) 255, obisCode.bytes()[5]);
    }
}
