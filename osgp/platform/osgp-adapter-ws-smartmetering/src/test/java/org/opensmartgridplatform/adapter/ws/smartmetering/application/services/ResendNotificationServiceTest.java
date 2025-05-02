/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ResendNotificationServiceTest {

  private final int resendPageSize = 100;
  private final short resendNotificationMaximum = 3;
  @Mock private ResponseDataRepository responseDataRepository;
  @Mock private NotificationService smartMeteringNotificationService;

  private ResendNotificationService resendNotificationService;

  @BeforeEach
  void setUp() {
    final int resendNotificationMultiplier = 2;
    final int resendThresholdInMinutes = 2;
    final String webserviceNotificationApplicationName = "application-1";
    this.resendNotificationService =
        new ResendNotificationService(
            this.smartMeteringNotificationService, webserviceNotificationApplicationName);
    ReflectionTestUtils.setField(
        this.resendNotificationService,
        "resendNotificationMultiplier",
        resendNotificationMultiplier);
    ReflectionTestUtils.setField(
        this.resendNotificationService,
        "resendNotificationMaximum",
        this.resendNotificationMaximum);
    ReflectionTestUtils.setField(
        this.resendNotificationService, "resendThresholdInMinutes", resendThresholdInMinutes);
    ReflectionTestUtils.setField(
        this.resendNotificationService, "resendPageSize", this.resendPageSize);
    ReflectionTestUtils.setField(
        this.resendNotificationService, "responseDataRepository", this.responseDataRepository);
  }

  @Test
  void testResponseDataPageIsNotSorted() {
    final ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
    when(this.responseDataRepository.findByNumberOfNotificationsSentAndCreationTimeBefore(
            any(Short.class), any(Instant.class), any(Pageable.class)))
        .thenReturn(List.of());
    this.resendNotificationService.execute();
    verify(this.responseDataRepository, times(this.resendNotificationMaximum))
        .findByNumberOfNotificationsSentAndCreationTimeBefore(
            any(Short.class), any(Instant.class), pageableArgumentCaptor.capture());
    final Pageable pageable = pageableArgumentCaptor.getValue();
    assertThat(pageable).isNotNull();
    assertThat(pageable.getPageSize()).isEqualTo(this.resendPageSize);
    assertThat(pageable.getSort().isSorted()).isFalse();
  }
}
