/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgpfoundation.osgp.webdemoapp.application.infra.platform;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import org.springframework.core.io.FileSystemResource;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;

/**
 * Contains settings for keystore and truststore.
 *
 */
public class KeyStoreHelper {

    private String keyStorePw;

    private KeyStoreFactoryBean trustStoreFactory;

    private KeyStoreFactoryBean keyStoreFactory;

    public KeyStoreHelper(final String trustStoreType, final String trustStoreLoc, final String trustStorePw,
            final String keyStoreLocation, final String keyStoreType, final String keyStorePw) {

        this.keyStorePw = keyStorePw;

        this.trustStoreFactory = new KeyStoreFactoryBean();
        this.trustStoreFactory.setType(trustStoreType);
        this.trustStoreFactory.setLocation(new FileSystemResource(trustStoreLoc));
        this.trustStoreFactory.setPassword(trustStorePw);

        this.keyStoreFactory = new KeyStoreFactoryBean();
        this.keyStoreFactory.setType(keyStoreType);
        this.keyStoreFactory.setLocation(new FileSystemResource(keyStoreLocation));
        this.keyStoreFactory.setPassword(keyStorePw);
        try {
            this.keyStoreFactory.afterPropertiesSet();
            this.trustStoreFactory.afterPropertiesSet();
        } catch (GeneralSecurityException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public KeyStore getTrustStore() {
        return this.trustStoreFactory.getObject();
    }

    public KeyStore getKeyStore() {
        return this.keyStoreFactory.getObject();
    }

    public String getKeyStorePw() {
        return this.keyStorePw;
    }

}
