// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;
import org.springframework.stereotype.Component;

@Component
public class SetConfigurationObjectServiceDsmr4 extends SetConfigurationObjectService {

  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  public SetConfigurationObjectServiceDsmr4(
      final DlmsHelper dlmsHelper,
      final ObjectConfigServiceHelper objectConfigServiceHelper,
      final DlmsDeviceRepository dlmsDeviceRepository) {
    super(dlmsHelper, dlmsDeviceRepository);
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  @Override
  public boolean handles(final Protocol protocol) {
    return protocol != null && protocol.isDsmr42();
  }

  @Override
  DataObject buildSetParameterData(
      final ConfigurationObjectDto configurationToSet,
      final ConfigurationObjectDto configurationOnDevice)
      throws ProtocolAdapterException {
    final List<DataObject> dataObjects = new LinkedList<>();
    this.addGprsOperationMode(configurationToSet, configurationOnDevice, dataObjects);
    this.addFlags(configurationToSet, configurationOnDevice, dataObjects);
    return DataObject.newStructureData(dataObjects);
  }

  private void addGprsOperationMode(
      final ConfigurationObjectDto configurationToSet,
      final ConfigurationObjectDto configurationOnDevice,
      final List<DataObject> dataObjects) {
    if (configurationToSet.getGprsOperationMode() != null) {
      this.addGprsOperationMode(configurationToSet.getGprsOperationMode(), dataObjects);
    } else if (configurationOnDevice.getGprsOperationMode() != null) {
      this.addGprsOperationMode(configurationOnDevice.getGprsOperationMode(), dataObjects);
    }
  }

  private void addGprsOperationMode(
      final GprsOperationModeTypeDto type, final List<DataObject> dataObjects) {
    dataObjects.add(DataObject.newEnumerateData(type.getNumber()));
  }

  private void addFlags(
      final ConfigurationObjectDto configurationToSet,
      final ConfigurationObjectDto configurationOnDevice,
      final List<DataObject> dataObjects)
      throws ProtocolAdapterException {
    final BitString flags = this.getFlags(configurationToSet, configurationOnDevice);
    final DataObject bitString = DataObject.newBitStringData(flags);
    dataObjects.add(bitString);
  }

  @Override
  Optional<Integer> getBitPosition(final ConfigurationFlagTypeDto type) {
    return type.getBitPositionDsmr4();
  }

  @Override
  AttributeAddress getAttributeAddress(final Protocol protocol) throws ProtocolAdapterException {
    return this.objectConfigServiceHelper
        .findOptionalDefaultAttributeAddress(protocol, DlmsObjectType.CONFIGURATION_OBJECT)
        .orElseThrow();
  }
}
