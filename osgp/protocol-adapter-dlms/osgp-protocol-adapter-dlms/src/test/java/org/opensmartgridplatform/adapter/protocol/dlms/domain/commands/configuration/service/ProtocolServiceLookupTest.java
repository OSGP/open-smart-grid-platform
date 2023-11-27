// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

public class ProtocolServiceLookupTest {

  private ProtocolServiceLookup instance;
  private GetConfigurationObjectServiceDsmr4 getDsmr4Service;
  private GetConfigurationObjectServiceDsmr43 getDsmr43Service;
  private GetConfigurationObjectServiceSmr5 getSmr5Service;

  @BeforeEach
  void setUp() {
    this.getDsmr4Service = new GetConfigurationObjectServiceDsmr4(null, null);
    this.getDsmr43Service = new GetConfigurationObjectServiceDsmr43(null, null);
    this.getSmr5Service = new GetConfigurationObjectServiceSmr5(null, null);
    final List<ProtocolService> services = new ArrayList<>();
    services.add(this.getDsmr4Service);
    services.add(this.getDsmr43Service);
    services.add(this.getSmr5Service);
    this.instance = new ProtocolServiceLookup(services);
  }

  @ParameterizedTest
  @EnumSource(Protocol.class)
  @NullSource
  void lookupGetService(final Protocol protocol) throws ProtocolAdapterException {

    if (protocol == null || (!protocol.isDsmr4() && !protocol.isSmr5())) {
      this.assertGetServiceNotFound(protocol);
    } else {
      this.assertGetServiceFound(protocol);
    }
  }

  private void assertGetServiceFound(final Protocol protocol) throws ProtocolAdapterException {

    final GetConfigurationObjectService result = this.instance.lookupGetService(protocol);

    if (protocol.isDsmr4()) {
      if ("4.3".equals(protocol.getVersion())) {
        assertThat(result).isSameAs(this.getDsmr43Service);
      } else {
        assertThat(result).isSameAs(this.getDsmr4Service);
      }
    } else if (protocol.isSmr5()) {
      assertThat(result).isSameAs(this.getSmr5Service);
    }
  }

  private void assertGetServiceNotFound(final Protocol protocol) {

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(() -> this.instance.lookupSetService(protocol));
  }
}
