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
import org.openmuc.openiec61850.ServerModel;

import com.alliander.osgp.simulator.protocol.iec61850.server.QualityType;

public class Pv extends LogicalDevice {

    public Pv(final String physicalDeviceName, final String logicalDeviceName, final ServerModel serverModel) {
        super(physicalDeviceName, logicalDeviceName, serverModel);
    }

    @Override
    public List<BasicDataAttribute> getAttributesAndSetValues(final Date timestamp) {
        final List<BasicDataAttribute> values = new ArrayList<>();

        values.add(this.setRandomByte(Node.LLN0_HEALTH_STVAL, 1, 2));
        values.add(this.setQuality(Node.LLN0_HEALTH_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.LLN0_HEALTH_T, timestamp));

        values.add(this.setRandomByte(Node.LLN0_BEH_STVAL, 1, 2));
        values.add(this.setQuality(Node.LLN0_BEH_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.LLN0_BEH_T, timestamp));

        values.add(this.setRandomByte(Node.LLN0_MOD_STVAL, 1, 2));
        values.add(this.setQuality(Node.LLN0_MOD_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.LLN0_MOD_T, timestamp));

        values.add(this.setRandomFloat(Node.MMXU1_MAXWPHS_MAG_F, 500, 1000));
        values.add(this.setQuality(Node.MMXU1_MAXWPHS_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.MMXU1_MAXWPHS_T, timestamp));

        values.add(this.setRandomFloat(Node.MMXU1_MINWPHS_MAG_F, 0, 500));
        values.add(this.setQuality(Node.MMXU1_MINWPHS_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.MMXU1_MINWPHS_T, timestamp));

        values.add(this.setRandomFloat(Node.MMXU1_TOTW_MAG_F, 0, 1000));
        values.add(this.setQuality(Node.MMXU1_TOTW_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.MMXU1_TOTW_T, timestamp));

        values.add(this.setRandomFloat(Node.DRCC1_OUTWSET_SUBVAL_F, 0, 1000));
        values.add(this.setQuality(Node.DRCC1_OUTWSET_SUBQ, QualityType.VALIDITY_GOOD));

        values.add(this.setRandomFloat(Node.DGEN1_TOTWH_MAG_F, 0, 1000));
        values.add(this.setQuality(Node.DGEN1_TOTWH_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.DGEN1_TOTWH_T, timestamp));

        values.add(this.setRandomByte(Node.DGEN1_GNOPST_STVAL, 1, 2));
        values.add(this.setQuality(Node.DGEN1_GNOPST_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.DGEN1_GNOPST_T, timestamp));

        values.add(this.incrementInt(Node.DGEN1_OPTMSRS_STVAL));
        values.add(this.setQuality(Node.DGEN1_OPTMSRS_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.DGEN1_OPTMSRS_T, timestamp));

        values.add(this.setBoolean(Node.GGIO1_ALM1_STVAL, false));
        values.add(this.setQuality(Node.GGIO1_ALM1_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_ALM1_T, timestamp));

        values.add(this.setBoolean(Node.GGIO1_ALM2_STVAL, false));
        values.add(this.setQuality(Node.GGIO1_ALM2_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_ALM2_T, timestamp));

        values.add(this.setBoolean(Node.GGIO1_ALM3_STVAL, false));
        values.add(this.setQuality(Node.GGIO1_ALM3_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_ALM3_T, timestamp));

        values.add(this.setBoolean(Node.GGIO1_ALM4_STVAL, false));
        values.add(this.setQuality(Node.GGIO1_ALM4_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_ALM4_T, timestamp));

        values.add(this.setRandomInt(Node.GGIO1_INTIN1_STVAL, 1, 100));
        values.add(this.setQuality(Node.GGIO1_INTIN1_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_INTIN1_T, timestamp));

        values.add(this.setBoolean(Node.GGIO1_WRN1_STVAL, false));
        values.add(this.setQuality(Node.GGIO1_WRN1_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_WRN1_T, timestamp));

        values.add(this.setBoolean(Node.GGIO1_WRN2_STVAL, false));
        values.add(this.setQuality(Node.GGIO1_WRN2_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_WRN2_T, timestamp));

        values.add(this.setBoolean(Node.GGIO1_WRN3_STVAL, false));
        values.add(this.setQuality(Node.GGIO1_WRN3_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_WRN3_T, timestamp));

        values.add(this.setBoolean(Node.GGIO1_WRN4_STVAL, false));
        values.add(this.setQuality(Node.GGIO1_WRN4_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_WRN4_T, timestamp));

        values.add(this.setRandomInt(Node.GGIO1_INTIN2_STVAL, 1, 100));
        values.add(this.setQuality(Node.GGIO1_INTIN2_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_INTIN2_T, timestamp));

        return values;
    }
}
