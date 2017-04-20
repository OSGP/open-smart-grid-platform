/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.mocks.iec61850;

import org.slf4j.LoggerFactory;

//@Component
public class Iec61850MockServerWago extends Iec61850MockServerBase {

    public Iec61850MockServerWago() {
        super("WAGO123.icd", 62104, "WAGO123", LoggerFactory.getLogger(Iec61850MockServerWago.class));
    }

}
