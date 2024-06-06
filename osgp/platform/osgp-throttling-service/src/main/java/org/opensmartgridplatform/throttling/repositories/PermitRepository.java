// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.opensmartgridplatform.throttling.api.NonUniqueRequestIdException;
import org.opensmartgridplatform.throttling.entities.Permit;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PermitRepository extends JpaRepository<Permit, Long> {

  interface PermitCountByNetworkSegment {
    int getBaseTransceiverStationId();

    int getCellId();

    int getNumberOfPermits();
  }

  @Query(
      "SELECT p.baseTransceiverStationId AS baseTransceiverStationId, p.cellId AS cellId, "
          + "COUNT(p.id) AS numberOfPermits FROM Permit AS p "
          + "WHERE p.throttlingConfigId = :throttlingConfigId "
          + "GROUP BY p.throttlingConfigId, p.baseTransceiverStationId, p.cellId "
          + "ORDER BY p.baseTransceiverStationId ASC, p.cellId ASC")
  List<PermitCountByNetworkSegment> permitsByNetworkSegment(
      @Param("throttlingConfigId") short throttlingConfigId);

  default double secondsSinceEpoch() {
    return System.currentTimeMillis() / 1000.0;
  }

  default boolean grantPermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId) {

    try {
      return this.storePermit(
              throttlingConfigId,
              clientId,
              baseTransceiverStationId,
              cellId,
              requestId,
              this.secondsSinceEpoch())
          == 1;
    } catch (final DataIntegrityViolationException e) {
      if (e.getMessage() != null && e.getMessage().contains("permit_client_request_idx")) {
        throw new NonUniqueRequestIdException(clientId, requestId);
      }
      throw e;
    }
  }

  @Modifying
  @Query(
      value =
          "INSERT INTO permit (throttling_config_id, client_id, bts_id, cell_id, request_id, created_at) "
              + "VALUES (:throttlingConfigId, :clientId, :btsId, :cellId, :requestId, "
              + "to_timestamp(:seconds) AT TIME ZONE 'UTC')",
      nativeQuery = true)
  int storePermit(
      @Param("throttlingConfigId") final short throttlingConfigId,
      @Param("clientId") final int clientId,
      @Param("btsId") final int baseTransceiverStationId,
      @Param("cellId") final int cellId,
      @Param("requestId") final int requestId,
      @Param("seconds") final double secondsSinceEpoch);

  @Modifying
  @Query(
      value =
          "DELETE FROM permit WHERE id IN (SELECT id FROM permit WHERE bts_id = :btsId AND "
              + "cell_id = :cellId AND throttling_config_id = :throttlingConfigId AND "
              + "client_id = :clientId AND request_id = :requestId ORDER BY id ASC LIMIT 1)",
      nativeQuery = true)
  int releasePermit(
      @Param("throttlingConfigId") final short throttlingConfigId,
      @Param("clientId") final int clientId,
      @Param("btsId") final int baseTransceiverStationId,
      @Param("cellId") final int cellId,
      @Param("requestId") final int requestId);

  Optional<Permit> findByClientIdAndRequestId(int clientId, int requestId);

  long countByClientId(int clientId);

  List<Permit> findByCreatedAtBefore(Instant minus, Pageable pageable);
}
