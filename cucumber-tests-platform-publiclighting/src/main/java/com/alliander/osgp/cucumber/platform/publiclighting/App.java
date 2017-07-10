/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.publiclighting;

import com.alliander.osgp.cucumber.execution.AppBase;

public class App extends AppBase {

    public static void main(final String[] args) throws Throwable {

        final String[] testClasses = { "com.alliander.osgp.cucumber.platform.publiclighting.AcceptanceTests" };
        final App app = new App();
        System.exit(AppBase.run(app, testClasses, args));

    }
}
