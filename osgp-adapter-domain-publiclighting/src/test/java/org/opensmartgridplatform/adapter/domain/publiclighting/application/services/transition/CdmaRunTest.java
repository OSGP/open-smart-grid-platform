/**

 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.services.transition;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.mapping.DomainPublicLightingMapper;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaMastSegment;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaRun;
import org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;
import org.opensmartgridplatform.domain.core.valueobjects.TransitionType;

@RunWith(MockitoJUnitRunner.class)
public class CdmaRunTest {

    @Mock
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Mock
    private SsldRepository ssldRepository;

    @Mock
    private DomainPublicLightingMapper domainCoreMapper;

    private final InetAddress loopbackAddress = InetAddress.getLoopbackAddress();

    private static final String IP_ADDRESS = "127.0.0.1";
    private static final String SET_TRANSITION = "SET_TRANSITION";

    @InjectMocks
    private SetTransitionService service = new SetTransitionService(Executors.newScheduledThreadPool(1), 15);

    @Test
    public void emptyCdmaRun() {
        final CdmaRun run = new CdmaRun();
        Assert.assertFalse("Empty CdmaRun iterator should not have items", run.getMastSegmentIterator().hasNext());

        this.service.transitionCdmaRun(run, "LianderNetManagement", "zero-devices-cdma-run-test",
                TransitionType.DAY_NIGHT);
        this.verifySentMessages(0);
    }

    @Test
    public void itemPerMastSegment() {
        final CdmaRun run = new CdmaRun();
        for (int i = 0; i < 5; i++) {
            run.add(new CdmaDevice("cd" + i, this.loopbackAddress, "200/" + i, (short) 1));
        }

        final Iterator<CdmaMastSegment> iterator = run.getMastSegmentIterator();
        try {
            for (int i = 0; i < 5; i++) {
                iterator.next();
            }
        } catch (final Exception e) {
            Assert.fail("Iterating the mast segments failed");
        }
        Assert.assertFalse("Iterator should not have any items left", iterator.hasNext());
    }

    @Test
    public void oneDeviceCdmaRun() {
        final CdmaRun run = new CdmaRun();
        run.add(new CdmaDevice("cd1", this.loopbackAddress, "200/1", (short) 1));
        this.service.transitionCdmaRun(run, "LianderNetManagement", "one-device-cdma-run-test",
                TransitionType.NIGHT_DAY);
        this.verifySentMessages(1);
    }

    @Test
    public void tenDevicesCdmaRun() {
        final CdmaRun run = new CdmaRun();
        for (int i = 0; i < 10; i++) {
            run.add(new CdmaDevice("cd" + i, this.loopbackAddress, "200/1", (short) 1));
        }

        this.service.transitionCdmaRun(run, "LianderNetManagement", "ten-devices-cdma-run-test",
                TransitionType.DAY_NIGHT);
        this.verifySentMessages(10);
    }

    @Test
    public void twoBatchesCdmaRun() {
        // The CDMA run will contain two batches for mast segment 200/1.
        // Within the timeout of 1000 milliseconds, only the send for device cd1
        // should take place.
        final CdmaRun run = new CdmaRun();
        run.add(new CdmaDevice("cd1", this.loopbackAddress, "200/1", (short) 1));
        run.add(new CdmaDevice("cd2", this.loopbackAddress, "200/1", (short) 2));

        this.service.transitionCdmaRun(run, "LianderNetManagement", "two-batches-cdma-run-test",
                TransitionType.NIGHT_DAY);
        this.verifySentMessages(1);
    }

    @Test
    public void threeBatchesNoDelayCdmaRun() {
        // Set a delay of 0 seconds for this scenario
        this.service = new SetTransitionService(Executors.newScheduledThreadPool(1), 0);
        MockitoAnnotations.initMocks(this);

        final CdmaRun run = new CdmaRun();
        for (short i = 0; i < 3; i++) {
            run.add(new CdmaDevice("cd" + i, this.loopbackAddress, "200/1", i));
        }

        this.service.transitionCdmaRun(run, "LianderNetManagement", "ten-batches-cdma-run-test",
                TransitionType.DAY_NIGHT);
        this.verifySentMessages(3);
    }

    private void verifySentMessages(final int noOfCalls) {
        // Verify the number of calls to the method, which sends out an ActiveMQ
        // message
        Mockito.verify(this.osgpCoreRequestMessageSender, Mockito.timeout(1000).times(noOfCalls)).send(Mockito.any(),
                Mockito.eq(SET_TRANSITION), Mockito.eq(0), Mockito.eq(IP_ADDRESS));
    }
}
