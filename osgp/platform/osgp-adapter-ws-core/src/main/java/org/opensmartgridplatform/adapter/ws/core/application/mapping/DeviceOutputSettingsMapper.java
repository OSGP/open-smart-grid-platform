//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdatedDevice;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;

/**
 * Maps the device output settings, because those can not be mapped automatically by Orika. Orika
 * initially tries to clear the outputSettings field, which is a List, of the Ssld. This fails,
 * because Ssld.outputSettings is an unmodifiable list.
 */
public class DeviceOutputSettingsMapper extends CustomMapper<UpdatedDevice, Ssld> {

  @Override
  public void mapAtoB(
      final UpdatedDevice source, final Ssld destination, final MappingContext context) {

    destination.updateOutputSettings(this.mapOutputSettings(source.getOutputSettings()));
  }

  private List<DeviceOutputSetting> mapOutputSettings(
      final List<
              org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting>
          outputSettings) {

    if (outputSettings == null) {
      return new ArrayList<>();
    } else {
      return outputSettings.stream().map(this::mapOutputSetting).collect(Collectors.toList());
    }
  }

  private DeviceOutputSetting mapOutputSetting(
      final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting
          dos) {

    final RelayType relayType = RelayType.valueOf(dos.getRelayType().toString());

    return new DeviceOutputSetting(
        dos.getInternalId(), dos.getExternalId(), relayType, dos.getAlias());
  }
}
