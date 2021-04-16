/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.domain.entities;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Location extends AbstractEntity {

  @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<Feeder> feeders;

  @Column(length = 12, nullable = false)
  private String substationIdentification;

  @Column(length = 32, nullable = false)
  private String name;

  public Optional<Feeder> getFeeder(final int feederNumber) {
    if (this.getFeeders() == null) {
      return Optional.empty();
    }

    final Predicate<Feeder> byFeederNumber = f -> f.getFeederNumber().intValue() == feederNumber;

    return this.getFeeders().stream().filter(byFeederNumber).findFirst();
  }
}
