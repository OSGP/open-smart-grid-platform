/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openmuc.openiec61850.Fc;

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuWriteCommand;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.NodeReadException;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.NodeWriteException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import com.alliander.osgp.dto.valueobjects.microgrids.ProfileDto;
import com.alliander.osgp.dto.valueobjects.microgrids.ProfileEntryDto;

public class Iec61850ScheduleAbsTimeCommand implements RtuReadCommand<ProfileDto>, RtuWriteCommand<ProfileDto> {

    private static final String NODE_NAME = "DSCH";
    private static final DataAttribute DATA_ATTRIBUTE = DataAttribute.SCHEDULE_ABS_TIME;
    private static final Fc FC = Fc.SP;

    // TODO - Refactor to determine the size by reading the number of points
    // (numPts) from the device
    private static final int ARRAY_SIZE = 50;

    private static final float DEFAULT_VALUE = 0f;
    private static final Date DEFAULT_TIME = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeZone.UTC).toDate();

    private LogicalNode logicalNode;
    private int index;

    public Iec61850ScheduleAbsTimeCommand(final int index) {
        this.index = index;
        this.logicalNode = LogicalNode.fromString(NODE_NAME + index);

    }

    @Override
    public ProfileDto execute(final Iec61850Client client, final DeviceConnection connection,
            final LogicalDevice logicalDevice) throws NodeReadException {
        final NodeContainer containingNode = connection.getFcModelNode(logicalDevice, this.logicalNode, DATA_ATTRIBUTE,
                FC);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return this.translate(containingNode);
    }

    @Override
    public ProfileDto translate(final NodeContainer containingNode) {

        final List<ProfileEntryDto> profileEntries = this.convert(containingNode);

        return new ProfileDto(this.index, DATA_ATTRIBUTE.getDescription(), profileEntries);
    }

    @Override
    public void executeWrite(final Iec61850Client client, final DeviceConnection connection,
            final LogicalDevice logicalDevice, final ProfileDto profile) throws NodeWriteException {

        this.checkProfile(profile);

        final NodeContainer containingNode = connection.getFcModelNode(logicalDevice, this.logicalNode, DATA_ATTRIBUTE,
                FC);

        final ProfilePair profilePair = this.convert(profile.getProfileEntries());

        containingNode.writeFloatArray(SubDataAttribute.VALUES, profilePair.values);
        containingNode.writeDateArray(SubDataAttribute.TIMES, profilePair.times);
    }

    private void checkProfile(final ProfileDto profile) throws NodeWriteException {
        if (profile == null) {
            throw new NodeWriteException("Invalid profile. Profile is null.");
        }
        if (profile.getProfileEntries() == null) {
            throw new NodeWriteException("Invalid profile. Profile entries list is null.");
        }
        final int size = profile.getProfileEntries().size();
        if (size > ARRAY_SIZE) {
            throw new NodeWriteException(String.format(
                    "Invalid profile. Profile entries list size {} is larger then allowed {}.", size, ARRAY_SIZE));
        }
    }

    private List<ProfileEntryDto> convert(final NodeContainer profileNode) {
        // final BdaInt32 numberOfPoints =
        // profileNode.getInteger(SubDataAttribute.NUMBER_OF_POINTS);
        final Float[] values = profileNode.getFloatArray(SubDataAttribute.VALUES);
        final Date[] times = profileNode.getDateArray(SubDataAttribute.TIMES);

        final List<ProfileEntryDto> profileEntries = new ArrayList<>();

        for (int i = 0; i < ARRAY_SIZE; i++) {
            final int index = i + 1;
            final double value = values[i];
            final DateTime time = (DateTime) (times[i] == null ? DEFAULT_TIME : new DateTime(times[i]));

            if (value != DEFAULT_VALUE && !time.equals(DEFAULT_TIME)) {
                profileEntries.add(new ProfileEntryDto(index, time, value));
            }
        }

        return profileEntries;
    }

    private ProfilePair convert(final List<ProfileEntryDto> profileEntries) {

        final Float[] values = new Float[ARRAY_SIZE];
        final Date[] times = new Date[ARRAY_SIZE];

        for (final ProfileEntryDto pe : profileEntries) {
            final int index = pe.getId() - 1;
            values[index] = (float) pe.getValue();
            times[index] = pe.getTime().toDate();
        }

        // Fill rest of array with default values
        final int start = profileEntries.size();
        final int end = ARRAY_SIZE;

        for (int i = start; i < end; i++) {
            values[i] = DEFAULT_VALUE;
            times[i] = DEFAULT_TIME;
        }

        return new ProfilePair(values, times);
    }

    private class ProfilePair {

        protected final Float[] values;
        protected final Date[] times;

        protected ProfilePair(final Float[] values, final Date[] times) {
            this.values = values;
            this.times = times;
        }
    }
}
