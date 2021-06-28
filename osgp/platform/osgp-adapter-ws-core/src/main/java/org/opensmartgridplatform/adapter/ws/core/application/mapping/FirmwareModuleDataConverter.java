package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData;

public class FirmwareModuleDataConverter
    extends CustomConverter<
        org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData,
        FirmwareModuleData> {

  private boolean isForSmartMeter = true;

  @Override
  public FirmwareModuleData convert(
      final org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData
          source,
      final Type<? extends FirmwareModuleData> destinationType,
      final MappingContext mappingContext) {

    String moduleVersionFunc = source.getModuleVersionFunc();
    if (this.isForSmartMeter) {
      moduleVersionFunc = source.getModuleVersionActive();
    }

    final FirmwareModuleData output =
        new FirmwareModuleData(
            source.getModuleVersionComm(),
            moduleVersionFunc,
            source.getModuleVersionMa(),
            source.getModuleVersionMbus(),
            source.getModuleVersionSec(),
            source.getModuleVersionMBusDriverActive(),
            source.getModuleVersionSimple());

    return output;
  }

  public void setForSmartMeter(final boolean isForSmartMeter) {
    this.isForSmartMeter = isForSmartMeter;
  }
}
