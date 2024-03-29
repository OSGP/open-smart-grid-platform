// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import java.util.Optional;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.springframework.stereotype.Component;

@Component
public class SetConfigurationObjectServiceDsmr43 extends SetConfigurationObjectServiceDsmr4 {

  public SetConfigurationObjectServiceDsmr43(
      final DlmsHelper dlmsHelper,
      final ObjectConfigServiceHelper objectConfigServiceHelper,
      final DlmsDeviceRepository dlmsDeviceRepository) {
    super(dlmsHelper, objectConfigServiceHelper, dlmsDeviceRepository);
  }

  @Override
  public boolean handles(final Protocol protocol) {
    return protocol != null && protocol.isDsmr43();
  }

  @Override
  Optional<Integer> getBitPosition(final ConfigurationFlagTypeDto type) {
    return type.getBitPositionDsmr43();
  }
}
