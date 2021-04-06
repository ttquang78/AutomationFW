package com.browserextension.selenium.testcases;

import java.util.*;

import com.uid.common.config.TestAccountManager;
import com.uid.common.utils.SelWindowUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.uid.cloudportal.pages.NewCloudDesktopPage;
import com.uid.common.utils.ApplicationData;
import com.uid.common.utils.Reporter;
import com.uid.common.config.Setup;
import com.uid.common.utils.SelUtil;
import com.uid.common.config.Verification;

public class FixBrokenData
{
    private static final String PASSED = "PASSED";
    private static final String FAILED = "FAILED";
    private static final String UNKNOWN = "UNKNOWN";
    // Ignored List
    private static final List<String> ignoreApps =
            Arrays.asList("Seamless.com", "GoGrid", "Pandora",
                    "Benetrac", "ConnectandSell", "CoreCommerce",
                    "ePayroll Portal Employees", "ePayroll Portal Employers",
                    "PayChex Time and Labor Online", "Seamless.com", "Standard Parking",
                    "Valicom [Clearview]", "tracker.serengetilaw.com", "US Airways", "DNSstuff",
                    "Aflac", "athenahealth", "iCloud", "Pandora", "OnCare",
                    "McAfee Employee Portal", "McAfee Partner Portal",
                    "Staples Advantage", "VoIP Street", "Sterling HSA",
                    "Flowroute", "myStaffingPro", "ServiceLink LP users", "D&H Distributing",
                    "Verge Solutions VCredentialing: Administrator",
                    "Verge Solutions VCredentialing: Practitioner File", "Verge Solutions VSuite", "ZocDoc");

    private FixBrokenData() {}

    public static void testLearnPlayAppInCatalog(WebDriver driver, ApplicationData appData)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        // ------------------------------------------------------------

        String testUsername = TestAccountManager.getUserName();
        String testPassword = TestAccountManager.getPassword();
        String testURL = TestAccountManager.getCDPUrl();

        ArrayList<String> windowHdls = new ArrayList<>();
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        String appName = appData.getAppName();

        Verification.logStep("=======================================================");
        Verification.logStep("APP NAME: " + appName);

        newCD.loginCDP(testURL, testUsername, testPassword);

        String[] report;
        if (!ignoreApps.contains(appName))
        {
            if (newCD.addAppFromSearchBox(appName))
            {
                report = checkAndRemovePersonalApp(driver, windowHdls, appData, newCD);
            }
            else
            {
                report = new String[] {appName, UNKNOWN, "App could not be found."};
                Verification.takeScreenshot(driver, appName + " (Can't find app): ");
            }
        }
        else
        {
            report = new String[] {appName, UNKNOWN,
                    "App is ignored because it couldn't be tested in current network."};
        }
        Reporter.addReport(report);

        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    private static String[] checkAndRemovePersonalApp(WebDriver driver,
            ArrayList<String> windowHdls, ApplicationData appData, NewCloudDesktopPage newCD)
    {
        String[] report;
        String appName = appData.getAppName();
        String appURL = appData.getLoginUrl();
        int frameIndex = appData.getLearnedDocIdUserName();
        String privacyKey = TestAccountManager.getPrivacyKey();

        newCD.playAppFullStep(appName, frameIndex, windowHdls, privacyKey);
        String result = newCD.appPages.getBEPlayResult();
        report = checkResult(appName, appURL, result);
        if (report[1].equals(FAILED))
        {
            Verification.takeScreenshot(driver, appName + " is broken");
        }

        if (appName.equals("Agiloft Support")
                && SelWindowUtil.isNewWindowDisplay(driver, windowHdls))
        {
            SelWindowUtil.switchToNewWindow(driver, windowHdls);
            SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 1);
        }
        SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);

        // Remove personal app
        if (!(appName.equalsIgnoreCase(Setup.getDefaultApp()))
                && !(appName.equalsIgnoreCase("11Giraffes")))
        {
            newCD.removePersonalAppFromDock(NewCloudDesktopPage.Category.PERSONAL.getCat(), appName);
        }

        return report;
    }

    private static String[] checkResult(String appName, String appURL, String rtnResult)
    {
        String[] report;
        String result;
        String message;

        switch (rtnResult)
        {
            case "clickUsingTraining":
                result = PASSED;
                message = "BE Re-play Success By Training Data.";

                break;

            case "clickUsingFD":
                result = PASSED;
                message = "BE Re-play Success By FD. Please go to FD tool to fix data.";

                break;

            case "noinject":
                String newUrl = SelUtil.getRedirectedURL(appURL);
                if (newUrl != null)
                {
                    result = FAILED;
                    message = "Page is redirected to new url: " + newUrl + ". Please re-train this app.";
                }
                else
                {
                    result = PASSED;
                    message = "BE Re-play Success By Training Data or FD Data. Please go to FD tool to fix data.";
                }

                break;
				
			case "broken":
				result = FAILED;
                message = "App is broken";
				
				break;

            default:
                result = FAILED;
                message = "BE Re-play Fail. It could be a BE error or Login form is not presence anymore.";

                break;
        }

        report = new String[] {appName, result, message};

        return report;
    }

}
