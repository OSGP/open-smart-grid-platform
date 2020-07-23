/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Encrypted secret, which should not be mutated, because modifications should result in a new version (record in the
 * DB). Historic entries remain in the DB table and the current secret should be determined via a query on creationTime.
 */
@Entity
@Table(name = "encrypted_secret")
@Getter
@Setter
public class DbEncryptedSecret {
    @Id
    @SequenceGenerator(name = "encrypted_secret_seq_gen", sequenceName = "encrypted_secret_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "encrypted_secret_seq_gen")
    private Long id;
    private Date creationTime;
    private String deviceIdentification;
    @Enumerated(EnumType.STRING)
    private SecretType secretType;
    private String encodedSecret;
    @ManyToOne
    private DbEncryptionKeyReference encryptionKeyReference;
}
