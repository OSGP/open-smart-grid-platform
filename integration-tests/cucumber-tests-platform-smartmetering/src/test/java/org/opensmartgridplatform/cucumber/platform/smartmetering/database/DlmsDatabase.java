// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.database;

import java.util.Date;
import java.util.TimeZone;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptionKeyRepository;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** DLMS related database steps. */
@Component
public class DlmsDatabase {

  @Autowired private DlmsDeviceRepository dlmsDeviceRepo;

  @Autowired private DbEncryptionKeyRepository encryptionKeyRepository;

  @Autowired private DbEncryptedSecretRepository secretRepository;

  /** Before each scenario dlms related stuff needs to be removed. */
  @Transactional(transactionManager = "txMgrDlms")
  public void prepareDatabaseForScenario() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    this.dlmsDeviceRepo.deleteAllInBatch();

    this.secretRepository.deleteAllInBatch();
    if (this.encryptionKeyRepository.count() == 0) {
      final DbEncryptionKeyReference jreEncryptionKey = this.getJreEncryptionKey(new Date());
      this.encryptionKeyRepository.save(jreEncryptionKey);
    }
  }

  private DbEncryptionKeyReference getJreEncryptionKey(final Date now) {
    final DbEncryptionKeyReference jreEncryptionKey = new DbEncryptionKeyReference();
    jreEncryptionKey.setEncryptionProviderType(EncryptionProviderType.JRE);
    jreEncryptionKey.setReference("1");
    jreEncryptionKey.setValidFrom(now);
    jreEncryptionKey.setCreationTime(now);
    jreEncryptionKey.setModificationTime(now);
    jreEncryptionKey.setModifiedBy("DlmsDatabase (Cucumber)");
    jreEncryptionKey.setVersion(1L);
    return jreEncryptionKey;
  }
}
