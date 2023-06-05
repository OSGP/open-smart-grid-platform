// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
 * Encrypted secret, which should not be mutated, because modifications should result in a new
 * version (record in the DB). Historic entries remain in the DB table and the current secret should
 * be determined via a query on creationTime.
 */
@Entity
@Table(name = "encrypted_secret")
@Getter
@Setter
public class DbEncryptedSecret {
  @Id
  @SequenceGenerator(
      name = "encrypted_secret_seq_gen",
      sequenceName = "encrypted_secret_id_seq",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "encrypted_secret_seq_gen")
  private Long id;

  private Date creationTime;
  private String deviceIdentification;

  @Enumerated(EnumType.STRING)
  private SecretType secretType;

  @Enumerated(EnumType.STRING)
  private SecretStatus secretStatus;

  private String encodedSecret;
  @ManyToOne private DbEncryptionKeyReference encryptionKeyReference;
}
