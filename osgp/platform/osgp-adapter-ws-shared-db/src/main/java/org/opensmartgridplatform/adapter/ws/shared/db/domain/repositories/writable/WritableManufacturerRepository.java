// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable;

import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WritableManufacturerRepository extends JpaRepository<Manufacturer, Long> {
  Manufacturer findByCode(String code);

  Manufacturer findByName(String manufacturerName);
}
