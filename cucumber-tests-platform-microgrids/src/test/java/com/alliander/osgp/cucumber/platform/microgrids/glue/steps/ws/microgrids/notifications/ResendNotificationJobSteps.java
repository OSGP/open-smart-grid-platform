/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.glue.steps.ws.microgrids.notifications;

import cucumber.api.java.en.When;

public class ResendNotificationJobSteps {

    @When("^OSGP checks for which response data a notification has to be resend$")
    public void osgpChecksForWhichResponseDataANotificationHasToBeResend() throws Throwable {
        /*
         * It would probably be nice if the implementation of this step could
         * check that the execution of the ResendNotificationJob was triggered
         * during this test.
         *
         * Until a good way to check this has been built, this step will just do
         * nothing. A consequence of this is that any checks done later in a
         * scenario that depend on the job having been executed will need to
         * take into account some waiting.
         *
         * While doing the waiting here to keep following steps unburdened with
         * the knowledge of the ResendNotificationJobs scheduling might be
         * cleaner from a theoretical viewpoint, the practical benefit of faster
         * tests is given priority.
         */
    }
}
