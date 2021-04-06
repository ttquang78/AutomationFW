package com.uid.common.config;

import com.uid.common.utils.ApplicationData;
import com.uid.common.utils.CsvUtils;
import com.uid.common.utils.Reporter;
import org.apache.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.io.IOException;
import java.util.Set;

public class AppScanRetry
        implements IRetryAnalyzer
{
    private static Logger log = Logger.getLogger(Retry.class.getName());
    // Below method always returns 'true'
    // and it takes the 'Result' as parameter of the test method that just ran
    public boolean retry(ITestResult result)
    {
        try
        {
            //Remove fail test in TestNG
            ITestContext context = result.getTestContext();
            Set<ITestResult> failedTests = context.getFailedTests().getAllResults();
            failedTests.remove(result);

            // Export result
            if (!Reporter.getCsvBETestResult().isEmpty())
            {
                String exportLocation = Reporter.REPORT_LOCATION + "BE_Result.csv";
                CsvUtils.exportCsv(Reporter.getCsvBETestResult(), exportLocation);
            }

            // Restart browser
            Object[] params = result.getParameters();
            ApplicationData appData = (ApplicationData)params[0];
            log.info("====================================================================");
            log.info("Retrying test " + result.getName() + "[" + appData.getAppName() + "]");

            WebdriverManager.get().quit();
            WebdriverManager.setBrowserDriver(Setup.getBrowserType(), true);
            WebdriverManager.setErrorBuffer();
            WebdriverManager.setErrorExpectedFalse();
        }
        catch (IOException e)
        {
            log.error(e.toString());
        }

        return true;
    }
}
