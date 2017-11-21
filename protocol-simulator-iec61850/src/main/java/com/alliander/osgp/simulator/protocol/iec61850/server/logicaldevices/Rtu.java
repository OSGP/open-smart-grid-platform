/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openmuc.openiec61850.BasicDataAttribute;
import org.openmuc.openiec61850.BdaType;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.ServerModel;

import com.alliander.osgp.simulator.protocol.iec61850.server.QualityType;

public class Rtu extends LogicalDevice {

    private static final Map<Node, Fc> FC_BY_NODE;
    static {
        final Map<Node, Fc> fcByNode = new TreeMap<>();

        fcByNode.put(Node.DSCH1_SCHDID_SETVAL, Fc.SP);
        fcByNode.put(Node.DSCH1_SCHDTYP_SETVAL, Fc.SP);
        fcByNode.put(Node.DSCH1_SCHDCAT_SETVAL, Fc.SP);
        fcByNode.put(Node.DSCH1_SCHDABSTM_VAL_0, Fc.SP);
        fcByNode.put(Node.DSCH1_SCHDABSTM_TIME_0, Fc.SP);
        fcByNode.put(Node.DSCH1_SCHDABSTM_VAL_1, Fc.SP);
        fcByNode.put(Node.DSCH1_SCHDABSTM_TIME_1, Fc.SP);
        fcByNode.put(Node.DSCH1_SCHDABSTM_VAL_2, Fc.SP);
        fcByNode.put(Node.DSCH1_SCHDABSTM_TIME_2, Fc.SP);
        fcByNode.put(Node.DSCH1_SCHDABSTM_VAL_3, Fc.SP);
        fcByNode.put(Node.DSCH1_SCHDABSTM_TIME_3, Fc.SP);

        fcByNode.put(Node.DSCH2_SCHDID_SETVAL, Fc.SP);
        fcByNode.put(Node.DSCH2_SCHDTYP_SETVAL, Fc.SP);
        fcByNode.put(Node.DSCH2_SCHDCAT_SETVAL, Fc.SP);
        fcByNode.put(Node.DSCH2_SCHDABSTM_VAL_0, Fc.SP);
        fcByNode.put(Node.DSCH2_SCHDABSTM_TIME_0, Fc.SP);
        fcByNode.put(Node.DSCH2_SCHDABSTM_VAL_1, Fc.SP);
        fcByNode.put(Node.DSCH2_SCHDABSTM_TIME_1, Fc.SP);
        fcByNode.put(Node.DSCH2_SCHDABSTM_VAL_2, Fc.SP);
        fcByNode.put(Node.DSCH2_SCHDABSTM_TIME_2, Fc.SP);
        fcByNode.put(Node.DSCH2_SCHDABSTM_VAL_3, Fc.SP);
        fcByNode.put(Node.DSCH2_SCHDABSTM_TIME_3, Fc.SP);

        fcByNode.put(Node.DSCH3_SCHDID_SETVAL, Fc.SP);
        fcByNode.put(Node.DSCH3_SCHDTYP_SETVAL, Fc.SP);
        fcByNode.put(Node.DSCH3_SCHDCAT_SETVAL, Fc.SP);
        fcByNode.put(Node.DSCH3_SCHDABSTM_VAL_0, Fc.SP);
        fcByNode.put(Node.DSCH3_SCHDABSTM_TIME_0, Fc.SP);
        fcByNode.put(Node.DSCH3_SCHDABSTM_VAL_1, Fc.SP);
        fcByNode.put(Node.DSCH3_SCHDABSTM_TIME_1, Fc.SP);
        fcByNode.put(Node.DSCH3_SCHDABSTM_VAL_2, Fc.SP);
        fcByNode.put(Node.DSCH3_SCHDABSTM_TIME_2, Fc.SP);
        fcByNode.put(Node.DSCH3_SCHDABSTM_VAL_3, Fc.SP);
        fcByNode.put(Node.DSCH3_SCHDABSTM_TIME_3, Fc.SP);

        fcByNode.put(Node.DSCH4_SCHDID_SETVAL, Fc.SP);
        fcByNode.put(Node.DSCH4_SCHDTYP_SETVAL, Fc.SP);
        fcByNode.put(Node.DSCH4_SCHDCAT_SETVAL, Fc.SP);
        fcByNode.put(Node.DSCH4_SCHDABSTM_VAL_0, Fc.SP);
        fcByNode.put(Node.DSCH4_SCHDABSTM_TIME_0, Fc.SP);
        fcByNode.put(Node.DSCH4_SCHDABSTM_VAL_1, Fc.SP);
        fcByNode.put(Node.DSCH4_SCHDABSTM_TIME_1, Fc.SP);
        fcByNode.put(Node.DSCH4_SCHDABSTM_VAL_2, Fc.SP);
        fcByNode.put(Node.DSCH4_SCHDABSTM_TIME_2, Fc.SP);
        fcByNode.put(Node.DSCH4_SCHDABSTM_VAL_3, Fc.SP);
        fcByNode.put(Node.DSCH4_SCHDABSTM_TIME_3, Fc.SP);

        FC_BY_NODE = Collections.unmodifiableMap(fcByNode);
    }

