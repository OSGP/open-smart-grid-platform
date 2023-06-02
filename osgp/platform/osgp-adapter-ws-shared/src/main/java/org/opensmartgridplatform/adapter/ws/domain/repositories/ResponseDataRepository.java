//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.domain.repositories;

import java.util.Date;
import java.util.List;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseDataRepository extends JpaRepository<ResponseData, Long> {

  List<ResponseData> findByOrganisationIdentification(String organisationIdentification);

  List<ResponseData> findByMessageType(String messageType);

  List<ResponseData> findByDeviceIdentification(String deviceIdentification);

  List<ResponseData> findByNumberOfNotificationsSentAndCreationTimeBefore(
      Short numberOfNotificationsSent, Date createdBefore, Pageable pageable);

  ResponseData findByCorrelationUid(String correlationUid);

  void removeByCreationTimeBefore(Date date);
}
