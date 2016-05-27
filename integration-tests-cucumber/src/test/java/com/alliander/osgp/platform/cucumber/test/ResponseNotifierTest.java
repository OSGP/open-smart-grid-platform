package com.alliander.osgp.platform.cucumber.test;

import org.junit.Assert;
import org.junit.Ignore;

import com.alliander.osgp.platform.cucumber.support.ResponseNotifier;
import com.alliander.osgp.platform.cucumber.support.ResponseNotifierImpl;

public class ResponseNotifierTest {

    /*
     * You can run this successfully, if you insert a valid correlid !!! 
     */
    @Ignore
    public void testConnection() {
        ResponseNotifier notifier = new ResponseNotifierImpl();
        boolean result = notifier.waitForResponse("dummy", 500, 5);
        Assert.assertFalse(result);
    }

    @Ignore
    public void testExistingCorrelId() {
        ResponseNotifier notifier = new ResponseNotifierImpl();
        boolean result = notifier.waitForResponse("LianderNetManagement|||EXXXX001692675614|||20160523100933708", 500, 5);
        Assert.assertTrue(result);
    }
    
    
}
