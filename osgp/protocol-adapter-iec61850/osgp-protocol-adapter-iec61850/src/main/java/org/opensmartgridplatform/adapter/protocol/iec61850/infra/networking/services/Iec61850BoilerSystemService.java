/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuWriteCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.SystemService;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementFilterDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.ProfileDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.ProfileFilterDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetDataSystemIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetPointDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SystemFilterDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Iec61850BoilerSystemService implements SystemService {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850BoilerSystemService.class);
  private static final LogicalDevice DEVICE = LogicalDevice.BOILER;

  @Autowired private Iec61850BoilerCommandFactory iec61850BoilerCommandFactory;

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
          this.iec61850BoilerCommandFactory.getCommand(filter);
      if (command == null) {
        LOGGER.warn("Unsupported data attribute [{}], skip get data for it", filter.getNode());
      } else {
        measurements.add(command.execute(client, connection, DEVICE, logicalDeviceIndex));
      }
    }

    final List<ProfileDto> profiles = new ArrayList<>();

    for (final ProfileFilterDto filter : systemFilter.getProfileFilters()) {

      final RtuReadCommand<ProfileDto> command =
          Iec61850RtuReadProfileCommandFactory.getInstance().getCommand(filter);
      if (command == null) {
        LOGGER.warn("Unsupported data attribute [{}], skip get data for it", filter.getNode());
      } else {
        profiles.add(command.execute(client, connection, DEVICE, logicalDeviceIndex));
      }
    }

    return new GetDataSystemIdentifierDto(
        systemFilter.getId(), systemFilter.getSystemType(), measurements, profiles);
  }

  @Override
  public void setData(
      final SetDataSystemIdentifierDto systemIdentifier,
      final Iec61850Client client,
      final DeviceConnection connection)
      throws NodeException {
    final int logicalDeviceIndex = systemIdentifier.getId();

    LOGGER.info(
        "Set data called for logical device {}{}", DEVICE.getDescription(), logicalDeviceIndex);

    /*
     * Set profiles before setpoints, so that profile updates can be
     * detected by an increment of the SchdId after the profiles are already
     * set to the RTU
     */
    for (final ProfileDto p : systemIdentifier.getProfiles()) {
      final RtuWriteCommand<ProfileDto> command =
          Iec61850WriteProfileCommandFactory.getInstance().getCommand(p.getNode() + p.getId());
      if (command == null) {
        LOGGER.warn("Unsupported profile [{}], skip set data for it.", p.getNode() + p.getId());
      } else {
        command.executeWrite(client, connection, DEVICE, logicalDeviceIndex, p);
      }
    }

    for (final SetPointDto sp : systemIdentifier.getSetPoints()) {
      final RtuWriteCommand<SetPointDto> command =
          Iec61850SetPointCommandFactory.getInstance().getCommand(sp.getNode() + sp.getId());

      if (command == null) {
        LOGGER.warn("Unsupported set point [{}], skip set data for it.", sp.getNode() + sp.getId());
      } else {
        command.executeWrite(client, connection, DEVICE, logicalDeviceIndex, sp);
      }
    }
  }
}
