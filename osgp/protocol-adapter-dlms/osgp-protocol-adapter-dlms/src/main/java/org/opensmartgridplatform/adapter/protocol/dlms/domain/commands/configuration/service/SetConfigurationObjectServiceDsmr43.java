// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import java.util.Optional;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.springframework.stereotype.Component;

@Component
public class SetConfigurationObjectServiceDsmr43 extends SetConfigurationObjectServiceDsmr4 {

  public SetConfigurationObjectServiceDsmr43(
      final DlmsHelper dlmsHelper, final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(dlmsHelper, objectConfigServiceHelper);
  }

  @Override
  public boolean handles(final Protocol protocol) {
    return protocol != null && protocol.isDsmr4() && "4.3".equals(protocol.getVersion());
  }

  @Override
  Optional<Integer> getBitPosition(final ConfigurationFlagTypeDto type) {
    return type.getBitPositionDsmr43();
  }
}
