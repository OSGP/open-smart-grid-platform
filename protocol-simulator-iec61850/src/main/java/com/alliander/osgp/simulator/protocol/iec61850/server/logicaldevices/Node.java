/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices;

import org.openmuc.openiec61850.BdaType;

public enum Node {

    DSCH1_SCHDID_SETVAL("DSCH1.SchdId.setVal", BdaType.INT32),
    DSCH1_SCHDTYP_SETVAL("DSCH1.SchdTyp.setVal", BdaType.INT32),
    DSCH1_SCHDCAT_SETVAL("DSCH1.SchCat.setVal", BdaType.INT32),
    DSCH1_SCHDABSTM_VAL_0("DSCH1.SchdAbsTm.val.0", BdaType.FLOAT32),
    DSCH1_SCHDABSTM_TIME_0("DSCH1.SchdAbsTm.time.0", BdaType.TIMESTAMP),
    DSCH1_SCHDABSTM_VAL_1("DSCH1.SchdAbsTm.val.1", BdaType.FLOAT32),
    DSCH1_SCHDABSTM_TIME_1("DSCH1.SchdAbsTm.time.1", BdaType.TIMESTAMP),
    DSCH1_SCHDABSTM_VAL_2("DSCH1.SchdAbsTm.val.2", BdaType.FLOAT32),
    DSCH1_SCHDABSTM_TIME_2("DSCH1.SchdAbsTm.time.2", BdaType.TIMESTAMP),
    DSCH1_SCHDABSTM_VAL_3("DSCH1.SchdAbsTm.val.3", BdaType.FLOAT32),
    DSCH1_SCHDABSTM_TIME_3("DSCH1.SchdAbsTm.time.3", BdaType.TIMESTAMP),

    DSCH2_SCHDID_SETVAL("DSCH2.SchdId.setVal", BdaType.INT32),
    DSCH2_SCHDTYP_SETVAL("DSCH2.SchdTyp.setVal", BdaType.INT32),
    DSCH2_SCHDCAT_SETVAL("DSCH2.SchCat.setVal", BdaType.INT32),
    DSCH2_SCHDABSTM_VAL_0("DSCH2.SchdAbsTm.val.0", BdaType.FLOAT32),
    DSCH2_SCHDABSTM_TIME_0("DSCH2.SchdAbsTm.time.0", BdaType.TIMESTAMP),
    DSCH2_SCHDABSTM_VAL_1("DSCH2.SchdAbsTm.val.1", BdaType.FLOAT32),
    DSCH2_SCHDABSTM_TIME_1("DSCH2.SchdAbsTm.time.1", BdaType.TIMESTAMP),
    DSCH2_SCHDABSTM_VAL_2("DSCH2.SchdAbsTm.val.2", BdaType.FLOAT32),
    DSCH2_SCHDABSTM_TIME_2("DSCH2.SchdAbsTm.time.2", BdaType.TIMESTAMP),
    DSCH2_SCHDABSTM_VAL_3("DSCH2.SchdAbsTm.val.3", BdaType.FLOAT32),
    DSCH2_SCHDABSTM_TIME_3("DSCH2.SchdAbsTm.time.3", BdaType.TIMESTAMP),

    DSCH3_SCHDID_SETVAL("DSCH3.SchdId.setVal", BdaType.INT32),
    DSCH3_SCHDTYP_SETVAL("DSCH3.SchdTyp.setVal", BdaType.INT32),
    DSCH3_SCHDCAT_SETVAL("DSCH3.SchCat.setVal", BdaType.INT32),
    DSCH3_SCHDABSTM_VAL_0("DSCH3.SchdAbsTm.val.0", BdaType.FLOAT32),
    DSCH3_SCHDABSTM_TIME_0("DSCH3.SchdAbsTm.time.0", BdaType.TIMESTAMP),
    DSCH3_SCHDABSTM_VAL_1("DSCH3.SchdAbsTm.val.1", BdaType.FLOAT32),
    DSCH3_SCHDABSTM_TIME_1("DSCH3.SchdAbsTm.time.1", BdaType.TIMESTAMP),
    DSCH3_SCHDABSTM_VAL_2("DSCH3.SchdAbsTm.val.2", BdaType.FLOAT32),
    DSCH3_SCHDABSTM_TIME_2("DSCH3.SchdAbsTm.time.2", BdaType.TIMESTAMP),
    DSCH3_SCHDABSTM_VAL_3("DSCH3.SchdAbsTm.val.3", BdaType.FLOAT32),
    DSCH3_SCHDABSTM_TIME_3("DSCH3.SchdAbsTm.time.3", BdaType.TIMESTAMP),

