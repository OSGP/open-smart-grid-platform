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

public class HeatBuffer extends LogicalDevice {

    private static final String TTMP1_TMPSV_INSTMAG_F = "TTMP1.TmpSv.instMag.f";
    private static final String TTMP1_TMPSV_Q = "TTMP1.TmpSv.q";
    private static final String TTMP1_TMPSV_T = "TTMP1.TmpSv.t";
    private static final String TTMP2_TMPSV_INSTMAG_F = "TTMP2.TmpSv.instMag.f";
    private static final String TTMP2_TMPSV_Q = "TTMP2.TmpSv.q";
    private static final String TTMP2_TMPSV_T = "TTMP2.TmpSv.t";
    private static final String TTMP3_TMPSV_INSTMAG_F = "TTMP3.TmpSv.instMag.f";
    private static final String TTMP3_TMPSV_Q = "TTMP3.TmpSv.q";
    private static final String TTMP3_TMPSV_T = "TTMP3.TmpSv.t";
    private static final String KTNK1_VLMCAP_SETMAG_F = "KTNK1.VlmCap.setMag.f";

    private static final Set<String> FIXED_FLOAT_NODES = Collections.unmodifiableSet(new TreeSet<>(
            Arrays.asList(TTMP1_TMPSV_INSTMAG_F, TTMP2_TMPSV_INSTMAG_F, TTMP3_TMPSV_INSTMAG_F, KTNK1_VLMCAP_SETMAG_F)));

    private static final Set<String> QUALITY_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(TTMP1_TMPSV_Q, TTMP2_TMPSV_Q, TTMP3_TMPSV_Q)));

    private static final Set<String> TIMESTAMP_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(TTMP1_TMPSV_T, TTMP2_TMPSV_T, TTMP3_TMPSV_T)));

    private static final Map<String, Fc> FC_BY_NODE;
    static {
        final Map<String, Fc> fcByNode = new TreeMap<>();

        fcByNode.put(TTMP1_TMPSV_INSTMAG_F, Fc.MX);
        fcByNode.put(TTMP1_TMPSV_Q, Fc.MX);
        fcByNode.put(TTMP1_TMPSV_T, Fc.MX);
        fcByNode.put(TTMP2_TMPSV_INSTMAG_F, Fc.MX);
        fcByNode.put(TTMP2_TMPSV_Q, Fc.MX);
        fcByNode.put(TTMP2_TMPSV_T, Fc.MX);
        fcByNode.put(TTMP3_TMPSV_INSTMAG_F, Fc.MX);
        fcByNode.put(TTMP3_TMPSV_Q, Fc.MX);
        fcByNode.put(TTMP3_TMPSV_T, Fc.MX);

        fcByNode.put(KTNK1_VLMCAP_SETMAG_F, Fc.SP);

        FC_BY_NODE = Collections.unmodifiableMap(fcByNode);
    }

    public HeatBuffer(final String physicalDeviceName, final String logicalDeviceName, final ServerModel serverModel) {
        super(physicalDeviceName, logicalDeviceName, serverModel);
    }

    @Override
    public List<BasicDataAttribute> getAttributesAndSetValues(final Date timestamp) {

        final List<BasicDataAttribute> values = new ArrayList<>();

        values.add(this.setFixedFloat(TTMP1_TMPSV_INSTMAG_F, Fc.MX, 314));
        values.add(this.setQuality(TTMP1_TMPSV_Q, Fc.MX, QualityType.INACCURATE.getValue()));
        values.add(this.setTime(TTMP1_TMPSV_T, Fc.MX, timestamp));

        values.add(this.setFixedFloat(TTMP2_TMPSV_INSTMAG_F, Fc.MX, 324));
        values.add(this.setQuality(TTMP2_TMPSV_Q, Fc.MX, QualityType.VALIDITY_INVALID.getValue()));
        values.add(this.setTime(TTMP2_TMPSV_T, Fc.MX, timestamp));

        values.add(this.setFixedFloat(TTMP3_TMPSV_INSTMAG_F, Fc.MX, 334));
        values.add(this.setQuality(TTMP3_TMPSV_Q, Fc.MX, QualityType.VALIDITY_QUESTIONABLE.getValue()));
        values.add(this.setTime(TTMP3_TMPSV_T, Fc.MX, timestamp));

        values.add(this.setFixedFloat(KTNK1_VLMCAP_SETMAG_F, Fc.SP, 1313));

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

    @Override
    public BasicDataAttribute getAttributeAndSetValue(final String node, final String value) {
        final Fc fc = this.getFunctionalConstraint(node);
        if (fc == null) {
            throw this.illegalNodeException(node);
        }

        if (FIXED_FLOAT_NODES.contains(node)) {
            return this.setFixedFloat(node, fc, Float.parseFloat(value));
        }

        if (QUALITY_NODES.contains(node)) {
            return this.setQuality(node, fc, QualityType.valueOf(value).getValue());
        }

        if (TIMESTAMP_NODES.contains(node)) {
            return this.setTime(node, fc, this.parseDate(value));
        }
        throw this.nodeTypeNotConfiguredException(node);
    }

    @Override
    public Fc getFunctionalConstraint(final String node) {
        return FC_BY_NODE.get(node);
    }
}
