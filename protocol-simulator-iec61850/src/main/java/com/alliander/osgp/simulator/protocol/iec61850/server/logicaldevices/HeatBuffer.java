/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmuc.openiec61850.BasicDataAttribute;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.ServerModel;

import com.alliander.osgp.simulator.protocol.iec61850.server.QualityType;

public class HeatBuffer extends LogicalDevice {

    private static final String MMXU = "MMXU";
    private static final String MMTR = "MMTR";

    public HeatBuffer(final String physicalDeviceName, final String logicalDeviceName, final ServerModel serverModel) {
        super(physicalDeviceName, logicalDeviceName, serverModel);
    }

    @Override
    public List<BasicDataAttribute> getValues(final Date timestamp) {

        final List<BasicDataAttribute> values = new ArrayList<>();

        values.add(this.setFixedFloat("TTMP1.TmpSv.instMag.f", Fc.MX, 314));
        values.add(this.setQuality("TTMP1.TmpSv.q", Fc.MX, QualityType.INACCURATE.getValue()));

        values.add(this.setFixedFloat("TTMP2.TmpSv.instMag.f", Fc.MX, 324));
        values.add(this.setQuality("TTMP2.TmpSv.q", Fc.MX, QualityType.VALIDITY_INVALID.getValue()));

        values.add(this.setFixedFloat("TTMP3.TmpSv.instMag.f", Fc.MX, 334));
        values.add(this.setQuality("TTMP3.TmpSv.q", Fc.MX, QualityType.VALIDITY_QUESTIONABLE.getValue()));

        values.add(this.setFixedFloat("KTNK1.VlmCap.setMag.f", Fc.SP, 1313));

        values.add(this.setRandomByte("LLN0.Health.stVal", Fc.ST, 1, 2));
        values.add(this.setQuality("LLN0.Health.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("LLN0.Health.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("LLN0.Beh.stVal", Fc.ST, 1, 2));
        values.add(this.setQuality("LLN0.Beh.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("LLN0.Beh.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("LLN0.Mod.stVal", Fc.ST, 1, 2));
        values.add(this.setQuality("LLN0.Mod.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("LLN0.Mod.t", Fc.ST, timestamp));

        values.add(this.setBoolean("GGIO1.Alm1.stVal", Fc.ST, false));
        values.add(this.setQuality("GGIO1.Alm1.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("GGIO1.Alm1.t", Fc.ST, timestamp));

        values.add(this.setBoolean("GGIO1.Alm2.stVal", Fc.ST, false));
        values.add(this.setQuality("GGIO1.Alm2.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("GGIO1.Alm2.t", Fc.ST, timestamp));

        values.add(this.setBoolean("GGIO1.Alm3.stVal", Fc.ST, false));
        values.add(this.setQuality("GGIO1.Alm3.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("GGIO1.Alm3.t", Fc.ST, timestamp));

        values.add(this.setBoolean("GGIO1.Alm4.stVal", Fc.ST, false));
        values.add(this.setQuality("GGIO1.Alm4.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("GGIO1.Alm4.t", Fc.ST, timestamp));

        values.add(this.setRandomInt("GGIO1.IntIn1.stVal", Fc.ST, 1, 100));
        values.add(this.setQuality("GGIO1.IntIn1.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("GGIO1.IntIn1.t", Fc.ST, timestamp));

        values.add(this.setBoolean("GGIO1.Wrn1.stVal", Fc.ST, false));
        values.add(this.setQuality("GGIO1.Wrn1.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("GGIO1.Wrn1.t", Fc.ST, timestamp));

        values.add(this.setBoolean("GGIO1.Wrn2.stVal", Fc.ST, false));
        values.add(this.setQuality("GGIO1.Wrn2.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("GGIO1.Wrn2.t", Fc.ST, timestamp));

        values.add(this.setBoolean("GGIO1.Wrn3.stVal", Fc.ST, false));
        values.add(this.setQuality("GGIO1.Wrn3.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("GGIO1.Wrn3.t", Fc.ST, timestamp));

        values.add(this.setBoolean("GGIO1.Wrn4.stVal", Fc.ST, false));
        values.add(this.setQuality("GGIO1.Wrn4.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("GGIO1.Wrn4.t", Fc.ST, timestamp));

        values.add(this.setRandomInt("GGIO1.IntIn2.stVal", Fc.ST, 1, 100));
        values.add(this.setQuality("GGIO1.IntIn2.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("GGIO1.IntIn2.t", Fc.ST, timestamp));

        return values;
    }

}
