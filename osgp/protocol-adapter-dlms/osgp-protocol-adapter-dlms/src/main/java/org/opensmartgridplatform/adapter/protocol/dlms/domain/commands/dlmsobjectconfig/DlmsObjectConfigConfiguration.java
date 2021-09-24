/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
    return configs;
  }
}
