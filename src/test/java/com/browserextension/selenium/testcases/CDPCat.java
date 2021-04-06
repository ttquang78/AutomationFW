package com.browserextension.selenium.testcases;

import com.uid.cloudportal.pages.CloudDesktopPage;
import com.uid.cloudportal.pages.NewCloudDesktopPage;
import com.uid.common.base.CommonMessage;
import com.uid.common.config.Setup;
import com.uid.common.utils.AccountManager;
import com.uid.common.utils.GmailParser;
import com.uid.common.config.TestAccountManager;
import com.uid.common.utils.SelWindowUtil;
import com.uid.common.utils.SelUtil;
import com.uid.common.config.Verification;
import com.uid.webportal.pages.WebPortalPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;

public class CDPCat
{
    private static final String PING_ID_SERVICE = "PingID";
    private static final String VERIFY_STEP_2 = "Verify step 2";
    
    private CDPCat() {}

    public static void loginToCDPWithPingID(WebDriver driver)
    {

        // initialize steps logging formatting
        GmailParser gmailParser = new GmailParser();
        gmailParser.clearAllOldMail();
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);

        AccountManager.MyAccount account =  AccountManager.getInstance().getAccountByKey("pidenable");
        driver.get(account.getCloudUrl());
        newCD.goToLoginPage();
        newCD.loginPage.login(account.getUserName(), account.getPassword());
        Verification.verifyTrue(driver, newCD.loginPage.isDisplayVerifyForm(),
                "Verification: Verify Form is loaded", false);

        //Login Gmail to get verify code
        String code = gmailParser.getVerifyCodeFromGmail(CommonMessage.NEW_AUTHEN_REQUEST_MSG);
        Verification.logStep("Code: " + code);
        newCD.loginPage.verifyLoginStep2(code);

        Verification.verifyTrue(driver, newCD.waitForNewDockLoad(), CommonMessage.CDP_IS_LOADED_MSG, false);

        newCD.logout();

        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void setByPassPingID(WebDriver driver)
    {
        GmailParser gmailParser = new GmailParser();
        gmailParser.clearAllOldMail();

        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        driver.get(TestAccountManager.getAdminUrl());
        webPortalPage.loginPage.login(AccountManager.getInstance().getAccountByKey("bypass").getUserName(),
                        AccountManager.getInstance().getAccountByKey("bypass").getPassword());

        if (webPortalPage.loginPage.isVerifyStep2())
        {
            Verification.logStep(VERIFY_STEP_2);
            String code = gmailParser.getVerifyCodeFromGmail(CommonMessage.NEW_AUTHEN_REQUEST_MSG);
            webPortalPage.loginPage.verifyLoginStep2(code);
        }

        Verification.verifyTrue(driver, webPortalPage.dashboardPage.waitPageLoad(),
                CommonMessage.DASHBOARD_DISPLAYED_MSG, false);

        webPortalPage.goToMenu(WebPortalPage.WPMenu.USERS, WebPortalPage.WPMenu.USER_BY_SERVICE);
        Verification.verifyTrue(driver, webPortalPage.usersByServicePage.isUserByServiceDisplayed(),
                CommonMessage.USERBYSERVICES_DISPLAYED_MSG, false);

        Verification.logStep("Search User thanhndtran+bypass@kms-technology.com");
        webPortalPage.usersByServicePage.fillSearchBox("do, do");
        webPortalPage.usersByServicePage.clickFirstRowDetail();
        Verification.verifyTrue(driver, webPortalPage.usersByServicePage.isExpandDetailUser(),
                CommonMessage.USERBYSERVICES_DISPLAYED_MSG, false);

        Verification.logStep("Set by pass");
        webPortalPage.usersByServicePage.expandServiceByName(PING_ID_SERVICE);
        webPortalPage.usersByServicePage.clickEditPingService();
        webPortalPage.usersByServicePage.setByPass();

        String currTab = driver.getWindowHandle();
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        webPortalPage.goToCDP();
        for (String tab : driver.getWindowHandles())
        {
            if (!tab.equals(currTab))
            {
                driver.switchTo().window(tab);
            }
        }

        Verification.verifyTrue(driver, newCD.waitForNewDockLoad(), CommonMessage.CDP_IS_LOADED_MSG, false);

        newCD.logout();
        driver.close();
        driver.switchTo().window(currTab);
        webPortalPage.goToMenu(WebPortalPage.WPMenu.USERS, WebPortalPage.WPMenu.USER_BY_SERVICE);
        webPortalPage.usersByServicePage.fillSearchBox("do, do");
        webPortalPage.usersByServicePage.clickFirstRowDetail();
        webPortalPage.usersByServicePage.expandServiceByName(PING_ID_SERVICE);
        webPortalPage.usersByServicePage.clickEditPingService();
        webPortalPage.usersByServicePage.revertByPass();

        webPortalPage.logout();

        Verification.endCollapseHtml();
        Verification.checkForVerificationErrors();
    }

