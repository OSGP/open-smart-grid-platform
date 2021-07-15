/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest;

public class ReadAlarmRegisterRequestMappingTest {

  private MonitoringMapper monitoringMapper = new MonitoringMapper();

  /** Tests if a ReadAlarmRegisterRequest is mapped correctly. */
  @Test
  public void testReadAlarmRegisterRequestMapping() {

    // build test data
    final ReadAlarmRegisterRequest original = new ReadAlarmRegisterRequest();
    original.setDeviceIdentification("id");

    // actual mapping
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest
        mapped =
            this.monitoringMapper.map(
                original,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .ReadAlarmRegisterRequest.class);

    // check mapping
    assertThat(mapped).isNotNull();
    assertThat(mapped.getDeviceIdentification()).isNotNull();
    assertThat(mapped.getDeviceIdentification()).isEqualTo(original.getDeviceIdentification());
  }
}
