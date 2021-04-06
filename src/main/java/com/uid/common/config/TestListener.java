package com.uid.common.config;

import com.uid.common.utils.ApplicationData;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener
        implements ITestListener
{

    @Override
    public void onFinish(ITestContext context)
    {
        // Do nothing
    }

    public void onStart(ITestContext arg0)
    {
        // Do nothing

    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult arg0)
    {
        // Do nothing

    }

    public void onTestFailure(ITestResult arg0)
    {
        // Do nothing
    }

    public void onTestSkipped(ITestResult arg0)
    {
        // Do nothing
    }

    public void onTestStart(ITestResult arg0)
    {
        Object[] params = arg0.getParameters();
        String key = arg0.getName();
        if (params.length == 1)
        {
            ApplicationData appData = (ApplicationData)params[0];
            key = arg0.getName() + "[" + appData.getAppName() + "]";
        }

        if (!Setup.dataMap.containsKey(key))
        {
            Setup.dataMap.put(key, 0);
            Setup.result.put(key, "failed");
        }
    }

    public void onTestSuccess(ITestResult arg0)
    {
        Object[] params = arg0.getParameters();
        String key = arg0.getName();
        if (params.length == 1)
        {
            ApplicationData appData = (ApplicationData)params[0];
            key = arg0.getName() + "[" + appData.getAppName() + "]";
        }
        Setup.dataMap.remove(key);
        Setup.result.put(key, "passed");
    }
}
