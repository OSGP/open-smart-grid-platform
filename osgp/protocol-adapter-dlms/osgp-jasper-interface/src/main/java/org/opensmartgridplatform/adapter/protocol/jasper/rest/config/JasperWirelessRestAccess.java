/*
 * Copyright 2022 Alliander.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.rest.config;

import lombok.Getter;

@Getter
public class JasperWirelessRestAccess {

  private String url;
  private String licenseKey;
  private String username;
  private String apiVersion;

  public JasperWirelessRestAccess(
      final String url, final String licenseKey, final String username, final String apiVersion) {
    this.url = url;
    this.licenseKey = licenseKey;
    this.username = username;
    this.apiVersion = apiVersion;
  }
}
