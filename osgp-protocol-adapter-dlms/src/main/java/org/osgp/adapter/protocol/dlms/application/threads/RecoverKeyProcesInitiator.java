package org.osgp.adapter.protocol.dlms.application.threads;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;

public class RecoverKeyProcesInitiator {
    private ScheduledExecutorService executorService;

    private Provider<RecoverKeyProcess> recoverKeyProcesProvider;

    private int recoverKeyDelay;

    public RecoverKeyProcesInitiator(final ScheduledExecutorService executorService,
            final Provider<RecoverKeyProcess> recoverKeyProcessProvider, final int recoverKeyDelay) {
        this.executorService = executorService;
        this.recoverKeyProcesProvider = recoverKeyProcessProvider;
        this.recoverKeyDelay = recoverKeyDelay;
    }

    public void initiate(final String deviceIdentification, final String ipAddress) {
        final RecoverKeyProcess process = this.recoverKeyProcesProvider.get();
        process.setDeviceIdentification(deviceIdentification);
        process.setIpAddress(ipAddress);
        this.executorService.schedule(process, this.recoverKeyDelay, TimeUnit.MILLISECONDS);
    }

}
