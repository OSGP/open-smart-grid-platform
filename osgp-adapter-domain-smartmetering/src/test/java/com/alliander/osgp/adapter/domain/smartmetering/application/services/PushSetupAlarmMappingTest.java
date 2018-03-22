/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Test;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ClockStatus;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDate;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDateTime;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemObisCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemObjectDefinition;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemTime;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SendDestinationAndMethod;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.WindowElement;
import com.alliander.osgp.dto.valueobjects.smartmetering.ClockStatusDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTimeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCodeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemTimeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.WindowElementDto;

public class PushSetupAlarmMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();

    // To test if a PushSetupAlarm can be mapped if instance variables are null.
    @Test
    public void testPushSetupAlarmMappingNull() {

        // build test data
        final PushSetupAlarm pushSetupAlarm = new PushSetupAlarmBuilder().withNullValues().build();

        // actual mapping
        final PushSetupAlarmDto pushSetupAlarmDto = this.configurationMapper.map(pushSetupAlarm,
                PushSetupAlarmDto.class);

        // check values
        assertNotNull(pushSetupAlarmDto);
        assertNull(pushSetupAlarmDto.getLogicalName());
        assertNull(pushSetupAlarmDto.getPushObjectList());
        assertNull(pushSetupAlarmDto.getSendDestinationAndMethod());
        assertNull(pushSetupAlarmDto.getCommunicationWindow());
        assertNull(pushSetupAlarmDto.getRandomisationStartInterval());
        assertNull(pushSetupAlarmDto.getNumberOfRetries());
        assertNull(pushSetupAlarmDto.getRepetitionDelay());
    }

    // To test if a PushSetupAlarm can be mapped if instance variables are
    // initialized and lists are empty.
    @Test
    public void testPushSetupAlarmMappingWithEmptyLists() {

        // build test data
        final ArrayList<CosemObjectDefinition> pushObjectList = new ArrayList<>();
        final ArrayList<WindowElement> communicationWindow = new ArrayList<>();

        final PushSetupAlarm pushSetupAlarm = new PushSetupAlarmBuilder()
                .withEmptyLists(pushObjectList, communicationWindow).build();

        // actual mapping
        final PushSetupAlarmDto pushSetupAlarmDto = this.configurationMapper.map(pushSetupAlarm,
                PushSetupAlarmDto.class);

        // check values
        this.checkCosemObisCodeMapping(pushSetupAlarm.getLogicalName(), pushSetupAlarmDto.getLogicalName());
        this.checkSendDestinationAndMethodMapping(pushSetupAlarm, pushSetupAlarmDto);
        this.checkIntegerMapping(pushSetupAlarm, pushSetupAlarmDto);

        assertNotNull(pushSetupAlarmDto.getPushObjectList());
        assertNotNull(pushSetupAlarmDto.getCommunicationWindow());

    }

    // To test Mapping if lists contain values
    @Test
    public void testPushSetupAlarmMappingWithLists() {

        // build test data
        final CosemObisCode logicalName = new CosemObisCode(new int[] { 1, 2, 3, 4, 5, 6 });
        final CosemObjectDefinition cosemObjectDefinition = new CosemObjectDefinition(1, logicalName, 2);
        final CosemDateTime startTime = new CosemDateTime(new CosemDate(2016, 3, 17), new CosemTime(11, 52, 45), 0,
                new ClockStatus(ClockStatus.STATUS_NOT_SPECIFIED));
        final CosemDateTime endTime = new CosemDateTime(new CosemDate(2016, 3, 17), new CosemTime(11, 52, 45), 0,
                new ClockStatus(ClockStatus.STATUS_NOT_SPECIFIED));
        final WindowElement windowElement = new WindowElement(startTime, endTime);

        final PushSetupAlarm pushSetupAlarm = new PushSetupAlarmBuilder()
                .withFilledLists(cosemObjectDefinition, windowElement).build();

        // actual mapping
        final PushSetupAlarmDto pushSetupAlarmDto = this.configurationMapper.map(pushSetupAlarm,
                PushSetupAlarmDto.class);

        // check values
        this.checkCosemObisCodeMapping(pushSetupAlarm.getLogicalName(), pushSetupAlarmDto.getLogicalName());
        this.checkSendDestinationAndMethodMapping(pushSetupAlarm, pushSetupAlarmDto);
        this.checkIntegerMapping(pushSetupAlarm, pushSetupAlarmDto);
        this.checkNonEmptyListMapping(pushSetupAlarm, pushSetupAlarmDto);
    }

    // method to test Integer object mapping
    private void checkIntegerMapping(final PushSetupAlarm pushSetupAlarm, final PushSetupAlarmDto pushSetupAlarmDto) {

        // make sure none is null
        assertNotNull(pushSetupAlarmDto.getRandomisationStartInterval());
        assertNotNull(pushSetupAlarmDto.getNumberOfRetries());
        assertNotNull(pushSetupAlarmDto.getRepetitionDelay());

        // make sure all values are equal
        assertEquals(pushSetupAlarm.getRandomisationStartInterval(), pushSetupAlarmDto.getRandomisationStartInterval());
        assertEquals(pushSetupAlarm.getNumberOfRetries(), pushSetupAlarmDto.getNumberOfRetries());
        assertEquals(pushSetupAlarm.getRepetitionDelay(), pushSetupAlarmDto.getRepetitionDelay());
    }

    // method to test CosemObisCode object mapping
    private void checkCosemObisCodeMapping(final CosemObisCode cosemObisCode, final CosemObisCodeDto cosemObisCodeDto) {

        // make sure neither is null
        assertNotNull(cosemObisCode);
        assertNotNull(cosemObisCodeDto);

        // make sure all instance variables are equal
        assertEquals(cosemObisCode.getA(), cosemObisCodeDto.getA());
        assertEquals(cosemObisCode.getB(), cosemObisCodeDto.getB());
        assertEquals(cosemObisCode.getC(), cosemObisCodeDto.getC());
        assertEquals(cosemObisCode.getD(), cosemObisCodeDto.getD());
        assertEquals(cosemObisCode.getE(), cosemObisCodeDto.getE());
        assertEquals(cosemObisCode.getF(), cosemObisCodeDto.getF());

    }

    // method to test SendDestinationAndMethod mapping
    private void checkSendDestinationAndMethodMapping(final PushSetupAlarm pushSetupAlarm,
            final PushSetupAlarmDto pushSetupAlarmDto) {
        final SendDestinationAndMethod sendDestinationAndMethod = pushSetupAlarm.getSendDestinationAndMethod();
        final SendDestinationAndMethodDto sendDestinationAndMethodDto = pushSetupAlarmDto.getSendDestinationAndMethod();

        // make sure neither is null
        assertNotNull(sendDestinationAndMethod);
        assertNotNull(sendDestinationAndMethodDto);

        // make sure all instance variables are equal
        assertEquals(sendDestinationAndMethod.getTransportService().name(),
                sendDestinationAndMethodDto.getTransportService().name());
        assertEquals(sendDestinationAndMethod.getMessage().name(), sendDestinationAndMethodDto.getMessage().name());
        assertEquals(sendDestinationAndMethod.getDestination(), sendDestinationAndMethodDto.getDestination());
    }

    // method to test non-empty list mapping
    private void checkNonEmptyListMapping(final PushSetupAlarm pushSetupAlarm,
            final PushSetupAlarmDto pushSetupAlarmDto) {

        // test pushObjectList mapping
        assertNotNull(pushSetupAlarm.getPushObjectList());
        assertNotNull(pushSetupAlarmDto.getPushObjectList());
        assertEquals(pushSetupAlarm.getPushObjectList().size(), pushSetupAlarmDto.getPushObjectList().size());

        final CosemObjectDefinition cosemObjectDefinition = pushSetupAlarm.getPushObjectList().get(0);
        final CosemObjectDefinitionDto cosemObjectDefinitionDto = pushSetupAlarmDto.getPushObjectList().get(0);
        assertEquals(cosemObjectDefinition.getAttributeIndex(), cosemObjectDefinitionDto.getAttributeIndex());
        assertEquals(cosemObjectDefinition.getClassId(), cosemObjectDefinitionDto.getClassId());
        assertEquals(cosemObjectDefinition.getDataIndex(), cosemObjectDefinitionDto.getDataIndex());
        this.checkCosemObisCodeMapping(cosemObjectDefinition.getLogicalName(),
                cosemObjectDefinitionDto.getLogicalName());

        // test communicationWindow mapping
        assertNotNull(pushSetupAlarm.getCommunicationWindow());
        assertNotNull(pushSetupAlarmDto.getCommunicationWindow());
        assertEquals(pushSetupAlarm.getCommunicationWindow().size(), pushSetupAlarmDto.getCommunicationWindow().size());

        final WindowElement windowElement = pushSetupAlarm.getCommunicationWindow().get(0);
        final WindowElementDto windowElementDto = pushSetupAlarmDto.getCommunicationWindow().get(0);

        this.checkCosemDateTimeMapping(windowElement.getStartTime(), windowElementDto.getStartTime());
        this.checkCosemDateTimeMapping(windowElement.getEndTime(), windowElementDto.getEndTime());

    }

    // method to test mapping of CosemDateTime objects
    private void checkCosemDateTimeMapping(final CosemDateTime cosemDateTime, final CosemDateTimeDto cosemDateTimeDto) {

        // make sure neither is null
        assertNotNull(cosemDateTime);
        assertNotNull(cosemDateTimeDto);

        // check variables
        assertEquals(cosemDateTime.getDeviation(), cosemDateTimeDto.getDeviation());

        final ClockStatus clockStatus = cosemDateTime.getClockStatus();
        final ClockStatusDto clockStatusDto = cosemDateTimeDto.getClockStatus();
        assertEquals(clockStatus.getStatus(), clockStatusDto.getStatus());
        assertEquals(clockStatus.isSpecified(), clockStatusDto.isSpecified());

        final CosemDate cosemDate = cosemDateTime.getDate();
        final CosemDateDto cosemDateDto = cosemDateTimeDto.getDate();
        assertEquals(cosemDate.getYear(), cosemDateDto.getYear());
        assertEquals(cosemDate.getMonth(), cosemDateDto.getMonth());
        assertEquals(cosemDate.getDayOfMonth(), cosemDateDto.getDayOfMonth());
        assertEquals(cosemDate.getDayOfWeek(), cosemDateDto.getDayOfWeek());

        final CosemTime cosemTime = cosemDateTime.getTime();
        final CosemTimeDto cosemTimeDto = cosemDateTimeDto.getTime();
        assertEquals(cosemTime.getHour(), cosemTimeDto.getHour());
        assertEquals(cosemTime.getMinute(), cosemTimeDto.getMinute());
        assertEquals(cosemTime.getSecond(), cosemTimeDto.getSecond());
        assertEquals(cosemTime.getHundredths(), cosemTimeDto.getHundredths());
    }

}
