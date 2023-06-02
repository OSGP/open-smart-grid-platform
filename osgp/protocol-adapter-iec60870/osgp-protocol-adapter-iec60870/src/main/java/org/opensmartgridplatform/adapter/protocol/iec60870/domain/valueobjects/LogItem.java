//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

import org.apache.commons.lang3.StringUtils;

public class LogItem {

  private static final int MAX_MESSAGE_LENGTH = 8000;

  private boolean incoming;

  private String message;

  private String deviceIdentification;

  private String organisationIdentification;

  public LogItem(
      final String deviceIdentification,
      final String organisationIdentification,
      final boolean incoming,
      final String message) {
    this.deviceIdentification = deviceIdentification;
    this.organisationIdentification = organisationIdentification;
    this.incoming = incoming;

    // Truncate the log-items to max length.
    this.message = StringUtils.substring(message, 0, MAX_MESSAGE_LENGTH);
  }

  public Boolean isIncoming() {
    return this.incoming;
  }

  public String getMessage() {
    return this.message;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public String getOrganisationIdentification() {
    return this.organisationIdentification;
  }
}
