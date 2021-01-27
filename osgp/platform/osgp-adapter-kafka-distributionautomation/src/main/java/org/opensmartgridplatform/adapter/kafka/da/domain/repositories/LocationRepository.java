/**
 * Copyright 2021 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.domain.repositories;

import org.opensmartgridplatform.adapter.kafka.da.domain.entities.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Location repository interface
 */
@Repository
public interface LocationRepository extends CrudRepository<Location, Long> {
    Location findOneByName(String name);

    Location findOneBySubstationIdentification(String substationIdentification);
}
