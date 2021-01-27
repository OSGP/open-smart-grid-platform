/**
 * Copyright 2021 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

import lombok.Data;

@Entity
@Data
public class Feeder extends AbstractEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    private Location location;

    @Column(nullable = false)
    private Long feederNumber;

    @Column(length = 32, nullable = false)
    private String name;
}