    DSCH4_SCHDID_SETVAL("DSCH4.SchdId.setVal", BdaType.INT32),
    DSCH4_SCHDTYP_SETVAL("DSCH4.SchdTyp.setVal", BdaType.INT32),
    DSCH4_SCHDCAT_SETVAL("DSCH4.SchCat.setVal", BdaType.INT32),
    DSCH4_SCHDABSTM_VAL_0("DSCH4.SchdAbsTm.val.0", BdaType.FLOAT32),
    DSCH4_SCHDABSTM_TIME_0("DSCH4.SchdAbsTm.time.0", BdaType.TIMESTAMP),
    DSCH4_SCHDABSTM_VAL_1("DSCH4.SchdAbsTm.val.1", BdaType.FLOAT32),
    DSCH4_SCHDABSTM_TIME_1("DSCH4.SchdAbsTm.time.1", BdaType.TIMESTAMP),
    DSCH4_SCHDABSTM_VAL_2("DSCH4.SchdAbsTm.val.2", BdaType.FLOAT32),
    DSCH4_SCHDABSTM_TIME_2("DSCH4.SchdAbsTm.time.2", BdaType.TIMESTAMP),
    DSCH4_SCHDABSTM_VAL_3("DSCH4.SchdAbsTm.val.3", BdaType.FLOAT32),
    DSCH4_SCHDABSTM_TIME_3("DSCH4.SchdAbsTm.time.3", BdaType.TIMESTAMP),

    LLN0_HEALTH_STVAL("LLN0.Health.stVal", BdaType.INT8),
    LLN0_HEALTH_Q("LLN0.Health.q", BdaType.QUALITY),
    LLN0_HEALTH_T("LLN0.Health.t", BdaType.TIMESTAMP),

    LLN0_BEH_STVAL("LLN0.Beh.stVal", BdaType.INT8),
    LLN0_BEH_Q("LLN0.Beh.q", BdaType.QUALITY),
    LLN0_BEH_T("LLN0.Beh.t", BdaType.TIMESTAMP),

    LLN0_MOD_STVAL("LLN0.Mod.stVal", BdaType.INT8),
    LLN0_MOD_Q("LLN0.Mod.q", BdaType.QUALITY),
    LLN0_MOD_T("LLN0.Mod.t", BdaType.TIMESTAMP),

    MMXU1_MAXWPHS_MAG_F("MMXU1.MaxWPhs.mag.f", BdaType.FLOAT32),
    MMXU1_MAXWPHS_Q("MMXU1.MaxWPhs.q", BdaType.QUALITY),
    MMXU1_MAXWPHS_T("MMXU1.MaxWPhs.t", BdaType.TIMESTAMP),

    MMXU1_MINWPHS_MAG_F("MMXU1.MinWPhs.mag.f", BdaType.FLOAT32),
    MMXU1_MINWPHS_Q("MMXU1.MinWPhs.q", BdaType.QUALITY),
    MMXU1_MINWPHS_T("MMXU1.MinWPhs.t", BdaType.TIMESTAMP),

    MMXU1_TOTW_MAG_F("MMXU1.TotW.mag.f", BdaType.FLOAT32),
    MMXU1_TOTW_Q("MMXU1.TotW.q", BdaType.QUALITY),
    MMXU1_TOTW_T("MMXU1.TotW.t", BdaType.TIMESTAMP),

    MMXU1_TOTPF_MAG_F("MMXU1.TotPF.mag.f", BdaType.FLOAT32),
    MMXU1_TOTPF_Q("MMXU1.TotPF.q", BdaType.QUALITY),
    MMXU1_TOTPF_T("MMXU1.TotPF.t", BdaType.TIMESTAMP),

