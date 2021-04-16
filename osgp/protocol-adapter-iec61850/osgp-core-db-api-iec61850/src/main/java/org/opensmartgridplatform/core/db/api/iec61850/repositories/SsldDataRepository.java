/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.db.api.iec61850.repositories;

import org.opensmartgridplatform.core.db.api.iec61850.entities.Ssld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SsldDataRepository extends JpaRepository<Ssld, Long> {
  Ssld findByDeviceIdentification(String deviceIdentification);
}
