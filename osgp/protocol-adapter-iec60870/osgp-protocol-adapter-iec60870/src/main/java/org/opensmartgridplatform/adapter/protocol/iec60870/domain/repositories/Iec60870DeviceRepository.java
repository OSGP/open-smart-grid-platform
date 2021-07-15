/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories;

import java.util.List;
import java.util.Optional;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Iec60870DeviceRepository extends JpaRepository<Iec60870Device, Long> {

  Optional<Iec60870Device> findByDeviceIdentification(String deviceIdentification);

  List<Iec60870Device> findByGatewayDeviceIdentification(String gatewayDeviceIdentification);
}
