/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Feeder extends AbstractEntity {

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "location_id", nullable = false, updatable = false)
  private Location location;

  @Column(nullable = false)
  private Integer feederNumber;

  @Column(length = 32, nullable = false)
  private String name;
}
