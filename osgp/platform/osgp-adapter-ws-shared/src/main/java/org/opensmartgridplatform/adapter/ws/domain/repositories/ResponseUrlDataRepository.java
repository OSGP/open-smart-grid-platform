/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.domain.repositories;

import java.util.Date;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseUrlData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseUrlDataRepository extends JpaRepository<ResponseUrlData, Long> {

  ResponseUrlData findSingleResultByCorrelationUid(String correlationUid);

  void removeByCreationTimeBefore(Date date);
}
