package com.browserextension.selenium.testcases;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.uid.common.base.CommonMessage;
import com.uid.common.utils.SelWindowUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.uid.cloudportal.pages.NewCloudDesktopPage;
import com.uid.cloudportal.pages.NewPVExtension;
import com.uid.common.config.Setup;
import com.uid.common.config.TestAccountManager;
import com.uid.common.utils.SelUtil;
import com.uid.common.config.Verification;

public class NewBEPrivacyKeyCat
{
    private static final String NEW_PK = "PingOne2";
    
    private NewBEPrivacyKeyCat() {}

    /**
     * =============================================================== /*
     * [PRIVACYKEY] /* -This test combine tests which relate to privacy key.
     * Intent to install BE just one time on IE /* -Use test account with
     * existed privacy key
     * /*===============================================================
     */
    public static void testLoginCDPByUserWithPK(WebDriver driver) throws MalformedURLException
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        // ------------------------------------------------------------

        String appName = Setup.getDefaultApp();
        String appURL = Setup.getDefaultAppUrl();
        String newPK = TestAccountManager.getPrivacyKey();
        String dockURL = TestAccountManager.getCDPUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        if (Setup.getBrowserType().equalsIgnoreCase("ie"))
        {
            newPK = "PingOne" + Math.random();
        }

        ArrayList<String> windowHdls = new ArrayList<>();

        // For IE, reset PK
        if (Setup.getBrowserType().equalsIgnoreCase("ie"))
        {
            launchLocalChromeAndChangePK(newPK);
        }

        newCD.loginCDP(dockURL, username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);
        Verification.takeScreenshot(driver, "Dock screen");

        NewBESharedStep.addDefaultAppFromCatalog(driver, windowHdls, appName);

        // Step: Check Input PK dialog when opening an app
        Verification.logStep("Verify Input Privacy Key dialog is displayed when user sign-on to dock firstime");
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyTrue(driver, newCD.privacyKeyDlg.waitPKDlgLoad(), CommonMessage.INPUT_PK_DISPLAYED_MSG, false);
        Verification.verifyTrue(driver, newCD.privacyKeyDlg.isDisableSignOnButtonDisplay(), CommonMessage.SIGNON_BTN_DISABLED_MSG);
        Verification.takeScreenshot(driver, "Input Privacy Key dialog");

        // Close PK popup
        newCD.privacyKeyDlg.closePKDlg();
        Verification.verifyTrue(driver, newCD.privacyKeyDlg.waitPKDlgClose(), CommonMessage.INPUT_PK_CLOSED_MSG, false);
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyTrue(driver, newCD.privacyKeyDlg.waitPKDlgLoad(), CommonMessage.INPUT_PK_DISPLAYED_MSG, false);

        // Step: Fill less than 8 characters
        Verification.logStep("Fill less than 8 characters");
        newCD.privacyKeyDlg.fillPK("1234567");
        Verification.verifyTrue(driver, newCD.privacyKeyDlg.isDisableSignOnButtonDisplay(), CommonMessage.SIGNON_BTN_DISABLED_MSG);

        // Step: Fill invalid privacy key
        Verification.logStep("Fill invalid privacy key");
        newCD.privacyKeyDlg.fillPKAndSignOn("WrongPKey");
        Verification.verifyTrue(driver, newCD.privacyKeyDlg.waitPKDlgLoad(), CommonMessage.INPUT_PK_DISPLAYED_MSG, false);
        Verification.verifyTrue(driver, newCD.privacyKeyDlg.isInvalidErrorDisplay(),
                "Try again error is displayed");
        Verification.verifyTrue(driver, newCD.privacyKeyDlg.isDisableSignOnButtonDisplay(), CommonMessage.SIGNON_BTN_DISABLED_MSG);
        Verification.takeScreenshot(driver, "Input Privacy Key dialog with Error message");

