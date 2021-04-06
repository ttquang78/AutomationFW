package com.browserextension.selenium.testsuites;

import com.uid.common.base.TestSuiteBase;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.browserextension.selenium.testcases.BELegacyCat;
import com.browserextension.selenium.testcases.BEPrivacyKeyCat;
import com.uid.common.utils.ApplicationData;
import com.uid.common.config.Setup;
import com.uid.common.config.TestAccountManager;
import com.uid.common.config.Verification;

public class BELegacySuite extends TestSuiteBase
{
    // Initialize TestSuiteBase variable
    public BELegacySuite()
    {
        addExtension = true;
        useTestAccount = true;
        Setup.setMode("production");
        Setup.setMaxRetryCount(1);
    }

    @Test(groups = { "legacy" }, priority = 1)
    public void testCDPPrivacyKey()
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        // Start Test
        driver.get(TestAccountManager.getCDPUrl());
        BEPrivacyKeyCat.testPrivacyKey(driver);
    }

    @Test(groups = { "legacy" }, priority = 2)
    @Parameters({"appName1", "appURL1", "appData1"})
    public void testCDPLearnPlayUpdateDeleteAppCred(String appName, String appURL, String appTraining)
            throws Exception
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        // App data
        appTraining = appTraining.replace("'", "\"");
        ApplicationData appData = new ApplicationData(appName, appURL, appTraining);

        // Start Test
        driver.get(TestAccountManager.getCDPUrl());
        BELegacyCat.testLearnReplayUpdateDeleteCredential(driver, appData, false);
    }

    @Test(groups = { "legacy" }, priority = 3)
    @Parameters({"appName1", "appURL1", "appData1"})
    public void testCDPLearnPlayUpdatePK(String appName, String appURL, String appTraining)
            throws Exception
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        // App data
        appTraining = appTraining.replace("'", "\"");
        ApplicationData appData = new ApplicationData(appName, appURL, appTraining);

        // Start Test
        driver.get(TestAccountManager.getCDPUrl());
        BELegacyCat.testLearnReplayUpdatePK(driver, appData, false);
    }

    @Test(groups = { "legacy" }, priority = 4)
    @Parameters({"appName2", "appURL2", "appData2"})
    public void testCDPLearnPlayUpdateDeleteAppCredPersonal(String appName, String appURL,
            String appTraining) throws Exception
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        // App data
        appTraining = appTraining.replace("'", "\"");
        ApplicationData appData = new ApplicationData(appName, appURL, appTraining);

        // Start Test
        driver.get(TestAccountManager.getCDPUrl());
        BELegacyCat.testLearnReplayUpdateDeleteCredential(driver, appData, true);
    }

    @Test(groups = { "legacy" }, priority = 5)
    public void testCDPSignOnDlg()
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        // Start Test
        driver.get(TestAccountManager.getCDPUrl());
        BELegacyCat.testSignOnDlg(driver);
    }

    // Just run this test on FF and Chrome
    @Test(groups = { "legacy" }, priority = 6)
    public void testCDPDownloadBE()
    {
        if (Setup.getBrowserType().equalsIgnoreCase("chrome") && Setup.getTestType()
                .equalsIgnoreCase("new"))
        {
            // Get localThread driver
            WebDriver driver = getDriver();

            // Start Test
            driver.get(TestAccountManager.getCDPUrl());
            BELegacyCat.testBEInstallation(driver);
        }
        else
        {
            Verification.startCollapseHtml();  //initialize steps logging formatting
            Verification.logStep("This test is ignored on IE browser");
            Verification.endCollapseHtml(); // End steps logging formatting
        }
    }

    @Test(groups = { "legacy" }, priority = 7)
    @Parameters({"appName1", "appURL1", "appData1"})
    public void testCDPLearnCredentialAndNoSave(String appName, String appURL, String appTraining)
            throws Exception
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        // App data
        appTraining = appTraining.replace("'", "\"");
        ApplicationData appData = new ApplicationData(appName, appURL, appTraining);

        // Start Test
        driver.get(TestAccountManager.getCDPUrl());
        BELegacyCat.testLearnCredentialAndNoSave(driver, appData, false);
    }

    @Test(groups = { "legacy" }, priority = 8)
    public void testCDPUpdateInvalidPK()
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        // Start Test
        driver.get(TestAccountManager.getCDPUrl());
        BELegacyCat.testUpdateInvalidPK(driver, false);
    }

    @Test(groups = { "legacy" }, priority = 9)
    public void testCDPConfirmPrivacyKeyDialog()
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        // Start Test
        driver.get(TestAccountManager.getCDPUrl());
        BELegacyCat.testConfirmPrivacyKeyDialog(driver);
    }

}
