/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.simulator;

import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.DataAttribute;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.node.ObjectNode;

import cucumber.api.java.en.Given;

public class SimulatedAlarmObjectSteps {

    private static final int CLASS_ID = InterfaceClass.DATA.id();
    private static final ObisCode OBIS_CODE = new ObisCode(0, 0, 97, 98, 0, 255);
    private static final int ATTRIBUTE_ID_VALUE = DataAttribute.VALUE.attributeId();
    private static final String OBJECT_DESCRIPTION = "the alarm object";

    @Autowired
    private DeviceSimulatorSteps deviceSimulatorSteps;

    @Autowired
    private JsonObjectCreator jsonObjectCreator;

    @Given("^device \"([^\"]*)\" has some alarms registered$")
    public void deviceHasSomeAlarmsRegistered(final String deviceIdentification) {
        this.deviceSimulatorSteps.deviceSimulationOfEquipmentIdentifier(deviceIdentification);

        final ObjectNode attributeValue = this.jsonObjectCreator.createAttributeValue("double-long-unsigned",
                "33693956");
        this.deviceSimulatorSteps.setDlmsAttributeValue(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_VALUE, attributeValue,
                OBJECT_DESCRIPTION);
    }
}
