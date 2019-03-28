package org.opensmartgridplatform.shared.domain.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class CorrelationProviderIdServiceV2 { // implements
                                                // CorrelationIdProviderService
                                                // {

    private static final String SEPARATOR = "|||";

    // @Override
    public String getCorrelationId(final String organisationIdentification, final String deviceIdentification) {

        return organisationIdentification + SEPARATOR + deviceIdentification + SEPARATOR + this.getCurrentDateString()
                + SEPARATOR + UUID.randomUUID();
    }

    private String getCurrentDateString() {
        final Date now = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmssSSS");
        return sdf.format(now);
    }
}
