package com.browserextension.selenium.testsuites;

import com.browserextension.selenium.testcases.CDPCat;
import com.uid.common.base.TestSuiteBase;
import com.uid.common.config.Setup;
import org.testng.annotations.Test;

public class NewCDPSuite
        extends TestSuiteBase
{

    @Test(enabled = true, priority = 1) public void loginCDPWithPingID()
    {
        CDPCat.loginToCDPWithPingID(getDriver());
    }

    @Test(enabled = true, priority = 2) public void byPassPingID()
    {
        CDPCat.setByPassPingID(getDriver());
    }

    @Test(enabled = true, priority = 3) public void disablePingID()
    {
        CDPCat.disablePingID(getDriver());
    }

    @Test(enabled = true, priority = 4) public void removePingIDUserByService()
    {
        CDPCat.removePingIDUserByService(getDriver());
    }

    @Test(enabled = true, priority = 5) public void preventImpersonate()
    {
        if (Setup.getEnvironment().equalsIgnoreCase("prod"))
        {
            return;
        }

        CDPCat.preventImpersonateFromMSPAcc(getDriver());
    }
}
