package com.alliander.osgp.platform.cucumber.test;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.platform.cucumber.support.ResponseNotifier;
import com.alliander.osgp.platform.cucumber.support.ResponseNotifierImpl;

public class ResponseNotifierTest {

    @Test
    public void test() {
        ResponseNotifier notifier = new ResponseNotifierImpl();
        boolean result = notifier.isResponseAvailable("dummy");
        Assert.assertFalse(result);
    }

}
