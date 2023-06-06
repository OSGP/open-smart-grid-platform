// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DlmsObjectConfigConfiguration {

  @Bean
  public List<DlmsObjectConfig> getDlmsObjectConfigs() {
    final List<DlmsObjectConfig> configs = new ArrayList<>();
    configs.add(new DlmsObjectConfigDsmr422());
    configs.add(new DlmsObjectConfigSmr50());
    configs.add(new DlmsObjectConfigSmr51());
    configs.add(new DlmsObjectConfigSmr52());
    configs.add(new DlmsObjectConfigSmr55());
    return configs;
  }
}
