// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.repositories;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceFirmwareFileRepository
    extends JpaRepository<DeviceFirmwareFile, Long>, JpaSpecificationExecutor<DeviceFirmwareFile> {

  DeviceFirmwareFile findByDeviceAndFirmwareFile(Device device, FirmwareFile firmwareFile);

  List<DeviceFirmwareFile> findByDeviceOrderByInstallationDateAsc(Device device);
}
