package com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.adhoc;

import java.math.BigInteger;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SpecificAttributeValueRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.cucumber.platform.dlms.Keys;

public class SpecificAttributeValueRequestBuilder {

    private String deviceIdentification;
    private BigInteger classId;
    private ObisCodeValues obisCode;
    private BigInteger attribute;

    public SpecificAttributeValueRequestBuilder fromParameterMap(final Map<String, String> parameters) {

        this.deviceIdentification = parameters.get(Keys.DEVICE_IDENTIFICATION);
        this.classId = new BigInteger(parameters.get(Keys.CLASS_ID));
        this.obisCode = new ObisCodeValues();
        this.obisCode.setA(Short.parseShort(parameters.get(Keys.OBIS_CODE_A)));
        this.obisCode.setB(Short.parseShort(parameters.get(Keys.OBIS_CODE_B)));
        this.obisCode.setC(Short.parseShort(parameters.get(Keys.OBIS_CODE_C)));
        this.obisCode.setD(Short.parseShort(parameters.get(Keys.OBIS_CODE_D)));
        this.obisCode.setE(Short.parseShort(parameters.get(Keys.OBIS_CODE_E)));
        this.obisCode.setF(Short.parseShort(parameters.get(Keys.OBIS_CODE_F)));
        this.attribute = new BigInteger(parameters.get(Keys.ATTRIBUTE));
        return this;
    }

    public SpecificAttributeValueRequest build() {
        final SpecificAttributeValueRequest request = new SpecificAttributeValueRequest();
        request.setDeviceIdentification(this.deviceIdentification);
        request.setClassId(this.classId);
        request.setObisCode(this.obisCode);
        request.setAttribute(this.attribute);
        return request;
    }
}
