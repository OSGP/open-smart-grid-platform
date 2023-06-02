//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.throttling.repositories;

import java.util.Optional;
import org.opensmartgridplatform.throttling.entities.ThrottlingConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThrottlingConfigRepository extends JpaRepository<ThrottlingConfig, Short> {

  Optional<ThrottlingConfig> findOneByName(String name);
}
