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

public class Load extends LogicalDevice {

    private static final String LLN0_MOD_STVAL = "LLN0.Mod.stVal";
    private static final String LLN0_MOD_Q = "LLN0.Mod.q";
    private static final String LLN0_MOD_T = "LLN0.Mod.t";

    private static final String LLN0_BEH_STVAL = "LLN0.Beh.stVal";
    private static final String LLN0_BEH_Q = "LLN0.Beh.q";
    private static final String LLN0_BEH_T = "LLN0.Beh.t";

    private static final String LLN0_HEALTH_STVAL = "LLN0.Health.stVal";
    private static final String LLN0_HEALTH_Q = "LLN0.Health.q";
    private static final String LLN0_HEALTH_T = "LLN0.Health.t";

    private static final String MMXU1_TOTW_MAG_F = "MMXU1.TotW.mag.f";
    private static final String MMXU1_TOTW_Q = "MMXU1.TotW.q";
    private static final String MMXU1_TOTW_T = "MMXU1.TotW.t";

    private static final String MMXU1_MINWPHS_MAG_F = "MMXU1.MinWPhs.mag.f";
    private static final String MMXU1_MINWPHS_Q = "MMXU1.MinWPhs.q";
    private static final String MMXU1_MINWPHS_T = "MMXU1.MinWPhs.t";

    private static final String MMXU1_MAXWPHS_MAG_F = "MMXU1.MaxWPhs.mag.f";
    private static final String MMXU1_MAXWPHS_Q = "MMXU1.MaxWPhs.q";
    private static final String MMXU1_MAXWPHS_T = "MMXU1.MaxWPhs.t";

    private static final String MMXU2_TOTW_MAG_F = "MMXU2.TotW.mag.f";
    private static final String MMXU2_TOTW_Q = "MMXU2.TotW.q";
    private static final String MMXU2_TOTW_T = "MMXU2.TotW.t";

    private static final String MMXU2_MINWPHS_MAG_F = "MMXU2.MinWPhs.mag.f";
    private static final String MMXU2_MINWPHS_Q = "MMXU2.MinWPhs.q";
    private static final String MMXU2_MINWPHS_T = "MMXU2.MinWPhs.t";

    private static final String MMXU2_MAXWPHS_MAG_F = "MMXU2.MaxWPhs.mag.f";
    private static final String MMXU2_MAXWPHS_Q = "MMXU2.MaxWPhs.q";
    private static final String MMXU2_MAXWPHS_T = "MMXU2.MaxWPhs.t";

    private static final String MMTR1_TOTWH_ACTVAL = "MMTR1.TotWh.actVal";
    private static final String MMTR1_TOTWH_Q = "MMTR1.TotWh.q";
    private static final String MMTR1_TOTWH_T = "MMTR1.TotWh.t";

    private static final String GGIO1_ALM1_STVAL = "GGIO1.Alm1.stVal";
    private static final String GGIO1_ALM1_Q = "GGIO1.Alm1.q";
    private static final String GGIO1_ALM1_T = "GGIO1.Alm1.t";

    private static final String GGIO1_ALM2_STVAL = "GGIO1.Alm2.stVal";
    private static final String GGIO1_ALM2_Q = "GGIO1.Alm2.q";
    private static final String GGIO1_ALM2_T = "GGIO1.Alm2.t";

    private static final String GGIO1_ALM3_STVAL = "GGIO1.Alm3.stVal";
    private static final String GGIO1_ALM3_Q = "GGIO1.Alm3.q";
    private static final String GGIO1_ALM3_T = "GGIO1.Alm3.t";

    private static final String GGIO1_ALM4_STVAL = "GGIO1.Alm4.stVal";
    private static final String GGIO1_ALM4_Q = "GGIO1.Alm4.q";
    private static final String GGIO1_ALM4_T = "GGIO1.Alm4.t";

    private static final String GGIO1_INTIN1_STVAL = "GGIO1.IntIn1.stVal";
    private static final String GGIO1_INTIN1_Q = "GGIO1.IntIn1.q";
    private static final String GGIO1_INTIN1_T = "GGIO1.IntIn1.t";

    private static final String GGIO1_INTIN2_STVAL = "GGIO1.IntIn2.stVal";
    private static final String GGIO1_INTIN2_Q = "GGIO1.IntIn2.q";
    private static final String GGIO1_INTIN2_T = "GGIO1.IntIn2.t";

