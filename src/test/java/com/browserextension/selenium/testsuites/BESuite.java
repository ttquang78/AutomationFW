package com.browserextension.selenium.testsuites;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.browserextension.selenium.testcases.*;
import com.uid.common.base.CommonMessage;
import com.uid.common.base.TestSuiteBase;
import org.json.JSONException;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.uid.common.utils.ApplicationData;
import com.uid.common.utils.CsvUtils;
import com.uid.common.utils.Reporter;
import com.uid.common.config.Setup;
import com.uid.common.config.Verification;

public class BESuite extends TestSuiteBase
{
    // --------------------

    // Initialize TestSuiteBase variable
    public BESuite()
    {
        addExtension = true;
        useTestAccount = true;
        Setup.setMode("new");
        Setup.setMaxRetryCount(1);
    }

    // --------------------

    // ============================================================================
    // [DATAPROVIDER]: |
    // Specify the path to the Excel file, the sheet name and the data table
    // name|

    @DataProvider(name = "Training data for training manually")
    public Object[][] applicationDataForTrainingManually() throws JSONException, IOException
    {
        // Import CSV
        List<String[]> csvImport =
                CsvUtils.importCsv(new FileInputStream(Setup.getTrainingDataForTrainingManually()));

        // Verify and eliminate invalid training data
        return CsvUtils.validateCsvAndOutputInvalidRows(csvImport,
                Reporter.CSV_PROBLEM_APP_EXPORT_LOCATION);
    }

    @DataProvider(name = "Training data for learning FD")
    public Object[][] applicationDataForTrainingFD() throws JSONException, IOException
    {
        // Import CSV
        List<String[]> csvImport =
                CsvUtils.importCsv(new FileInputStream(Setup.getTrainingDataForTrainingFD()));

        // Verify and eliminate invalid training data
        return CsvUtils.validateCsvAndOutputInvalidRows(csvImport,
                Reporter.CSV_PROBLEM_APP_EXPORT_LOCATION);
    }

    @DataProvider(name = "Personal app training data")
    public Object[][] personalAppTrainingData() throws JSONException, IOException
    {
        // Import CSV
        List<String[]> csvImport = CsvUtils.importCsv(new FileInputStream(Setup.getPersonalAppData()));

        // Verify and eliminate invalid training data
        return CsvUtils.validateCsvAndOutputInvalidRows(csvImport,
                Reporter.CSV_PROBLEM_APP_EXPORT_LOCATION);
    }

    @DataProvider(name = "Test Training data")
    public Object[][] trainingData() throws JSONException, IOException
    {
        // Import CSV
        List<String[]> csvImport = CsvUtils.importCsv(new FileInputStream("DataFiles/TrainingData.csv"));

        // Verify and eliminate invalid training data
        return CsvUtils.validateCsvAndOutputInvalidRows(csvImport,
                Reporter.CSV_PROBLEM_APP_EXPORT_LOCATION);
    }

    // ============================================================================

    // =======================================================================================
    // TEST SET |
    // =======================================================================================

    // ------

    @Test(groups = { "sanity", "regression" }, priority = 1)
	public void testCDPLoginCDPByUserWithPKAndPlayApp() throws MalformedURLException
    {
        if (!Setup.getTestType().equalsIgnoreCase(CommonMessage.UPGRADE_STR))
        {
            // Get localThread driver
            WebDriver driver = getDriver();

            NewBEPrivacyKeyCat.testLoginCDPByUserWithPK(driver);
        }
        else
        {
            // formatting
            Verification.logStep(CommonMessage.TEST_IGNORE_ON_UPGRADE_STR);
            Verification.endCollapseHtml(); // End steps logging formatting
        }
    }

    @Test(groups = { "sanity", "regression" }, priority = 2)
	public void testCDPLoginCDPByUserWithoutPKAndPlayApp() throws MalformedURLException
    {
        if (!Setup.getTestType().equalsIgnoreCase(CommonMessage.UPGRADE_STR))
        {
            // Get localThread driver
            WebDriver driver = getDriver();

            NewBEPrivacyKeyCat.testCreateNewPKAndPlayApp(driver, "PingOne2");
        }
        else
        {
            Verification.logStep(CommonMessage.TEST_IGNORE_ON_UPGRADE_STR);
            Verification.endCollapseHtml();
        }
    }

