/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import java.math.BigInteger;
import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class GetSpecificAttributeValueRequestBuilder {

    private static final BigInteger DEFAULT_CLASS_ID = BigInteger.valueOf(1);
    private static final short DEFAULT_OBIS_CODE_A = 0;
    private static final short DEFAULT_OBIS_CODE_B = 0;
    private static final short DEFAULT_OBIS_CODE_C = 42;
    private static final short DEFAULT_OBIS_CODE_D = 0;
    private static final short DEFAULT_OBIS_CODE_E = 0;
    private static final short DEFAULT_OBIS_CODE_F = 255;
    private static final BigInteger DEFAULT_ATTRIBUTE = BigInteger.valueOf(2);

    private BigInteger classId;
    private ObisCodeValues obisCode;
    private BigInteger attribute;

    public GetSpecificAttributeValueRequestBuilder withDefaults() {
        this.classId = DEFAULT_CLASS_ID;
        this.obisCode = new ObisCodeValues();
        this.obisCode.setA(DEFAULT_OBIS_CODE_A);
        this.obisCode.setB(DEFAULT_OBIS_CODE_B);
        this.obisCode.setC(DEFAULT_OBIS_CODE_C);
        this.obisCode.setD(DEFAULT_OBIS_CODE_D);
        this.obisCode.setE(DEFAULT_OBIS_CODE_E);
        this.obisCode.setF(DEFAULT_OBIS_CODE_F);
        this.attribute = DEFAULT_ATTRIBUTE;

        return this;
    }

    public GetSpecificAttributeValueRequestBuilder fromParameterMap(final Map<String, String> parameters) {

        this.classId = new BigInteger(parameters.get(PlatformSmartmeteringKeys.CLASS_ID));
        this.obisCode = new ObisCodeValues();
        this.obisCode.setA(Short.parseShort(parameters.get(PlatformSmartmeteringKeys.OBIS_CODE_A)));
        this.obisCode.setB(Short.parseShort(parameters.get(PlatformSmartmeteringKeys.OBIS_CODE_B)));
        this.obisCode.setC(Short.parseShort(parameters.get(PlatformSmartmeteringKeys.OBIS_CODE_C)));
        this.obisCode.setD(Short.parseShort(parameters.get(PlatformSmartmeteringKeys.OBIS_CODE_D)));
        this.obisCode.setE(Short.parseShort(parameters.get(PlatformSmartmeteringKeys.OBIS_CODE_E)));
        this.obisCode.setF(Short.parseShort(parameters.get(PlatformSmartmeteringKeys.OBIS_CODE_F)));
        this.attribute = new BigInteger(parameters.get(PlatformSmartmeteringKeys.ATTRIBUTE));
        return this;
    }

    public GetSpecificAttributeValueRequest build() {
        final GetSpecificAttributeValueRequest request = new GetSpecificAttributeValueRequest();
        request.setClassId(this.classId);
        request.setObisCode(this.obisCode);
        request.setAttribute(this.attribute);
        return request;
    }
}
