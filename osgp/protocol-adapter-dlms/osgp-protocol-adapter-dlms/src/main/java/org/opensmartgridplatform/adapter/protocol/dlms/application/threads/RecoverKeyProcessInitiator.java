// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.threads;

import jakarta.inject.Provider;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

public class RecoverKeyProcessInitiator {
  private final ScheduledExecutorService executorService;

  private final Provider<RecoverKeyProcess> recoverKeyProcessProvider;

  private final int recoverKeyDelay;

  public RecoverKeyProcessInitiator(
      final ScheduledExecutorService executorService,
      final Provider<RecoverKeyProcess> recoverKeyProcessProvider,
      final int recoverKeyDelay) {
    this.executorService = executorService;
    this.recoverKeyProcessProvider = recoverKeyProcessProvider;
    this.recoverKeyDelay = recoverKeyDelay;
  }

  public void initiate(final MessageMetadata messageMetadata, final String deviceIdentification) {
    final RecoverKeyProcess process = this.recoverKeyProcessProvider.get();
    process.setMessageMetadata(messageMetadata);
    process.setDeviceIdentification(deviceIdentification);
    this.executorService.schedule(process, this.recoverKeyDelay, TimeUnit.MILLISECONDS);
  }
}