    DRCC1_OUTWSET_SUBVAL_F("DRCC1.OutWSet.subVal.f", BdaType.FLOAT32),
    DRCC1_OUTWSET_SUBQ("DRCC1.OutWSet.subQ", BdaType.QUALITY),

    DGEN1_TOTWH_MAG_F("DGEN1.TotWh.mag.f", BdaType.FLOAT32),
    DGEN1_TOTWH_Q("DGEN1.TotWh.q", BdaType.QUALITY),
    DGEN1_TOTWH_T("DGEN1.TotWh.t", BdaType.TIMESTAMP),

    DGEN1_GNOPST_STVAL("DGEN1.GnOpSt.stVal", BdaType.INT8),
    DGEN1_GNOPST_Q("DGEN1.GnOpSt.q", BdaType.QUALITY),
    DGEN1_GNOPST_T("DGEN1.GnOpSt.t", BdaType.TIMESTAMP),

    DGEN1_OPTMSRS_STVAL("DGEN1.OpTmsRs.stVal", BdaType.INT32),
    DGEN1_OPTMSRS_Q("DGEN1.OpTmsRs.q", BdaType.QUALITY),
    DGEN1_OPTMSRS_T("DGEN1.OpTmsRs.t", BdaType.TIMESTAMP),

    GGIO1_ALM1_STVAL("GGIO1.Alm1.stVal", BdaType.BOOLEAN),
    GGIO1_ALM1_Q("GGIO1.Alm1.q", BdaType.QUALITY),
    GGIO1_ALM1_T("GGIO1.Alm1.t", BdaType.TIMESTAMP),

    GGIO1_ALM2_STVAL("GGIO1.Alm2.stVal", BdaType.BOOLEAN),
    GGIO1_ALM2_Q("GGIO1.Alm2.q", BdaType.QUALITY),
    GGIO1_ALM2_T("GGIO1.Alm2.t", BdaType.TIMESTAMP),

    GGIO1_ALM3_STVAL("GGIO1.Alm3.stVal", BdaType.BOOLEAN),
    GGIO1_ALM3_Q("GGIO1.Alm3.q", BdaType.QUALITY),
    GGIO1_ALM3_T("GGIO1.Alm3.t", BdaType.TIMESTAMP),

    GGIO1_ALM4_STVAL("GGIO1.Alm4.stVal", BdaType.BOOLEAN),
    GGIO1_ALM4_Q("GGIO1.Alm4.q", BdaType.QUALITY),
    GGIO1_ALM4_T("GGIO1.Alm4.t", BdaType.TIMESTAMP),

    GGIO1_INTIN1_STVAL("GGIO1.IntIn1.stVal", BdaType.INT32),
    GGIO1_INTIN1_Q("GGIO1.IntIn1.q", BdaType.QUALITY),
    GGIO1_INTIN1_T("GGIO1.IntIn1.t", BdaType.TIMESTAMP),

    GGIO1_INTIN2_STVAL("GGIO1.IntIn2.stVal", BdaType.INT32),
    GGIO1_INTIN2_Q("GGIO1.IntIn2.q", BdaType.QUALITY),
    GGIO1_INTIN2_T("GGIO1.IntIn2.t", BdaType.TIMESTAMP),

    GGIO1_WRN1_STVAL("GGIO1.Wrn1.stVal", BdaType.BOOLEAN),
    GGIO1_WRN1_Q("GGIO1.Wrn1.q", BdaType.QUALITY),
    GGIO1_WRN1_T("GGIO1.Wrn1.t", BdaType.TIMESTAMP),

    GGIO1_WRN2_STVAL("GGIO1.Wrn2.stVal", BdaType.BOOLEAN),
    GGIO1_WRN2_Q("GGIO1.Wrn2.q", BdaType.QUALITY),
    GGIO1_WRN2_T("GGIO1.Wrn2.t", BdaType.TIMESTAMP),

    GGIO1_WRN3_STVAL("GGIO1.Wrn3.stVal", BdaType.BOOLEAN),
    GGIO1_WRN3_Q("GGIO1.Wrn3.q", BdaType.QUALITY),
    GGIO1_WRN3_T("GGIO1.Wrn3.t", BdaType.TIMESTAMP),