    public Rtu(final String physicalDeviceName, final String logicalDeviceName, final ServerModel serverModel) {
        super(physicalDeviceName, logicalDeviceName, serverModel);
    }

    @Override
    public List<BasicDataAttribute> getAttributesAndSetValues(final Date timestamp) {
        final List<BasicDataAttribute> values = new ArrayList<>();

        values.add(this.setRandomByte(Node.LLN0_HEALTH_STVAL, Fc.ST, 1, 2));
        values.add(this.setQuality(Node.LLN0_HEALTH_Q, Fc.ST, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.LLN0_HEALTH_T, Fc.ST, timestamp));

        values.add(this.setRandomByte(Node.LLN0_BEH_STVAL, Fc.ST, 1, 2));
        values.add(this.setQuality(Node.LLN0_BEH_Q, Fc.ST, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.LLN0_BEH_T, Fc.ST, timestamp));

        values.add(this.setRandomByte(Node.LLN0_MOD_STVAL, Fc.ST, 1, 2));
        values.add(this.setQuality(Node.LLN0_MOD_Q, Fc.ST, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.LLN0_MOD_T, Fc.ST, timestamp));

        values.add(this.setBoolean(Node.GGIO1_ALM1_STVAL, Fc.ST, false));
        values.add(this.setQuality(Node.GGIO1_ALM1_Q, Fc.ST, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_ALM1_T, Fc.ST, timestamp));

        values.add(this.setBoolean(Node.GGIO1_ALM2_STVAL, Fc.ST, false));
        values.add(this.setQuality(Node.GGIO1_ALM2_Q, Fc.ST, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_ALM2_T, Fc.ST, timestamp));

        values.add(this.setBoolean(Node.GGIO1_ALM3_STVAL, Fc.ST, false));
        values.add(this.setQuality(Node.GGIO1_ALM3_Q, Fc.ST, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_ALM3_T, Fc.ST, timestamp));

        values.add(this.setBoolean(Node.GGIO1_ALM4_STVAL, Fc.ST, false));
        values.add(this.setQuality(Node.GGIO1_ALM4_Q, Fc.ST, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_ALM4_T, Fc.ST, timestamp));

        values.add(this.setRandomInt(Node.GGIO1_INTIN1_STVAL, Fc.ST, 1, 100));
        values.add(this.setQuality(Node.GGIO1_INTIN1_Q, Fc.ST, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_INTIN1_T, Fc.ST, timestamp));

        values.add(this.setBoolean(Node.GGIO1_WRN1_STVAL, Fc.ST, false));
        values.add(this.setQuality(Node.GGIO1_WRN1_Q, Fc.ST, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_WRN1_T, Fc.ST, timestamp));

        values.add(this.setBoolean(Node.GGIO1_WRN2_STVAL, Fc.ST, false));
        values.add(this.setQuality(Node.GGIO1_WRN2_Q, Fc.ST, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_WRN2_T, Fc.ST, timestamp));

        values.add(this.setBoolean(Node.GGIO1_WRN3_STVAL, Fc.ST, false));
        values.add(this.setQuality(Node.GGIO1_WRN3_Q, Fc.ST, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_WRN3_T, Fc.ST, timestamp));

        values.add(this.setBoolean(Node.GGIO1_WRN4_STVAL, Fc.ST, false));
        values.add(this.setQuality(Node.GGIO1_WRN4_Q, Fc.ST, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_WRN4_T, Fc.ST, timestamp));

        values.add(this.setRandomInt(Node.GGIO1_INTIN2_STVAL, Fc.ST, 1, 100));
        values.add(this.setQuality(Node.GGIO1_INTIN2_Q, Fc.ST, QualityType.VALIDITY_GOOD));
        values.add(this.setTime(Node.GGIO1_INTIN2_T, Fc.ST, timestamp));

        values.add(this.setRandomInt(Node.DSCH1_SCHDID_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(Node.DSCH1_SCHDTYP_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(Node.DSCH1_SCHDCAT_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomFloat(Node.DSCH1_SCHDABSTM_VAL_0, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH1_SCHDABSTM_TIME_0, Fc.SP, timestamp));
        values.add(this.setRandomFloat(Node.DSCH1_SCHDABSTM_VAL_1, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH1_SCHDABSTM_TIME_1, Fc.SP, timestamp));
        values.add(this.setRandomFloat(Node.DSCH1_SCHDABSTM_VAL_2, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH1_SCHDABSTM_TIME_2, Fc.SP, timestamp));
        values.add(this.setRandomFloat(Node.DSCH1_SCHDABSTM_VAL_3, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH1_SCHDABSTM_TIME_3, Fc.SP, timestamp));

        values.add(this.setRandomInt(Node.DSCH2_SCHDID_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(Node.DSCH2_SCHDTYP_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(Node.DSCH2_SCHDCAT_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomFloat(Node.DSCH2_SCHDABSTM_VAL_0, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH2_SCHDABSTM_TIME_0, Fc.SP, timestamp));
        values.add(this.setRandomFloat(Node.DSCH2_SCHDABSTM_VAL_1, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH2_SCHDABSTM_TIME_1, Fc.SP, timestamp));
        values.add(this.setRandomFloat(Node.DSCH2_SCHDABSTM_VAL_2, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH2_SCHDABSTM_TIME_2, Fc.SP, timestamp));
        values.add(this.setRandomFloat(Node.DSCH2_SCHDABSTM_VAL_3, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH2_SCHDABSTM_TIME_3, Fc.SP, timestamp));

        values.add(this.setRandomInt(Node.DSCH3_SCHDID_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(Node.DSCH3_SCHDTYP_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(Node.DSCH3_SCHDCAT_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomFloat(Node.DSCH3_SCHDABSTM_VAL_0, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH3_SCHDABSTM_TIME_0, Fc.SP, timestamp));
        values.add(this.setRandomFloat(Node.DSCH3_SCHDABSTM_VAL_1, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH3_SCHDABSTM_TIME_1, Fc.SP, timestamp));
        values.add(this.setRandomFloat(Node.DSCH3_SCHDABSTM_VAL_2, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH3_SCHDABSTM_TIME_2, Fc.SP, timestamp));
        values.add(this.setRandomFloat(Node.DSCH3_SCHDABSTM_VAL_3, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH3_SCHDABSTM_TIME_3, Fc.SP, timestamp));

        values.add(this.setRandomInt(Node.DSCH4_SCHDID_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(Node.DSCH4_SCHDTYP_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomInt(Node.DSCH4_SCHDCAT_SETVAL, Fc.SP, 1, 100));
        values.add(this.setRandomFloat(Node.DSCH4_SCHDABSTM_VAL_0, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH4_SCHDABSTM_TIME_0, Fc.SP, timestamp));
        values.add(this.setRandomFloat(Node.DSCH4_SCHDABSTM_VAL_1, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH4_SCHDABSTM_TIME_1, Fc.SP, timestamp));
        values.add(this.setRandomFloat(Node.DSCH4_SCHDABSTM_VAL_2, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH4_SCHDABSTM_TIME_2, Fc.SP, timestamp));
        values.add(this.setRandomFloat(Node.DSCH4_SCHDABSTM_VAL_3, Fc.SP, 0, 1000));
        values.add(this.setTime(Node.DSCH4_SCHDABSTM_TIME_3, Fc.SP, timestamp));

        return values;
    }

    @Override
    public BasicDataAttribute getAttributeAndSetValue(final Node node, final String value) {
        final Fc fc = this.getFunctionalConstraint(node);
        if (fc == null) {
            throw this.illegalNodeException(node);
        }

        if (node.getType().equals(BdaType.FLOAT32)) {
            return this.setFixedFloat(node, fc, Float.parseFloat(value));
        }

        if (node.getType().equals(BdaType.INT32)) {
            return this.setInt(node, fc, Integer.parseInt(value));
        }

        if (node.getType().equals(BdaType.TIMESTAMP)) {
            return this.setTime(node, fc, this.parseDate(value));
        }

        throw this.nodeTypeNotConfiguredException(node);
    }

    @Override
    public Fc getFunctionalConstraint(final Node node) {
        return FC_BY_NODE.get(node);
    }
}
