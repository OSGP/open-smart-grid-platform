package com.alliander.osgp.domain.core.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class CorrelationIdProviderService {

    public String getCorrelationId(final String organisationIdentification, final String deviceIdentification) {

        final String correlationId = organisationIdentification + "|||" + deviceIdentification + "|||"
                + this.getCurrentDateString();

        return correlationId;
    }

    private String getCurrentDateString() {
        final Date now = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmssSSS");
        return sdf.format(now);
    }
}
