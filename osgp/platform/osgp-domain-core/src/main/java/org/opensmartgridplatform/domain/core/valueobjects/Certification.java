// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

public class Certification implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -4654886302782337350L;

  private String certificateUrl;

  private String certificateDomain;

  public Certification(final String certificateUrl, final String certificateDomain) {
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
