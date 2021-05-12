/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.scheduling;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

@ExtendWith(MockitoExtension.class)
class OsgpSchedulerTest {

  @Mock private Scheduler quartzScheduler;

  @InjectMocks private OsgpScheduler osgpScheduler;

  @Test
  void testClear() throws SchedulerException {
    this.osgpScheduler.clear();
    verify(this.quartzScheduler).clear();
  }

  @Test
  void testShutdown() throws SchedulerException {
    this.osgpScheduler.shutdown();
    verify(this.quartzScheduler).shutdown(true);
    // Clearing on shutdown causes issues when deploying in Kubernetes
    verify(this.quartzScheduler, never()).clear();
  }
}