    public static void disablePingID(WebDriver driver)
    {
        GmailParser gmailParser = new GmailParser();
        gmailParser.clearAllOldMail();

        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        driver.get(TestAccountManager.getAdminUrl());
        webPortalPage.loginPage.login(AccountManager.getInstance().getAccountByKey("piddisable").getUserName(),
                        AccountManager.getInstance().getAccountByKey("piddisable").getPassword());

        if (webPortalPage.loginPage.isVerifyStep2())
        {
            Verification.logStep(VERIFY_STEP_2);
            String code = gmailParser.getVerifyCodeFromGmail(CommonMessage.NEW_AUTHEN_REQUEST_MSG);
            webPortalPage.loginPage.verifyLoginStep2(code);
        }

        Verification.verifyTrue(driver, webPortalPage.dashboardPage.waitPageLoad(),
                CommonMessage.DASHBOARD_DISPLAYED_MSG, false);
        webPortalPage.goToMenu(WebPortalPage.WPMenu.USERS, WebPortalPage.WPMenu.USER_BY_SERVICE);
        Verification.verifyTrue(driver, webPortalPage.usersByServicePage.isUserByServiceDisplayed(),
                CommonMessage.USERBYSERVICES_DISPLAYED_MSG, false);

        Verification.logStep("Search User thanhndtran+disablepid@kms-technology.com");
        webPortalPage.usersByServicePage.fillSearchBox("oh, oh");
        webPortalPage.usersByServicePage.clickFirstRowDetail();
        Verification.verifyTrue(driver, webPortalPage.usersByServicePage.isExpandDetailUser(),
                CommonMessage.USERBYSERVICES_DISPLAYED_MSG, false);

        Verification.logStep("Disable PingID");
        webPortalPage.usersByServicePage.expandServiceByName(PING_ID_SERVICE);
        webPortalPage.usersByServicePage.clickToggleButton();

        String currTab = driver.getWindowHandle();
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        webPortalPage.goToCDP();
        for (String tab : driver.getWindowHandles())
        {
            if (!tab.equals(currTab))
            {
                driver.switchTo().window(tab);
            }
        }

        Verification.verifyTrue(driver, newCD.loginPage.isDisableDock(), CommonMessage.CDP_IS_LOADED_MSG,
                        true);
        driver.close();
        driver.switchTo().window(currTab);
        webPortalPage.goToMenu(WebPortalPage.WPMenu.USERS, WebPortalPage.WPMenu.USER_BY_SERVICE);
        webPortalPage.usersByServicePage.fillSearchBox("oh, oh");
        webPortalPage.usersByServicePage.clickFirstRowDetail();
        webPortalPage.usersByServicePage.expandServiceByName(PING_ID_SERVICE);
        webPortalPage.usersByServicePage.clickToggleButton();

        webPortalPage.logout();

        Verification.endCollapseHtml();
        Verification.checkForVerificationErrors();
    }

    public static void removePingIDUserByService(WebDriver driver)
    {
        GmailParser gmailParser = new GmailParser();
        gmailParser.clearAllOldMail();

        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        driver.get(TestAccountManager.getAdminUrl());
        webPortalPage.loginPage.login(AccountManager.getInstance().getAccountByKey("removepid").getUserName(),
                        AccountManager.getInstance().getAccountByKey("removepid").getPassword());

        if (webPortalPage.loginPage.isVerifyStep2())
        {
            Verification.logStep(VERIFY_STEP_2);
            String code = gmailParser.getVerifyCodeFromGmail(CommonMessage.NEW_AUTHEN_REQUEST_MSG);
            webPortalPage.loginPage.verifyLoginStep2(code);
        }

        Verification.verifyTrue(driver, webPortalPage.dashboardPage.waitPageLoad(),
                CommonMessage.DASHBOARD_DISPLAYED_MSG, false);
        webPortalPage.goToMenu(WebPortalPage.WPMenu.USERS, WebPortalPage.WPMenu.USER_BY_SERVICE);
        Verification.verifyTrue(driver, webPortalPage.usersByServicePage.isUserByServiceDisplayed(),
                CommonMessage.USERBYSERVICES_DISPLAYED_MSG, false);

        Verification.logStep("Search User thanhndtran+rm@kms-technology.com");
        webPortalPage.usersByServicePage.fillSearchBox("minh, minh");
        webPortalPage.usersByServicePage.clickFirstRowDetail();
        Verification.verifyTrue(driver, webPortalPage.usersByServicePage.isExpandDetailUser(),
                CommonMessage.USERBYSERVICES_DISPLAYED_MSG, false);

        Verification.logStep("Remove PingID");
        webPortalPage.usersByServicePage.expandServiceByName(PING_ID_SERVICE);
        webPortalPage.usersByServicePage.clickEditPingID();
        webPortalPage.usersByServicePage.removePingID();

        String currTab = driver.getWindowHandle();
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        webPortalPage.goToCDP();
        for (String tab : driver.getWindowHandles())
        {
            if (!tab.equals(currTab))
            {
                driver.switchTo().window(tab);
            }
        }

        Verification.verifyTrue(driver, newCD.loginPage.isDisplayPairDevice(),
                "Verification: Pair device page is displayed", false);
        newCD.loginPage.verifyPairDevice();
        Verification
                .verifyTrue(driver, newCD.waitForNewDockLoad(), CommonMessage.CDP_IS_LOADED_MSG, false);

        newCD.logout();
        driver.switchTo().window(currTab);
        webPortalPage.logout();

        Verification.endCollapseHtml();
        Verification.checkForVerificationErrors();
    }

