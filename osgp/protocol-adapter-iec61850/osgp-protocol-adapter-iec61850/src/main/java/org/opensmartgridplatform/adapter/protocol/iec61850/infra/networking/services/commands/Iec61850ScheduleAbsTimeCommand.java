// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.Fc;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuWriteCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.ProfilePair;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeWriteException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import org.opensmartgridplatform.dto.valueobjects.microgrids.ProfileDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.ProfileEntryDto;

public class Iec61850ScheduleAbsTimeCommand
    implements RtuReadCommand<ProfileDto>, RtuWriteCommand<ProfileDto> {

  private static final String NODE_NAME = "DSCH";
  private static final DataAttribute DATA_ATTRIBUTE = DataAttribute.SCHEDULE_ABS_TIME;
  private static final Fc FC = Fc.SP;

  private static final int ARRAY_SIZE = 50;

  private static final float DEFAULT_VALUE = 0F;
  private static final Date DEFAULT_TIME =
      new DateTime(1970, 1, 1, 0, 0, 0, DateTimeZone.UTC).toDate();

  private final LogicalNode logicalNode;
  private final int index;

  public Iec61850ScheduleAbsTimeCommand(final int index) {
    this.index = index;
    this.logicalNode = LogicalNode.fromString(NODE_NAME + index);
  }

  @Override
  public ProfileDto execute(
      final Iec61850Client client,
      final DeviceConnection connection,
      final LogicalDevice logicalDevice,
      final int logicalDeviceIndex)
      throws NodeException {
    final NodeContainer containingNode =
        connection.getFcModelNode(
            logicalDevice, logicalDeviceIndex, this.logicalNode, DATA_ATTRIBUTE, FC);
    client.readNodeDataValues(
        connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
    return this.translate(containingNode);
  }

  @Override
  public ProfileDto translate(final NodeContainer containingNode) {

    final List<ProfileEntryDto> profileEntries = this.convert(containingNode);

    return new ProfileDto(this.index, DATA_ATTRIBUTE.getDescription(), profileEntries);
  }

  @Override
  public void executeWrite(
      final Iec61850Client client,
      final DeviceConnection connection,
      final LogicalDevice logicalDevice,
      final int logicalDeviceIndex,
      final ProfileDto profile)
      throws NodeException {

    this.checkProfile(profile);

    final NodeContainer containingNode =
        connection.getFcModelNode(
            logicalDevice, logicalDeviceIndex, this.logicalNode, DATA_ATTRIBUTE, FC);

    final ProfilePair profilePair = this.convert(profile.getProfileEntries());

    containingNode.writeFloatArray(SubDataAttribute.VALUES, profilePair.getValues());
    containingNode.writeDateArray(SubDataAttribute.TIMES, profilePair.getTimes());
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
      throw new NodeWriteException(
          String.format(
              "Invalid profile. Profile entries list size %d is larger then allowed %d.",
              size, ARRAY_SIZE));
    }
  }

  private ProfilePair convert(final List<ProfileEntryDto> profileEntries) {

    final Float[] values = new Float[ARRAY_SIZE];
    final Date[] times = new Date[ARRAY_SIZE];

    for (final ProfileEntryDto pe : profileEntries) {
      final int i = pe.getId() - 1;
      values[i] = (float) pe.getValue();
      times[i] = Date.from(pe.getTime().toInstant());
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

  private List<ProfileEntryDto> convert(final NodeContainer profileNode) {
    final Float[] values = profileNode.getFloatArray(SubDataAttribute.VALUES);
    final Date[] times = profileNode.getDateArray(SubDataAttribute.TIMES);

    final List<ProfileEntryDto> profileEntries = new ArrayList<>();

    for (int i = 0; i < ARRAY_SIZE; i++) {
      final double value = values[i];
      final Date time;
      if (times[i] == null) {
        time = DEFAULT_TIME;
      } else {
        time = times[i];
      }

      if (!time.equals(DEFAULT_TIME)) {
        profileEntries.add(
            new ProfileEntryDto(
                i + 1, ZonedDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault()), value));
      }
    }

    return profileEntries;
  }
}
