// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.repositories;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.SsldPendingFirmwareUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SsldPendingFirmwareUpdateRepository
    extends JpaRepository<SsldPendingFirmwareUpdate, Long> {

  List<SsldPendingFirmwareUpdate> findByDeviceIdentification(String deviceIdentification);
}
