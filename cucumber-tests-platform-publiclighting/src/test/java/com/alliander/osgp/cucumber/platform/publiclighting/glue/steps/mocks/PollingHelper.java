package com.alliander.osgp.cucumber.platform.publiclighting.glue.steps.mocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;

public class PollingHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollingHelper.class);

    private static int MAX_POLLS = 3;
    private int polls = 1;

    public boolean poll(final String osgpResultType) {
        LOGGER.info("osgpResultType: {}, polls: {}", osgpResultType, this.polls);

        if (OsgpResultType.NOT_FOUND.name().equals(osgpResultType) && this.polls <= MAX_POLLS) {
            this.polls++;
            return true;
        } else {
            return false;
        }
    }

}
