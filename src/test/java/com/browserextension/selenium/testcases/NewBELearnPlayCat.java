package com.browserextension.selenium.testcases;

import java.util.ArrayList;
import java.util.List;

import com.uid.common.base.CommonMessage;
import com.uid.common.utils.SelUtil;
import com.uid.common.utils.SelWindowUtil;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.uid.cloudportal.pages.NewCloudDesktopPage;
import com.uid.cloudportal.pages.NewPVExtension;
import com.uid.common.utils.ApplicationData;
import com.uid.common.config.Setup;
import com.uid.common.config.TestAccountManager;
import com.uid.common.config.Verification;
import com.uid.webportal.pages.WebPortalPage;

public class NewBELearnPlayCat
{
    private static Logger log = Logger.getLogger(NewBELearnPlayCat.class.getName());

    private NewBELearnPlayCat() {}

    public static void testLearnReplayApp(WebDriver driver, ApplicationData... appData)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        String appName;
        String appURL;
        String dockURL = TestAccountManager.getCDPUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();
        String privacyKey = TestAccountManager.getPrivacyKey();

        newCD.loginCDP(dockURL, username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        NewBESharedStep.preSetupTest(driver, windowHdls);

        boolean isTest = true;
        String cat;
        if (appData.length > 0) //Personal app
        {
            appName = appData[0].getAppName();
            appURL = appData[0].getLoginUrl();
            cat = NewCloudDesktopPage.Category.PERSONAL.getCat();

            if (!newCD.addAppFromSearchBox(appName))
            {
                isTest = false;
            }
        }
        else // Organization app
        {
            appName = Setup.getDefaultApp();
            appURL = Setup.getDefaultAppUrl();
            cat = NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat();
        }

        newCD.removeAppCred(privacyKey, appName);

        if (isTest)
        {
            newCD.openAppFromCategory(cat, appName);
            Verification.verifyTrue(driver, newCD.inputCredDlg.waitDlgLoad(),
                    CommonMessage.INPUT_CRED_DLG_IS_DISPLAYED_MSG, false);
            Verification.takeScreenshot(driver, "Input Credential dialog");

            newCD.inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
            Verification.verifyTrue(driver, SelWindowUtil.isNewWindowDisplay(driver, windowHdls),
                    CommonMessage.NEW_WINDOW_IS_OPENED_MSG, false);

            SelWindowUtil.switchToNewWindow(driver, windowHdls);
            Verification.verifyTrue(driver, newCD.appPages.isBEPlay(appURL),
                    CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);

            SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);
            newCD.goToManagePasswords(privacyKey);
            Verification.verifyTrue(driver, newCD.managePwdDialog.isAppExistingInManagePassword(appName),
                    CommonMessage.APP_PRESENCE_IN_MANAGEPASS_MSG, false);

            newCD.managePwdDialog.selectAppInManagePsw(appName);
            Verification.verifyTrue(driver, newCD.managePwdDialog.checkCred(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW),
                    CommonMessage.CRED_IS_STORED_SUCCESS_MSG, false);

            newCD.managePwdDialog.closeMangePasswordsDlg();
            newCD.openAppFromCategory(cat, appName);
            Verification.verifyFalse(driver, newCD.inputCredDlg.waitDlgLoad(),
                    CommonMessage.INPUT_CRED_DLG_IS_DISPLAYED_MSG, false);

            SelWindowUtil.switchToNewWindow(driver, windowHdls);
            Verification.verifyTrue(driver, newCD.appPages.isBEPlay(appURL),
                    CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);

            SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);
        }
        else
        {
            Verification.logStep(appName + " does not exist on " + Setup.getEnvironment());
            Verification.logStep("This test is ignored");
        }

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testFDCanDetectFormFieldsOnInactiveWindow(WebDriver driver,
            ApplicationData appData)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        String appName = appData.getAppName();

        // Launch CDP
        newCD.loginCDP(TestAccountManager.getCDPUrl(), TestAccountManager.getUserName(),
                TestAccountManager.getPassword());
        Verification
                .verifyTrue(driver, newCD.waitForNewDockLoad(), "Verification: CDP is loaded", false);
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        NewBESharedStep.addDefaultAppFromCatalog(driver, windowHdls, Setup.getDefaultApp());
        newCD.passPKPopups(TestAccountManager.getPrivacyKey(), windowHdls, false);

        log.info("TEST TRAINING APP");
        newCD.goToAdmin();
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        webPortalPage.waitForPageLoad();

        // Step: Training app by FD
        webPortalPage.trainAppAndAddToCDP(appData);
        webPortalPage.updateTrainingData(appData);

        // Go back CDP
        Verification.logStep("Close Web Portal and back to Dock window");
        SelWindowUtil.closeCurrentWindow(driver, windowHdls);
        SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);
        driver.navigate().refresh();
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);

        // Replay app
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        if (newCD.inputCredDlg.waitDlgLoad())
        {
            newCD.inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
        }
        SelWindowUtil.switchToNewWindow(driver, windowHdls);

        Verification.logStep("Move back to Dock window and wait 10 seconds");
        SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);
        SelUtil.wait(10, "wait before return window");

        Verification.logStep("Verify FD run on new windows");
        SelWindowUtil.switchWindowByIndex(driver, 1, windowHdls);
        Verification.verifyTrue(driver, newCD.appPages.isBEPlayByFD(),
                "--- Verification: BE can replay " + appName + ".", false);

        SelWindowUtil.closeCurrentWindow(driver, windowHdls);
        SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);

        // #END#
        // Step: Logout
        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testFDCanDetectFormFieldsForMultiApps(WebDriver driver, List<ApplicationData> appDataList)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        // Launch CDP
        newCD.loginCDP(TestAccountManager.getCDPUrl(), TestAccountManager.getUserName(),
                TestAccountManager.getPassword());
        Verification
                .verifyTrue(driver, newCD.waitForNewDockLoad(), "Verification: CDP is loaded", false);
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        NewBESharedStep.addDefaultAppFromCatalog(driver, windowHdls, Setup.getDefaultApp());
        newCD.passPKPopups(TestAccountManager.getPrivacyKey(), windowHdls, false);

        log.info("TEST TRAINING APP");
        // Step: Open Web Portal
        Verification.logSubStep("Launch Web Portal");
        newCD.goToAdmin();
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        webPortalPage.waitForPageLoad();

        // Step: Training app
        Verification.logStep("Training and update configuration apps");
        for (ApplicationData appData : appDataList)
        {
            webPortalPage.trainAppAndAddToCDP(appData);
            webPortalPage.updateTrainingData(appData);
        }

        // Go back CDP
        Verification.logStep("Close Web Portal and back to Dock window");
        SelWindowUtil.closeCurrentWindow(driver, windowHdls);
        SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);

        driver.navigate().refresh();
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);

        // Step: Replay app
        Verification.logStep("Input credential for apps");
        for (ApplicationData appData : appDataList)
        {
            String appName = appData.getAppName();
            Verification.logSubStep("Re-play the application: " + appName);
            newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
            newCD.inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
            SelWindowUtil.switchToNewWindow(driver, windowHdls);
            SelWindowUtil.closeCurrentWindow(driver, windowHdls);
            SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);
        }

        // Test
        Verification.logStep("Open apps, switch back to Dock tab and wait 10 seconds");
        for (ApplicationData appData : appDataList)
        {
            String appName = appData.getAppName();
            Verification.logSubStep("Re-play the application: " + appName);
            newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
            SelWindowUtil.switchToNewWindow(driver, windowHdls);
            SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);
        }

        SelUtil.wait(20, "wait for all windows reload");

        // Step: verify replay app
        Verification.logStep("Verify FD run on multi window");
        for (ApplicationData appData : appDataList)
        {
            String appName = appData.getAppName();

            SelWindowUtil.switchWindowByIndex(driver, 1, windowHdls);
            Verification.verifyTrue(driver, newCD.appPages.isBEPlayByFD(),
                    "--- Verification: BE can replay " + appName + ".", false);

            SelWindowUtil.closeCurrentWindow(driver, windowHdls);
            SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);
        }
        // #END#
        // Step: Logout
        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testLearnPlayDeletedApps(WebDriver driver, List<ApplicationData> appDataList)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewPVExtension pvDialog = PageFactory.initElements(driver, NewPVExtension.class);
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        // ------------------------------------------------------------
        ArrayList<String> windowHdls = new ArrayList<>();
        String dockURL = TestAccountManager.getCDPUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        newCD.loginCDP(dockURL, username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        NewBESharedStep.preSetupTest(driver, windowHdls);

        newCD.goToAdmin();
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        webPortalPage.waitForPageLoad();
        for (ApplicationData appData : appDataList)
        {
            webPortalPage.retrainAppAndAddToCDP(appData);
        }
        SelWindowUtil.closeCurrentWindow(driver, windowHdls);

        // Go back CDP
        Verification.logStep("Back to CDP window");
        SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);
        driver.navigate().refresh();
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);

        // Step: Input credential for first app
        Verification.logStep("Input credential for first app");
        ApplicationData appData = appDataList.get(0);
        String appName = appData.getAppName();
        newCD.openAppFromCategory("Other", appName);
        if (newCD.inputCredDlg.waitDlgLoad())
        {
            newCD.inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
        }
        Verification.verifyTrue(driver, SelWindowUtil.isNewWindowDisplay(driver, windowHdls),
                CommonMessage.NEW_WINDOW_IS_OPENED_MSG, false);
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);

        // Launch Web Portal, delete 2 apps
        Verification.logStep("Launch Web Portal and delete apps");
        newCD.goToAdmin();
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        webPortalPage.waitForPageLoad();
        webPortalPage.goToMenu(WebPortalPage.WPMenu.APPLICATIONS, WebPortalPage.WPMenu.MY_APPLICATIONS);
        webPortalPage.myApplicationsSAMLPage.waitForPageLoad();
        for (ApplicationData anAppDataList1 : appDataList)
        {
            Verification.logSubStep("Remove App " + anAppDataList1.getAppName());
            webPortalPage.removeApp(anAppDataList1.getAppName());
        }

        Verification.logStep("Close Web Portal and go back to CDP");
        SelWindowUtil.closeCurrentWindow(driver, windowHdls);
        SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);

        Verification.logStep("Play deleted app that has credential - App not found pop up appears");
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appDataList.get(0).getAppName());
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        Verification.verifyTrue(driver, pvDialog.isPVPopupLoad(),
                "Verification: PV popup is displayed.");
        Verification.verifyTrue(driver, pvDialog.getContentOfPVPopup()
                        .contentEquals("Application not found. Please refresh PingOne."),
                "Verification: Application not found. Please refresh PingOne.");
        Verification.takeScreenshot(driver, "Info dialog");
        Verification.verifyTrue(driver, newCD.isAuthMsgDisplay(),
                "Verification: Authentication page is still displayed.");

        SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);

        Verification.logStep("Play deleted app that does not have credential - App not found pop up appears");
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appDataList.get(1).getAppName());
        if (newCD.inputCredDlg.waitDlgLoad())
        {
            newCD.inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
        }
        Verification.verifyTrue(driver, pvDialog.isPVPopupLoad(),
                "Verification: PV popup is displayed.");
        Verification.verifyTrue(driver, pvDialog.getContentOfPVPopup()
                        .contentEquals("Application not found. Please refresh PingOne."),
                "Verification: Application not found. Please refresh PingOne.");
        Verification.takeScreenshot(driver, "Info dialog");

        // Refresh page
        Verification.logStep("Refresh Dock Page, find deleted App");
        driver.navigate().refresh();
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);
        for (ApplicationData anAppDataList : appDataList)
        {
            Verification.verifyFalse(driver,
                    newCD.waitForAppDisplayInCat(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), anAppDataList.getAppName()),
                    "Verification: " + anAppDataList.getAppName() + " was deleted from Dock.",
                    false);
        }

        // #END#
        // Step: Logout
        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testPasteAppLink(WebDriver driver, ApplicationData appData)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        WebPortalPage wp = PageFactory.initElements(driver, WebPortalPage.class);
        // ------------------------------------------------------------
        ArrayList<String> windowHdls = new ArrayList<>();

        String dockURL = TestAccountManager.getCDPUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        newCD.loginCDP(dockURL, username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        //OPTION: Add default app
        NewBESharedStep.addDefaultAppFromCatalog(driver, windowHdls, Setup.getDefaultApp());
        newCD.passPKPopups(TestAccountManager.getPrivacyKey(), windowHdls, true);

        //Train app
        newCD.goToAdmin();
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        wp.trainAppAndAddToCDP(appData);
        SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);

        newCD.refreshCDP();
        String appLink = newCD.getAppLink(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), Setup.getDefaultApp());
        newCD.replayApp(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appData.getAppName());
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        Verification.verifyTrue(driver, newCD.appPages.isBEPlay(appData.getLoginUrl()),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);

        //Test Paste CDP App Link
        Verification.logStep("Test: Paste CDP App link while signed on to CDP");
        driver.navigate().to(appLink);
        SelUtil.wait(3, "Waiting to reload application");
        Verification.verifyTrue(driver, newCD.appPages.isBEPlay(appData.getLoginUrl()),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);

        //Test Paste App Link
        Verification.logStep("Test: Paste App link while signed on to CDP");
        driver.navigate().to(appData.getLoginUrl());
        SelUtil.wait(3, "Waiting to reload application");
        Verification.verifyTrue(driver, newCD.appPages.isBEPlay(appData.getLoginUrl()),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);

        SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);

        newCD.logout();

        //Test Paste App Link
        Verification.logStep("Test: Paste App link while not signed on to CDP");
        driver.navigate().to(appData.getLoginUrl());
        Verification.verifyFalse(driver, newCD.appPages.isBEPlay(appData.getLoginUrl()),
                CommonMessage.APP_IS_NOT_REPLAY_BY_BE_MSG, false);

        //Test Paste CDP App Link
        Verification.logStep("Test: Paste CDP App link while not signed on to CDP");
        driver.navigate().to(appLink);
        newCD.loginPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
        Verification.verifyTrue(driver, newCD.appPages.isBEPlay(appData.getLoginUrl()),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);

        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

}
