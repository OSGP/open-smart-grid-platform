package com.alliander.osgp.cucumber.platform.smartmetering;

import com.alliander.osgp.cucumber.execution.AppBase;

public class App extends AppBase {

    public static void main(final String[] args) throws Throwable {

        final String[] testClasses = { "com.alliander.osgp.cucumber.platform.smartmetering.AcceptanceTests" };
        final App app = new App();
        System.exit(AppBase.run(app, testClasses, args));

    }
}
