// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.shared.services;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "transactionManager")
public class ResponseDataCleanupService {

  @Autowired private ResponseDataRepository responseDataRepository;

  @Autowired private int cleanupJobRetentionTimeInDays;

  public void execute() {

    final ZonedDateTime removeBeforeDateTime =
        ZonedDateTime.now(ZoneId.of("UTC")).minusDays(this.cleanupJobRetentionTimeInDays);
    this.responseDataRepository.removeByCreationTimeBefore(
        Date.from(removeBeforeDateTime.toInstant()));
  }
}
