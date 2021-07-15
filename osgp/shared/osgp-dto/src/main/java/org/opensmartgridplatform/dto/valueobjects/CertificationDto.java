/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public class CertificationDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -4199748600003850659L;

  private String certificateUrl;

  private String certificateDomain;

  public CertificationDto(final String certificateUrl, final String certificateDomain) {
    this.certificateUrl = certificateUrl;
    this.certificateDomain = certificateDomain;
  }

  public String getCertificateUrl() {
    return this.certificateUrl;
  }

  public String getCertificateDomain() {
    return this.certificateDomain;
  }
}
