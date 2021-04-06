package com.browserextension.selenium.testsuites;

import com.uid.common.base.TestSuiteBase;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import com.browserextension.selenium.testcases.BEInstallationCat;
import com.uid.common.config.Setup;
import com.uid.common.config.TestAccountManager;

public class BEInstallationSuite
        extends TestSuiteBase
{
    public BEInstallationSuite()
    {
        this.addExtension = false;
        this.useTestAccount = true;
        Setup.setMode("new");
    }

    @Test(enabled = false, priority = 1) public void testCDPSikuliInstallBEOnChrome()
    {
        //Get localThread driver
        WebDriver driver = getDriver();

        //Start Test
        driver.get(TestAccountManager.getCDPUrl());
        BEInstallationCat.testInstallBEOnChrome(driver);
    }

    @Test(enabled = false, priority = 2) public void testCDPSikuliInstallBEOnFirefox()
    {
        //Get localThread driver
        WebDriver driver = getDriver();

        //Start Test
        driver.get(TestAccountManager.getCDPUrl());
        BEInstallationCat.testInstallBEOnFF(driver);
    }

    @Test(enabled = false, priority = 3) public void testCDPSikuliInstallBEOnIE() throws Exception
    {
        //Get localThread driver
        WebDriver driver = getDriver();

        //Start Test
        driver.get(TestAccountManager.getCDPUrl());
        BEInstallationCat.testInstallBEOnIE(driver);
    }

    @Test(enabled = true, priority = 3) public void testCDPSikuliReplayOpenLinkInNewTab()
    {
        //Get localThread driver
        WebDriver driver = getDriver();

        //Start Test
        driver.get(TestAccountManager.getCDPUrl());
        BEInstallationCat.testReplayOpenLinkInNewTab(driver);
    }

}
