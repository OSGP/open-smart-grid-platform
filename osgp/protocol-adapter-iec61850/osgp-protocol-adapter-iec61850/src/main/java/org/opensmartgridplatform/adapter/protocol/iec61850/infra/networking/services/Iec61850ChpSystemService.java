// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
public class Iec61850ChpSystemService implements SystemService {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850ChpSystemService.class);
  private static final LogicalDevice DEVICE = LogicalDevice.CHP;

  @Autowired private Iec61850ChpCommandFactory iec61850ChpCommandFactory;

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
          this.iec61850ChpCommandFactory.getCommand(filter);
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

    throw new NotImplementedException("Set data is not yet implemented for CHP.");
  }
}
