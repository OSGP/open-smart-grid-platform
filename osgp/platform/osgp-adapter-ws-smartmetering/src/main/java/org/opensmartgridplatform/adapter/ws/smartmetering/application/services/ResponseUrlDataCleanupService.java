/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseUrlDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "transactionManager")
public class ResponseUrlDataCleanupService {

  @Autowired private ResponseUrlDataRepository responseUrlDataRepository;

  @Autowired private int urlDataCleanupJobRetentionTimeInDays;

  public void execute() {

    final DateTime removeBeforeDateTime =
        DateTime.now(DateTimeZone.UTC).minusDays(this.urlDataCleanupJobRetentionTimeInDays);
    this.responseUrlDataRepository.removeByCreationTimeBefore(removeBeforeDateTime.toDate());
  }
}
