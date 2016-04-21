package com.alliander.osgp.platform.cucumber;

import org.springframework.stereotype.Component;

@Component
public class SpringInjection {
    public String sayHello(final String input) {
        return "Hello Spring!" + input;
    }
}
