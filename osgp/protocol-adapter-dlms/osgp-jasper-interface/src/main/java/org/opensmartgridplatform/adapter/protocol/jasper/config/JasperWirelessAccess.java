/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.config;

import lombok.Getter;

@Getter
public class JasperWirelessAccess {

  private String uri;
  private String licenseKey;
  private String apiKey;
  private String username;
  private String password;
  private String apiVersion;
  private String apiType;

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
