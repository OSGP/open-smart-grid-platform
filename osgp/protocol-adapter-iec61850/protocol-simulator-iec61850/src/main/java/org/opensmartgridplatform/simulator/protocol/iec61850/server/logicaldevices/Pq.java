/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.ServerModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.QualityType;

public class Pq extends LogicalDevice {

  public Pq(
      final String physicalDeviceName,
      final String logicalDeviceName,
      final ServerModel serverModel) {
    super(physicalDeviceName, logicalDeviceName, serverModel);
  }

  @Override
  public List<BasicDataAttribute> getAttributesAndSetValues(final Date timestamp) {
    final List<BasicDataAttribute> values = new ArrayList<>();

    values.add(this.setRandomByte(LogicalDeviceNode.LLN0_HEALTH_STVAL, 1, 2));
    values.add(this.setQuality(LogicalDeviceNode.LLN0_HEALTH_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.LLN0_HEALTH_T, timestamp));

    values.add(this.setRandomByte(LogicalDeviceNode.LLN0_BEH_STVAL, 1, 2));
    values.add(this.setQuality(LogicalDeviceNode.LLN0_BEH_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.LLN0_BEH_T, timestamp));

    values.add(this.setRandomByte(LogicalDeviceNode.LLN0_MOD_STVAL, 1, 2));
    values.add(this.setQuality(LogicalDeviceNode.LLN0_MOD_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.LLN0_MOD_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_ALM1_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_ALM1_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_ALM1_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_ALM2_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_ALM2_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_ALM2_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_ALM3_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_ALM3_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_ALM3_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_ALM4_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_ALM4_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_ALM4_T, timestamp));

    values.add(this.setRandomInt(LogicalDeviceNode.GGIO1_INTIN1_STVAL, 1, 100));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_INTIN1_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_INTIN1_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_WRN1_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_WRN1_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_WRN1_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_WRN2_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_WRN2_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_WRN2_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_WRN3_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_WRN3_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_WRN3_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_WRN4_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_WRN4_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_WRN4_T, timestamp));

    values.add(this.setRandomInt(LogicalDeviceNode.GGIO1_INTIN2_STVAL, 1, 100));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_INTIN2_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_INTIN2_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU1_HZ_MAG_F, 500, 1000));
    values.add(this.setQuality(LogicalDeviceNode.MMXU1_HZ_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU1_HZ_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU1_PNV_PHSA_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU1_PNV_PHSA_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU1_PNV_PHSA_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU1_PNV_PHSB_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU1_PNV_PHSB_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU1_PNV_PHSB_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU1_PNV_PHSC_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU1_PNV_PHSC_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU1_PNV_PHSC_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU1_PF_PHSA_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU1_PF_PHSA_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU1_PF_PHSA_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU1_PF_PHSB_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU1_PF_PHSB_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU1_PF_PHSB_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU1_PF_PHSC_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU1_PF_PHSC_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU1_PF_PHSC_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU1_Z_PHSA_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU1_Z_PHSA_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU1_Z_PHSA_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU1_Z_PHSB_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU1_Z_PHSB_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU1_Z_PHSB_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU1_Z_PHSC_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU1_Z_PHSC_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU1_Z_PHSC_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU2_HZ_MAG_F, 500, 1000));
    values.add(this.setQuality(LogicalDeviceNode.MMXU2_HZ_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU2_HZ_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU2_PNV_PHSA_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU2_PNV_PHSA_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU2_PNV_PHSA_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU2_PNV_PHSB_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU2_PNV_PHSB_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU2_PNV_PHSB_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU2_PNV_PHSC_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU2_PNV_PHSC_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU2_PNV_PHSC_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU2_PF_PHSA_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU2_PF_PHSA_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU2_PF_PHSA_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU2_PF_PHSB_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU2_PF_PHSB_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU2_PF_PHSB_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU2_PF_PHSC_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU2_PF_PHSC_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU2_PF_PHSC_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU3_HZ_MAG_F, 500, 1000));
    values.add(this.setQuality(LogicalDeviceNode.MMXU3_HZ_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU3_HZ_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU3_PNV_PHSA_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU3_PNV_PHSA_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU3_PNV_PHSA_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU3_PNV_PHSB_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU3_PNV_PHSB_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU3_PNV_PHSB_CVAL_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU3_PNV_PHSC_CVAL_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU3_PNV_PHSC_CVAL_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU3_PNV_PHSC_CVAL_T, timestamp));

    values.add(this.setRandomByte(LogicalDeviceNode.QVVR1_OPCNTRS_CTLMODEL, 0, 100));

    return values;
  }
}
