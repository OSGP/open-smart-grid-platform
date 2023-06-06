// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.domain.repositories;

import java.util.List;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.OslpLogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OslpLogItemRepository extends JpaRepository<OslpLogItem, Long> {
  @Query("select o from OslpLogItem o order by o.modificationTime desc")
  List<OslpLogItem> findAllOrderByModificationTimeDesc();
}
