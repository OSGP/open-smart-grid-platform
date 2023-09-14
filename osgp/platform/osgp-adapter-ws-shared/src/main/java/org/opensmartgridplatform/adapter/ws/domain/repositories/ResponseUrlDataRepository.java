// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.domain.repositories;

import java.time.Instant;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseUrlData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseUrlDataRepository extends JpaRepository<ResponseUrlData, Long> {

  ResponseUrlData findSingleResultByCorrelationUid(String correlationUid);

  void removeByCreationTimeBefore(Instant date);
}