    @Test(groups = { "regression" }, priority = 3)
	public void testCDPCreatePKWithSpecialLetters() throws MalformedURLException
    {
        if (!Setup.getTestType().equalsIgnoreCase(CommonMessage.UPGRADE_STR))
        {
            // Get localThread driver
            WebDriver driver = getDriver();

            NewBEPrivacyKeyCat.testCreateNewPKAndPlayApp(driver, "(Pi#$%^&**()_+<scritpts>1)");
        }
        else
        {
            Verification.logStep(CommonMessage.TEST_IGNORE_ON_UPGRADE_STR);
            Verification.endCollapseHtml();
        }
    }

    @Test(groups = { "regression" }, priority = 4)
	public void testCDPCreatePKDialogUI() throws Exception
    {
        if (!Setup.getTestType().equalsIgnoreCase(CommonMessage.UPGRADE_STR))
        {
            // Get localThread driver
            WebDriver driver = getDriver();
            NewBEPrivacyKeyCat.testCreatePKUI(driver, "PingOne2");
        }
        else
        {
            // formatting
            Verification.logStep(CommonMessage.TEST_IGNORE_ON_UPGRADE_STR);
            Verification.endCollapseHtml(); // End steps logging formatting
        }
    }

    @Test(groups = { "sanity", "regression", "CDPSanity" }, priority = 6)
	public void testCDPLearnPlayUpdatePKUpdateDeleteAppCred()
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        NewBEManagePasswordCat.testLearnReplayUpdatePKUpdateDeleteCredential(driver);
    }

    @Test(groups = { "sanity", "regression", "CDPSanity" }, priority = 7)
    public void testCDPLearnPlayOnOrganizationApp()
    {
        WebDriver driver = getDriver();
        NewBELearnPlayCat.testLearnReplayApp(driver);
    }

    @Test(groups = { "sanity", "regression", "CDPSanity" }, priority = 7, dataProvider = "personalAppTrainingData")
    public void testCDPLearnPlayOnPersonalApp(ApplicationData appData)
    {
        WebDriver driver = getDriver();
        NewBELearnPlayCat.testLearnReplayApp(driver, appData);
    }

    @Test(groups = { "sanity", "regression", "training" }, priority = 8, dataProvider = "applicationDataForTrainingFD")
    public void testTrainingAppByFD(ApplicationData appData)
    {
        // Get localThread driver
        WebDriver driver = getDriver();
        NewBETrainingCat.testTrainingAppByFD(driver, appData);
    }

    @Test(groups = { "sanity", "regression" }, priority = 9, dataProvider = "applicationDataForTrainingManually")
    public void testTrainingApp2FieldsByManual(ApplicationData appData)
    {
        // Get localThread driver
        WebDriver driver = getDriver();
        NewBETrainingCat.testTrainingApp2FieldsByManual(driver, appData);
    }

    @Test(groups = { "regression" }, priority = 9)
    @Parameters({"appName4", "appURL4", "appData4"})
    public void testTrainingAppWhenFailedFD(String appName, String appURL, String appTraining)
            throws JSONException
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        // App data
        appTraining = appTraining.replace("'", "\"");
        ApplicationData appData = new ApplicationData(appName, appURL, appTraining);

        NewBETrainingCat.testTrainingAppWhenFailedFD(driver, appData);
    }

    @Test(groups = { "regression" }, priority = 9)
    @Parameters({"appName1", "appURL1", "appData1"})
    public void testReTrainingAppAfterManualTrain(String appName, String appURL, String appTraining)
            throws JSONException
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        // App data
        appTraining = appTraining.replace("'", "\"");
        ApplicationData appData = new ApplicationData(appName, appURL, appTraining);

        NewBETrainingCat.testReTrainingAppAfterManualTrain(driver, appData);
    }

    @Test(groups = { "regression" }, priority = 9)
    @Parameters({"appName1", "appURL1", "appData1"})
    public void testAccessHelpPopupAndRefreshPageAndCancelTraining(String appName, String appURL, String appTraining)
            throws JSONException
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        // App data
        appTraining = appTraining.replace("'", "\"");
        ApplicationData appData = new ApplicationData(appName, appURL, appTraining);

        NewBETrainingCat.testAccessHelpPopupAndRefreshPageAndCancelTraining(driver, appData);
    }

    @Test(groups = { "regression" }, priority = 11, dataProvider = "Test Training data")
	public void testCDPChangeConfiguration(ApplicationData appData)
    {
        // Get localThread driver
        WebDriver driver = getDriver();
        NewBETrainingCat.testVerifyTrainingConfiguration(driver, appData);
    }

    @Test(groups = { "sanity", "regression" }, priority = 12)
	public void testCDPDownloadBEWithInstalledBE()
    {
        // Get localThread driver
        WebDriver driver = getDriver();
        BEInstallationCat.testBEInstallationPopupWithInstalledBE(driver);
    }

    @Test(groups = { "regression" }, priority = 13)
    public void testCDPInputCredDlg()
    {
        // Get localThread driver
        WebDriver driver = getDriver();
        NewBEManagePasswordCat.testInputCredDlg(driver);
    }

    @Test(groups = { "regression" }, priority = 13)
    public void testUpdatePKDlg()
    {
        // Get localThread driver
        WebDriver driver = getDriver();
        NewBEManagePasswordCat.testUpdatePKDlg(driver);
    }

    @Test(groups = { "regression" }, priority = 13)
    public void testUpdateAppCredDlg()
    {
        // Get localThread driver
        WebDriver driver = getDriver();
        NewBEManagePasswordCat.testUpdateAppCredDlg(driver);
    }

    @Test(groups = { "regression" }, enabled = false, priority = 14) //This test is obsoleted
    public void testCDPSignOnDlg()
    {
        WebDriver driver = getDriver();
        NewBEManagePasswordCat.testSignOnDlg(driver);
    }

    @Test(groups = { "regression" }, priority = 15)
	public void testCDPManagePasswordSwitchMode()
    {
        if (!Setup.getBrowserType().equalsIgnoreCase("firefox"))
        {
            // Get localThread driver
            WebDriver driver = getDriver();
            CDPCat.testSwitchMode(driver);
        }
        else
        {
            Verification.logStep(CommonMessage.TEST_IGNORE_ON_FIREFOX_STR);
            Verification.endCollapseHtml(); // End steps logging formatting
        }
    }

    @Test(groups = { "sanity", "regression" }, priority = 16)
	public void testCDPChangePKAndReopenBrowser() throws Exception
    {
        // Get localThread driver
        WebDriver driver = getDriver();
        NewBEPrivacyKeyCat.testChangePKAndReopenBrowser(driver);
    }

    @Test(groups = { "regression" }, priority = 17)
    public void testCDPChangePKOnAnotherBrowserPlayAppWithCred() throws MalformedURLException
    {
        // Get localThread driver
        WebDriver driver = getDriver();
        NewBEPrivacyKeyCat.testChangePKOnAnotherBrowserPlayApp(driver, true);
    }

    @Test(groups = { "regression" }, priority = 18)
    public void testCDPChangePKOnAnotherBrowserPlayAppWithoutCred() throws MalformedURLException
    {
        // Get localThread driver
        WebDriver driver = getDriver();
        NewBEPrivacyKeyCat.testChangePKOnAnotherBrowserPlayApp(driver, false);
    }

    @Test(groups = { "regression" }, priority = 19)
	public void testCDPVerifyPKWith2Accounts()
    {
        // Get localThread driver
        WebDriver driver = getDriver();
        NewBEPrivacyKeyCat.testVerifyPKWith2Accounts(driver);
    }

    @Test(groups = { "regression" }, priority = 20)
    @Parameters({"appName1", "appURL1", "appFDData1"})
	public void testFDCanDetectFormFieldsOnInactiveWindow(String appName, String appURL, String appTraining) throws Exception
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        // App data
        appTraining = appTraining.replace("'", "\"");
        ApplicationData appData = new ApplicationData(appName, appURL, appTraining);

        NewBELearnPlayCat.testFDCanDetectFormFieldsOnInactiveWindow(driver, appData);
    }

    @Test(groups = { "sanity", "regression" }, priority = 21)
    @Parameters({"appName1", "appURL1", "appFDData1", "appName3", "appURL3", "appFDData3"})
    public void testFDCanDetectFormFieldsForMultiApps(String appName1, String appURL1,
            String appTraining1, String appName2, String appURL2, String appTraining2) throws Exception
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        appTraining1 = appTraining1.replace("'", "\"");
        ApplicationData appData1 = new ApplicationData(appName1, appURL1, appTraining1);
        appTraining2 = appTraining2.replace("'", "\"");
        ApplicationData appData2 = new ApplicationData(appName2, appURL2, appTraining2);

        List<ApplicationData> appDataList = new ArrayList<>();
        appDataList.add(appData1);
        appDataList.add(appData2);

        NewBELearnPlayCat.testFDCanDetectFormFieldsForMultiApps(driver, appDataList);
    }

    @Test(groups = { "regression" }, priority = 22)
    @Parameters({"appName1", "appURL1", "appData1"})
    public void testTrainingExistingApp(String appName, String appURL, String appTraining) throws Exception
    {
        // Get localThread driver
        WebDriver driver = getDriver();

        // App data
        appTraining = appTraining.replace("'", "\"");
        ApplicationData appData = new ApplicationData(appName, appURL, appTraining);

        NewBETrainingCat.testTrainingExistingApp(driver, appData);
    }

    @Test(groups = { "regression" }, priority = 24)
    @Parameters({"appName1", "appURL1", "appData1"})
	public void testDragRibbonWhileTraining(String appName, String appURL, String appTraining) throws Exception
    {
        if (!Setup.getBrowserType().equalsIgnoreCase("firefox"))
        {
            // Get localThread driver
            WebDriver driver = getDriver();

            // App data
            appTraining = appTraining.replace("'", "\"");
            ApplicationData appData = new ApplicationData(appName, appURL, appTraining);

            // Start Test
            NewBETrainingCat.testDragRibbonWhileTraining(driver, appData);
        }
        else
        {
            Verification.logStep("This test is ignored on Firefox becuase issue with gecko driver");
            Verification.endCollapseHtml(); // End steps logging formatting
        }
    }

    @Test(groups = { "regression" }, priority = 25)
	public void testConfirmPrivacyKeyDialog()
    {
        // Ger localThread driver
        WebDriver driver = getDriver();
        NewBEManagePasswordCat.testConfirmPrivacyKeyDialog(driver);
    }

    @Test(groups = { "regression" }, priority = 26)
    @Parameters({"appName1", "appURL1", "appData1", "appName3", "appURL3", "appData3"})
    public void testLearnPlayDeletedApps(String appName1, String appURL1, String appTraining1,
            String appName2, String appURL2, String appTraining2) throws Exception
    {
        // Ger localThread driver
        WebDriver driver = getDriver();

        appTraining1 = appTraining1.replace("'", "\"");
        ApplicationData appData1 = new ApplicationData(appName1, appURL1, appTraining1);
        appTraining2 = appTraining2.replace("'", "\"");
        ApplicationData appData2 = new ApplicationData(appName2, appURL2, appTraining2);

        List<ApplicationData> appDataList = new ArrayList<>();
        appDataList.add(appData1);
        appDataList.add(appData2);

        NewBELearnPlayCat.testLearnPlayDeletedApps(driver, appDataList);
    }

    @Test(groups = { "regression" }, priority = 26)
    @Parameters({"appName1", "appURL1", "appFDData1"})
	public void testPastAppLink(String appName, String appURL, String appTraining) throws JSONException
    {
        // Ger localThread driver
        WebDriver driver = getDriver();

        // App data
        appTraining = appTraining.replace("'", "\"");
        ApplicationData appData = new ApplicationData(appName, appURL, appTraining);

        NewBELearnPlayCat.testPasteAppLink(driver, appData);
    }

    // END TESTSUITE
}
