/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.config;

import java.io.File;
import java.io.IOException;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-smartmetering.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsSmartMetering/config}", ignoreResourceNotFound = true)
public class SecurityConfig extends AbstractConfig {

  @Value("${osgp.rsa.private.key.resource}")
  private Resource gxfRsaPrivateKeyResource;

  @Bean(name = "gxfDecrypter")
  public RsaEncrypter gxfRsaDecrypter() {
    try {
      final File privateRsaKeyFile = this.gxfRsaPrivateKeyResource.getFile();
      final RsaEncrypter rsaEncrypter = new RsaEncrypter();
      rsaEncrypter.setPrivateKeyStore(privateRsaKeyFile);
      return rsaEncrypter;
    } catch (final IOException e) {
      throw new IllegalStateException(
          "Could not initialize RsaEncrypter for decryption with the private key from the GXF RSA key pair",
          e);
    }
  }
}
