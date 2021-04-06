package com.uid.common.config;

import java.util.Set;

import com.uid.common.utils.ApplicationData;
import org.apache.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestResult;

public class Retry
        implements IRetryAnalyzer
{
    private static Logger log = Logger.getLogger(Retry.class.getName());
    // Below method returns 'true' if the test method has to be retried else
    // 'false'
    // and it takes the 'Result' as parameter of the test method that just ran
    public boolean retry(ITestResult result)
    {
        Object[] params = result.getParameters();
        String key = result.getName();
        if (params.length == 1)
        {
            ApplicationData appData = (ApplicationData)params[0];
            key = result.getName() + "[" + appData.getAppName() + "]";
        }

        int retryCount = Setup.dataMap.get(key);
        if (retryCount < Setup.getMaxRetryCount())
        {
            Setup.dataMap.put(key, ++retryCount);
            log.info(
                    "Retrying test " + result.getName() + " with status " + getResultStatusName(
                            result.getStatus()) + " for the " + retryCount + " time(s).");
            ITestContext context = result.getTestContext();
            Set<ITestResult> failedTests = context.getFailedTests().getAllResults();
            failedTests.remove(result);
            return true;
        }
        Setup.dataMap.remove(key);
        return false;
    }

    private String getResultStatusName(int status)
    {
        String resultName = null;
        if (status == 1)
            resultName = "SUCCESS";
        if (status == 2)
            resultName = "FAILURE";
        if (status == 3)
            resultName = "SKIP";
        return resultName;
    }
}
