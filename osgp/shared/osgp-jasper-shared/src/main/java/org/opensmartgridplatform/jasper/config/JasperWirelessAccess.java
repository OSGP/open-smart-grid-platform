// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.jasper.config;

import lombok.Getter;

@Getter
public class JasperWirelessAccess {

  private final String uri;
  private final String licenseKey;
  private final String apiKey;
  private final String username;
  private final String password;
  private final String apiVersion;
  private final String apiType;

  public JasperWirelessAccess(
      final String uri,
      final String licenseKey,
      final String apiKey,
      final String username,
      final String password,
      final String apiVersion,
      final String apiType) {
    this.uri = uri;
    this.licenseKey = licenseKey;
    this.apiKey = apiKey;
    this.username = username;
    this.password = password;
    this.apiVersion = apiVersion;
    this.apiType = apiType;
  }
}