        // Step: Fill valid privacy key
        Verification.logStep("Fill valid privacy key");
        newCD.privacyKeyDlg.fillPKAndSignOn(newPK);
        Verification.verifyTrue(driver, newCD.privacyKeyDlg.waitPKDlgClose(), CommonMessage.INPUT_PK_CLOSED_MSG, false);
        newCD.inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
        Verification.verifyTrue(driver, SelWindowUtil.isNewWindowDisplay(driver, windowHdls),
                CommonMessage.NEW_WINDOW_IS_OPENED_MSG, false);
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);

        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyFalse(driver, newCD.privacyKeyDlg.waitPKDlgLoad(),
                "Input Privacy Key popup is not displayed", false);
        Verification.verifyTrue(driver, SelWindowUtil.isNewWindowDisplay(driver, windowHdls),
                CommonMessage.NEW_WINDOW_IS_OPENED_MSG, false);
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        Verification.verifyTrue(driver, newCD.appPages.isBEPlay(appURL), CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);
        SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);
        
        newCD.goToManagePasswords(newPK);
        Verification.verifyTrue(driver, newCD.managePwdDialog.isAppExistingInManagePassword(appName),
                CommonMessage.APP_IS_DISPLAYED_IN_MANAGE_PSW_MSG, false);
        int countLearnedApp = newCD.managePwdDialog.countElementsInManagePassword();
        Verification.verifyTrue(driver, (countLearnedApp - 3 > 0),
                "App credentials are loaded", false);

        // Change PK back to PingOne1
        Verification.logStep("Change Privacy Key back to default");
        newCD.managePwdDialog.updatePrivacyKeyAndSave(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, newCD.waitForUpdatePKSuccessMsgLoad(),
                CommonMessage.PK_IS_UPDATED_MSG, false);
        newCD.waitForUpdatePKSuccessMsgInvisible();
        newCD.managePwdDialog.closeMangePasswordsDlg();

        newCD.logout();
        Verification.verifyTrue(driver, newCD.loginPage.waitForPageLoad(),
                CommonMessage.SIGNON_PAGE_DISPLAYED_MSG, true);
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testCreateNewPKAndPlayApp(WebDriver driver, String newPK) throws MalformedURLException
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        // ------------------------------------------------------------

        String appName = Setup.getDefaultApp();
        String appURL = Setup.getDefaultAppUrl();
        String dockURL = TestAccountManager.getCDPUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        ArrayList<String> windowHdls = new ArrayList<>();

        // For IE, Clear PK
        if (Setup.getBrowserType().equalsIgnoreCase("ie"))
        {
            launchLocalChromeAndChangePK(newPK);
        }

        newCD.loginCDP(dockURL, username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        //OPTION: Add default app
        NewBESharedStep.addDefaultAppFromCatalog(driver, windowHdls, appName);

        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);

        newCD.privacyKeyDlg.clearAllPasswords();
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.waitDlgLoad(),
                CommonMessage.CREATE_PK_DISPLAYED_MSG, false);

        // Step: Create new privacy key
        Verification.logStep("Create new privacy key");
        newCD.createPrivacyKeyDlg.createPrivacyKey(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.waitDlgClose(),
                CommonMessage.CREATE_PK_DLG_IS_CLOSED_MSG, true);
        Verification.verifyTrue(driver, newCD.inputCredDlg.waitDlgLoad(),
                CommonMessage.INPUT_CRED_DLG_IS_DISPLAYED_MSG, false);
        newCD.inputCredDlg.closeInputCredDlg();

        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        int countLearnedApp = newCD.managePwdDialog.countElementsInManagePassword();
        Verification.verifyTrue(driver, (countLearnedApp - 3 == 0),
                "Verification: No app credentials are displayed", false);
        newCD.managePwdDialog.closeMangePasswordsDlg();

        // Learn and replay app
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyFalse(driver, newCD.createPrivacyKeyDlg.waitDlgLoad(),
                "Verification: Create dialog is not displayed", false);
        Verification.verifyTrue(driver, newCD.inputCredDlg.waitDlgLoad(),
                CommonMessage.INPUT_CRED_DLG_IS_DISPLAYED_MSG, false);
        newCD.inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
        Verification.verifyTrue(driver, SelWindowUtil.isNewWindowDisplay(driver, windowHdls),
                CommonMessage.NEW_WINDOW_IS_OPENED_MSG, false);
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        newCD.waitAuthMsgDisappear();
        Verification.verifyTrue(driver, newCD.appPages.isBEPlay(appURL),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);
        SelWindowUtil.closeCurrentWindow(driver, windowHdls);
        SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);

        newCD.logout();
        Verification.verifyTrue(driver, newCD.loginPage.waitForPageLoad(),
                CommonMessage.SIGNON_PAGE_DISPLAYED_MSG, true);
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testCreatePKUI(WebDriver driver, String newPK) throws IOException
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        // ------------------------------------------------------------

        String appName = Setup.getDefaultApp();
        String dockURL = TestAccountManager.getCDPUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        ArrayList<String> windowHdls = new ArrayList<>();

        // For IE, Clear PK
        if (Setup.getBrowserType().equalsIgnoreCase("ie"))
        {
            launchLocalChromeAndChangePK(newPK);
        }

        newCD.loginCDP(dockURL, username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        //OPTION: Add default app
        NewBESharedStep.addDefaultAppFromCatalog(driver, windowHdls, appName);

        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyTrue(driver, newCD.privacyKeyDlg.waitPKDlgLoad(),
                CommonMessage.INPUT_PK_DISPLAYED_MSG, false);

        // Step: Select Delete Start Over link
        Verification.logStep("Select Delete and Start Over link");
        newCD.privacyKeyDlg.clickClearAllPasswords();
        Verification.verifyTrue(driver, newCD.privacyKeyDlg.waitConfirmDlgLoad(),
                CommonMessage.CONFIRM_DLG_DISPLAYED_MSG);
        Verification.takeScreenshot(driver, "Confirm Clear PK dialog");

        // Step: Cancel clearing privacy key from dialog
        Verification.logStep("Cancel clearing privacy key");
        newCD.privacyKeyDlg.closeConfirmDlg();
        Verification.takeScreenshot(driver, "Input Privacy Key dialog");
        Verification.verifyFalse(driver, newCD.privacyKeyDlg.waitConfirmDlgLoad(),
                CommonMessage.CONFIRM_DLG_CLOSED_MSG);
        Verification.verifyTrue(driver, newCD.privacyKeyDlg.waitPKDlgLoad(), CommonMessage.INPUT_PK_DISPLAYED_MSG);

        // Step: Confirm clearing privacy key from dialog
        Verification.logStep("Confirm clearing privacy key");
        newCD.privacyKeyDlg.clearAllPasswords();
        Verification.verifyFalse(driver, newCD.privacyKeyDlg.waitConfirmDlgLoad(),
                CommonMessage.CONFIRM_DLG_CLOSED_MSG);
        Verification.verifyTrue(driver, newCD.privacyKeyDlg.waitPKDlgClose(), CommonMessage.INPUT_PK_CLOSED_MSG);
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.waitDlgLoad(),
                CommonMessage.CREATE_PK_DISPLAYED_MSG, true);
        Verification.takeScreenshot(driver, "New Privacy Key dialog");
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.isDisableContinueButtonDisplay(),
                CommonMessage.CONTINUE_BTN_DISABLED_MSG, true);

        // Close PK popup
        Verification.logStep("Close New PK dialog");
        newCD.createPrivacyKeyDlg.closeCreatePKPopup();
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.waitDlgClose(), CommonMessage.CREATE_PK_DLG_IS_CLOSED_MSG, false);

        // Step: Re-open Create PK dialog
        Verification.logStep("Click on an app");
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.waitDlgLoad(), CommonMessage.CREATE_PK_DISPLAYED_MSG, true);

        // Step: Verify user without privacy key login to dock
        driver = NewBESharedStep.restartBrowser(driver, windowHdls, true);
        newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        newCD.loginCDP(dockURL, username, pass);
        
        if (!newCD.createPrivacyKeyDlg.waitDlgLoad() && !newCD.privacyKeyDlg.waitPKDlgLoad())
        {
            newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        }
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.waitDlgLoad(), CommonMessage.CREATE_PK_DISPLAYED_MSG);

        // Step: Save with main key only
        Verification.logStep("Save with main key only");
        newCD.createPrivacyKeyDlg.fillPK("PingOne1");
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.isDisableContinueButtonDisplay(), CommonMessage.CONTINUE_BTN_DISABLED_MSG);
        Verification.verifyFalse(driver, newCD.createPrivacyKeyDlg.isValidateIconDisplay(), CommonMessage.PASS_VALIDATE_DISPLAYED_MSG);
        newCD.createPrivacyKeyDlg.closeCreatePKPopup();

        // Step: Save with confirmation key only
        Verification.logStep("Save confirmation key only");
        driver.navigate().refresh();
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        newCD.createPrivacyKeyDlg.fillConfirmPK(NEW_PK);
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.isDisableContinueButtonDisplay(),
                CommonMessage.CONTINUE_BTN_DISABLED_MSG);
        Verification.verifyFalse(driver, newCD.createPrivacyKeyDlg.isValidateIconDisplay(),
                CommonMessage.PASS_VALIDATE_DISPLAYED_MSG);
        newCD.createPrivacyKeyDlg.closeCreatePKPopup();

        // Step: Save with mismatch key
        Verification.logStep("Save with mismatch key");
        driver.navigate().refresh();
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        newCD.createPrivacyKeyDlg.fillPK("PingOne1");
        newCD.createPrivacyKeyDlg.fillConfirmPK(NEW_PK);
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.isDisableContinueButtonDisplay(),
                CommonMessage.CONTINUE_BTN_DISABLED_MSG);
        Verification.verifyFalse(driver, newCD.createPrivacyKeyDlg.isValidateIconDisplay(),
                CommonMessage.PASS_VALIDATE_DISPLAYED_MSG);
        newCD.createPrivacyKeyDlg.closeCreatePKPopup();

        // Step: Save with key less than 8 charaters
        Verification.logStep("Save with key less than 8 charaters");
        driver.navigate().refresh();
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        newCD.createPrivacyKeyDlg.fillPK("PingOn1");
        newCD.createPrivacyKeyDlg.fillConfirmPK("PingOn1");
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.isDisableContinueButtonDisplay(),
                CommonMessage.CONTINUE_BTN_DISABLED_MSG);
        Verification.verifyFalse(driver, newCD.createPrivacyKeyDlg.isValidateIconDisplay(),
                CommonMessage.PASS_VALIDATE_DISPLAYED_MSG);
        newCD.createPrivacyKeyDlg.closeCreatePKPopup();

        // Step: Save with key without lower-case letter
        Verification.logStep("Save with key without lower-case letter");
        driver.navigate().refresh();
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        newCD.createPrivacyKeyDlg.fillPK("PINGONE1");
        newCD.createPrivacyKeyDlg.fillConfirmPK("PINGONE1");
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.isDisableContinueButtonDisplay(),
                CommonMessage.CONTINUE_BTN_DISABLED_MSG);
        Verification.verifyFalse(driver, newCD.createPrivacyKeyDlg.isValidateIconDisplay(),
                CommonMessage.PASS_VALIDATE_DISPLAYED_MSG);
        newCD.createPrivacyKeyDlg.closeCreatePKPopup();

        // Step: Save with key without upper-case letter
        Verification.logStep("Save with key without upper-case letter");
        driver.navigate().refresh();
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        newCD.createPrivacyKeyDlg.fillPK("pingone1");
        newCD.createPrivacyKeyDlg.fillConfirmPK("pingone1");
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.isDisableContinueButtonDisplay(),
                CommonMessage.CONTINUE_BTN_DISABLED_MSG);
        Verification.verifyFalse(driver, newCD.createPrivacyKeyDlg.isValidateIconDisplay(),
                CommonMessage.PASS_VALIDATE_DISPLAYED_MSG);
        newCD.createPrivacyKeyDlg.closeCreatePKPopup();

        // Step: Save with key without digit
        Verification.logStep("Save with key without digit");
        driver.navigate().refresh();
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        newCD.createPrivacyKeyDlg.fillPK("PingOneA");
        newCD.createPrivacyKeyDlg.fillConfirmPK("PingOneA");
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.isDisableContinueButtonDisplay(),
                CommonMessage.CONTINUE_BTN_DISABLED_MSG);
        Verification.verifyFalse(driver, newCD.createPrivacyKeyDlg.isValidateIconDisplay(),
                CommonMessage.PASS_VALIDATE_DISPLAYED_MSG);
        newCD.createPrivacyKeyDlg.closeCreatePKPopup();

        // Step: Create new privacy key
        Verification.logStep("Create new privacy key");
        driver.navigate().refresh();
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        newCD.createPrivacyKeyDlg.fillPK(TestAccountManager.getPrivacyKey());
        newCD.createPrivacyKeyDlg.fillConfirmPK(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.isEnableContinueButtonDisplay(),
                "Verification: Continue button is enabled");
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.isValidateIconDisplay(),
                "Verification: Pass Validate icon is displayed");
        newCD.createPrivacyKeyDlg.clickContinue();
        Verification.verifyTrue(driver, newCD.createPrivacyKeyDlg.waitDlgClose(),
                CommonMessage.CREATE_PK_DLG_IS_CLOSED_MSG);
        Verification.verifyTrue(driver, newCD.inputCredDlg.waitDlgLoad(),
                CommonMessage.INPUT_CRED_DLG_IS_DISPLAYED_MSG, false);
        newCD.inputCredDlg.closeInputCredDlg();

        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyFalse(driver, newCD.createPrivacyKeyDlg.waitDlgLoad(),
                "Verification: Create dialog is not displayed", false);
        Verification.verifyTrue(driver, newCD.inputCredDlg.waitDlgLoad(),
                CommonMessage.INPUT_CRED_DLG_IS_DISPLAYED_MSG, false);
        newCD.inputCredDlg.closeInputCredDlg();

        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        newCD.managePwdDialog.waitManagePasswordDlgLoad();
        int countLearnedApp = newCD.managePwdDialog.countElementsInManagePassword();
        Verification.verifyTrue(driver, (countLearnedApp - 3 == 0),
                "Verification: No app credentials are displayed", false);
        newCD.managePwdDialog.closeMangePasswordsDlg();

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testVerifyPKWith2Accounts(WebDriver driver)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        String appName = Setup.getDefaultApp();
        String password = TestAccountManager.getPassword();

        String username2;
        String url2;
        String username1 = TestAccountManager.getUserName();
        String url1 = TestAccountManager.getCDPUrl();

        switch (Setup.getEnvironment())
        {
            case "test":
                username2 = "qtran+ntest06@pingidentity.com";
                url2 = "https://test-desktop.pingone.com/cd-1004813878.pingidentity";
                break;
            case "ort":
                username2 = "qtran+ortnew06@pingidentity.com";
                url2 = "https://ort-desktop.pingone.com/cd-142724332.pingidentity";
                break;
            default:
                username2 = "qtran+prodnew07@pingidentity.com";
                url2 = "https://desktop.pingone.com/cd-1599863711.pingidentity";
                break;
        }

        // Launch CDP by user1
        newCD.loginCDP(url1, username1, password);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        //OPTION: Add default app
        NewBESharedStep.addDefaultAppFromCatalog(driver, windowHdls, appName);

        newCD.passPKPopups(TestAccountManager.getPrivacyKey(), windowHdls, false);
        Verification.logStep("Logout " + username1);
        newCD.logout();

        Verification.logStep("Login to dock by: " + username2);
        newCD.loginCDP(url2, username2, password);

        //OPTION: Add default app
        NewBESharedStep.addDefaultAppFromCatalog(driver, windowHdls, appName);

        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyTrue(driver, (newCD.privacyKeyDlg.waitPKDlgLoad() || newCD.createPrivacyKeyDlg.waitDlgLoad()),
                CommonMessage.CREATE_PK_DISPLAYED_MSG, false);

        Verification.logStep("Pass over PK dialogs");
        newCD.passPKeyDlg(TestAccountManager.getPrivacyKey());
        newCD.bypassLearnOrReplay(windowHdls, false);

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testChangePKAndReopenBrowser(WebDriver driver) throws IOException
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        // ------------------------------------------------------------

        String appName = Setup.getDefaultApp();
        String dockURL = TestAccountManager.getCDPUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        ArrayList<String> windowHdls = new ArrayList<>();

        newCD.loginCDP(dockURL, username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        NewBESharedStep.preSetupTest(driver, windowHdls);
        
        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        if (newCD.managePwdDialog.isAppExistingInManagePassword(appName))
        {
            newCD.managePwdDialog.removeAppSecret(appName);
            Verification.verifyFalse(driver, newCD.managePwdDialog.isAppExistingInManagePassword(appName),
                    CommonMessage.APP_IS_REMOVED_OUT_OF_MANAGE_PSW_MSG, false);
        }
        newCD.managePwdDialog.closeMangePasswordsDlg();
        newCD.logout();

        // Restart browser to start test
        driver = NewBESharedStep.restartBrowser(driver, windowHdls, true);
        newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        newCD.loginCDP(dockURL, username, pass);

        // OPTION: Pass PK dialog
        newCD.passPKPopups(TestAccountManager.getPrivacyKey(), windowHdls, false);

        // STEP: Learn app credential
        Verification.logStep("Learn " + appName);
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyTrue(driver, newCD.inputCredDlg.waitDlgLoad(),
                CommonMessage.INPUT_CRED_DLG_IS_DISPLAYED_MSG, false);
        newCD.inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
        Verification.verifyTrue(driver, SelWindowUtil.isNewWindowDisplay(driver, windowHdls),
                CommonMessage.NEW_WINDOW_IS_OPENED_MSG, false);
        SelWindowUtil.closeNewWindow(driver, windowHdls);

        // STEP: Change Privacy key
        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        newCD.managePwdDialog.waitManagePasswordDlgLoad();
        Verification.verifyTrue(driver, newCD.managePwdDialog.isAppExistingInManagePassword(appName),
                        CommonMessage.APP_IS_DISPLAYED_IN_MANAGE_PSW_MSG, false);
        int countLearnedApp = newCD.managePwdDialog.countElementsInManagePassword();
        Verification.logSubStep("Number of apps: " + (countLearnedApp - 3));
        newCD.managePwdDialog.updatePrivacyKeyAndSave(NEW_PK);
        Verification.verifyTrue(driver, newCD.waitForUpdatePKSuccessMsgLoad(),
                CommonMessage.PK_IS_UPDATED_MSG, false);
        newCD.waitForUpdatePKSuccessMsgInvisible();
        newCD.managePwdDialog.closeMangePasswordsDlg();
        newCD.logout();

        // STEP: Reopen browser
        driver = NewBESharedStep.restartBrowser(driver, windowHdls, true);
        newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        newCD.loginCDP(dockURL, username, pass);

        // STEP: Play the app
        if (!newCD.privacyKeyDlg.waitPKDlgLoad() && !newCD.createPrivacyKeyDlg.waitDlgLoad())
        {
            Verification.logSubStep("Click on " + appName);
            newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        }
        newCD.passPKeyDlg(NEW_PK);
        SelWindowUtil.closeNewWindow(driver, windowHdls);

        // STEP: Check number of apps in Manage Password
        Verification.logStep("Verify Manage Password");
        newCD.goToManagePasswords(NEW_PK);
        newCD.managePwdDialog.waitManagePasswordDlgLoad();
        Verification.verifyTrue(driver, newCD.managePwdDialog.isAppExistingInManagePassword(appName),
                        CommonMessage.APP_IS_DISPLAYED_IN_MANAGE_PSW_MSG, false);
        int recountLearnedApp = newCD.managePwdDialog.countElementsInManagePassword();
        Verification.verifyTrue(driver, (countLearnedApp == recountLearnedApp),
                "Number of learned apps: " + (countLearnedApp - 3), false);

        Verification.logStep("Reset privacy key to " + TestAccountManager.getPrivacyKey());
        newCD.managePwdDialog.updatePrivacyKeyAndSave(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, newCD.waitForUpdatePKSuccessMsgLoad(),
                CommonMessage.PK_IS_UPDATED_MSG, false);
        newCD.waitForUpdatePKSuccessMsgInvisible();
        newCD.managePwdDialog.closeMangePasswordsDlg();

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testChangePKOnAnotherBrowserPlayApp(WebDriver driver, boolean hasCred) throws MalformedURLException
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        NewPVExtension pvExtension = PageFactory.initElements(driver, NewPVExtension.class);
        // ------------------------------------------------------------

        String appName = Setup.getDefaultApp();
        String dockURL = TestAccountManager.getCDPUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        ArrayList<String> windowHdls = new ArrayList<>();

        String newPK = NEW_PK;

        // Launch CDP on BROWSER1
        Verification.logStep("Launch the first browser: " + Setup.getBrowserType());
        newCD.loginCDP(dockURL, username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        NewBESharedStep.addDefaultAppFromCatalog(driver, windowHdls, appName);

        // Replay app on BROWSER1
        Verification.logStep("Replay " + appName);
        if (hasCred)
        {
            newCD.passPKPopups(TestAccountManager.getPrivacyKey(), windowHdls, true);
        }
        else
        {
            newCD.passPKPopups(TestAccountManager.getPrivacyKey(), windowHdls, false);
            // Remove credential
            Verification.logStep("Remove credential of " + appName);
            newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
            newCD.managePwdDialog.waitManagePasswordDlgLoad();
            if (newCD.managePwdDialog.isAppExistingInManagePassword(appName))
            {
                newCD.managePwdDialog.removeAppSecret(appName);
            }
            newCD.managePwdDialog.closeMangePasswordsDlg();
        }

        // Launch CDP on BROWSER2
        WebDriver driver2 = SelUtil.initWebDriver();
        ArrayList<String> windowHdls2 = new ArrayList<>();
        NewCloudDesktopPage newCD2 = PageFactory.initElements(driver2, NewCloudDesktopPage.class);
        newCD2.loginCDP(dockURL, username, pass);
        SelWindowUtil.storeWindowHandler(driver2, windowHdls2);

        newCD2.passPKPopups(TestAccountManager.getPrivacyKey(), windowHdls2, false);

        // Change PK on BROWSER2
        newCD2.goToManagePasswords(TestAccountManager.getPrivacyKey());
        newCD2.managePwdDialog.updatePrivacyKeyAndSave(newPK);
        Verification.verifyTrue(driver2, newCD2.waitForUpdatePKSuccessMsgLoad(),
                CommonMessage.PK_IS_UPDATED_MSG, false);
        newCD.waitForUpdatePKSuccessMsgInvisible();
        newCD2.managePwdDialog.closeMangePasswordsDlg();

        // Quit BROWSER2
        newCD2.logout();
        driver2.quit();
        windowHdls2.clear();

        // Back to BROWSER1
        Verification.logStep("Back to the first browser");
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        if (hasCred)
        {
            SelWindowUtil.switchToNewWindow(driver, windowHdls);
        }
        else
        {
            Verification.verifyTrue(driver, newCD.inputCredDlg.waitDlgLoad(),
                    "Verification: Input Credential dialog is displayed.");
            newCD.inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
        }
        Verification.verifyTrue(driver, pvExtension.isPVPopupLoad(),
                "Verification: PV popup is displayed.");
        Verification.verifyTrue(driver, pvExtension.isPingLogoDisplayOnPVPopup(),
                "Verification: Ping icon is displayed on PV popup.");
        Verification.verifyFalse(driver, pvExtension.isHelpBtnDisplayOnPVPopup(),
                "Verification: Help button is not displayed on PV popup.");
        Verification.verifyTrue(driver, pvExtension.isCloseBtnDisplayOnPVPopup(),
                "Verification: Close button is displayed on PV popup.");
        Verification.verifyTrue(driver, pvExtension.getContentOfPVPopup().contentEquals("Please refresh PingOne and re-enter your Privacy Key."),
                "Verification: Please refresh PingOne and re-enter your Privacy Key.");
        Verification.takeScreenshot(driver, "Info dialog");

        if (hasCred)
        {
            Verification.verifyTrue(driver, newCD.isAuthMsgDisplay(),
                    "Verification: Authentication page is still displayed.");

            SelWindowUtil.closeCurrentWindow(driver, windowHdls);
            SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);
        }

        // Refresh and re-enter PK
        Verification.logStep("Refresh and re-enter PK");
        driver.navigate().refresh();
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);
        // Click on app
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyTrue(driver, newCD.privacyKeyDlg.waitPKDlgLoad(),
                "Verification: Input Privacy Key dialog is displayed", false);
        newCD.privacyKeyDlg.fillPKAndSignOn(newPK);
        if (!hasCred)
        {
            Verification.verifyTrue(driver, newCD.inputCredDlg.waitDlgLoad(),
                    "Verification: Input Credential dialog is displayed.");
            newCD.inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
        }
        SelWindowUtil.closeNewWindow(driver, windowHdls);

        // Change PK back to PingOne1
        Verification.logStep("Change Privacy Key back to default");
        newCD.goToManagePasswords(newPK);
        newCD.managePwdDialog.updatePrivacyKeyAndSave(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, newCD.waitForUpdatePKSuccessMsgLoad(),
                CommonMessage.PK_IS_UPDATED_MSG, false);
        newCD.waitForUpdatePKSuccessMsgInvisible();
        newCD.managePwdDialog.closeMangePasswordsDlg();

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    private static void launchLocalChromeAndChangePK(String newPK) throws MalformedURLException
    {
        WebDriver driver = SelUtil.initWebDriver();
        ArrayList<String> windowHdls = new ArrayList<>();
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);

        driver.get(TestAccountManager.getCDPUrl());
        newCD.loginPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
        Verification.verifyTrue(driver, newCD.waitForNewDockLoad(), CommonMessage.CDP_IS_LOADED_MSG,
                        false);
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        //OPTION: Add default app
        NewBESharedStep.addDefaultAppFromCatalog(driver, windowHdls, Setup.getDefaultApp());
        newCD.passPKPopups(TestAccountManager.getPrivacyKey(), windowHdls, false);

        // Change PK
        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        newCD.managePwdDialog.updatePrivacyKeyAndSave(newPK);
        Verification.verifyTrue(driver, newCD.waitForUpdatePKSuccessMsgLoad(),
                CommonMessage.PK_IS_UPDATED_MSG, false);
        newCD.waitForUpdatePKSuccessMsgInvisible();
        newCD.managePwdDialog.closeMangePasswordsDlg();

        // Quit BROWSER2
        newCD.logout();
        driver.quit();
        windowHdls.clear();
    }

}
