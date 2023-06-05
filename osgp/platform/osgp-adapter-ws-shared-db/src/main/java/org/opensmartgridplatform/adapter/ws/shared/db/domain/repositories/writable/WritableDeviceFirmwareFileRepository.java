// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WritableDeviceFirmwareFileRepository
    extends JpaRepository<DeviceFirmwareFile, Long> {
  List<DeviceFirmwareFile> findByDevice(Device device);

  List<DeviceFirmwareFile> findByDeviceOrderByInstallationDateAsc(Device device);

  List<DeviceFirmwareFile> findByFirmwareFile(FirmwareFile firmwareFile);
}
