/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.openmuc.openiec61850.BasicDataAttribute;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.ServerModel;

import com.alliander.osgp.simulator.protocol.iec61850.server.QualityType;

public class Chp extends LogicalDevice {
    private static final String TTMP1_TMPSV_INSTMAG_F = "TTMP1.TmpSv.instMag.f";
    private static final String TTMP2_TMPSV_INSTMAG_F = "TTMP2.TmpSv.instMag.f";

    private static final Set<String> FIXED_FLOAT_NODES = Collections.unmodifiableSet(new TreeSet<>(Arrays.asList(
            TTMP1_TMPSV_INSTMAG_F, TTMP2_TMPSV_INSTMAG_F)));

    private static final Map<String, Fc> FC_BY_NODE;
    static {
        final Map<String, Fc> fcByNode = new TreeMap<>();

        fcByNode.put(TTMP1_TMPSV_INSTMAG_F, Fc.MX);
        fcByNode.put(TTMP2_TMPSV_INSTMAG_F, Fc.MX);

        FC_BY_NODE = Collections.unmodifiableMap(fcByNode);
    }

    public Chp(final String physicalDeviceName, final String logicalDeviceName, final ServerModel serverModel) {
        super(physicalDeviceName, logicalDeviceName, serverModel);
    }

    @Override
    public List<BasicDataAttribute> getAttributesAndSetValues(final Date timestamp) {

        final List<BasicDataAttribute> values = new ArrayList<>();

        values.add(this.setFixedFloat(TTMP1_TMPSV_INSTMAG_F, Fc.MX, 314));
        values.add(this.setQuality("TTMP1.TmpSv.q", Fc.MX, QualityType.INACCURATE.getValue()));

        values.add(this.setFixedFloat(TTMP2_TMPSV_INSTMAG_F, Fc.MX, 324));
        values.add(this.setQuality("TTMP2.TmpSv.q", Fc.MX, QualityType.VALIDITY_INVALID.getValue()));

        values.add(this.setFixedFloat("MFLW1.FlwRte.mag.f", Fc.MX, 314));
        values.add(this.setQuality("MFLW1.FlwRte.q", Fc.MX, QualityType.INACCURATE.getValue()));
        values.add(this.setTime("MFLW1.FlwRte.t", Fc.MX, timestamp));

        values.add(this.setFixedFloat("MFLW2.FlwRte.mag.f", Fc.MX, 314));
        values.add(this.setQuality("MFLW2.FlwRte.q", Fc.MX, QualityType.INACCURATE.getValue()));
        values.add(this.setTime("MFLW2.FlwRte.t", Fc.MX, timestamp));

        values.add(this.setRandomFloat("MMXU1.MaxWPhs.mag.f", Fc.MX, 500, 1000));
        values.add(this.setTime("MMXU1.MaxWPhs.t", Fc.MX, timestamp));

        values.add(this.setRandomFloat("MMXU1.MinWPhs.mag.f", Fc.MX, 0, 500));
        values.add(this.setTime("MMXU1.MinWPhs.t", Fc.MX, timestamp));

        values.add(this.setFixedFloat("MMXU1.TotW.mag.f", Fc.MX, 500));
        values.add(this.setTime("MMXU1.TotW.t", Fc.MX, timestamp));

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
        final Fc fc = FC_BY_NODE.get(node);
        if (fc == null) {
            throw this.illegalNodeException(node);
        }

        if (FIXED_FLOAT_NODES.contains(node)) {
            return this.setFixedFloat(node, fc, Integer.parseInt(value));
        }

        throw this.nodeTypeNotConfiguredException(node);
    }

    @Override
    public Fc getFunctionalConstraint(final String node) {
        return FC_BY_NODE.get(node);
    }
}
