package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import java.math.BigInteger;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetSpecificAttributeValueRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class GetSpecificAttributeValueRequestBuilder {

    private BigInteger classId;
    private ObisCodeValues obisCode;
    private BigInteger attribute;

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