    GGIO1_WRN4_STVAL("GGIO1.Wrn4.stVal", BdaType.BOOLEAN),
    GGIO1_WRN4_Q("GGIO1.Wrn4.q", BdaType.QUALITY),
    GGIO1_WRN4_T("GGIO1.Wrn4.t", BdaType.TIMESTAMP),

    TTMP1_TMPSV_INSTMAG_F("TTMP1.TmpSv.instMag.f", BdaType.FLOAT32),
    TTMP1_TMPSV_Q("TTMP1.TmpSv.q", BdaType.QUALITY),
    TTMP1_TMPSV_T("TTMP1.TmpSv.t", BdaType.TIMESTAMP),
    TTMP2_TMPSV_INSTMAG_F("TTMP2.TmpSv.instMag.f", BdaType.FLOAT32),
    TTMP2_TMPSV_Q("TTMP2.TmpSv.q", BdaType.QUALITY),
    TTMP2_TMPSV_T("TTMP2.TmpSv.t", BdaType.TIMESTAMP),
    TTMP3_TMPSV_INSTMAG_F("TTMP3.TmpSv.instMag.f", BdaType.FLOAT32),
    TTMP3_TMPSV_Q("TTMP3.TmpSv.q", BdaType.QUALITY),
    TTMP3_TMPSV_T("TTMP3.TmpSv.t", BdaType.TIMESTAMP),
    TTMP4_TMPSV_INSTMAG_F("TTMP4.TmpSv.instMag.f", BdaType.FLOAT32),
    TTMP4_TMPSV_Q("TTMP4.TmpSv.q", BdaType.QUALITY),
    TTMP4_TMPSV_T("TTMP4.TmpSv.t", BdaType.TIMESTAMP),

    MFLW1_FLWRTE_MAG_F("MFLW1.FlwRte.mag.f", BdaType.FLOAT32),
    MFLW1_FLWRTE_Q("MFLW1.FlwRte.q", BdaType.QUALITY),
    MFLW1_FLWRTE_T("MFLW1.FlwRte.t", BdaType.TIMESTAMP),

    MFLW2_FLWRTE_MAG_F("MFLW2.FlwRte.mag.f", BdaType.FLOAT32),
    MFLW2_FLWRTE_Q("MFLW2.FlwRte.q", BdaType.QUALITY),
    MFLW2_FLWRTE_T("MFLW2.FlwRte.t", BdaType.TIMESTAMP),

    KTNK1_VLMCAP_SETMAG_F("KTNK1.VlmCap.setMag.f", BdaType.FLOAT32),

    MMXU2_TOTW_MAG_F("MMXU2.TotW.mag.f", BdaType.FLOAT32),
    MMXU2_TOTW_Q("MMXU2.TotW.q", BdaType.QUALITY),
    MMXU2_TOTW_T("MMXU2.TotW.t", BdaType.TIMESTAMP),

    MMXU2_MINWPHS_MAG_F("MMXU2.MinWPhs.mag.f", BdaType.FLOAT32),
    MMXU2_MINWPHS_Q("MMXU2.MinWPhs.q", BdaType.QUALITY),
    MMXU2_MINWPHS_T("MMXU2.MinWPhs.t", BdaType.TIMESTAMP),

    MMXU2_MAXWPHS_MAG_F("MMXU2.MaxWPhs.mag.f", BdaType.FLOAT32),
    MMXU2_MAXWPHS_Q("MMXU2.MaxWPhs.q", BdaType.QUALITY),
    MMXU2_MAXWPHS_T("MMXU2.MaxWPhs.t", BdaType.TIMESTAMP),

    MMTR1_TOTWH_ACTVAL("MMTR1.TotWh.actVal", BdaType.INT64),
    MMTR1_TOTWH_Q("MMTR1.TotWh.q", BdaType.QUALITY),
    MMTR1_TOTWH_T("MMTR1.TotWh.t", BdaType.TIMESTAMP),