    public static void preventImpersonateFromMSPAcc(WebDriver driver)
    {
        GmailParser gmailParser = new GmailParser();
        gmailParser.clearAllOldMail();
        
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        driver.get(TestAccountManager.getAdminUrl());
        webPortalPage.loginPage.login(AccountManager.getInstance().getAccountByKey("preventimp").getUserName(),
                        AccountManager.getInstance().getAccountByKey("preventimp").getPassword());

        if (webPortalPage.loginPage.isVerifyStep2())
        {
            Verification.logStep(VERIFY_STEP_2);
            String code = gmailParser.getVerifyCodeFromGmail(CommonMessage.NEW_AUTHEN_REQUEST_MSG);
            webPortalPage.loginPage.verifyLoginStep2(code);
        }

        Verification.verifyTrue(driver, webPortalPage.dashboardPage.waitPageLoad(),
                CommonMessage.DASHBOARD_DISPLAYED_MSG, false);
        webPortalPage.goToMenu(WebPortalPage.WPMenu.CUSTOMERS);
        Verification.verifyTrue(driver, webPortalPage.customersPage.isCustomersDisplayed(),
                "Verification: Customers is displayed", false);

        Verification.logStep("Access Impersonate Mode");
        webPortalPage.customersPage.clickFirstRowDetail();
        Verification.verifyTrue(driver, webPortalPage.customersPage.isExpandUserDetail(),
                "Verification: UserDetail is expaned", false);
        webPortalPage.customersPage.accessImpersonateMode();

        String currTab = driver.getWindowHandle();
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        webPortalPage.goToCDP();
        for (String tab : driver.getWindowHandles())
        {
            if (!tab.equals(currTab))
            {
                driver.switchTo().window(tab);
            }
        }

        Verification.verifyTrue(driver, newCD.loginPage.waitForPageLoad(),
                "Verification: isSignOnPageDisplay", false);

        driver.switchTo().window(currTab);
        SelUtil.clickElement(driver, By.xpath("//button[contains(text(), 'Exit')]"));
        webPortalPage.logout();

        Verification.endCollapseHtml();
        Verification.checkForVerificationErrors();
    }

    public static void testSwitchMode(WebDriver driver)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        CloudDesktopPage cloudDesktopPage = PageFactory.initElements(driver, CloudDesktopPage.class);
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        String appName = Setup.getDefaultApp();
        String appUrl = Setup.getDefaultAppUrl();
        String adminURL = TestAccountManager.getAdminUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        driver.get(adminURL);
        webPortalPage.logout(); //prevent browser cache

        Verification.logStep("OPTION: Check and set test account in New mode");
        webPortalPage.loginPage.login(username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);// window 0 - dock 1

        webPortalPage.waitForPageLoad();

        // Switch to new mode if being legacy mode
        webPortalPage.goToMenu(WebPortalPage.WPMenu.SETUP, WebPortalPage.WPMenu.DOCK);
        webPortalPage.setupDockPage.upgradeDock();

        // Go to dock and check new dock is launched
        webPortalPage.goToCDP();
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        Verification.verifyTrue(driver, newCD.waitForNewDockLoad(),
                CommonMessage.CDP_IS_LOADED_MSG, false);
        Verification.verifyTrue(driver, newCD.waitForBEActive(),
                CommonMessage.BE_IS_ACTIVE_MSG, false);

        newCD.passPKPopups(TestAccountManager.getPrivacyKey(), windowHdls, false);

