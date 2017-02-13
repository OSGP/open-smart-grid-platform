package com.alliander.osgp.platform.dlms.cucumber.builders;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getShort;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;

public class ObisCodeValuesBuilder {

    private short obisCodeA;
    private short obisCodeB;
    private short obisCodeC;
    private short obisCodeD;
    private short obisCodeE;
    private short obisCodeF;

    public ObisCodeValuesBuilder withObisCodeA(final short value) {
        this.obisCodeA = value;
        return this;
    }

    public ObisCodeValuesBuilder withObisCodeB(final short value) {
        this.obisCodeB = value;
        return this;
    }

    public ObisCodeValuesBuilder withObisCodeC(final short value) {
        this.obisCodeC = value;
        return this;
    }

    public ObisCodeValuesBuilder withObisCodeD(final short value) {
        this.obisCodeD = value;
        return this;
    }

    public ObisCodeValuesBuilder withObisCodeE(final short value) {
        this.obisCodeE = value;
        return this;
    }

    public ObisCodeValuesBuilder withObisCodeF(final short value) {
        this.obisCodeF = value;
        return this;
    }

    public ObisCodeValuesBuilder fromSettings(final Map<String, String> settings) {
        this.obisCodeA = (getShort(settings, "ObisCodeA", (short) 0));
        this.obisCodeB = (getShort(settings, "ObisCodeB", (short) 0));
        this.obisCodeC = (getShort(settings, "ObisCodeC", (short) 0));
        this.obisCodeD = (getShort(settings, "ObisCodeD", (short) 0));
        this.obisCodeE = (getShort(settings, "ObisCodeE", (short) 0));
        this.obisCodeF = (getShort(settings, "ObisCodeF", (short) 0));
        return this;
    }

    public ObisCodeValues build() {
        ObisCodeValues result = new ObisCodeValues();
        result.setA(this.obisCodeA);
        result.setB(this.obisCodeB);
        result.setC(this.obisCodeC);
        result.setD(this.obisCodeD);
        result.setE(this.obisCodeE);
        result.setF(this.obisCodeF);
        return result;
    }

}
