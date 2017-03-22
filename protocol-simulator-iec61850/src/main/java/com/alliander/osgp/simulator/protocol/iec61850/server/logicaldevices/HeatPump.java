/**
 * Copyright 2016 Smart Society Services B.V.
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

public class HeatPump extends LogicalDevice {

    private static final String LLN0_HEALTH_STVAL = "LLN0.Health.stVal";
    private static final String LLN0_HEALTH_Q = "LLN0.Health.q";
    private static final String LLN0_HEALTH_T = "LLN0.Health.t";
    private static final String LLN0_BEH_STVAL = "LLN0.Beh.stVal";
    private static final String LLN0_BEH_Q = "LLN0.Beh.q";
    private static final String LLN0_BEH_T = "LLN0.Beh.t";
    private static final String LLN0_MOD_STVAL = "LLN0.Mod.stVal";
    private static final String LLN0_MOD_Q = "LLN0.Mod.q";
    private static final String LLN0_MOD_T = "LLN0.Mod.t";

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
    private static final String GGIO1_INTIN2_STVAL = "GGIO1.IntIn2.stVal";
    private static final String GGIO1_INTIN2_Q = "GGIO1.IntIn2.q";
    private static final String GGIO1_INTIN2_T = "GGIO1.IntIn2.t";

    private static final String DSCH1_SCHDID_SETVAL = "DSCH1.SchdId.setVal";
    private static final String DSCH1_SCHDTYP_SETVAL = "DSCH1.SchdTyp.setVal";
    private static final String DSCH1_SCHDCAT_SETVAL = "DSCH1.SchCat.setVal";
    private static final String DSCH1_SCHDABSTM_VAL_0 = "DSCH1.SchdAbsTm.val.0";
    private static final String DSCH1_SCHDABSTM_TIME_0 = "DSCH1.SchdAbsTm.time.0";
    private static final String DSCH1_SCHDABSTM_VAL_1 = "DSCH1.SchdAbsTm.val.1";
    private static final String DSCH1_SCHDABSTM_TIME_1 = "DSCH1.SchdAbsTm.time.1";
    private static final String DSCH1_SCHDABSTM_VAL_2 = "DSCH1.SchdAbsTm.val.2";
    private static final String DSCH1_SCHDABSTM_TIME_2 = "DSCH1.SchdAbsTm.time.2";
    private static final String DSCH1_SCHDABSTM_VAL_3 = "DSCH1.SchdAbsTm.val.3";
    private static final String DSCH1_SCHDABSTM_TIME_3 = "DSCH1.SchdAbsTm.time.3";

    private static final String DSCH2_SCHDID_SETVAL = "DSCH2.SchdId.setVal";
    private static final String DSCH2_SCHDTYP_SETVAL = "DSCH2.SchdTyp.setVal";
    private static final String DSCH2_SCHDCAT_SETVAL = "DSCH2.SchCat.setVal";
    private static final String DSCH2_SCHDABSTM_VAL_0 = "DSCH2.SchdAbsTm.val.0";
    private static final String DSCH2_SCHDABSTM_TIME_0 = "DSCH2.SchdAbsTm.time.0";
    private static final String DSCH2_SCHDABSTM_VAL_1 = "DSCH2.SchdAbsTm.val.1";
    private static final String DSCH2_SCHDABSTM_TIME_1 = "DSCH2.SchdAbsTm.time.1";
    private static final String DSCH2_SCHDABSTM_VAL_2 = "DSCH2.SchdAbsTm.val.2";
    private static final String DSCH2_SCHDABSTM_TIME_2 = "DSCH2.SchdAbsTm.time.2";
    private static final String DSCH2_SCHDABSTM_VAL_3 = "DSCH2.SchdAbsTm.val.3";
    private static final String DSCH2_SCHDABSTM_TIME_3 = "DSCH2.SchdAbsTm.time.3";

    private static final String DSCH3_SCHDID_SETVAL = "DSCH3.SchdId.setVal";
    private static final String DSCH3_SCHDTYP_SETVAL = "DSCH3.SchdTyp.setVal";
    private static final String DSCH3_SCHDCAT_SETVAL = "DSCH3.SchCat.setVal";
    private static final String DSCH3_SCHDABSTM_VAL_0 = "DSCH3.SchdAbsTm.val.0";
    private static final String DSCH3_SCHDABSTM_TIME_0 = "DSCH3.SchdAbsTm.time.0";
    private static final String DSCH3_SCHDABSTM_VAL_1 = "DSCH3.SchdAbsTm.val.1";
    private static final String DSCH3_SCHDABSTM_TIME_1 = "DSCH3.SchdAbsTm.time.1";
    private static final String DSCH3_SCHDABSTM_VAL_2 = "DSCH3.SchdAbsTm.val.2";
    private static final String DSCH3_SCHDABSTM_TIME_2 = "DSCH3.SchdAbsTm.time.2";
    private static final String DSCH3_SCHDABSTM_VAL_3 = "DSCH3.SchdAbsTm.val.3";
    private static final String DSCH3_SCHDABSTM_TIME_3 = "DSCH3.SchdAbsTm.time.3";

    private static final String DSCH4_SCHDID_SETVAL = "DSCH4.SchdId.setVal";
    private static final String DSCH4_SCHDTYP_SETVAL = "DSCH4.SchdTyp.setVal";
    private static final String DSCH4_SCHDCAT_SETVAL = "DSCH4.SchCat.setVal";
    private static final String DSCH4_SCHDABSTM_VAL_0 = "DSCH4.SchdAbsTm.val.0";
    private static final String DSCH4_SCHDABSTM_TIME_0 = "DSCH4.SchdAbsTm.time.0";
    private static final String DSCH4_SCHDABSTM_VAL_1 = "DSCH4.SchdAbsTm.val.1";
    private static final String DSCH4_SCHDABSTM_TIME_1 = "DSCH4.SchdAbsTm.time.1";
    private static final String DSCH4_SCHDABSTM_VAL_2 = "DSCH4.SchdAbsTm.val.2";
    private static final String DSCH4_SCHDABSTM_TIME_2 = "DSCH4.SchdAbsTm.time.2";
    private static final String DSCH4_SCHDABSTM_VAL_3 = "DSCH4.SchdAbsTm.val.3";
    private static final String DSCH4_SCHDABSTM_TIME_3 = "DSCH4.SchdAbsTm.time.3";

    private static final String MMXU1_TOTW_MAG_F = "MMXU1.TotW.mag.f";
    private static final String MMXU1_TOTW_Q = "MMXU1.TotW.q";
    private static final String MMXU1_TOTW_T = "MMXU1.TotW.t";
    private static final String MMXU1_MINWPHS_MAG_F = "MMXU1.MinWPhs.mag.f";
    private static final String MMXU1_MINWPHS_Q = "MMXU1.MinWPhs.q";
    private static final String MMXU1_MINWPHS_T = "MMXU1.MinWPhs.t";
    private static final String MMXU1_MAXWPHS_MAG_F = "MMXU1.MaxWPhs.mag.f";
    private static final String MMXU1_MAXWPHS_Q = "MMXU1.MaxWPhs.q";
    private static final String MMXU1_MAXWPHS_T = "MMXU1.MaxWPhs.t";
    private static final String MMXU1_TOTPF_MAG_F = "MMXU1.TotPF.mag.f";
    private static final String MMXU1_TOTPF_Q = "MMXU1.TotPF.q";
    private static final String MMXU1_TOTPF_T = "MMXU1.TotPF.t";

    private static final String DRCC1_OUTWSET_SUBVAL_F = "DRCC1.OutWSet.subVal.f";
    private static final String DRCC1_OUTWSET_SUBQ = "DRCC1.OutWSet.subQ";

    private static final String DGEN1_TOTWH_MAG_F = "DGEN1.TotWh.mag.f";
    private static final String DGEN1_TOTWH_Q = "DGEN1.TotWh.q";
    private static final String DGEN1_TOTWH_T = "DGEN1.TotWh.t";
    private static final String DGEN1_GNOPST_STVAL = "DGEN1.GnOpSt.stVal";
    private static final String DGEN1_GNOPST_Q = "DGEN1.GnOpSt.q";
    private static final String DGEN1_GNOPST_T = "DGEN1.GnOpSt.t";
    private static final String DGEN1_OPTMSRS_STVAL = "DGEN1.OpTmsRs.stVal";
    private static final String DGEN1_OPTMSRS_Q = "DGEN1.OpTmsRs.q";
    private static final String DGEN1_OPTMSRS_T = "DGEN1.OpTmsRs.t";

    private static final String TTMP1_TMPSV_INSTMAG_F = "TTMP1.TmpSv.instMag.f";
    private static final String TTMP1_TMPSV_Q = "TTMP1.TmpSv.q";
    private static final String TTMP1_TMPSV_T = "TTMP1.TmpSv.t";
    private static final String TTMP2_TMPSV_INSTMAG_F = "TTMP2.TmpSv.instMag.f";
    private static final String TTMP2_TMPSV_Q = "TTMP2.TmpSv.q";
    private static final String TTMP2_TMPSV_T = "TTMP2.TmpSv.t";
    private static final String TTMP3_TMPSV_INSTMAG_F = "TTMP3.TmpSv.instMag.f";
    private static final String TTMP3_TMPSV_Q = "TTMP3.TmpSv.q";
    private static final String TTMP3_TMPSV_T = "TTMP3.TmpSv.t";
    private static final String TTMP4_TMPSV_INSTMAG_F = "TTMP4.TmpSv.instMag.f";
    private static final String TTMP4_TMPSV_Q = "TTMP4.TmpSv.q";
    private static final String TTMP4_TMPSV_T = "TTMP4.TmpSv.t";

    private static final String MFLW1_FLWRTE_MAG_F = "MFLW1.FlwRte.mag.f";
    private static final String MFLW1_FLWRTE_Q = "MFLW1.FlwRte.q";
    private static final String MFLW1_FLWRTE_T = "MFLW1.FlwRte.t";

    private static final Set<String> BOOLEAN_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(GGIO1_ALM1_STVAL, GGIO1_ALM2_STVAL, GGIO1_ALM3_STVAL,
                    GGIO1_ALM4_STVAL, GGIO1_WRN1_STVAL, GGIO1_WRN2_STVAL, GGIO1_WRN3_STVAL, GGIO1_WRN4_STVAL)));

    private static final Set<String> FLOAT32_NODES = Collections.unmodifiableSet(
            new TreeSet<>(Arrays.asList(TTMP1_TMPSV_INSTMAG_F, TTMP2_TMPSV_INSTMAG_F, TTMP3_TMPSV_INSTMAG_F,
                    TTMP4_TMPSV_INSTMAG_F, MFLW1_FLWRTE_MAG_F, MMXU1_TOTW_MAG_F, MMXU1_MINWPHS_MAG_F,
                    MMXU1_MAXWPHS_MAG_F, MMXU1_TOTPF_MAG_F, DRCC1_OUTWSET_SUBVAL_F, DGEN1_TOTWH_MAG_F,
                    DSCH1_SCHDABSTM_VAL_0, DSCH1_SCHDABSTM_VAL_1, DSCH1_SCHDABSTM_VAL_2,
                    DSCH1_SCHDABSTM_VAL_3, DSCH2_SCHDABSTM_VAL_0, DSCH2_SCHDABSTM_VAL_1,
                    DSCH2_SCHDABSTM_VAL_2, DSCH2_SCHDABSTM_VAL_3, DSCH3_SCHDABSTM_VAL_0,
                    DSCH3_SCHDABSTM_VAL_1, DSCH3_SCHDABSTM_VAL_2, DSCH3_SCHDABSTM_VAL_3)));

    private static final Set<String> INT8_NODES = Collections.unmodifiableSet(
            new TreeSet<>(Arrays.asList(LLN0_HEALTH_STVAL, LLN0_BEH_STVAL, LLN0_MOD_STVAL, DGEN1_GNOPST_STVAL)));

    private static final Set<String> INT32_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(GGIO1_INTIN1_STVAL, GGIO1_INTIN2_STVAL, DSCH1_SCHDID_SETVAL,
                    DGEN1_OPTMSRS_STVAL, DSCH1_SCHDID_SETVAL, DSCH1_SCHDTYP_SETVAL, DSCH1_SCHDCAT_SETVAL,
                    DSCH2_SCHDID_SETVAL, DSCH2_SCHDTYP_SETVAL, DSCH2_SCHDCAT_SETVAL,
                    DSCH3_SCHDID_SETVAL, DSCH3_SCHDTYP_SETVAL, DSCH3_SCHDCAT_SETVAL,
                    DSCH4_SCHDID_SETVAL, DSCH4_SCHDTYP_SETVAL, DSCH4_SCHDCAT_SETVAL)));

    private static final Set<String> QUALITY_NODES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(LLN0_HEALTH_Q, LLN0_BEH_Q, LLN0_MOD_Q, GGIO1_ALM1_Q,
                    GGIO1_ALM2_Q, GGIO1_ALM3_Q, GGIO1_ALM4_Q, GGIO1_INTIN1_Q, GGIO1_INTIN2_Q, GGIO1_WRN1_Q,
                    GGIO1_WRN2_Q, GGIO1_WRN3_Q, GGIO1_WRN4_Q, MMXU1_TOTW_Q, MMXU1_MINWPHS_Q, MMXU1_MAXWPHS_Q,
                    MMXU1_TOTPF_Q, DRCC1_OUTWSET_SUBQ, DGEN1_TOTWH_Q, DGEN1_GNOPST_Q, DGEN1_OPTMSRS_Q, TTMP1_TMPSV_Q,
                    TTMP2_TMPSV_Q, TTMP3_TMPSV_Q, TTMP4_TMPSV_Q, MFLW1_FLWRTE_Q)));

    private static final Set<String> TIMESTAMP_NODES = Collections.unmodifiableSet(new TreeSet<>(
            Arrays.asList(LLN0_HEALTH_T, LLN0_BEH_T, LLN0_MOD_T, GGIO1_ALM1_T, GGIO1_ALM2_T, GGIO1_ALM3_T, GGIO1_ALM4_T,
                    GGIO1_INTIN1_T, GGIO1_INTIN2_T, GGIO1_WRN1_T, GGIO1_WRN2_T, GGIO1_WRN3_T, GGIO1_WRN4_T,
                    MMXU1_TOTW_T, MMXU1_MINWPHS_T, MMXU1_MAXWPHS_T, MMXU1_TOTPF_T, DGEN1_TOTWH_T, DGEN1_GNOPST_T,
                    DGEN1_OPTMSRS_T, TTMP1_TMPSV_T, TTMP2_TMPSV_T, TTMP3_TMPSV_T, TTMP4_TMPSV_T, MFLW1_FLWRTE_T,
                    DSCH1_SCHDABSTM_TIME_0, DSCH1_SCHDABSTM_TIME_1, DSCH1_SCHDABSTM_TIME_2, DSCH1_SCHDABSTM_TIME_3,
                    DSCH2_SCHDABSTM_TIME_0, DSCH2_SCHDABSTM_TIME_1, DSCH2_SCHDABSTM_TIME_2, DSCH2_SCHDABSTM_TIME_3,
                    DSCH3_SCHDABSTM_TIME_0, DSCH3_SCHDABSTM_TIME_1,DSCH3_SCHDABSTM_TIME_2, DSCH3_SCHDABSTM_TIME_3,
                    DSCH4_SCHDABSTM_TIME_0, DSCH4_SCHDABSTM_TIME_1, DSCH4_SCHDABSTM_TIME_2, DSCH4_SCHDABSTM_TIME_3)));

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
        fcByNode.put(GGIO1_INTIN2_STVAL, Fc.ST);
        fcByNode.put(GGIO1_INTIN2_Q, Fc.ST);
        fcByNode.put(GGIO1_INTIN2_T, Fc.ST);

        fcByNode.put(DSCH1_SCHDID_SETVAL, Fc.SP);
        fcByNode.put(DSCH1_SCHDTYP_SETVAL, Fc.SP);
        fcByNode.put(DSCH1_SCHDCAT_SETVAL, Fc.SP);
        fcByNode.put(DSCH1_SCHDABSTM_VAL_0, Fc.SP);
        fcByNode.put(DSCH1_SCHDABSTM_TIME_0, Fc.SP);
        fcByNode.put(DSCH1_SCHDABSTM_VAL_1, Fc.SP);
        fcByNode.put(DSCH1_SCHDABSTM_TIME_1, Fc.SP);
        fcByNode.put(DSCH1_SCHDABSTM_VAL_2, Fc.SP);
        fcByNode.put(DSCH1_SCHDABSTM_TIME_2, Fc.SP);
        fcByNode.put(DSCH1_SCHDABSTM_VAL_3, Fc.SP);
        fcByNode.put(DSCH1_SCHDABSTM_TIME_3, Fc.SP);

        fcByNode.put(DSCH2_SCHDID_SETVAL, Fc.SP);
        fcByNode.put(DSCH2_SCHDTYP_SETVAL, Fc.SP);
        fcByNode.put(DSCH2_SCHDCAT_SETVAL, Fc.SP);
        fcByNode.put(DSCH2_SCHDABSTM_VAL_0, Fc.SP);
        fcByNode.put(DSCH2_SCHDABSTM_TIME_0, Fc.SP);
        fcByNode.put(DSCH2_SCHDABSTM_VAL_1, Fc.SP);
        fcByNode.put(DSCH2_SCHDABSTM_TIME_1, Fc.SP);
        fcByNode.put(DSCH2_SCHDABSTM_VAL_2, Fc.SP);
        fcByNode.put(DSCH2_SCHDABSTM_TIME_2, Fc.SP);
        fcByNode.put(DSCH2_SCHDABSTM_VAL_3, Fc.SP);
        fcByNode.put(DSCH2_SCHDABSTM_TIME_3, Fc.SP);

        fcByNode.put(DSCH3_SCHDID_SETVAL, Fc.SP);
        fcByNode.put(DSCH3_SCHDTYP_SETVAL, Fc.SP);
        fcByNode.put(DSCH3_SCHDCAT_SETVAL, Fc.SP);
        fcByNode.put(DSCH3_SCHDABSTM_VAL_0, Fc.SP);
        fcByNode.put(DSCH3_SCHDABSTM_TIME_0, Fc.SP);
        fcByNode.put(DSCH3_SCHDABSTM_VAL_1, Fc.SP);
        fcByNode.put(DSCH3_SCHDABSTM_TIME_1, Fc.SP);
        fcByNode.put(DSCH3_SCHDABSTM_VAL_2, Fc.SP);
        fcByNode.put(DSCH3_SCHDABSTM_TIME_2, Fc.SP);
        fcByNode.put(DSCH3_SCHDABSTM_VAL_3, Fc.SP);
        fcByNode.put(DSCH3_SCHDABSTM_TIME_3, Fc.SP);

        fcByNode.put(DSCH4_SCHDID_SETVAL, Fc.SP);
        fcByNode.put(DSCH4_SCHDTYP_SETVAL, Fc.SP);
        fcByNode.put(DSCH4_SCHDCAT_SETVAL, Fc.SP);
        fcByNode.put(DSCH4_SCHDABSTM_VAL_0, Fc.SP);
        fcByNode.put(DSCH4_SCHDABSTM_TIME_0, Fc.SP);
        fcByNode.put(DSCH4_SCHDABSTM_VAL_1, Fc.SP);
        fcByNode.put(DSCH4_SCHDABSTM_TIME_1, Fc.SP);
        fcByNode.put(DSCH4_SCHDABSTM_VAL_2, Fc.SP);
        fcByNode.put(DSCH4_SCHDABSTM_TIME_2, Fc.SP);
        fcByNode.put(DSCH4_SCHDABSTM_VAL_3, Fc.SP);
        fcByNode.put(DSCH4_SCHDABSTM_TIME_3, Fc.SP);

        fcByNode.put(MMXU1_TOTW_MAG_F, Fc.MX);
        fcByNode.put(MMXU1_TOTW_Q, Fc.MX);
        fcByNode.put(MMXU1_TOTW_T, Fc.MX);
        fcByNode.put(MMXU1_MINWPHS_MAG_F, Fc.MX);
        fcByNode.put(MMXU1_MINWPHS_Q, Fc.MX);
        fcByNode.put(MMXU1_MINWPHS_T, Fc.MX);
        fcByNode.put(MMXU1_MAXWPHS_MAG_F, Fc.MX);
        fcByNode.put(MMXU1_MAXWPHS_Q, Fc.MX);
        fcByNode.put(MMXU1_MAXWPHS_T, Fc.MX);
        fcByNode.put(MMXU1_TOTPF_MAG_F, Fc.MX);
        fcByNode.put(MMXU1_TOTPF_Q, Fc.MX);
        fcByNode.put(MMXU1_TOTPF_T, Fc.MX);

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

        fcByNode.put(TTMP1_TMPSV_INSTMAG_F, Fc.MX);
        fcByNode.put(TTMP1_TMPSV_Q, Fc.MX);
        fcByNode.put(TTMP1_TMPSV_T, Fc.MX);
        fcByNode.put(TTMP2_TMPSV_INSTMAG_F, Fc.MX);
        fcByNode.put(TTMP2_TMPSV_Q, Fc.MX);
        fcByNode.put(TTMP2_TMPSV_T, Fc.MX);
        fcByNode.put(TTMP3_TMPSV_INSTMAG_F, Fc.MX);
        fcByNode.put(TTMP3_TMPSV_Q, Fc.MX);
        fcByNode.put(TTMP3_TMPSV_T, Fc.MX);
        fcByNode.put(TTMP4_TMPSV_INSTMAG_F, Fc.MX);
        fcByNode.put(TTMP4_TMPSV_Q, Fc.MX);
        fcByNode.put(TTMP4_TMPSV_T, Fc.MX);

        fcByNode.put(MFLW1_FLWRTE_MAG_F, Fc.MX);
        fcByNode.put(MFLW1_FLWRTE_Q, Fc.MX);
        fcByNode.put(MFLW1_FLWRTE_T, Fc.MX);

        FC_BY_NODE = Collections.unmodifiableMap(fcByNode);
    }

    public HeatPump(final String physicalDeviceName, final String logicalDeviceName, final ServerModel serverModel) {
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

        values.add(this.setRandomInt(DSCH1_SCHDID_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(DSCH1_SCHDTYP_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(DSCH1_SCHDCAT_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomFloat(DSCH1_SCHDABSTM_VAL_0, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH1_SCHDABSTM_TIME_0, Fc.SP, timestamp));
        values.add(this.setRandomFloat(DSCH1_SCHDABSTM_VAL_1, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH1_SCHDABSTM_TIME_1, Fc.SP, timestamp));
        values.add(this.setRandomFloat(DSCH1_SCHDABSTM_VAL_2, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH1_SCHDABSTM_TIME_2, Fc.SP, timestamp));
        values.add(this.setRandomFloat(DSCH1_SCHDABSTM_VAL_3, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH1_SCHDABSTM_TIME_3, Fc.SP, timestamp));

        values.add(this.setRandomInt(DSCH2_SCHDID_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(DSCH2_SCHDTYP_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(DSCH2_SCHDCAT_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomFloat(DSCH2_SCHDABSTM_VAL_0, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH2_SCHDABSTM_TIME_0, Fc.SP, timestamp));
        values.add(this.setRandomFloat(DSCH2_SCHDABSTM_VAL_1, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH2_SCHDABSTM_TIME_1, Fc.SP, timestamp));
        values.add(this.setRandomFloat(DSCH2_SCHDABSTM_VAL_2, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH2_SCHDABSTM_TIME_2, Fc.SP, timestamp));
        values.add(this.setRandomFloat(DSCH2_SCHDABSTM_VAL_3, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH2_SCHDABSTM_TIME_3, Fc.SP, timestamp));

        values.add(this.setRandomInt(DSCH3_SCHDID_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(DSCH3_SCHDTYP_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(DSCH3_SCHDCAT_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomFloat(DSCH3_SCHDABSTM_VAL_0, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH3_SCHDABSTM_TIME_0, Fc.SP, timestamp));
        values.add(this.setRandomFloat(DSCH3_SCHDABSTM_VAL_1, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH3_SCHDABSTM_TIME_1, Fc.SP, timestamp));
        values.add(this.setRandomFloat(DSCH3_SCHDABSTM_VAL_2, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH3_SCHDABSTM_TIME_2, Fc.SP, timestamp));
        values.add(this.setRandomFloat(DSCH3_SCHDABSTM_VAL_3, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH3_SCHDABSTM_TIME_3, Fc.SP, timestamp));

        values.add(this.setRandomInt(DSCH4_SCHDID_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(DSCH4_SCHDTYP_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(DSCH4_SCHDCAT_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomFloat(DSCH4_SCHDABSTM_VAL_0, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH4_SCHDABSTM_TIME_0, Fc.SP, timestamp));
        values.add(this.setRandomFloat(DSCH4_SCHDABSTM_VAL_1, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH4_SCHDABSTM_TIME_1, Fc.SP, timestamp));
        values.add(this.setRandomFloat(DSCH4_SCHDABSTM_VAL_2, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH4_SCHDABSTM_TIME_2, Fc.SP, timestamp));
        values.add(this.setRandomFloat(DSCH4_SCHDABSTM_VAL_3, Fc.SP, 0, 1000));
        values.add(this.setTime(DSCH4_SCHDABSTM_TIME_3, Fc.SP, timestamp));

        values.add(this.setRandomFloat(MMXU1_TOTW_MAG_F, Fc.MX, 0, 1000));
        values.add(this.setQuality(MMXU1_TOTW_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU1_TOTW_T, Fc.MX, timestamp));
        values.add(this.setRandomFloat(MMXU1_MINWPHS_MAG_F, Fc.MX, 0, 500));
        values.add(this.setQuality(MMXU1_MINWPHS_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU1_MINWPHS_T, Fc.MX, timestamp));
        values.add(this.setRandomFloat(MMXU1_MAXWPHS_MAG_F, Fc.MX, 500, 1000));
        values.add(this.setQuality(MMXU1_MAXWPHS_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU1_MAXWPHS_T, Fc.MX, timestamp));
        values.add(this.setRandomFloat(MMXU1_TOTPF_MAG_F, Fc.MX, 0, 1000));
        values.add(this.setQuality(MMXU1_TOTPF_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MMXU1_TOTPF_T, Fc.MX, timestamp));

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

        values.add(this.setFixedFloat(TTMP1_TMPSV_INSTMAG_F, Fc.MX, 314));
        values.add(this.setQuality(TTMP1_TMPSV_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(TTMP1_TMPSV_T, Fc.MX, timestamp));
        values.add(this.setFixedFloat(TTMP2_TMPSV_INSTMAG_F, Fc.MX, 324));
        values.add(this.setQuality(TTMP2_TMPSV_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(TTMP2_TMPSV_T, Fc.MX, timestamp));
        values.add(this.setFixedFloat(TTMP3_TMPSV_INSTMAG_F, Fc.MX, 334));
        values.add(this.setQuality(TTMP3_TMPSV_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(TTMP3_TMPSV_T, Fc.MX, timestamp));
        values.add(this.setFixedFloat(TTMP4_TMPSV_INSTMAG_F, Fc.MX, 344));
        values.add(this.setQuality(TTMP4_TMPSV_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(TTMP4_TMPSV_T, Fc.MX, timestamp));

        values.add(this.setFixedFloat(MFLW1_FLWRTE_MAG_F, Fc.MX, 314));
        values.add(this.setQuality(MFLW1_FLWRTE_Q, Fc.MX, QualityType.VALIDITY_GOOD.getValue()));
        values.add(this.setTime(MFLW1_FLWRTE_T, Fc.MX, timestamp));

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
            return this.setFixedFloat(node, fc, Integer.parseInt(value));
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