        /////////////////////////////////////////////////////
        // 1. BE_ADV_16: Switch BE mode from New to Legacy
        /////////////////////////////////////////////////////
        Verification.logStep("Switch mode from New to Legacy");
        SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);
        webPortalPage.goToMenu(WebPortalPage.WPMenu.SETUP, WebPortalPage.WPMenu.DOCK);

        webPortalPage.setupDockPage.switchToLegacy();

        int oldWindowCount = driver.getWindowHandles().size();
        Verification.logSubStep("Open more dock by link");
        webPortalPage.setupDockPage.clickDockLink();
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        Verification.verifyTrue(driver, cloudDesktopPage.isLegacyDock(),
                CommonMessage.LEGACY_CDP_IS_LOADED_MSG, false);
        Verification.verifyTrue(driver, cloudDesktopPage.isBEActively(),
                CommonMessage.BE_IS_ACTIVE_MSG, false);
        int newWindowCount = driver.getWindowHandles().size();
        Verification.verifyTrue(driver, newWindowCount == (oldWindowCount + 1),
                " --- Verification: No tabs are closed", false);

        cloudDesktopPage.gotoManageAppPasswords(TestAccountManager.getPrivacyKey());
        cloudDesktopPage.reSignOn();
        Verification.verifyTrue(driver, cloudDesktopPage.isLegacyPM(),
                " --- Verification: This is legacy manage password", false);
        Verification.takeScreenshot(driver, "Legacy Manage Password screen");
        cloudDesktopPage.managePwdDialog.closeDialog();

        // Check PREVIOUS DOCK
        Verification.logSubStep("CHECKING PREVIOUS DOCK");
        SelWindowUtil.switchWindowByIndex(driver, 1, windowHdls);// dock 1
        Verification.verifyTrue(driver, cloudDesktopPage.isLegacyDock(),
                " --- Verification: Previous Dock is legacy dock", false);
        SelWindowUtil.closeCurrentWindow(driver, windowHdls);

        //////////////////////////////////////////////////////////////////////////////////
        // 2. BE_ADV_17: Switch BE mode from Legacy to New
        //////////////////////////////////////////////////////////////////////////////////

        Verification.logStep("Switch mode from Legacy to New");
        Verification.logSubStep("Back to Admin Portal");
        SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);
        webPortalPage.goToMenu(WebPortalPage.WPMenu.SETUP, WebPortalPage.WPMenu.DOCK);
        webPortalPage.setupDockPage.upgradeDock();

        oldWindowCount = driver.getWindowHandles().size();
        Verification.logSubStep("Click dock link");
        webPortalPage.setupDockPage.clickDockLink();
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        newWindowCount = driver.getWindowHandles().size();

        Verification.verifyTrue(driver, newWindowCount == (oldWindowCount + 1),
                " --- Verification: No tabs are closed", false);

        // VERIFICATION: New dock is launched on all Dock windows
        // Check New DOCK
        Verification.logSubStep("CHECKING NEW DOCK");
        Verification.verifyTrue(driver, newCD.waitForNewDockLoad(),
                " --- Verification: New Dock is new dock", false);
        // VERIFICATION: Manage Password dialog of New dock is displayed
        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        newCD.managePwdDialog.waitManagePasswordDlgLoad();
        Verification.verifyTrue(driver, newCD.managePwdDialog.waitManagePasswordDlgLoad(),
                " --- Verification: This is new manage password", false);
        Verification.takeScreenshot(driver, "New Manage Password screen");
        newCD.managePwdDialog.closeMangePasswordsDlg();

        // Check PREVIOUS DOCK
        Verification.logSubStep("CHECKING PREVIOUS DOCK");
        SelWindowUtil.switchWindowByIndex(driver, 1, windowHdls);// dock 1
        Verification.verifyTrue(driver, newCD.waitForNewDockLoad(),
                " --- Verification: Previous Dock is new dock", false);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        // 3. BE_ADV_15: Verify that user can open multiple docks
        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        Verification.logStep("Verify replay function works correctly when replay app on different Dock tabs");

        // Switch to latest dock
        Verification.logSubStep("Switch to latest dock");
        SelWindowUtil.switchWindowByIndex(driver, 2, windowHdls);// dock 5
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        if (newCD.inputCredDlg.waitDlgLoad())
        {
            newCD.inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
        }
        // Switch back to new opened tab
        SelWindowUtil.switchToNewWindow(driver, windowHdls);// window 6 app
        Verification.verifyTrue(driver, newCD.appPages.isBEPlay(appUrl),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);

        // Switch to dock #1
        Verification.logSubStep("Switch to dock 1");
        SelWindowUtil.switchWindowByIndex(driver, 1, windowHdls);// dock 1
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        if (newCD.inputCredDlg.waitDlgLoad())
        {
            newCD.inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
        }
        // Switch back to new opened tab
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        Verification.verifyTrue(driver, newCD.appPages.isBEPlay(appUrl),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);

        // Switch to latest dock
        SelWindowUtil.switchWindowByIndex(driver, 2, windowHdls);

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

}
