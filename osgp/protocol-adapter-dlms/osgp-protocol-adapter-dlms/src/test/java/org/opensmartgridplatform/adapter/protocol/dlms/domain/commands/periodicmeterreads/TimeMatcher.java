package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import org.joda.time.DateTime;
import org.mockito.ArgumentMatcher;

public class TimeMatcher extends ArgumentMatcher<DateTime> {

    private long time;

    public TimeMatcher(long time) {
        this.time = time;
    }

    @Override
    public boolean matches(Object actual) {
        return getActualTime(actual) == time;
    }

    private long getActualTime(Object actual) {
        if (actual instanceof DateTime) {
            return ((DateTime) actual).getMillis();
        } else {
            throw new IllegalArgumentException("Cannot determine time of " + actual);
        }
    }

}
