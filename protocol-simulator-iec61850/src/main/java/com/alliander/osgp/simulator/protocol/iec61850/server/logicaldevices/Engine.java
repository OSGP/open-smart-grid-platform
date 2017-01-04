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

public class Engine extends LogicalDevice {

    public Engine(final String physicalDeviceName, final String logicalDeviceName, final ServerModel serverModel) {
        super(physicalDeviceName, logicalDeviceName, serverModel);
    }

    @Override
    public List<BasicDataAttribute> getAttributesAndSetValues(final Date timestamp) {
        final List<BasicDataAttribute> values = new ArrayList<>();

        values.add(this.setRandomByte("LLN0.Health.stVal", Fc.ST, 1, 2));
        values.add(this.setQuality("LLN0.Health.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("LLN0.Health.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("LLN0.Beh.stVal", Fc.ST, 1, 2));
        values.add(this.setQuality("LLN0.Beh.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("LLN0.Beh.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("LLN0.Mod.stVal", Fc.ST, 1, 2));
        values.add(this.setQuality("LLN0.Mod.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("LLN0.Mod.t", Fc.ST, timestamp));

        values.add(this.setRandomFloat("MMXU1.MaxWPhs.mag.f", Fc.MX, 500, 1000));
        values.add(this.setQuality("MMXU1.MaxWPhs.q", Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("MMXU1.MaxWPhs.t", Fc.MX, timestamp));

        values.add(this.setRandomFloat("MMXU1.MinWPhs.mag.f", Fc.MX, 0, 500));
        values.add(this.setQuality("MMXU1.MinWPhs.q", Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("MMXU1.MinWPhs.t", Fc.MX, timestamp));

        values.add(this.setRandomFloat("MMXU1.TotW.mag.f", Fc.MX, 0, 1000));
        values.add(this.setQuality("MMXU1.TotW.q", Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("MMXU1.TotW.t", Fc.MX, timestamp));

        values.add(this.setRandomFloat("DRCC1.OutWSet.subVal.f", Fc.SV, 0, 1000));
        values.add(this.setQuality("DRCC1.OutWSet.subQ", Fc.SV, QualityType.VALIDITY_GOOD.getValue()));

        values.add(this.setRandomFloat("DGEN1.TotWh.mag.f", Fc.MX, 0, 1000));
        values.add(this.setQuality("DGEN1.TotWh.q", Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("DGEN1.TotWh.t", Fc.MX, timestamp));

        values.add(this.setRandomByte("DGEN1.GnOpSt.stVal", Fc.ST, 1, 2));
        values.add(this.setQuality("DGEN1.GnOpSt.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("DGEN1.GnOpSt.t", Fc.ST, timestamp));

        values.add(this.incrementInt("DGEN1.OpTmsRs.stVal", Fc.ST));
        values.add(this.setQuality("DGEN1.OpTmsRs.q", Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime("DGEN1.OpTmsRs.t", Fc.ST, timestamp));

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

    @Override
    public BasicDataAttribute getAttributeAndSetValue(final String node, final String value) {
        throw this.nodeTypeNotConfiguredException(node);
    }

    @Override
    public Fc getFunctionalConstraint(final String node) {
        return null;
    }
}
