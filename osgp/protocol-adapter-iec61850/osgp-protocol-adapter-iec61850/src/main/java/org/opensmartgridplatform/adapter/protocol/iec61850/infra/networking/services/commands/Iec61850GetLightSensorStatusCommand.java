//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.Fc;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Function;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.services.DeviceMessageLoggingService;
import org.opensmartgridplatform.core.db.api.iec61850.entities.LightMeasurementDevice;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850GetLightSensorStatusCommand {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850GetLightSensorStatusCommand.class);

  private final DeviceMessageLoggingService loggingService;

  public Iec61850GetLightSensorStatusCommand(final DeviceMessageLoggingService loggingService) {
    this.loggingService = loggingService;
  }

  public LightSensorStatusDto getStatusFromDevice(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final LightMeasurementDevice lmd)
      throws ProtocolAdapterException {

    final Iec61850GetLightSensorStatusFunction function =
        new Iec61850GetLightSensorStatusFunction(iec61850Client, deviceConnection, lmd);

    return iec61850Client.sendCommandWithRetry(
        function, "GetLightSensorStatus", deviceConnection.getDeviceIdentification());
  }

  private class Iec61850GetLightSensorStatusFunction implements Function<LightSensorStatusDto> {

    final Iec61850Client iec61850Client;
    final DeviceConnection deviceConnection;
    final LightMeasurementDevice lmd;

    public Iec61850GetLightSensorStatusFunction(
        final Iec61850Client iec61850Client,
        final DeviceConnection deviceConnection,
        final LightMeasurementDevice lmd) {
      this.iec61850Client = iec61850Client;
      this.deviceConnection = deviceConnection;
      this.lmd = lmd;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.
     * helper .Function
     * #apply(org.opensmartgridplatform.adapter.protocol.iec61850.domain.
     * valueobjects .DeviceMessageLog)
     */
    @Override
    public LightSensorStatusDto apply(final DeviceMessageLog deviceMessageLog)
        throws ProtocolAdapterException {
      // Use digital input number as index for SPPGIO node.
      final int index = this.lmd.getDigitalInput();

      // Read the data attribute of the logical node.
      final LogicalNode logicalNode = LogicalNode.getSpggioByIndex(index);
      final NodeContainer indNode =
          this.deviceConnection.getFcModelNode(
              LogicalDevice.LD0, logicalNode, DataAttribute.IND, Fc.ST);
      this.iec61850Client.readNodeDataValues(
          this.deviceConnection.getConnection().getClientAssociation(), indNode.getFcmodelNode());
      LOGGER.info(
          "device: {}, indNode: {}", this.deviceConnection.getDeviceIdentification(), indNode);

      // Read the value of the data object.
      final BdaBoolean stVal = indNode.getBoolean(SubDataAttribute.STATE);
      LOGGER.info("device: {}, stVal: {}", this.deviceConnection.getDeviceIdentification(), stVal);
      deviceMessageLog.addVariable(
          logicalNode,
          DataAttribute.IND,
          Fc.ST,
          SubDataAttribute.STATE,
          Boolean.toString(stVal.getValue()));

      Iec61850GetLightSensorStatusCommand.this.loggingService.logMessage(
          deviceMessageLog,
          this.deviceConnection.getDeviceIdentification(),
          this.deviceConnection.getOrganisationIdentification(),
          false);

      return new LightSensorStatusDto(this.getStatus(stVal.getValue()));
    }

    private LightSensorStatusTypeDto getStatus(final boolean on) {
      return on ? LightSensorStatusTypeDto.DARK : LightSensorStatusTypeDto.LIGHT;
    }
  }
}
