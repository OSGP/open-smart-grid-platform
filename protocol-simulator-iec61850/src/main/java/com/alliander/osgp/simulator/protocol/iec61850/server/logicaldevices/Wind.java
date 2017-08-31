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

    private static final String LLN0_MOD_STVAL = "LLN0.Mod.stVal";
    private static final String LLN0_MOD_Q = "LLN0.Mod.q";
    private static final String LLN0_MOD_T = "LLN0.Mod.t";

    private static final String LLN0_BEH_STVAL = "LLN0.Beh.stVal";
    private static final String LLN0_BEH_Q = "LLN0.Beh.q";
    private static final String LLN0_BEH_T = "LLN0.Beh.t";

    private static final String LLN0_HEALTH_STVAL = "LLN0.Health.stVal";
    private static final String LLN0_HEALTH_Q = "LLN0.Health.q";
    private static final String LLN0_HEALTH_T = "LLN0.Health.t";

    private static final String DRCC1_OUTWSET_SUBVAL_F = "DRCC1.OutWSet.subVal.f";
    private static final String DRCC1_OUTWSET_SUBQ = "DRCC1.OutWSet.subQ";

    private static final String DGEN1_GNOPST_STVAL = "DGEN1.GnOpSt.stVal";
    private static final String DGEN1_GNOPST_Q = "DGEN1.GnOpSt.q";
    private static final String DGEN1_GNOPST_T = "DGEN1.GnOpSt.t";

    private static final String DGEN1_OPTMSRS_STVAL = "DGEN1.OpTmsRs.stVal";
    private static final String DGEN1_OPTMSRS_Q = "DGEN1.OpTmsRs.q";
    private static final String DGEN1_OPTMSRS_T = "DGEN1.OpTmsRs.t";

    private static final String DGEN1_TOTWH_MAG_F = "DGEN1.TotWh.mag.f";
    private static final String DGEN1_TOTWH_Q = "DGEN1.TotWh.q";
    private static final String DGEN1_TOTWH_T = "DGEN1.TotWh.t";

    private static final String MMXU1_TOTW_MAG_F = "MMXU1.TotW.mag.f";
    private static final String MMXU1_TOTW_Q = "MMXU1.TotW.q";
    private static final String MMXU1_TOTW_T = "MMXU1.TotW.t";

    private static final String MMXU1_TOTPF_MAG_F = "MMXU1.TotPF.mag.f";
    private static final String MMXU1_TOTPF_Q = "MMXU1.TotPF.q";
    private static final String MMXU1_TOTPF_T = "MMXU1.TotPF.t";

    private static final String MMXU1_MINWPHS_MAG_F = "MMXU1.MinWPhs.mag.f";
    private static final String MMXU1_MINWPHS_Q = "MMXU1.MinWPhs.q";
    private static final String MMXU1_MINWPHS_T = "MMXU1.MinWPhs.t";

    private static final String MMXU1_MAXWPHS_MAG_F = "MMXU1.MaxWPhs.mag.f";
    private static final String MMXU1_MAXWPHS_Q = "MMXU1.MaxWPhs.q";
    private static final String MMXU1_MAXWPHS_T = "MMXU1.MaxWPhs.t";

    private static final String MMXU1_W_PHSA_CVAL_MAG_F = "MMXU1.W.phsA.cVal.mag.f";
    private static final String MMXU1_W_PHSA_Q = "MMXU1.W.phsA.q";
    private static final String MMXU1_W_PHSA_T = "MMXU1.W.phsA.t";

    private static final String MMXU1_W_PHSB_CVAL_MAG_F = "MMXU1.W.phsB.cVal.mag.f";
    private static final String MMXU1_W_PHSB_Q = "MMXU1.W.phsB.q";
    private static final String MMXU1_W_PHSB_T = "MMXU1.W.phsB.t";

    private static final String MMXU1_W_PHSC_CVAL_MAG_F = "MMXU1.W.phsC.cVal.mag.f";
    private static final String MMXU1_W_PHSC_Q = "MMXU1.W.phsC.q";
    private static final String MMXU1_W_PHSC_T = "MMXU1.W.phsC.t";

    private static final String MMXU2_W_PHSA_CVAL_MAG_F = "MMXU2.W.phsA.cVal.mag.f";
    private static final String MMXU2_W_PHSA_Q = "MMXU2.W.phsA.q";
    private static final String MMXU2_W_PHSA_T = "MMXU2.W.phsA.t";

    private static final String MMXU2_W_PHSB_CVAL_MAG_F = "MMXU2.W.phsB.cVal.mag.f";
    private static final String MMXU2_W_PHSB_Q = "MMXU2.W.phsB.q";
    private static final String MMXU2_W_PHSB_T = "MMXU2.W.phsB.t";

    private static final String MMXU2_W_PHSC_CVAL_MAG_F = "MMXU2.W.phsC.cVal.mag.f";
    private static final String MMXU2_W_PHSC_Q = "MMXU2.W.phsC.q";
    private static final String MMXU2_W_PHSC_T = "MMXU2.W.phsC.t";

    private static final String MMXU3_W_PHSA_CVAL_MAG_F = "MMXU3.W.phsA.cVal.mag.f";
    private static final String MMXU3_W_PHSA_Q = "MMXU3.W.phsA.q";
    private static final String MMXU3_W_PHSA_T = "MMXU3.W.phsA.t";

    private static final String MMXU3_W_PHSB_CVAL_MAG_F = "MMXU3.W.phsB.cVal.mag.f";
    private static final String MMXU3_W_PHSB_Q = "MMXU3.W.phsB.q";
    private static final String MMXU3_W_PHSB_T = "MMXU3.W.phsB.t";

    private static final String MMXU3_W_PHSC_CVAL_MAG_F = "MMXU3.W.phsC.cVal.mag.f";
    private static final String MMXU3_W_PHSC_Q = "MMXU3.W.phsC.q";
    private static final String MMXU3_W_PHSC_T = "MMXU3.W.phsC.t";

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

    private static final Set<String> FLOAT32_NODES = Collections.unmodifiableSet(new TreeSet<>(
            Arrays.asList(DRCC1_OUTWSET_SUBVAL_F, DGEN1_TOTWH_MAG_F, MMXU1_MAXWPHS_MAG_F, MMXU1_MINWPHS_MAG_F,
                    MMXU1_TOTW_MAG_F, MMXU1_TOTPF_MAG_F, MMXU1_W_PHSA_CVAL_MAG_F, MMXU1_W_PHSB_CVAL_MAG_F,
                    MMXU1_W_PHSC_CVAL_MAG_F, MMXU2_W_PHSA_CVAL_MAG_F, MMXU2_W_PHSB_CVAL_MAG_F, MMXU2_W_PHSC_CVAL_MAG_F,
                    MMXU3_W_PHSA_CVAL_MAG_F, MMXU3_W_PHSB_CVAL_MAG_F, MMXU3_W_PHSC_CVAL_MAG_F)));

    private static final Set<String> INT8_NODES = Collections.unmodifiableSet(
            new TreeSet<>(Arrays.asList(LLN0_HEALTH_STVAL, LLN0_MOD_STVAL, LLN0_BEH_STVAL, DGEN1_GNOPST_STVAL)));

    private static final Set<String> INT32_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(GGIO1_INTIN1_STVAL, GGIO1_INTIN2_STVAL, DGEN1_OPTMSRS_STVAL)));

    private static final Set<String> QUALITY_NODES = Collections.unmodifiableSet(new TreeSet<>(
            Arrays.asList(LLN0_HEALTH_Q, LLN0_BEH_Q, LLN0_MOD_Q, DRCC1_OUTWSET_SUBQ, DGEN1_TOTWH_Q, DGEN1_GNOPST_Q,
                    DGEN1_OPTMSRS_Q, MMXU1_MAXWPHS_Q, MMXU1_MINWPHS_Q, MMXU1_TOTW_Q, MMXU1_TOTPF_Q, MMXU1_W_PHSA_Q,
                    MMXU1_W_PHSB_Q, MMXU1_W_PHSC_Q, MMXU2_W_PHSA_Q, MMXU2_W_PHSB_Q, MMXU2_W_PHSC_Q, MMXU3_W_PHSA_Q,
                    MMXU3_W_PHSB_Q, MMXU3_W_PHSC_Q, GGIO1_ALM1_Q, GGIO1_ALM2_Q, GGIO1_ALM3_Q, GGIO1_ALM4_Q,
                    GGIO1_INTIN1_Q, GGIO1_INTIN2_Q, GGIO1_WRN1_Q, GGIO1_WRN2_Q, GGIO1_WRN3_Q, GGIO1_WRN4_Q)));

    private static final Set<String> TIMESTAMP_NODES = Collections.unmodifiableSet(
            new TreeSet<>(Arrays.asList(LLN0_HEALTH_T, LLN0_BEH_T, LLN0_MOD_T, DGEN1_TOTWH_T, DGEN1_GNOPST_T,
                    DGEN1_OPTMSRS_T, MMXU1_MAXWPHS_T, MMXU1_MINWPHS_T, MMXU1_TOTW_T, MMXU1_TOTPF_T, MMXU1_W_PHSA_T,
                    MMXU1_W_PHSB_T, MMXU1_W_PHSC_T, MMXU2_W_PHSA_T, MMXU2_W_PHSB_T, MMXU2_W_PHSC_T, MMXU3_W_PHSA_T,
                    MMXU3_W_PHSB_T, MMXU3_W_PHSC_T, GGIO1_ALM1_T, GGIO1_ALM2_T, GGIO1_ALM3_T, GGIO1_ALM4_T,
                    GGIO1_INTIN1_T, GGIO1_INTIN2_T, GGIO1_WRN1_T, GGIO1_WRN2_T, GGIO1_WRN3_T, GGIO1_WRN4_T)));

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

        fcByNode.put(DRCC1_OUTWSET_SUBVAL_F, Fc.SV);
        fcByNode.put(DRCC1_OUTWSET_SUBQ, Fc.SV);

        fcByNode.put(DGEN1_TOTWH_MAG_F, Fc.MX);
        fcByNode.put(DGEN1_TOTWH_Q, Fc.MX);
        fcByNode.put(DGEN1_TOTWH_T, Fc.MX);

        fcByNode.put(DGEN1_GNOPST_STVAL, Fc.ST);
        fcByNode.put(DGEN1_GNOPST_Q, Fc.ST);
        fcByNode.put(DGEN1_GNOPST_T, Fc.ST);

        fcByNode.put(DGEN1_OPTMSRS_STVAL, Fc.ST);
        fcByNode.put(DGEN1_OPTMSRS_Q, Fc.ST);
        fcByNode.put(DGEN1_OPTMSRS_T, Fc.ST);

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

        fcByNode.put(MMXU2_W_PHSA_CVAL_MAG_F, Fc.MX);
        fcByNode.put(MMXU2_W_PHSA_Q, Fc.MX);
        fcByNode.put(MMXU2_W_PHSA_T, Fc.MX);

        fcByNode.put(MMXU2_W_PHSB_CVAL_MAG_F, Fc.MX);
        fcByNode.put(MMXU2_W_PHSB_Q, Fc.MX);
        fcByNode.put(MMXU2_W_PHSB_T, Fc.MX);

        fcByNode.put(MMXU2_W_PHSC_CVAL_MAG_F, Fc.MX);
        fcByNode.put(MMXU2_W_PHSC_Q, Fc.MX);
        fcByNode.put(MMXU2_W_PHSC_T, Fc.MX);

        fcByNode.put(MMXU3_W_PHSA_CVAL_MAG_F, Fc.MX);
        fcByNode.put(MMXU3_W_PHSA_Q, Fc.MX);
        fcByNode.put(MMXU3_W_PHSA_T, Fc.MX);

        fcByNode.put(MMXU3_W_PHSB_CVAL_MAG_F, Fc.MX);
        fcByNode.put(MMXU3_W_PHSB_Q, Fc.MX);
        fcByNode.put(MMXU3_W_PHSB_T, Fc.MX);

        fcByNode.put(MMXU3_W_PHSC_CVAL_MAG_F, Fc.MX);
        fcByNode.put(MMXU3_W_PHSC_Q, Fc.MX);
        fcByNode.put(MMXU3_W_PHSC_T, Fc.MX);

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

        values.add(this.setRandomByte(LLN0_MOD_STVAL, Fc.ST, 1, 2));
        values.add(this.setQuality(LLN0_MOD_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(LLN0_MOD_T, Fc.ST, timestamp));

        values.add(this.setRandomFloat(DRCC1_OUTWSET_SUBVAL_F, Fc.SV, 0, 1000));
        values.add(this.setQuality(DRCC1_OUTWSET_SUBQ, Fc.SV, QualityType.VALIDITY_GOOD.getValue()));

        values.add(this.setRandomFloat(DGEN1_TOTWH_MAG_F, Fc.MX, 0, 1000));
        values.add(this.setQuality(DGEN1_TOTWH_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(DGEN1_TOTWH_T, Fc.MX, timestamp));

        values.add(this.setRandomByte(DGEN1_GNOPST_STVAL, Fc.ST, 1, 2));
        values.add(this.setQuality(DGEN1_GNOPST_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(DGEN1_GNOPST_T, Fc.ST, timestamp));

        values.add(this.incrementInt(DGEN1_OPTMSRS_STVAL, Fc.ST));
        values.add(this.setQuality(DGEN1_OPTMSRS_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(DGEN1_OPTMSRS_T, Fc.ST, timestamp));

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

        values.add(this.setRandomFloat(MMXU2_W_PHSA_CVAL_MAG_F, Fc.MX, 0, 1000));
        values.add(this.setQuality(MMXU2_W_PHSA_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU2_W_PHSA_T, Fc.MX, timestamp));

        values.add(this.setRandomFloat(MMXU2_W_PHSB_CVAL_MAG_F, Fc.MX, 0, 1000));
        values.add(this.setQuality(MMXU2_W_PHSB_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU2_W_PHSB_T, Fc.MX, timestamp));

        values.add(this.setRandomFloat(MMXU2_W_PHSC_CVAL_MAG_F, Fc.MX, 0, 1000));
        values.add(this.setQuality(MMXU2_W_PHSC_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU2_W_PHSC_T, Fc.MX, timestamp));

        values.add(this.setRandomFloat(MMXU3_W_PHSA_CVAL_MAG_F, Fc.MX, 0, 1000));
        values.add(this.setQuality(MMXU3_W_PHSA_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU3_W_PHSA_T, Fc.MX, timestamp));

        values.add(this.setRandomFloat(MMXU3_W_PHSB_CVAL_MAG_F, Fc.MX, 0, 1000));
        values.add(this.setQuality(MMXU3_W_PHSB_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU3_W_PHSB_T, Fc.MX, timestamp));

        values.add(this.setRandomFloat(MMXU3_W_PHSC_CVAL_MAG_F, Fc.MX, 0, 1000));
        values.add(this.setQuality(MMXU3_W_PHSC_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU3_W_PHSC_T, Fc.MX, timestamp));

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

        values.add(this.setRandomInt(GGIO1_INTIN2_STVAL, Fc.ST, 1, 100));
        values.add(this.setQuality(GGIO1_INTIN2_Q, Fc.ST, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(GGIO1_INTIN2_T, Fc.ST, timestamp));

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
