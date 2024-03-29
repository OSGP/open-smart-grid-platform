// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectServiceDsmr4Test.assertAllProtocolSpecificFlags;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectServiceDsmr4Test.whenParseGetResult;

import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;

@ExtendWith(MockitoExtension.class)
class GetConfigurationObjectServiceDsmr43Test {

  private GetConfigurationObjectServiceDsmr43 instance;

  @Mock private GetResult getResult;
  @Mock private DlmsHelper dlmsHelper;

  @Mock private ObjectConfigServiceHelper objectConfigServiceHelper;
  @Mock private DlmsDeviceRepository dlmsDeviceRepository;

  @BeforeEach
  void setUp() {
    this.instance =
        new GetConfigurationObjectServiceDsmr43(
            this.dlmsHelper, this.objectConfigServiceHelper, this.dlmsDeviceRepository);
  }

  @ParameterizedTest
  @EnumSource(Protocol.class)
  @NullSource
  void handles(final Protocol protocol) {
    assertThat(this.instance.handles(protocol)).isEqualTo(protocol != null && protocol.isDsmr43());
  }

  @Test
  void getFlagType() {
    for (final ConfigurationFlagTypeDto flagTypeDto : ConfigurationFlagTypeDto.values()) {
      flagTypeDto
          .getBitPositionDsmr43()
          .ifPresent(
              bitPosition ->
                  assertThat(
                          this.instance
                              .getFlagType(bitPosition)
                              .orElseThrow(IllegalArgumentException::new))
                      .isEqualTo(flagTypeDto));
    }
  }

  @Test
  void getConfigurationObjectFlagsIncludeHighAndLowFlags() throws ProtocolAdapterException {

    whenParseGetResult(this.getResult, new byte[] {82, 32});

    final ConfigurationObjectDto configurationObject =
        this.instance.getConfigurationObject(this.getResult);

    final Predicate<ConfigurationFlagTypeDto> protocolVersionPredicate =
        fl -> fl.getBitPositionDsmr43().isPresent();

    assertAllProtocolSpecificFlags(configurationObject, protocolVersionPredicate);
  }
}
