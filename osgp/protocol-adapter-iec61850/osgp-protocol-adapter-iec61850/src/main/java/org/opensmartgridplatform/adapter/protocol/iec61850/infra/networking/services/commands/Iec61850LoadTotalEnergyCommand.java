/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.Fc;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.QualityConverter;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used by both combined load and separated load devices
 *
 * <p>Combined: single load device contains multiple mmxu/mmtr nodes (deprecated)
 *
 * <p>Separated: multiple load devices with single mmxu/mmtr nodes.
 */
public class Iec61850LoadTotalEnergyCommand implements RtuReadCommand<MeasurementDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850LoadTotalEnergyCommand.class);
  private static final String NODE = "MMTR";

  private final LogicalNode logicalNode;
  private final int index;

  public Iec61850LoadTotalEnergyCommand() {
    // Constructor for separated load devices
    this.logicalNode = LogicalNode.fromString(NODE + 1);
    this.index = 1;
  }

  public Iec61850LoadTotalEnergyCommand(final int index) {
    // Constructor for combined load devices
    this.logicalNode = LogicalNode.fromString(NODE + index);
    this.index = index;
  }

  @Override
  public MeasurementDto execute(
      final Iec61850Client client,
      final DeviceConnection connection,
      final LogicalDevice logicalDevice,
      final int logicalDeviceIndex)
      throws NodeException {
    final NodeContainer containingNode =
        connection.getFcModelNode(
            logicalDevice, logicalDeviceIndex, this.logicalNode, DataAttribute.TOTAL_ENERGY, Fc.ST);
    client.readNodeDataValues(
        connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
    return this.translate(containingNode);
  }

  @Override
  public MeasurementDto translate(final NodeContainer containingNode) {
    // Load total energy is implemented different on both RTUs
    // (one uses Int64, the other Int32)
    // As a workaround first try to read the value as Integer
    // If that fails read the value as Long
    long value;
    try {
      value = containingNode.getInteger(SubDataAttribute.ACTUAL_VALUE).getValue();
    } catch (final ClassCastException e) {
      LOGGER.info(
          "Reading integer value resulted in class cast exception, trying to read long value", e);
      value = containingNode.getLong(SubDataAttribute.ACTUAL_VALUE).getValue();
    }

    return new MeasurementDto(
        this.index,
        DataAttribute.TOTAL_ENERGY.getDescription(),
        QualityConverter.toShort(containingNode.getQuality(SubDataAttribute.QUALITY).getValue()),
        new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
        value);
  }
}
