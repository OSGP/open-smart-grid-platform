//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.SecurityLevel.HLS3;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.SecurityLevel.HLS4;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.SecurityLevel.HLS5;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.SecurityLevel.LLS0;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.SecurityLevel.LLS1;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;

public class SecurityLevelTest {
  @Test
  public void returnsSecurityLevelForDevice() {
    assertThat(SecurityLevel.forDevice(this.aHls5Device())).isEqualTo(HLS5);
    assertThat(SecurityLevel.forDevice(this.aHls4Device())).isEqualTo(HLS4);
    assertThat(SecurityLevel.forDevice(this.aHls3Device())).isEqualTo(HLS3);
    assertThat(SecurityLevel.forDevice(this.aLls1Device())).isEqualTo(LLS1);
    assertThat(SecurityLevel.forDevice(this.aLls0Device())).isEqualTo(LLS0);
  }

  private DlmsDevice aHls5Device() {
    return new DlmsDeviceBuilder().withHls5Active(true).build();
  }

  private DlmsDevice aHls4Device() {
    return new DlmsDeviceBuilder().withHls4Active(true).build();
  }

  private DlmsDevice aHls3Device() {
    return new DlmsDeviceBuilder().withHls3Active(true).build();
  }

  private DlmsDevice aLls1Device() {
    return new DlmsDeviceBuilder().withLls1Active(true).build();
  }

  private DlmsDevice aLls0Device() {
    return new DlmsDeviceBuilder().build();
  }
}
