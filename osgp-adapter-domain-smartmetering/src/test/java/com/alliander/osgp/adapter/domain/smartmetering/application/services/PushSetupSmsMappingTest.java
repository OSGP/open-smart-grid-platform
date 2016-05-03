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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SendDestinationAndMethod;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.WindowElement;
import com.alliander.osgp.dto.valueobjects.smartmetering.ClockStatusDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTimeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCodeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemTimeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupSmsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.WindowElementDto;

public class PushSetupSmsMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();

    // To test if a PushSetupAlarm can be mapped if instance variables are
    // null.
    @Test
    public void testPushSetupSmsMappingNull() {

        // build test data
        final PushSetupSms pushSetupSms = new PushSetupSmsBuilder().withNullValues().build();

        // actual mapping
        final PushSetupSmsDto pushSetupSmsDto = this.configurationMapper.map(pushSetupSms, PushSetupSmsDto.class);

        // check values
        assertNotNull(pushSetupSmsDto);
        assertNull(pushSetupSmsDto.getLogicalName());
        assertNull(pushSetupSmsDto.getPushObjectList());
        assertNull(pushSetupSmsDto.getSendDestinationAndMethod());
        assertNull(pushSetupSmsDto.getCommunicationWindow());
        assertNull(pushSetupSmsDto.getRandomisationStartInterval());
        assertNull(pushSetupSmsDto.getNumberOfRetries());
        assertNull(pushSetupSmsDto.getRepetitionDelay());
    }

    // To test if a PushSetupAlarm can be mapped if instance variables are
    // initialized and lists are empty.
    @Test
    public void testPushSetupSmsMappingWithEmptyLists() {

        // build test data
        final ArrayList<CosemObjectDefinition> pushObjectList = new ArrayList<CosemObjectDefinition>();
        final ArrayList<WindowElement> communicationWindow = new ArrayList<WindowElement>();

        final PushSetupSms pushSetupSms = new PushSetupSmsBuilder().withEmptyLists(pushObjectList, communicationWindow)
                .build();

        // actual mapping
        final PushSetupSmsDto pushSetupSmsDto = this.configurationMapper.map(pushSetupSms, PushSetupSmsDto.class);

        // check values
        this.checkCosemObisCodeMapping(pushSetupSms.getLogicalName(), pushSetupSmsDto.getLogicalName());
        this.checkSendDestinationAndMethodMapping(pushSetupSms, pushSetupSmsDto);
        this.checkIntegerMapping(pushSetupSms, pushSetupSmsDto);

        assertNotNull(pushSetupSmsDto.getPushObjectList());
        assertNotNull(pushSetupSmsDto.getCommunicationWindow());

    }

    // To test Mapping if lists contain values
    @Test
    public void testPushSetupSmsMappingWithLists() {

        // build test data
        final CosemObisCode logicalName = new CosemObisCode(1, 2, 3, 4, 5, 6);
        final CosemObjectDefinition cosemObjectDefinition = new CosemObjectDefinition(1, logicalName, 2);
        final CosemDateTime startTime = new CosemDateTime(new CosemDate(2016, 3, 17), new CosemTime(11, 52, 45), 0,
                new ClockStatus(ClockStatus.STATUS_NOT_SPECIFIED));
        final CosemDateTime endTime = new CosemDateTime(new CosemDate(2016, 3, 17), new CosemTime(11, 52, 45), 0,
                new ClockStatus(ClockStatus.STATUS_NOT_SPECIFIED));
        final WindowElement windowElement = new WindowElement(startTime, endTime);

        final PushSetupSms pushSetupSms = new PushSetupSmsBuilder().withFilledLists(cosemObjectDefinition,
                windowElement).build();

        // actual mapping
        final PushSetupSmsDto pushSetupSmsDto = this.configurationMapper.map(pushSetupSms, PushSetupSmsDto.class);

        // check values
        this.checkCosemObisCodeMapping(pushSetupSms.getLogicalName(), pushSetupSmsDto.getLogicalName());
        this.checkSendDestinationAndMethodMapping(pushSetupSms, pushSetupSmsDto);
        this.checkIntegerMapping(pushSetupSms, pushSetupSmsDto);
        this.checkNonEmptyListMapping(pushSetupSms, pushSetupSmsDto);

    }

    // method to test Integer object mapping
    private void checkIntegerMapping(final PushSetupSms pushSetupSms, final PushSetupSmsDto pushSetupSmsDto) {

        // make sure none is null
        assertNotNull(pushSetupSmsDto.getRandomisationStartInterval());
        assertNotNull(pushSetupSmsDto.getNumberOfRetries());
        assertNotNull(pushSetupSmsDto.getRepetitionDelay());

        // make sure all values are equal
        assertEquals(pushSetupSms.getRandomisationStartInterval(), pushSetupSmsDto.getRandomisationStartInterval());
        assertEquals(pushSetupSms.getNumberOfRetries(), pushSetupSmsDto.getNumberOfRetries());
        assertEquals(pushSetupSms.getRepetitionDelay(), pushSetupSmsDto.getRepetitionDelay());
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
    private void checkSendDestinationAndMethodMapping(final PushSetupSms pushSetupSms,
            final PushSetupSmsDto pushSetupSmsDto) {
        final SendDestinationAndMethod sendDestinationAndMethod = pushSetupSms.getSendDestinationAndMethod();
        final SendDestinationAndMethodDto sendDestinationAndMethodDto = pushSetupSmsDto.getSendDestinationAndMethod();

        // make sure neither is null
        assertNotNull(sendDestinationAndMethod);
        assertNotNull(sendDestinationAndMethodDto);

        // make sure all instance variables are equal
        assertEquals(sendDestinationAndMethod.getTransportService().name(), sendDestinationAndMethodDto
                .getTransportService().name());
        assertEquals(sendDestinationAndMethod.getMessage().name(), sendDestinationAndMethodDto.getMessage().name());
        assertEquals(sendDestinationAndMethod.getDestination(), sendDestinationAndMethodDto.getDestination());
    }

    // method to test non-empty list mapping
    private void checkNonEmptyListMapping(final PushSetupSms pushSetupSms, final PushSetupSmsDto pushSetupSmsDto) {

        // test pushObjectList mapping
        assertNotNull(pushSetupSms.getPushObjectList());
        assertNotNull(pushSetupSmsDto.getPushObjectList());
        assertEquals(pushSetupSms.getPushObjectList().size(), pushSetupSmsDto.getPushObjectList().size());

        final CosemObjectDefinition cosemObjectDefinition = pushSetupSms.getPushObjectList().get(0);
        final CosemObjectDefinitionDto cosemObjectDefinitionDto = pushSetupSmsDto.getPushObjectList().get(0);
        assertEquals(cosemObjectDefinition.getAttributeIndex(), cosemObjectDefinitionDto.getAttributeIndex());
        assertEquals(cosemObjectDefinition.getClassId(), cosemObjectDefinitionDto.getClassId());
        assertEquals(cosemObjectDefinition.getDataIndex(), cosemObjectDefinitionDto.getDataIndex());
        this.checkCosemObisCodeMapping(cosemObjectDefinition.getLogicalName(),
                cosemObjectDefinitionDto.getLogicalName());

        // test communicationWindow mapping
        assertNotNull(pushSetupSms.getCommunicationWindow());
        assertNotNull(pushSetupSmsDto.getCommunicationWindow());
        assertEquals(pushSetupSms.getCommunicationWindow().size(), pushSetupSmsDto.getCommunicationWindow().size());

        final WindowElement windowElement = pushSetupSms.getCommunicationWindow().get(0);
        final WindowElementDto windowElementDto = pushSetupSmsDto.getCommunicationWindow().get(0);

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
