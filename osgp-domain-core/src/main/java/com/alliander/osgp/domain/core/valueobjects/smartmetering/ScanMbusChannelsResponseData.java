/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class ScanMbusChannelsResponseData extends ActionResponse implements Serializable {

    private static final long serialVersionUID = -7904676670230333481L;

    private String mbusIdentificationNumber1;
    private String mbusIdentificationNumber2;
    private String mbusIdentificationNumber3;
    private String mbusIdentificationNumber4;

    public ScanMbusChannelsResponseData(final String mbusIdentificationNumber1, final String mbusIdentificationNumber2,
            final String mbusIdentificationNumber3, final String mbusIdentificationNumber4) {
        this.mbusIdentificationNumber1 = mbusIdentificationNumber1;
        this.mbusIdentificationNumber2 = mbusIdentificationNumber2;
        this.mbusIdentificationNumber3 = mbusIdentificationNumber3;
        this.mbusIdentificationNumber4 = mbusIdentificationNumber4;
    }

    public String getMbusIdentificationNumber1() {
        return this.mbusIdentificationNumber1;
    }

    public String getMbusIdentificationNumber2() {
        return this.mbusIdentificationNumber2;
    }

    public String getMbusIdentificationNumber3() {
        return this.mbusIdentificationNumber3;
    }

    public String getMbusIdentificationNumber4() {
        return this.mbusIdentificationNumber4;
    }

}
