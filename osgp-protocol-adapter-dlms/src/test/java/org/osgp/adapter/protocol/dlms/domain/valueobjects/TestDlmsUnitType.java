/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.valueobjects;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnitTypeDto;

public class TestDlmsUnitType {

    @Test
    public void testGetUnit() {
        final String result = DlmsUnitTypeDto.getUnit(1);
        Assert.assertTrue("YEAR".equals(result));
    }

    @Test
    public void testGetKwh() {
        final String result = DlmsUnitTypeDto.getUnit(30);
        Assert.assertTrue("KWH".equals(result));
    }

    @Test
    public void testGetUndefined() {
        final String result = DlmsUnitTypeDto.getUnit(0);
        Assert.assertTrue("UNDEFINED".equals(result));
    }

    @Test
    public void testUnknown() {
        final String result = DlmsUnitTypeDto.getUnit(100);
        Assert.assertTrue("UNDEFINED".equals(result));
    }

    /*
     * Use this method to (re)generate the corresponding xsd. Note enable the
     * System.out!
     */
    @Test
    public void generateXsd() {
        StringBuffer sb = new StringBuffer();
        sb.append("  <xsd:simpleType name=\"OsgpUnitType\">\n");
        sb.append("    <xsd:restriction base=\"xsd:string\">\n");
        for (DlmsUnitTypeDto unitType : DlmsUnitTypeDto.values()) {
            sb.append(this.xsdFragment(unitType));
        }
        sb.append("    </xsd:restriction>\n");
        sb.append("  </xsd:simpleType>\n");
        System.out.println(sb.toString());
    }

    /*
     * Thos method generates sometj=hin like: <xsd:enumeration value="M3">
     * <xsd:annotation> <xsd:documentation>cubic meter</xsd:documentation>
     * </xsd:annotation> </xsd:enumeration>
     */
    private String xsdFragment(final DlmsUnitTypeDto unitType) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("      <xsd:enumeration value=\"%s\">\n", unitType.name()));
        sb.append("      <xsd:annotation>\n");
        sb.append("        <xsd:documentation>todo</xsd:documentation>\n");
        sb.append("      </xsd:annotation>\n");
        sb.append("      </xsd:enumeration>\n");
        return sb.toString();
    }
}
