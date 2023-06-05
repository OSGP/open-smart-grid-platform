// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.OslpDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OslpDeviceRepository extends JpaRepository<OslpDevice, Long> {
  OslpDevice findByDeviceUid(String deviceUid);

  OslpDevice findByDeviceIdentification(String deviceIdentification);
}
