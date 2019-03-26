/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ProtocolTest {

    @Test
    public void testProtocolWithNameAndVersion() {
        Assertions.assertThat(Protocol.withNameAndVersion("DSMR", "4.2.2")).isEqualTo(Protocol.DSMR_4_2_2);
        Assertions.assertThat(Protocol.withNameAndVersion("SMR", "5.0")).isEqualTo(Protocol.SMR_5_0);
        Assertions.assertThat(Protocol.withNameAndVersion("SMR", "5.1")).isEqualTo(Protocol.SMR_5_1);
        Assertions.assertThat(Protocol.withNameAndVersion("other", "0.1")).isEqualTo(Protocol.OTHER_PROTOCOL);
    }

    @Test
    public void testIsSelectingValuesSupported() {
        Assertions.assertThat(Protocol.DSMR_4_2_2.isSelectValuesInSelectiveAccessSupported()).isEqualTo(true);
        Assertions.assertThat(Protocol.SMR_5_0.isSelectValuesInSelectiveAccessSupported()).isEqualTo(false);
        Assertions.assertThat(Protocol.SMR_5_1.isSelectValuesInSelectiveAccessSupported()).isEqualTo(false);
        Assertions.assertThat(Protocol.OTHER_PROTOCOL.isSelectValuesInSelectiveAccessSupported()).isEqualTo(true);
    }
}
