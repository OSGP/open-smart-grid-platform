/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.throttling.web.api;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.throttling.entities.Client;
import org.opensmartgridplatform.throttling.repositories.ClientRepository;

@ExtendWith(MockitoExtension.class)
class ClientApiServiceTest {

  @InjectMocks private ClientApiService service;
  @Mock private ClientRepository clientRepository;

  @Test
  void shouldSetLastSeen() {
    final Client clientWithoutLastSeen = new Client(1, "test client", null, null, null);
    when(this.clientRepository.findById(any())).thenReturn(Optional.of(clientWithoutLastSeen));

    final Client resultClient = this.service.getAndNoticeClient(1);

    Assertions.assertThat(resultClient.getLastSeenAt()).isNotNull();
  }
}
