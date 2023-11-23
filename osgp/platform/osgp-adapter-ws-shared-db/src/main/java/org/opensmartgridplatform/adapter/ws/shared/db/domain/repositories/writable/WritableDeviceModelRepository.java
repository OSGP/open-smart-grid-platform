// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WritableDeviceModelRepository extends JpaRepository<DeviceModel, Long> {
  DeviceModel findByManufacturerAndModelCodeIgnoreCase(Manufacturer manufacturer, String modelCode);

  DeviceModel findByManufacturerCodeAndModelCode(String string, String modelCode);

  List<DeviceModel> findByManufacturer(Manufacturer manufacturer);

  List<DeviceModel> findByModelCode(String modelCode);
}