    MMXU1_W_PHSA_CVAL_MAG_F("MMXU1.W.phsA.cVal.mag.f", BdaType.FLOAT32),
    MMXU1_W_PHSA_Q("MMXU1.W.phsA.q", BdaType.QUALITY),
    MMXU1_W_PHSA_T("MMXU1.W.phsA.t", BdaType.TIMESTAMP),

    MMXU1_W_PHSB_CVAL_MAG_F("MMXU1.W.phsB.cVal.mag.f", BdaType.FLOAT32),
    MMXU1_W_PHSB_Q("MMXU1.W.phsB.q", BdaType.QUALITY),
    MMXU1_W_PHSB_T("MMXU1.W.phsB.t", BdaType.TIMESTAMP),

    MMXU1_W_PHSC_CVAL_MAG_F("MMXU1.W.phsC.cVal.mag.f", BdaType.FLOAT32),
    MMXU1_W_PHSC_Q("MMXU1.W.phsC.q", BdaType.QUALITY),
    MMXU1_W_PHSC_T("MMXU1.W.phsC.t", BdaType.TIMESTAMP),

    MMXU2_W_PHSA_CVAL_MAG_F("MMXU2.W.phsA.cVal.mag.f", BdaType.FLOAT32),
    MMXU2_W_PHSA_Q("MMXU2.W.phsA.q", BdaType.QUALITY),
    MMXU2_W_PHSA_T("MMXU2.W.phsA.t", BdaType.TIMESTAMP),

    MMXU2_W_PHSB_CVAL_MAG_F("MMXU2.W.phsB.cVal.mag.f", BdaType.FLOAT32),
    MMXU2_W_PHSB_Q("MMXU2.W.phsB.q", BdaType.QUALITY),
    MMXU2_W_PHSB_T("MMXU2.W.phsB.t", BdaType.TIMESTAMP),

    MMXU2_W_PHSC_CVAL_MAG_F("MMXU2.W.phsC.cVal.mag.f", BdaType.FLOAT32),
    MMXU2_W_PHSC_Q("MMXU2.W.phsC.q", BdaType.QUALITY),
    MMXU2_W_PHSC_T("MMXU2.W.phsC.t", BdaType.TIMESTAMP),

    MMXU3_W_PHSA_CVAL_MAG_F("MMXU3.W.phsA.cVal.mag.f", BdaType.FLOAT32),
    MMXU3_W_PHSA_Q("MMXU3.W.phsA.q", BdaType.QUALITY),
    MMXU3_W_PHSA_T("MMXU3.W.phsA.t", BdaType.TIMESTAMP),

    MMXU3_W_PHSB_CVAL_MAG_F("MMXU3.W.phsB.cVal.mag.f", BdaType.FLOAT32),
    MMXU3_W_PHSB_Q("MMXU3.W.phsB.q", BdaType.QUALITY),
    MMXU3_W_PHSB_T("MMXU3.W.phsB.t", BdaType.TIMESTAMP),

    MMXU3_W_PHSC_CVAL_MAG_F("MMXU3.W.phsC.cVal.mag.f", BdaType.FLOAT32),
    MMXU3_W_PHSC_Q("MMXU3.W.phsC.q", BdaType.QUALITY),
    MMXU3_W_PHSC_T("MMXU3.W.phsC.t", BdaType.TIMESTAMP),

    SPGGIO1_IND_D("SPGGIO1.Ind.d", BdaType.VISIBLE_STRING),
    SPGGIO1_IND_STVAL("SPGGIO1.Ind.stVal", BdaType.BOOLEAN),
    SPGGIO2_IND_STVAL("SPGGIO2.Ind.stVal", BdaType.BOOLEAN),
    SPGGIO3_IND_STVAL("SPGGIO3.Ind.stVal", BdaType.BOOLEAN),
    SPGGIO4_IND_STVAL("SPGGIO4.Ind.stVal", BdaType.BOOLEAN);

    private String description;
    private BdaType type;

    Node(final String description, final BdaType type) {
        this.description = description;
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public BdaType getType() {
        return this.type;
    }

    public static Node fromDescription(final String description) {
        for (final Node node : Node.values()) {
            if (node.description.equals(description)) {
                return node;
            }
        }
        throw new IllegalArgumentException("No node with description " + description + " found");
    }

}
