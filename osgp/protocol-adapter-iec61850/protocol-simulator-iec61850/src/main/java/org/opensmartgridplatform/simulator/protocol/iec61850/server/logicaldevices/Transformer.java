/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmuc.openiec61850.BasicDataAttribute;
import org.openmuc.openiec61850.ServerModel;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.QualityType;

public class Transformer extends LogicalDevice {

    public Transformer(final String physicalDeviceName, final String logicalDeviceName, final ServerModel serverModel) {
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

        values.add(this.setFixedFloat(LogicalDeviceNode.TTMP1_TMPSV_INSTMAG_F, 314));
        values.add(this.setQuality(LogicalDeviceNode.TTMP1_TMPSV_Q, QualityType.INACCURATE));
        values.add(this.setTime(LogicalDeviceNode.TTMP1_TMPSV_T, timestamp));

        values.add(this.setFixedFloat(LogicalDeviceNode.MMXU1_TOTW_MAG_F, 750));
        values.add(this.setQuality(LogicalDeviceNode.MMXU1_TOTW_Q, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(LogicalDeviceNode.MMXU1_TOTW_T, timestamp));

        return values;
    }
}
