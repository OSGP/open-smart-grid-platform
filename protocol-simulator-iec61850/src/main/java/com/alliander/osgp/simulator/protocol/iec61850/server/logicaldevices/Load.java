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
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.ServerModel;

/**
 * @author sander
 *
 */
public class Load extends LogicalDevice {

    private static final String MMXU = "MMXU";
    private static final String MMTR = "MMTR";

    /**
     * @param physicalDeviceName
     * @param logicalDeviceName
     * @param serverModel
     */
    public Load(final String physicalDeviceName, final String logicalDeviceName, final ServerModel serverModel) {
        super(physicalDeviceName, logicalDeviceName, serverModel);
    }

    @Override
    public List<BasicDataAttribute> getValues(final Date timestamp) {

        final List<BasicDataAttribute> values = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            values.add(this.setRandomFloat(MMXU + i + ".MaxWPhs.mag.f", Fc.MX, 500, 1000));
            values.add(this.setTime(MMXU + i + ".MaxWPhs.t", Fc.MX, timestamp));

            values.add(this.setRandomFloat(MMXU + i + ".MinWPhs.mag.f", Fc.MX, 0, 500));
            values.add(this.setTime(MMXU + i + ".MinWPhs.t", Fc.MX, timestamp));

            values.add(this.setFixedFloat(MMXU + i + ".TotW.mag.f", Fc.MX, i));
            values.add(this.setTime(MMXU + i + ".TotW.t", Fc.MX, timestamp));

            values.add(this.setFixedInt(MMTR + i + ".TotWh.actVal", Fc.ST, i));
            values.add(this.setTime(MMTR + i + ".TotWh.t", Fc.ST, timestamp));
        }

        return values;
    }

}
