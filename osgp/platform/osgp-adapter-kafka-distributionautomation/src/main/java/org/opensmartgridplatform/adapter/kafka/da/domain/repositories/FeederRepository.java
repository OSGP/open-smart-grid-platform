/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.domain.repositories;

import org.opensmartgridplatform.adapter.kafka.da.domain.entities.Feeder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Feeder repository interface
 */
@Repository
public interface FeederRepository extends JpaRepository<Feeder, Long> {
}