    private static final String GGIO1_WRN1_STVAL = "GGIO1.Wrn1.stVal";
    private static final String GGIO1_WRN1_Q = "GGIO1.Wrn1.q";
    private static final String GGIO1_WRN1_T = "GGIO1.Wrn1.t";

    private static final String GGIO1_WRN2_STVAL = "GGIO1.Wrn2.stVal";
    private static final String GGIO1_WRN2_Q = "GGIO1.Wrn2.q";
    private static final String GGIO1_WRN2_T = "GGIO1.Wrn2.t";

    private static final String GGIO1_WRN3_STVAL = "GGIO1.Wrn3.stVal";
    private static final String GGIO1_WRN3_Q = "GGIO1.Wrn3.q";
    private static final String GGIO1_WRN3_T = "GGIO1.Wrn3.t";

    private static final String GGIO1_WRN4_STVAL = "GGIO1.Wrn4.stVal";
    private static final String GGIO1_WRN4_Q = "GGIO1.Wrn4.q";
    private static final String GGIO1_WRN4_T = "GGIO1.Wrn4.t";

    private static final Set<String> BOOLEAN_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(GGIO1_ALM1_STVAL, GGIO1_ALM2_STVAL, GGIO1_ALM3_STVAL,
                    GGIO1_ALM4_STVAL, GGIO1_WRN1_STVAL, GGIO1_WRN2_STVAL, GGIO1_WRN3_STVAL, GGIO1_WRN4_STVAL)));

    private static final Set<String> FLOAT32_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(MMXU1_MAXWPHS_MAG_F, MMXU1_MINWPHS_MAG_F, MMXU1_TOTW_MAG_F,
                    MMXU2_MAXWPHS_MAG_F, MMXU2_MINWPHS_MAG_F, MMXU2_TOTW_MAG_F)));

    private static final Set<String> INT8_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(LLN0_HEALTH_STVAL, LLN0_MOD_STVAL, LLN0_BEH_STVAL)));

    private static final Set<String> INT32_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(GGIO1_INTIN1_STVAL, GGIO1_INTIN2_STVAL)));

    private static final Set<String> QUALITY_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(LLN0_HEALTH_Q, LLN0_BEH_Q, LLN0_MOD_Q, MMXU1_MAXWPHS_Q,
                    MMXU1_MINWPHS_Q, MMXU1_TOTW_Q, MMXU2_MAXWPHS_Q, MMXU2_MINWPHS_Q, MMXU2_TOTW_Q, GGIO1_ALM1_Q,
                    GGIO1_ALM2_Q, GGIO1_ALM3_Q, GGIO1_ALM4_Q, GGIO1_INTIN1_Q, GGIO1_INTIN2_Q, GGIO1_WRN1_Q,
                    GGIO1_WRN2_Q, GGIO1_WRN3_Q, GGIO1_WRN4_Q)));

    private static final Set<String> TIMESTAMP_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(LLN0_HEALTH_T, LLN0_BEH_T, LLN0_MOD_T, MMXU1_MAXWPHS_T,
                    MMXU1_MINWPHS_T, MMXU1_TOTW_T, MMXU2_MAXWPHS_T, MMXU2_MINWPHS_T, MMXU2_TOTW_T, GGIO1_ALM1_T,
                    GGIO1_ALM2_T, GGIO1_ALM3_T, GGIO1_ALM4_T, GGIO1_INTIN1_T, GGIO1_INTIN2_T, GGIO1_WRN1_T,
                    GGIO1_WRN2_T, GGIO1_WRN3_T, GGIO1_WRN4_T)));

    private static final Map<String, Fc> FC_BY_NODE;
    static {
        final Map<String, Fc> fcByNode = new TreeMap<>();

        fcByNode.put(LLN0_HEALTH_STVAL, Fc.ST);
        fcByNode.put(LLN0_HEALTH_Q, Fc.ST);
        fcByNode.put(LLN0_HEALTH_T, Fc.ST);

        fcByNode.put(LLN0_BEH_STVAL, Fc.ST);
        fcByNode.put(LLN0_BEH_Q, Fc.ST);
        fcByNode.put(LLN0_BEH_T, Fc.ST);

        fcByNode.put(LLN0_MOD_STVAL, Fc.ST);
        fcByNode.put(LLN0_MOD_Q, Fc.ST);
        fcByNode.put(LLN0_MOD_T, Fc.ST);

        fcByNode.put(MMXU1_MAXWPHS_MAG_F, Fc.MX);
        fcByNode.put(MMXU1_MAXWPHS_Q, Fc.MX);
        fcByNode.put(MMXU1_MAXWPHS_T, Fc.MX);

        fcByNode.put(MMXU1_MINWPHS_MAG_F, Fc.MX);
        fcByNode.put(MMXU1_MINWPHS_Q, Fc.MX);
        fcByNode.put(MMXU1_MINWPHS_T, Fc.MX);

        fcByNode.put(MMXU1_TOTW_MAG_F, Fc.MX);
        fcByNode.put(MMXU1_TOTW_Q, Fc.MX);
        fcByNode.put(MMXU1_TOTW_T, Fc.MX);

        fcByNode.put(MMXU2_MAXWPHS_MAG_F, Fc.MX);
        fcByNode.put(MMXU2_MAXWPHS_Q, Fc.MX);
        fcByNode.put(MMXU2_MAXWPHS_T, Fc.MX);

        fcByNode.put(MMXU2_MINWPHS_MAG_F, Fc.MX);
        fcByNode.put(MMXU2_MINWPHS_Q, Fc.MX);
        fcByNode.put(MMXU2_MINWPHS_T, Fc.MX);

        fcByNode.put(MMXU2_TOTW_MAG_F, Fc.MX);
        fcByNode.put(MMXU2_TOTW_Q, Fc.MX);
        fcByNode.put(MMXU2_TOTW_T, Fc.MX);

        fcByNode.put(GGIO1_ALM1_STVAL, Fc.ST);
        fcByNode.put(GGIO1_ALM1_Q, Fc.ST);
        fcByNode.put(GGIO1_ALM1_T, Fc.ST);

        fcByNode.put(GGIO1_ALM2_STVAL, Fc.ST);
        fcByNode.put(GGIO1_ALM2_Q, Fc.ST);
        fcByNode.put(GGIO1_ALM2_T, Fc.ST);

        fcByNode.put(GGIO1_ALM3_STVAL, Fc.ST);
        fcByNode.put(GGIO1_ALM3_Q, Fc.ST);
        fcByNode.put(GGIO1_ALM3_T, Fc.ST);

        fcByNode.put(GGIO1_ALM4_STVAL, Fc.ST);
        fcByNode.put(GGIO1_ALM4_Q, Fc.ST);
        fcByNode.put(GGIO1_ALM4_T, Fc.ST);

        fcByNode.put(GGIO1_INTIN1_STVAL, Fc.ST);
        fcByNode.put(GGIO1_INTIN1_Q, Fc.ST);
        fcByNode.put(GGIO1_INTIN1_T, Fc.ST);

        fcByNode.put(GGIO1_INTIN2_STVAL, Fc.ST);
        fcByNode.put(GGIO1_INTIN2_Q, Fc.ST);
        fcByNode.put(GGIO1_INTIN2_T, Fc.ST);

        fcByNode.put(GGIO1_WRN1_STVAL, Fc.ST);
        fcByNode.put(GGIO1_WRN1_Q, Fc.ST);
        fcByNode.put(GGIO1_WRN1_T, Fc.ST);

        fcByNode.put(GGIO1_WRN2_STVAL, Fc.ST);
        fcByNode.put(GGIO1_WRN2_Q, Fc.ST);
        fcByNode.put(GGIO1_WRN2_T, Fc.ST);

        fcByNode.put(GGIO1_WRN3_STVAL, Fc.ST);
        fcByNode.put(GGIO1_WRN3_Q, Fc.ST);
        fcByNode.put(GGIO1_WRN3_T, Fc.ST);

        fcByNode.put(GGIO1_WRN4_STVAL, Fc.ST);
        fcByNode.put(GGIO1_WRN4_Q, Fc.ST);
        fcByNode.put(GGIO1_WRN4_T, Fc.ST);

        FC_BY_NODE = Collections.unmodifiableMap(fcByNode);
    }

    public Load(final String physicalDeviceName, final String logicalDeviceName, final ServerModel serverModel) {
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

        values.add(this.setRandomByte(LLN0_MOD_STVAL, Fc.ST, 1, 2));
        values.add(this.setQuality(LLN0_MOD_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(LLN0_MOD_T, Fc.ST, timestamp));

        values.add(this.setRandomFloat(MMXU1_MAXWPHS_MAG_F, Fc.MX, 500, 1000));
        values.add(this.setQuality(MMXU1_MAXWPHS_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU1_MAXWPHS_T, Fc.MX, timestamp));

        values.add(this.setRandomFloat(MMXU1_MINWPHS_MAG_F, Fc.MX, 0, 500));
        values.add(this.setQuality(MMXU1_MINWPHS_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU1_MINWPHS_T, Fc.MX, timestamp));

        values.add(this.setFixedFloat(MMXU1_TOTW_MAG_F, Fc.MX, 1));
        values.add(this.setQuality(MMXU1_TOTW_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU1_TOTW_T, Fc.MX, timestamp));

        values.add(this.setRandomFloat(MMXU2_MAXWPHS_MAG_F, Fc.MX, 500, 1000));
        values.add(this.setQuality(MMXU2_MAXWPHS_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU2_MAXWPHS_T, Fc.MX, timestamp));

        values.add(this.setRandomFloat(MMXU2_MINWPHS_MAG_F, Fc.MX, 0, 500));
        values.add(this.setQuality(MMXU2_MINWPHS_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU2_MINWPHS_T, Fc.MX, timestamp));

        values.add(this.setFixedFloat(MMXU2_TOTW_MAG_F, Fc.MX, 1));
        values.add(this.setQuality(MMXU2_TOTW_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU2_TOTW_T, Fc.MX, timestamp));

        values.add(this.setFixedInt(MMTR1_TOTWH_ACTVAL, Fc.ST, 1));
        values.add(this.setQuality(MMTR1_TOTWH_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMTR1_TOTWH_T, Fc.ST, timestamp));

        values.add(this.setBoolean(GGIO1_ALM1_STVAL, Fc.ST, false));
        values.add(this.setQuality(GGIO1_ALM1_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(GGIO1_ALM1_T, Fc.ST, timestamp));

        values.add(this.setBoolean(GGIO1_ALM2_STVAL, Fc.ST, false));
        values.add(this.setQuality(GGIO1_ALM2_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(GGIO1_ALM2_T, Fc.ST, timestamp));

        values.add(this.setBoolean(GGIO1_ALM3_STVAL, Fc.ST, false));
        values.add(this.setQuality(GGIO1_ALM3_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(GGIO1_ALM3_T, Fc.ST, timestamp));

        values.add(this.setBoolean(GGIO1_ALM4_STVAL, Fc.ST, false));
        values.add(this.setQuality(GGIO1_ALM4_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(GGIO1_ALM4_T, Fc.ST, timestamp));

        values.add(this.setRandomInt(GGIO1_INTIN1_STVAL, Fc.ST, 1, 100));
        values.add(this.setQuality(GGIO1_INTIN1_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(GGIO1_INTIN1_T, Fc.ST, timestamp));

        values.add(this.setBoolean(GGIO1_WRN1_STVAL, Fc.ST, false));
        values.add(this.setQuality(GGIO1_WRN1_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(GGIO1_WRN1_T, Fc.ST, timestamp));

        values.add(this.setBoolean(GGIO1_WRN2_STVAL, Fc.ST, false));
        values.add(this.setQuality(GGIO1_WRN2_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(GGIO1_WRN2_T, Fc.ST, timestamp));

        values.add(this.setBoolean(GGIO1_WRN3_STVAL, Fc.ST, false));
        values.add(this.setQuality(GGIO1_WRN3_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(GGIO1_WRN3_T, Fc.ST, timestamp));

        values.add(this.setBoolean(GGIO1_WRN4_STVAL, Fc.ST, false));
        values.add(this.setQuality(GGIO1_WRN4_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(GGIO1_WRN4_T, Fc.ST, timestamp));

        values.add(this.setRandomInt(GGIO1_INTIN2_STVAL, Fc.ST, 1, 100));
        values.add(this.setQuality(GGIO1_INTIN2_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(GGIO1_INTIN2_T, Fc.ST, timestamp));

        return values;
    }

    @Override
    public BasicDataAttribute getAttributeAndSetValue(final String node, final String value) {
        final Fc fc = this.getFunctionalConstraint(node);
        if (fc == null) {
            throw this.illegalNodeException(node);
        }

        if (BOOLEAN_NODES.contains(node)) {
            return this.setBoolean(node, fc, Boolean.parseBoolean(value));
        }

        if (FLOAT32_NODES.contains(node)) {
            return this.setFixedFloat(node, fc, Float.parseFloat(value));
        }

        if (INT8_NODES.contains(node)) {
            return this.setByte(node, fc, Byte.parseByte(value));
        }

        if (INT32_NODES.contains(node)) {
            return this.setInt(node, fc, Integer.parseInt(value));
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
