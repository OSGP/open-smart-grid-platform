// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.Firmware;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FirmwareConverter
    extends CustomConverter<org.opensmartgridplatform.domain.core.entities.FirmwareFile, Firmware> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareConverter.class);

  @Override
  public org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.Firmware convert(
      final org.opensmartgridplatform.domain.core.entities.FirmwareFile source,
      final Type<
              ? extends
                  org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.Firmware>
          destinationType,
      final MappingContext context) {

    final Firmware output = new Firmware();

    final Set<DeviceModel> deviceModels = source.getDeviceModels();
    for (final DeviceModel deviceModel : deviceModels) {
      final org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.DeviceModel
          wsDeviceModel =
              new org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.DeviceModel();
      wsDeviceModel.setModelCode(deviceModel.getModelCode());
      wsDeviceModel.setDescription(deviceModel.getDescription());
      wsDeviceModel.setManufacturer(deviceModel.getManufacturer().getCode());
      output.getDeviceModels().add(wsDeviceModel);
    }

    output.setDescription(source.getDescription());
    output.setFilename(source.getFilename());
    output.setId(source.getId().intValue());
    output.setIdentification(source.getIdentification());
    output.setPushToNewDevices(source.getPushToNewDevices());
    output.setActive(source.isActive());

    final FirmwareModuleData firmwareModuleData = new FirmwareModuleData();
    final Map<FirmwareModule, String> moduleVersions = source.getModuleVersions();
    for (final Entry<FirmwareModule, String> moduleVersion : moduleVersions.entrySet()) {
      final FirmwareModule firmwareModule = moduleVersion.getKey();
      switch (firmwareModule.getDescription()) {
        case org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData
            .MODULE_DESCRIPTION_COMM:
          firmwareModuleData.setModuleVersionComm(moduleVersion.getValue());
          break;
        case org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData
            .MODULE_DESCRIPTION_FUNC:
          firmwareModuleData.setModuleVersionFunc(moduleVersion.getValue());
          break;
        case org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData
            .MODULE_DESCRIPTION_FUNC_SMART_METERING:
          firmwareModuleData.setModuleVersionFunc(moduleVersion.getValue());
          break;
        case org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData
            .MODULE_DESCRIPTION_MA:
          firmwareModuleData.setModuleVersionMa(moduleVersion.getValue());
          break;
        case org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData
            .MODULE_DESCRIPTION_MBUS:
          firmwareModuleData.setModuleVersionMbus(moduleVersion.getValue());
          break;
        case org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData
            .MODULE_DESCRIPTION_MBUS_DRIVER_ACTIVE:
          firmwareModuleData.setModuleVersionMBusDriverActive(moduleVersion.getValue());
          break;
        case org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData
            .MODULE_DESCRIPTION_SEC:
          firmwareModuleData.setModuleVersionSec(moduleVersion.getValue());
          break;
        case org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData
            .MODULE_DESCRIPTION_SIMPLE_VERSION_INFO:
          firmwareModuleData.setModuleVersionSimple(moduleVersion.getValue());
          break;
        default:
      }
    }
    output.setFirmwareModuleData(firmwareModuleData);

    final GregorianCalendar gCalendar = new GregorianCalendar();
    gCalendar.setTime(Date.from(source.getCreationTime()));

    try {
      output.setCreationTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar));
    } catch (final DatatypeConfigurationException e) {
      // This won't happen, so no further action is needed.
      LOGGER.error("Bad date format in one of Firmware installation dates", e);
    }

    return output;
  }
}
