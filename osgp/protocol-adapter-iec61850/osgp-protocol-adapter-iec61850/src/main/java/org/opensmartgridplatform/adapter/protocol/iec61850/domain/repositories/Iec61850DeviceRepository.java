/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories;

import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Iec61850DeviceRepository extends JpaRepository<Iec61850Device, Long> {

  Iec61850Device findByDeviceIdentification(String deviceIdentification);
}
