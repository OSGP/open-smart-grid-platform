package org.opensmartgridplatform.adapter.protocol.iec60870.testutils.matchers;

import org.mockito.ArgumentMatcher;
import org.openmuc.j60870.ASdu;

public class AsduMatcher implements ArgumentMatcher<ASdu> {
    private final ASdu expectedAsdu;

    public AsduMatcher(final ASdu expectedAsdu) {
        this.expectedAsdu = expectedAsdu;
    }

    @Override
    public boolean matches(final ASdu actualAsdu) {
        // TODO add more checks
        if (actualAsdu.getTypeIdentification() != this.expectedAsdu.getTypeIdentification()) {
            return false;
        }
        if (actualAsdu.getCauseOfTransmission() != this.expectedAsdu.getCauseOfTransmission()) {
            return false;
        }
        if (actualAsdu.getCommonAddress() != this.expectedAsdu.getCommonAddress()) {
            return false;
        }
        if (actualAsdu.getOriginatorAddress() != this.expectedAsdu.getOriginatorAddress()) {
            return false;
        }
        return true;
    }
}
