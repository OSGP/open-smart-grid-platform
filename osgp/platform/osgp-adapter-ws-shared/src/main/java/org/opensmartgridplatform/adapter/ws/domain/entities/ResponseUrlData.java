// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
