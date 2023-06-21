// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.repositories;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Ean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface EanRepository extends JpaRepository<Ean, Long>, JpaSpecificationExecutor<Ean> {

  List<Ean> findByDevice(Device device);

  Ean findByCode(Long code);

  /*
   * We need these native queries below because these entities don't have an
   * Id
   */
  @Modifying
  @Query(value = "delete from ean", nativeQuery = true)
  void deleteAllEans();
}
