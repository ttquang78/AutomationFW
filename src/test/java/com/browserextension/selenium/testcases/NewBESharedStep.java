package com.browserextension.selenium.testcases;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.uid.common.base.CommonMessage;
import com.uid.common.config.WebdriverManager;
import com.uid.common.utils.SelUtil;
import com.uid.common.utils.SelWindowUtil;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.uid.cloudportal.pages.NewCloudDesktopPage;
import com.uid.common.utils.CsvUtils;
import com.uid.common.config.Setup;
import com.uid.common.config.TestAccountManager;
import com.uid.common.config.Verification;
import com.uid.webportal.pages.WebPortalPage;

public class NewBESharedStep
{
    private static Logger log = Logger.getLogger(NewBESharedStep.class.getName());

    private NewBESharedStep() {}

    static void addDefaultAppFromCatalog(WebDriver driver, List<String> windowHdls, String appName)
    {
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);

        if (!newCD.waitForAppDisplayInCat("All Applications", appName))
        {
            newCD.goToAdmin();
            SelWindowUtil.switchToNewWindow(driver, windowHdls);
            webPortalPage.waitForPageLoad();
            webPortalPage.goToMenu(WebPortalPage.WPMenu.APPLICATIONS,
                    WebPortalPage.WPMenu.APPLICATION_CATALOG);
            webPortalPage.applicationCatalogPage.addAppFromCatalog(appName);
            webPortalPage.enableAppInUserGroup(appName);
            SelWindowUtil.closeCurrentWindow(driver, windowHdls);
            SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);
            driver.navigate().refresh();
            Verification
                    .verifyTrue(driver, newCD.waitForNewDockLoad(), "Verification: CDP is loaded",
                            false);
            Verification.verifyTrue(driver, newCD.waitForBEActive(), "Verification: BE is active",
                    false);
        }
    }

    public static void testVerifyVersion(WebDriver driver) throws IOException
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        // ------------------------------------------------------------
        ArrayList<String> windowHdls = new ArrayList<>();
        boolean isRun;

        driver.get(TestAccountManager.getAdminUrl());
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        String actualBrowserVersion = Setup.getBrowserVersion();
        String actualBEVersion = newCD.appPages.getBEVersion();

        String beInfoFile = "DataFiles/" + Setup.getBrowserName() + "_" + newCD.appPages.getBEEnv()
                + "_version.csv";
        File filePath = new File(beInfoFile);

        log.info(CommonMessage.SEPERATE_STR);
        log.info("Analyzing...");
        log.info(CommonMessage.SEPERATE_STR);
        log.info("TEST REQUEST:");
        log.info("- Machine: " + Setup.getWebRuntime());
        log.info("- Browser: " + Setup.getBrowserType());
        log.info("- BE env: " + Setup.getEnvironment());

        String environment = newCD.appPages.getBEEnv();
        if (!filePath.exists())
        {
            isRun = true;
            log.info(">>> CI is run the first time on this system.");
        }
        else
        {
            if (Setup.isForceRun())
            {
                isRun = true;
                log.info(">>> Test is forced to run on this system.");
            }
            else
            {
                List<String[]> info = CsvUtils.importCsv(new FileInputStream(beInfoFile));
                isRun = checkBEAndBrowser(info, environment, actualBrowserVersion, actualBEVersion);
            }
        }

        takeDecision(isRun, driver, environment, actualBrowserVersion, actualBEVersion, beInfoFile);

        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    private static boolean checkBEAndBrowser(List<String[]> info, String environment, String actualBrowserVersion, String actualBEVersion)
    {
        boolean isRun;

        log.info("---------");
        log.info("INFO OF THE LATTEST RUN:");
        log.info("Browser Version: " + info.get(0)[0]);
        log.info("BE Version: " + info.get(1)[0]);

        log.info("---------");
        log.info("CURRENT STATE OF BROWSER:");
        log.info("Browser: " + Setup.getBrowserName() + " " + actualBrowserVersion);
        log.info("BE Version: " + actualBEVersion);
        log.info("BE env: " + environment);
        log.info("");

        if (Setup.getEnvironment().startsWith("prod") || Setup.getEnvironment().startsWith("beta"))
        {
            isRun = checkBEAndBrowserOnPROD(info, actualBrowserVersion, actualBEVersion);
        }
        else
        {
            isRun = checkBEAndBrowserOnTESTAndORT(info, actualBEVersion);
        }

        return isRun;
    }

    private static boolean checkBEAndBrowserOnPROD(List<String[]> info, String actualBrowserVersion, String actualBEVersion)
    {
        boolean isRun = false;

        if (actualBEVersion != null
                && (!info.get(0)[0].equals(actualBrowserVersion) || !info.get(1)[0].equals(actualBEVersion)))
        {
            isRun = true;
            if (!info.get(0)[0].equals(actualBrowserVersion))
            {
                log.info(">>> Browser version is changed from " + info.get(0)[0] + " to "
                        + actualBrowserVersion);
            }
            if (!info.get(1)[0].equals(actualBEVersion))
            {
                log.info(">>> BE version is changed from " + info.get(1)[0] + " to "
                        + actualBEVersion);
            }
        }
        else
        {
            log.info(">>> No new browser version and no new BE version");
        }

        return isRun;
    }

    private static boolean checkBEAndBrowserOnTESTAndORT(List<String[]> info, String actualBEVersion)
    {
        boolean isRun = false;

        if (!info.get(1)[0].equals(actualBEVersion) && actualBEVersion != null)
        {
            isRun = true;
            log.info(">>> BE version is changed from " + info.get(1)[0] + " to "
                    + actualBEVersion);
        }
        else
        {
            log.info(">>> No new BE version");
        }

        return isRun;
    }

    private static void takeDecision(boolean isRun, WebDriver driver, String environment,
            String actualBrowserVersion, String actualBEVersion, String beInfoFile)
            throws IOException
    {
        if (isRun)
        {
            if (Setup.getEnvironment().startsWith(environment) || (
                    Setup.getEnvironment().equalsIgnoreCase("beta") && (environment.equalsIgnoreCase("prod"))))
            {
                log.info("CONCLUDE: Test is triggered!!!");
                List<String[]> beInfo = new ArrayList<>();
                String[] info1 = {actualBrowserVersion};
                String[] info2 = {actualBEVersion};
                beInfo.add(info1);
                beInfo.add(info2);
                CsvUtils.exportCsv(beInfo, beInfoFile);
            }
            else
            {
                log.info(">>> BE is on incorrect getEnvironment().");
                log.info(CommonMessage.TEST_NOT_TRIGGERED_STR);
                log.info(CommonMessage.SEPERATE_STR);
                Verification.verifyTrue(driver, false, "BE is on incorrect environment().", false);
            }
        }
        else
        {
            log.info(CommonMessage.TEST_NOT_TRIGGERED_STR);
            log.info(CommonMessage.SEPERATE_STR);
            Verification.verifyTrue(driver, false, CommonMessage.TEST_NOT_TRIGGERED_STR, false);
        }
        log.info(CommonMessage.SEPERATE_STR);
    }

    static void preSetupTest(WebDriver driver, List<String> windowHdls)
    {
        addDefaultAppFromCatalog(driver, windowHdls, Setup.getDefaultApp());
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        newCD.passPKPopups(TestAccountManager.getPrivacyKey(), windowHdls, false);
    }

    public static WebDriver restartBrowser(WebDriver driver, List<String> windowHdls, boolean addExt)
            throws IOException
    {
        Verification.logSubStep("<font color='red'><b>Restart browser</b></font>");
        driver.quit();
        windowHdls.clear();

        SelUtil.wait(10, "Wait for resource cleanup before start new browser");
        WebdriverManager.setBrowserDriver(Setup.getBrowserType(), addExt);

        driver = WebdriverManager.get();
        driver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        return driver;
    }

}