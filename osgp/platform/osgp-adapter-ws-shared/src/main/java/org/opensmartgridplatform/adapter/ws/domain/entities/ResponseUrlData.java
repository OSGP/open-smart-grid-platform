/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
@Table(name = "response_url")
public class ResponseUrlData extends AbstractEntity {

  /** Serial Version UID. */
  private static final long serialVersionUID = 493031146792643786L;

  @Column(length = 255)
  private String correlationUid;

  @Column(length = 255)
  private String responseUrl;

  @SuppressWarnings("unused")
  private ResponseUrlData() {}

  public ResponseUrlData(final String correlationUid, final String responseUrl) {
    this.correlationUid = correlationUid;
    this.responseUrl = responseUrl;
  }

  public String getCorrelationUid() {
    return this.correlationUid;
  }

  public String getResponseUrl() {
    return this.responseUrl;
  }
}
