/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.opensmartgridplatform.ws.schema.core.secret.management.SecretType;

public enum SecurityKeyType {
    /**
     * DLMS master key (Key Encryption Key)
     */
    E_METER_MASTER,
    /**
     * DLMS authentication key
     */
    E_METER_AUTHENTICATION,
    /**
     * DLMS global unicast encryption key
     */
    E_METER_ENCRYPTION,
    /**
     * M-Bus Default key
     */
    G_METER_MASTER,
    /**
     * M-Bus User key
     */
    G_METER_ENCRYPTION,
    /**
     * Password (e.g. used as DLMS Low Level Security secret)
     */
    PASSWORD;

    private static class Pair<A,B> {
        A a;
        B b;

        public Pair(A a, B b) {
            this.a=a;
            this.b=b;
        }

        public A get0() {
            return this.a;
        }
        public B get1() {
            return this.b;
        }
    }

    private static final List<Pair<SecurityKeyType,SecretType>> typeMapping;

    static {
        typeMapping = Arrays.asList(
                new Pair(E_METER_ENCRYPTION, SecretType.E_METER_ENCRYPTION_KEY_UNICAST),
                new Pair(E_METER_AUTHENTICATION, SecretType.E_METER_AUTHENTICATION_KEY),
                new Pair(E_METER_MASTER, SecretType.E_METER_MASTER_KEY),
                new Pair(G_METER_ENCRYPTION, SecretType.G_METER_ENCRYPTION_KEY),
                new Pair(G_METER_MASTER, SecretType.G_METER_MASTER_KEY),
                new Pair(PASSWORD, SecretType.PPP_PASSWORD));
    }

    public SecretType toSecretType() {
        Optional<Pair<SecurityKeyType,SecretType>> typePair =
                typeMapping.stream().filter(tp -> tp.get0().equals(this)).findFirst();
        if(typePair.isPresent()) {
            return typePair.get().get1();
        } else {
            throw new IllegalArgumentException("Could not get secret type of value " + this);
        }
    }

    public static SecurityKeyType fromSecretType(SecretType secretType) {
        Optional<Pair<SecurityKeyType,SecretType>> typePair =
                typeMapping.stream().filter(tp -> tp.get1().equals(secretType)).findFirst();
        if(typePair.isPresent()) {
            return typePair.get().get0();
        } else {
            throw new IllegalArgumentException("Could not get value from secret type " + secretType);
        }
    }
}
