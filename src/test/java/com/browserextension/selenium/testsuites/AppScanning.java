package com.browserextension.selenium.testsuites;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import com.uid.common.config.TestAccountManager;
import com.uid.common.config.WebdriverManager;
import com.uid.common.utils.*;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import com.browserextension.selenium.testcases.FixBrokenData;
import com.uid.common.config.Setup;

public class AppScanning
{
    private static Logger log = Logger.getLogger(AppScanning.class.getName());
    private String startItem;
    private boolean isTest = false;

    // Initialize TestSuiteBase variable
    public AppScanning()
    {
        Setup.setMode("new");
        Setup.setMaxRetryCount(0);
    }

    // ============================================================================
    // [DATAPROVIDER]: |
    // ============================================================================
    @DataProvider(name = "PV Training data")
    public Object[][] applicationData() throws JSONException, IOException
    {
        // Import CSV
        List<String[]> csvImport = CsvUtils.importCsv(new FileInputStream(Setup.getDataFilesPath()));

        // Verify and eliminate invalid training data
        return CsvUtils.validateCsvAndOutputInvalidRows(csvImport,
                Reporter.CSV_PROBLEM_APP_EXPORT_LOCATION);

    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws IOException
    {
        //Setup test environment
        Setup.initTestEnv();
        FileUtil.downloadExtension();

        // Check report
        String exportLocation = Reporter.REPORT_LOCATION + "BE_Result.csv";
        List<String[]> currentResultItems = CsvUtils.importCsv(new FileInputStream(exportLocation));
        List<String[]> dataItems = CsvUtils.importCsv(new FileInputStream(Setup.getDataFilesPath()));
        startItem = dataItems.get(0)[0];
        if (!currentResultItems.isEmpty()
                && !currentResultItems.get(currentResultItems.size()-1)[0].equalsIgnoreCase(dataItems.get(dataItems.size()-1)[0]))
        {
            Reporter.setCsvBETestResult(currentResultItems);
            startItem = dataItems.get(currentResultItems.size())[0];
        }

        WebdriverManager.setBrowserDriver(Setup.getBrowserType(), true);
        WebdriverManager.setErrorBuffer();
        WebdriverManager.setErrorExpectedFalse();

        if (TestAccountManager.getNumberOfLoadedAcc() == 0)
        {
            TestAccountManager.loadTestAccounts();
        }
        TestAccountManager.set();

        log.info("Browser: " + Setup.getBrowserName() + " " + Setup.getBrowserVersion());
        System.setProperty("org.uncommons.reportng.title",
                "Automation Results" + " <font size=2 color=\"purple\">" + "<br>Machine: "
                        + Setup.getWebRuntime() + "<br> Browser: " + Setup.getBrowserType().toUpperCase()
                        + "<br> Test Env: " + Setup.getEnvironment().toUpperCase() + "<br> Test type: "
                        + Setup.getTestType().toUpperCase() + "<br> BE Version: " + Setup.getBEVersion()
                        + "<br> BE User: " + Setup.getBeUser() + "</font>");
    }

    @BeforeMethod(alwaysRun = true)
    public static void beforeMethod(Method method)
    {
        Setup.setTestName(method.getName());
    }

    @Test(dataProvider = "PV Training data")
    public void testVerifyBEOnEnabledApps(ApplicationData appData)
    {
        if (startItem != null && startItem.equalsIgnoreCase(appData.getAppName()))
        {
            isTest = true;
        }

        if (isTest)
        {
            // Get localThread driver
            WebDriver driver = WebdriverManager.get();

            // Start Test
            FixBrokenData.testLearnPlayAppInCatalog(driver, appData);
        }
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() throws IOException
    {
        // Export result
        if (!Reporter.getCsvBETestResult().isEmpty())
        {
            String exportLocation = Reporter.REPORT_LOCATION + "BE_Result.csv";
            CsvUtils.exportCsv(Reporter.getCsvBETestResult(), exportLocation);
        }

        WebdriverManager.get().quit();
        TestAccountManager.reset();
    }

}
