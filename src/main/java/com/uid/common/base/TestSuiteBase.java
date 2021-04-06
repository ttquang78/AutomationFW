package com.uid.common.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.uid.common.config.Setup;
import com.uid.common.config.TestAccountManager;
import com.uid.common.utils.CsvUtils;
import com.uid.common.utils.FileUtil;
import com.uid.common.utils.SelUtil;
import com.uid.common.config.WebdriverManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

public class TestSuiteBase
{
    private static Logger log = Logger.getLogger(TestSuiteBase.class.getName());
    protected static Boolean addExtension = false;
            //Overwritten at TestSuite level to control the extension in the WebDriver options
    protected static Boolean useTestAccount = true;
            //Overwritten at TestSuite level to use the predefined test accounts

    //============================
    //        [BEFORE]           |
    //============================

    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws IOException
    {
        //Setup test environment
        Setup.initTestEnv();
        FileUtil.downloadExtension();
    }

    @BeforeMethod(alwaysRun = true)
    public static void beforeMethod(Method method) throws IOException
    {
        log.info("======================================================================");
        log.info(method.getName());
        //#-Get current test name @test
        Setup.setTestName(method.getName());
        if (Setup.getTestName().equals("testCDPDownloadBE")
                || Setup.getTestName().equals("TestCDP_DownloadBEWithoutInstalledBE"))
        {
            addExtension = false;
        }

        if (Setup.getTestName().contains("Sikuli"))
        {
            Setup.setWebRuntime("local");

            if (Setup.getTestName().equalsIgnoreCase("TestCDP_Sikuli_InstallBEOnChrome"))
            {
                Setup.setBrowserType("chrome");
            }
            else if (Setup.getTestName().equalsIgnoreCase("TestCDP_Sikuli_InstallBEOnFirefox"))
            {
                Setup.setBrowserType("firefox");
            }
            else if (Setup.getTestName().equalsIgnoreCase("TestCDP_Sikuli_InstallBEOnIE"))
            {
                Setup.setBrowserType("ie");
            }
            else
            {
                addExtension = true;
            }
        }

        //Load WebDriver
        WebdriverManager.setBrowserDriver(Setup.getBrowserType(), addExtension);

        //Load test accounts
        if (TestAccountManager.getNumberOfLoadedAcc() == 0)
        {
            TestAccountManager.loadTestAccounts();
        }

        //#-Get a free test account and set as localThread (if needed)
        if (useTestAccount)
        {
            TestAccountManager.set();
        }

        //#-Initialize error engine as localThread
        WebdriverManager.setErrorBuffer();
        WebdriverManager.setErrorExpectedFalse();
        log.info("Browser: " + Setup.getBrowserName() + " " + Setup.getBrowserVersion());
        System.setProperty("org.uncommons.reportng.title",
                "Automation Results" + " <font size=2 color=\"purple\">" + "<br>Machine: "
                        + Setup.getWebRuntime() + "<br> Browser: " + Setup.getBrowserType().toUpperCase()
                        + "<br> Test Env: " + Setup.getEnvironment().toUpperCase() + "<br> Test type: "
                        + Setup.getTestType().toUpperCase() + "<br> BE Version: " + Setup.getBEVersion()
                        + "<br> BE User: " + Setup.getBeUser() + "</font>");
    }

    //============================
    //       [METHODS]           |
    //============================

    /**
     * [GETDRIVER:]
     * /* -Returns the localThread driver
     * /*--------------------------------
     */
    public WebDriver getDriver()
    {
        return WebdriverManager.get();
    }

    //============================
    //       [AFTER]             |
    //============================

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        //Reset test account to free
        if (useTestAccount)
        {
            TestAccountManager.reset();
        }

        //Quit driver
        getDriver().quit();

        if (Setup.getBrowserType().equalsIgnoreCase("ie"))
        {
            log.info("Wait 15 seconds for dllhost process is closed");
            SelUtil.wait(15, "For IE, wait for dllhost.exe process is closed completely");
        }
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() throws IOException
    {
        String beInfoFile = "DataFiles/" + Setup.getBrowserName() + "_" + Setup.getEnvironment()
                + "_version.csv";
        List<String[]> beInfoItems = new ArrayList<>();
        String[] info1 = {Setup.getBrowserVersion()};
        String[] info2 = {Setup.getBEVersion()};
        beInfoItems.add(info1);
        beInfoItems.add(info2);
        CsvUtils.exportCsv(beInfoItems, beInfoFile);

        //Export build result
        if (Setup.isResultExisting())
        {
            String filePath = Setup.getReportFilePath();
            List<String[]> csvImport = CsvUtils.importCsv(new FileInputStream(filePath));
            List<String[]> csvExport = new ArrayList<>();

            String env = Setup.getEnvironment().toUpperCase();
            String browser = Setup.getBrowserType().toUpperCase();
            String browserVersion = Setup.getBrowserVersion().toUpperCase();
            if (env.equalsIgnoreCase("beta") && browser.equalsIgnoreCase("firefox"))
            {
                browserVersion += "beta";
            }
            String beVersion = Setup.getBEVersion().toUpperCase();

            String[] latestItem = {env, browser, browserVersion, beVersion, Setup.getFinalResult()};
            csvExport.add(latestItem);
            csvExport.addAll(csvImport);
            CsvUtils.exportCsv(csvExport, filePath);
        }
    }

    //#END CLASS
}