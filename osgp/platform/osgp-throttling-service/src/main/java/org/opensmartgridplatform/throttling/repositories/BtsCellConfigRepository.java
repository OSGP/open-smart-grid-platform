// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.repositories;

import java.util.Optional;
import org.opensmartgridplatform.throttling.entities.BtsCellConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BtsCellConfigRepository extends JpaRepository<BtsCellConfig, Short> {

  Optional<BtsCellConfig> findByBaseTransceiverStationIdAndCellId(
      int baseTransceiverStationId, int cellId);
}
