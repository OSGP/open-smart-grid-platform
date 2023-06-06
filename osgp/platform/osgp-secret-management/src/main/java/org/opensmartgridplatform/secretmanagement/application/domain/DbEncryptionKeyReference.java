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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;

@Entity
@Table(name = "encryption_key_reference")
@Getter
@Setter
public class DbEncryptionKeyReference {
  @Id
  @SequenceGenerator(
      name = "encryption_key_seq_gen",
      sequenceName = "encryption_key_reference_id_seq",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "encryption_key_seq_gen")
  private Long id;

  private Date creationTime;
  private Date modificationTime;
  private Long version; // for optimistic locking

  @Enumerated(EnumType.STRING)
  private EncryptionProviderType encryptionProviderType;

  private String reference;
  private Date validFrom;
  private Date validTo;
  private String modifiedBy;
}
