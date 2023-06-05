// Copyright 2022 Alliander N.V.
// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.config;

import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.opensmartgridplatform.shared.security.providers.JreEncryptionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class SecurityConfig {

  @Value("${jre.encryption.key.resource}")
  private Resource jreEncryptionKeyResource;

  @Bean
  public JreEncryptionProvider getJreEncryptionProvider() {
    try {
      return new JreEncryptionProvider(
          IOUtils.toByteArray(this.jreEncryptionKeyResource.getInputStream()));
    } catch (final IOException e) {
      throw new IllegalStateException("Error creating JRE encryption provider", e);
    }
  }
}
