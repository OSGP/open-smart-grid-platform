/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable;

import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WritableSsldRepository extends JpaRepository<Ssld, Long> {
  Ssld findByDeviceIdentification(String deviceIdentification);
}
