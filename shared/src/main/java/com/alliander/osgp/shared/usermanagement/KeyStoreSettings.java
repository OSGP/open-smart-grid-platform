package com.alliander.osgp.shared.usermanagement;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.TrustManagerFactory;

public class KeyStoreSettings {

    private final String keyStoreLocation;
    private final String keyStoreType;

    private final KeyStore keyStore;
    private final TrustManagerFactory trustManagerFactory;

    public KeyStoreSettings(final String keyStoreLocation, final String keyStorePassword, final String keyStoreType)
            throws WebClientException {
        this.keyStoreLocation = keyStoreLocation;
        this.keyStoreType = keyStoreType;

        this.keyStore = this.initializeKeyStore(keyStoreLocation, keyStorePassword, keyStoreType);
        this.trustManagerFactory = this.initializeTrustManagerFactory(this.keyStore);
    }

    private KeyStore initializeKeyStore(final String keyStoreLocation, final String keyStorePassword,
            final String keyStoreType) throws WebClientException {
        try (InputStream stream = new FileInputStream(keyStoreLocation)) {
            final KeyStore ks = KeyStore.getInstance(keyStoreType.toUpperCase());
            ks.load(stream, keyStorePassword.toCharArray());
            return ks;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new WebClientException("Error initializing KeyStore", e);
        }
    }

    private TrustManagerFactory initializeTrustManagerFactory(final KeyStore keyStore) throws WebClientException {
        try {
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            return tmf;
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new WebClientException("Error initializing TrustManagerFactory", e);
        }
    }

    @Override
    public String toString() {
        return "KeyStoreSettings[location=" + this.keyStoreLocation + ", type=" + this.keyStoreType + "]";
    }

    public KeyStore getKeyStore() {
        return this.keyStore;
    }

    public TrustManagerFactory getTrustManagerFactory() {
        return this.trustManagerFactory;
    }
}
