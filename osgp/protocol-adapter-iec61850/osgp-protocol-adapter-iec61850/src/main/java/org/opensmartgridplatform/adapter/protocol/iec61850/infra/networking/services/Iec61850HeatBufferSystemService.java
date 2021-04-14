/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeWriteException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.SystemService;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementFilterDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetDataSystemIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SystemFilterDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Iec61850HeatBufferSystemService implements SystemService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850HeatBufferSystemService.class);
  private static final LogicalDevice DEVICE = LogicalDevice.HEAT_BUFFER;

  @Autowired private Iec61850HeatBufferCommandFactory iec61850HeatBufferCommandFactory;

  @Override
  public GetDataSystemIdentifierDto getData(
      final SystemFilterDto systemFilter,
      final Iec61850Client client,
      final DeviceConnection connection)
      throws NodeException {

    final int logicalDeviceIndex = systemFilter.getId();

    LOGGER.info(
        "Get data called for logical device {}{}", DEVICE.getDescription(), logicalDeviceIndex);

    final List<MeasurementDto> measurements = new ArrayList<>();

    for (final MeasurementFilterDto filter : systemFilter.getMeasurementFilters()) {

      final RtuReadCommand<MeasurementDto> command =
          this.iec61850HeatBufferCommandFactory.getCommand(filter);
      if (command == null) {
        LOGGER.warn("Unsupported data attribute [{}], skip get data for it", filter.getNode());
      } else {
        measurements.add(command.execute(client, connection, DEVICE, logicalDeviceIndex));
      }
    }

    return new GetDataSystemIdentifierDto(
        systemFilter.getId(), systemFilter.getSystemType(), measurements);
  }

  @Override
  public void setData(
      final SetDataSystemIdentifierDto systemIdentifier,
      final Iec61850Client client,
      final DeviceConnection connection)
      throws NodeWriteException {

    throw new NotImplementedException("Set data is not yet implemented for" + DEVICE);
  }
}
