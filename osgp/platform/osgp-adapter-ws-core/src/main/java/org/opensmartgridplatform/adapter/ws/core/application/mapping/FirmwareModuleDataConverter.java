// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData;

public class FirmwareModuleDataConverter
    extends CustomConverter<
        org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData,
        FirmwareModuleData> {

  @Override
  public FirmwareModuleData convert(
      final org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData
          source,
      final Type<? extends FirmwareModuleData> destinationType,
      final MappingContext mappingContext) {

    // The Func(tional) ModuleVersionType is reused for the Active ModuleVersionType in use with
    // SmartMetering. SmartMetering is not using the Func type so they will not coexist.
    String moduleVersionFunc = source.getModuleVersionFunc();
    if (source.getModuleVersionActive() != null) {
      moduleVersionFunc = source.getModuleVersionActive();
    }

    return new FirmwareModuleData(
        source.getModuleVersionComm(),
        moduleVersionFunc,
        source.getModuleVersionMa(),
        source.getModuleVersionMbus(),
        source.getModuleVersionSec(),
        source.getModuleVersionMBusDriverActive(),
        source.getModuleVersionSimple());
  }
}
