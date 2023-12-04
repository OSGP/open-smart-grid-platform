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
public class GetConfigurationObjectServiceDsmr43 extends GetConfigurationObjectServiceDsmr4 {

  public GetConfigurationObjectServiceDsmr43(
      final DlmsHelper dlmsHelper, final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(dlmsHelper, objectConfigServiceHelper);
  }

  @Override
  public boolean handles(final Protocol protocol) {
    return protocol != null && protocol.isDsmr43();
  }

  @Override
  Optional<ConfigurationFlagTypeDto> getFlagType(final int bitPosition) {
    return ConfigurationFlagTypeDto.getDsmr43FlagType(bitPosition);
  }
}
