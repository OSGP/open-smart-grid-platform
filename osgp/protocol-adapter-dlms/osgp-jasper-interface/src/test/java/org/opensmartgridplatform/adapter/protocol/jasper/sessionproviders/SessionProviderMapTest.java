// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionProviderMapTest {

  @InjectMocks private SessionProviderMap sessionProviderMap;

  @Test
  void callToGetProviderWithNullProviderShouldResultInNullSessionProvider() {

    final SessionProvider sessionProvider = this.sessionProviderMap.getProvider(null);
    Assertions.assertThat(sessionProvider).isNull();
  }

  @Test
  void callToGetProviderWithUnknownProviderShouldResultInNullSessionProvider() {

    final SessionProvider sessionProvider = this.sessionProviderMap.getProvider("NOT_KNOWN");
    Assertions.assertThat(sessionProvider).isNull();
  }
}
