package com.browserextension.selenium.testsuites;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import com.uid.common.base.TestSuiteBase;
import org.json.JSONException;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.browserextension.selenium.testcases.NewBELearnPlayCat;
import com.browserextension.selenium.testcases.NewBEManagePasswordCat;
import com.uid.common.utils.ApplicationData;
import com.uid.common.utils.CsvUtils;
import com.uid.common.utils.Reporter;
import com.uid.common.config.Setup;

public class DockBESmokeTestsSuite
        extends TestSuiteBase
{

    public DockBESmokeTestsSuite()
    {
        this.addExtension = true;
        this.useTestAccount = true;
        Setup.setMode("new");
        Setup.setMaxRetryCount(1);
    }

    @DataProvider(name = "Personal app training data") public Object[][] personalAppTrainingData()
            throws JSONException, IOException
    {
        // Import CSV
        List<String[]> csvImport = CsvUtils.importCsv(new FileInputStream(Setup.getPersonalAppData()));

        // Verify and eliminate invalid training data
        return CsvUtils.validateCsvAndOutputInvalidRows(csvImport,
                Reporter.CSV_PROBLEM_APP_EXPORT_LOCATION);
    }

    @Test(enabled = true, priority = 1) public void testCDPLearnPlayUpdatePKUpdateDeleteAppCred()
    {
        // Get localThread driver
        WebDriver driver = getDriver();
        NewBEManagePasswordCat.testLearnReplayUpdatePKUpdateDeleteCredential(driver);
    }

    @Test(enabled = true, priority = 4, dataProvider = "personalAppTrainingData")
    public void testCDPLearnPlayOnPersonalApp(ApplicationData appData)
    {
        WebDriver driver = getDriver();
        NewBELearnPlayCat.testLearnReplayApp(driver, appData);
    }

}
