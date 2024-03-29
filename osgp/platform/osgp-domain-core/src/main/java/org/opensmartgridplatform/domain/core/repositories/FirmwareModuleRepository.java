// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.repositories;

import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FirmwareModuleRepository extends JpaRepository<FirmwareModule, Long> {

  FirmwareModule findByDescriptionIgnoreCase(String description);
}
