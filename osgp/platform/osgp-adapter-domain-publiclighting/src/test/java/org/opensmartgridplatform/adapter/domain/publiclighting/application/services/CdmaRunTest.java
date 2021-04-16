/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.mapping.DomainPublicLightingMapper;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaMastSegment;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaRun;
import org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;
import org.opensmartgridplatform.domain.core.valueobjects.TransitionType;

@ExtendWith(MockitoExtension.class)
public class CdmaRunTest {

  @Mock private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

  @Mock private SsldRepository ssldRepository;

  @Mock private DomainPublicLightingMapper domainCoreMapper;

  private final InetAddress loopbackAddress = InetAddress.getLoopbackAddress();

  private static final String IP_ADDRESS = "127.0.0.1";
  private static final String SET_TRANSITION = "SET_TRANSITION";

  @InjectMocks
  private SetTransitionService service =
      new SetTransitionService(Executors.newScheduledThreadPool(1), 15);

  @Test
  public void empty() {
    final CdmaRun run = new CdmaRun();
    assertThat(run.getMastSegmentIterator().hasNext())
        .withFailMessage("Empty CdmaRun iterator should not have items")
        .isFalse();

    this.service.setTransitionForCdmaRun(
        run, "LianderNetManagement", "zero-devices-cdma-run-test", TransitionType.DAY_NIGHT);
    this.verifySentMessages(0);
  }

  @Test
  public void itemPerMastSegment() {
    final CdmaRun run = new CdmaRun();
    for (int i = 0; i < 5; i++) {
      run.add(new CdmaDevice("cd" + i, this.loopbackAddress, "200/" + i, (short) 1));
    }

    final Iterator<CdmaMastSegment> iterator = run.getMastSegmentIterator();
    for (int i = 0; i < 5; i++) {
      iterator.next();
    }

    assertThat(iterator.hasNext())
        .withFailMessage("Iterator should not have any items left")
        .isFalse();
  }

  @Test
  public void oneDevice() {
    final CdmaRun run = new CdmaRun();
    run.add(new CdmaDevice("cd1", this.loopbackAddress, "200/1", (short) 1));
    this.service.setTransitionForCdmaRun(
        run, "LianderNetManagement", "one-device-cdma-run-test", TransitionType.NIGHT_DAY);
    this.verifySentMessages(1);
  }

  @Test
  public void tenDevices() {
    final CdmaRun run = new CdmaRun();
    for (int i = 0; i < 10; i++) {
      run.add(new CdmaDevice("cd" + i, this.loopbackAddress, "200/1", (short) 1));
    }

    this.service.setTransitionForCdmaRun(
        run, "LianderNetManagement", "ten-devices-cdma-run-test", TransitionType.DAY_NIGHT);
    this.verifySentMessages(10);
  }

  @Test
  public void twoBatches() {
    // The CDMA run will contain two batches for mast segment 200/1.
    // Within the timeout of 1000 milliseconds, only the send for device cd1
    // should take place.
    final CdmaRun run = new CdmaRun();
    run.add(new CdmaDevice("cd1", this.loopbackAddress, "200/1", (short) 1));
    run.add(new CdmaDevice("cd2", this.loopbackAddress, "200/1", (short) 2));

    this.service.setTransitionForCdmaRun(
        run, "LianderNetManagement", "two-batches-cdma-run-test", TransitionType.NIGHT_DAY);
    this.verifySentMessages(1);
  }

  @Test
  public void threeBatchesNoDelay() {
    // Set a delay of 0 seconds for this scenario
    this.service = new SetTransitionService(Executors.newScheduledThreadPool(1), 0);
    MockitoAnnotations.initMocks(this);

    final CdmaRun run = new CdmaRun();
    for (short i = 0; i < 3; i++) {
      run.add(new CdmaDevice("cd" + i, this.loopbackAddress, "200/1", i));
    }

    this.service.setTransitionForCdmaRun(
        run, "LianderNetManagement", "ten-batches-cdma-run-test", TransitionType.DAY_NIGHT);
    this.verifySentMessages(3);
  }

  private void verifySentMessages(final int noOfCalls) {
    // Verify the number of calls to the method, which sends out an ActiveMQ
    // message
    Mockito.verify(this.osgpCoreRequestMessageSender, Mockito.timeout(1000).times(noOfCalls))
        .send(Mockito.any(), eq(SET_TRANSITION), eq(0), eq(IP_ADDRESS));
  }
}
