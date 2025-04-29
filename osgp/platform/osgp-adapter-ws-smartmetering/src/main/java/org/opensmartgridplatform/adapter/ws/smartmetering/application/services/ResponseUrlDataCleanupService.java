// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseUrlDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "transactionManager")
public class ResponseUrlDataCleanupService {

  private final ResponseUrlDataRepository responseUrlDataRepository;
  private final int cleanupJobRetentionTimeInDays;

  public ResponseUrlDataCleanupService(
      final ResponseUrlDataRepository responseUrlDataRepository,
      final int cleanupJobRetentionTimeInDays) {
    this.responseUrlDataRepository = responseUrlDataRepository;
    this.cleanupJobRetentionTimeInDays = cleanupJobRetentionTimeInDays;
  }

  public void execute() {

    final ZonedDateTime removeBeforeDateTime =
        ZonedDateTime.now(ZoneId.of("UTC")).minusDays(this.cleanupJobRetentionTimeInDays);
    this.responseUrlDataRepository.removeByCreationTimeBefore(removeBeforeDateTime.toInstant());
  }
}
