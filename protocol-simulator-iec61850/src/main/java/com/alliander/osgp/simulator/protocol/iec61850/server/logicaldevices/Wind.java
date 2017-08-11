/**
 * Copyright 2017 Smart Society Services B.V.
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

public class Wind extends LogicalDevice {

    private static final String LLN0_HEALTH_STVAL = "LLN0.Health.stVal";
    private static final String LLN0_HEALTH_Q = "LLN0.Health.q";
    private static final String LLN0_HEALTH_T = "LLN0.Health.t";

    private static final String LLN0_BEH_STVAL = "LLN0.Beh.stVal";
    private static final String LLN0_BEH_Q = "LLN0.Beh.q";
    private static final String LLN0_BEH_T = "LLN0.Beh.t";

    private static final String MMXU1_TOTW_MAG_F = "MMXU1.TotW.mag.f";
    private static final String MMXU1_TOTW_Q = "MMXU1.TotW.q";
    private static final String MMXU1_TOTW_T = "MMXU1.TotW.t";

    private static final String MMXU1_TOTPF_MAG_F = "MMXU1.TotPF.mag.f";
    private static final String MMXU1_TOTPF_Q = "MMXU1.TotPF.q";
    private static final String MMXU1_TOTPF_T = "MMXU1.TotPF.t";

    private static final String MMXU1_MAXWPHS_MAG_F = "MMXU1.MaxWPhs.mag.f";
    private static final String MMXU1_MAXWPHS_Q = "MMXU1.MaxWPhs.q";
    private static final String MMXU1_MAXWPHS_T = "MMXU1.MaxWPhs.t";

    private static final String MMXU1_MINWPHS_MAG_F = "MMXU1.MinWPhs.mag.f";
    private static final String MMXU1_MINWPHS_Q = "MMXU1.MinWPhs.q";
    private static final String MMXU1_MINWPHS_T = "MMXU1.MinWPhs.t";

    // new WIND
    private static final String MMXU1_W_PHSA_CVAL_MAG_F = "MMXU1.W.phsA.cVal.mag.f";
    private static final String MMXU1_W_PHSA_Q = "MMXU1.W.phsA.q";
    private static final String MMXU1_W_PHSA_T = "MMXU1.W.phsA.t";

    private static final String MMXU1_W_PHSB_CVAL_MAG_F = "MMXU1.W.phsB.cVal.mag.f";
    private static final String MMXU1_W_PHSB_Q = "MMXU1.W.phsB.q";
    private static final String MMXU1_W_PHSB_T = "MMXU1.W.phsB.t";

    private static final String MMXU1_W_PHSC_CVAL_MAG_F = "MMXU1.W.phsC.cVal.mag.f";
    private static final String MMXU1_W_PHSC_Q = "MMXU1.W.phsC.q";
    private static final String MMXU1_W_PHSC_T = "MMXU1.W.phsC.t";

    private static final Set<String> FLOAT32_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(MMXU1_MAXWPHS_MAG_F, MMXU1_MINWPHS_MAG_F, MMXU1_TOTW_MAG_F,
                    MMXU1_TOTPF_MAG_F, MMXU1_W_PHSA_CVAL_MAG_F, MMXU1_W_PHSB_CVAL_MAG_F, MMXU1_W_PHSC_CVAL_MAG_F)));

    private static final Set<String> INT8_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(LLN0_HEALTH_STVAL, LLN0_BEH_STVAL)));

    private static final Set<String> QUALITY_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(LLN0_HEALTH_Q, LLN0_BEH_Q, MMXU1_MAXWPHS_Q, MMXU1_MINWPHS_Q,
                    MMXU1_TOTW_Q, MMXU1_TOTPF_Q, MMXU1_W_PHSA_Q, MMXU1_W_PHSB_Q, MMXU1_W_PHSC_Q)));

    private static final Set<String> TIMESTAMP_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(LLN0_HEALTH_T, LLN0_BEH_T, MMXU1_MAXWPHS_T, MMXU1_MINWPHS_T,
                    MMXU1_TOTW_T, MMXU1_TOTPF_T, MMXU1_W_PHSA_T, MMXU1_W_PHSB_T, MMXU1_W_PHSC_T)));

    private static final Map<String, Fc> FC_BY_NODE;
    static {
        final Map<String, Fc> fcByNode = new TreeMap<>();

        fcByNode.put(LLN0_HEALTH_STVAL, Fc.ST);
        fcByNode.put(LLN0_HEALTH_Q, Fc.ST);
        fcByNode.put(LLN0_HEALTH_T, Fc.ST);

        fcByNode.put(LLN0_BEH_STVAL, Fc.ST);
        fcByNode.put(LLN0_BEH_Q, Fc.ST);
        fcByNode.put(LLN0_BEH_T, Fc.ST);

        fcByNode.put(MMXU1_MAXWPHS_MAG_F, Fc.MX);
        fcByNode.put(MMXU1_MAXWPHS_Q, Fc.MX);
        fcByNode.put(MMXU1_MAXWPHS_T, Fc.MX);

        fcByNode.put(MMXU1_MINWPHS_MAG_F, Fc.MX);
        fcByNode.put(MMXU1_MINWPHS_Q, Fc.MX);
        fcByNode.put(MMXU1_MINWPHS_T, Fc.MX);

        fcByNode.put(MMXU1_TOTW_MAG_F, Fc.MX);
        fcByNode.put(MMXU1_TOTW_Q, Fc.MX);
        fcByNode.put(MMXU1_TOTW_T, Fc.MX);

        fcByNode.put(MMXU1_TOTPF_MAG_F, Fc.MX);
        fcByNode.put(MMXU1_TOTPF_Q, Fc.MX);
        fcByNode.put(MMXU1_TOTPF_T, Fc.MX);

        fcByNode.put(MMXU1_W_PHSA_CVAL_MAG_F, Fc.MX);
        fcByNode.put(MMXU1_W_PHSA_Q, Fc.MX);
        fcByNode.put(MMXU1_W_PHSA_T, Fc.MX);

        fcByNode.put(MMXU1_W_PHSB_CVAL_MAG_F, Fc.MX);
        fcByNode.put(MMXU1_W_PHSB_Q, Fc.MX);
        fcByNode.put(MMXU1_W_PHSB_T, Fc.MX);

        fcByNode.put(MMXU1_W_PHSC_CVAL_MAG_F, Fc.MX);
        fcByNode.put(MMXU1_W_PHSC_Q, Fc.MX);
        fcByNode.put(MMXU1_W_PHSC_T, Fc.MX);

        FC_BY_NODE = Collections.unmodifiableMap(fcByNode);
    }

    public Wind(final String physicalDeviceName, final String logicalDeviceName, final ServerModel serverModel) {
        super(physicalDeviceName, logicalDeviceName, serverModel);
    }

    @Override
    public List<BasicDataAttribute> getAttributesAndSetValues(final Date timestamp) {
        final List<BasicDataAttribute> values = new ArrayList<>();

        values.add(this.setRandomByte(LLN0_HEALTH_STVAL, Fc.ST, 1, 2));
        values.add(this.setQuality(LLN0_HEALTH_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(LLN0_HEALTH_T, Fc.ST, timestamp));

        values.add(this.setRandomByte(LLN0_BEH_STVAL, Fc.ST, 1, 2));
        values.add(this.setQuality(LLN0_BEH_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(LLN0_BEH_T, Fc.ST, timestamp));

        values.add(this.setRandomFloat(MMXU1_MAXWPHS_MAG_F, Fc.MX, 500, 1000));
        values.add(this.setQuality(MMXU1_MAXWPHS_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU1_MAXWPHS_T, Fc.MX, timestamp));

        values.add(this.setRandomFloat(MMXU1_MINWPHS_MAG_F, Fc.MX, 0, 500));
        values.add(this.setQuality(MMXU1_MINWPHS_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU1_MINWPHS_T, Fc.MX, timestamp));

        values.add(this.setRandomFloat(MMXU1_TOTW_MAG_F, Fc.MX, 0, 1000));
        values.add(this.setQuality(MMXU1_TOTW_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU1_TOTW_T, Fc.MX, timestamp));

        values.add(this.setRandomFloat(MMXU1_TOTPF_MAG_F, Fc.MX, 0, 1000));
        values.add(this.setQuality(MMXU1_TOTPF_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU1_TOTPF_T, Fc.MX, timestamp));

        values.add(this.setRandomFloat(MMXU1_W_PHSA_CVAL_MAG_F, Fc.MX, 0, 1000));
        values.add(this.setQuality(MMXU1_W_PHSA_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU1_W_PHSA_T, Fc.MX, timestamp));

        values.add(this.setRandomFloat(MMXU1_W_PHSB_CVAL_MAG_F, Fc.MX, 0, 1000));
        values.add(this.setQuality(MMXU1_W_PHSB_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU1_W_PHSB_T, Fc.MX, timestamp));

        values.add(this.setRandomFloat(MMXU1_W_PHSC_CVAL_MAG_F, Fc.MX, 0, 1000));
        values.add(this.setQuality(MMXU1_W_PHSC_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU1_W_PHSC_T, Fc.MX, timestamp));

        return values;
    }

    @Override
    public BasicDataAttribute getAttributeAndSetValue(final String node, final String value) {
        final Fc fc = this.getFunctionalConstraint(node);
        if (fc == null) {
            throw this.illegalNodeException(node);
        }

        if (FLOAT32_NODES.contains(node)) {
            return this.setFixedFloat(node, fc, Float.parseFloat(value));
        }

        if (INT8_NODES.contains(node)) {
            return this.setByte(node, fc, Byte.parseByte(value));
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
