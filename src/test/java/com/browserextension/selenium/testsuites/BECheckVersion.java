package com.browserextension.selenium.testsuites;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import com.browserextension.selenium.testcases.NewBESharedStep;
import com.uid.common.config.Setup;
import com.uid.common.base.TestSuiteBase;

public class BECheckVersion
        extends TestSuiteBase
{
    public BECheckVersion()
    {
        addExtension = true;
        useTestAccount = true;
        Setup.setMode("new");
        Setup.setMaxRetryCount(0);
    }

    @Test
    public void testCDPCheckVersion() throws Exception
    {
        // Get localThread driver
        WebDriver driver = getDriver();
        NewBESharedStep.testVerifyVersion(driver);
    }
}
