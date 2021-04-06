package com.browserextension.selenium.testcases;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.uid.common.base.CommonMessage;
import com.uid.common.utils.SelUtil;
import com.uid.common.utils.SelWindowUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.uid.cloudportal.pages.NewCloudDesktopPage;
import com.uid.common.config.Setup;
import com.uid.common.config.TestAccountManager;
import com.uid.common.config.Verification;

public class NewBEManagePasswordCat
{
    private NewBEManagePasswordCat() {}

    private static String generateRandomUsername()
    {
        return generateRandomPsw() + "@new.com";
    }

    private static String generateRandomPsw()
    {
        String randomStr = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
        return "new" + randomStr;
    }

    public static void testLearnReplayUpdatePKUpdateDeleteCredential(WebDriver driver)
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

        NewBESharedStep.addDefaultAppFromCatalog(driver, windowHdls, appName);
        newCD.passPKPopups(TestAccountManager.getPrivacyKey(), windowHdls, true);

        Verification.logStep("TEST: UPDATE APP CREDENTIAL");
        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());

        String newUserName = generateRandomUsername();
        String newPsw = generateRandomPsw();

        newCD.managePwdDialog.updateAppSecret(appName, newUserName, newPsw);
        Verification.verifyTrue(driver, newCD.waitForUpdateCredSuccessMsgLoad(),
                "Verification: 'You successfully changed your credentials.' message is displayed");
        newCD.waitForUpdateCredSuccessMsgInvisible();
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableSaveButtonDisplay(),
                CommonMessage.SAVE_BTN_DISABLED_MSG, false);
        newCD.managePwdDialog.closeMangePasswordsDlg();
        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        newCD.managePwdDialog.selectAppInManagePsw(appName);
        Verification.verifyTrue(driver, newCD.managePwdDialog.checkCred(newUserName, newPsw),
                "Verification: Username: " + newUserName + " password: " + newPsw, false);
        newCD.managePwdDialog.closeMangePasswordsDlg();

        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        newCD.waitAuthMsgDisappear();
        Verification.verifyTrue(driver, newCD.appPages.isBEPlay(Setup.getDefaultAppUrl()),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);

        SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);

        // TEST: UPDATE PK
        Verification.logStep("Test update privacy key in Manage Pasword");
        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        newCD.managePwdDialog.updatePrivacyKeyAndSave(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, newCD.waitForUpdatePKSuccessMsgLoad(), CommonMessage.PK_IS_UPDATED_MSG);
        newCD.waitForUpdatePKSuccessMsgInvisible();
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableChangeBtnDisplay(), CommonMessage.CHANGE_BTN_DISABLED_MSG);
        Verification.verifyTrue(driver, newCD.managePwdDialog.waitManagePasswordDlgLoad(), CommonMessage.MANAGE_PSW_DISPLAYED_MSG);
        newCD.managePwdDialog.closeMangePasswordsDlg();

        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        newCD.waitAuthMsgDisappear();
        Verification.verifyTrue(driver, newCD.appPages.isBEPlay(Setup.getDefaultAppUrl()),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);
        SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);

        // TEST: UPDATE PK AND APP CREDENTIAL
        Verification.logStep("TEST: UPDATE PK AND APP CREDENTIAL");
        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        newCD.managePwdDialog.updatePrivacyKeyAndSave("PingOne1");
        Verification.verifyTrue(driver, newCD.waitForUpdatePKSuccessMsgLoad(), CommonMessage.PK_IS_UPDATED_MSG);
        newCD.waitForUpdatePKSuccessMsgInvisible();
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableChangeBtnDisplay(), CommonMessage.CHANGE_BTN_DISABLED_MSG);

        newUserName = generateRandomUsername();
        newPsw = generateRandomPsw();

        newCD.managePwdDialog.updateAppSecret(appName, newUserName, newPsw);
        Verification.verifyTrue(driver, newCD.waitForUpdateCredSuccessMsgLoad(),
                "Verification: 'You successfully changed your credentials.' message is displayed");
        newCD.waitForUpdateCredSuccessMsgInvisible();
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableSaveButtonDisplay(),
                CommonMessage.SAVE_BTN_DISABLED_MSG, false);
        newCD.managePwdDialog.closeMangePasswordsDlg();
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        newCD.waitAuthMsgDisappear();
        Verification.verifyTrue(driver, newCD.appPages.isBEPlay(Setup.getDefaultAppUrl()),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);
        SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);

        // TEST: REMOVE APP CREDENTIAL
        Verification.logStep("TEST: REMOVE APP CREDENTIAL");
        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, newCD.managePwdDialog.isAppExistingInManagePassword(appName),
                        CommonMessage.APP_IS_DISPLAYED_IN_MANAGE_PSW_MSG);
        newCD.managePwdDialog.removeAppSecret(appName);
        Verification.verifyTrue(driver, newCD.managePwdDialog.waitManagePasswordDlgLoad(),
                CommonMessage.MANAGE_PSW_DISPLAYED_MSG);
        Verification.verifyFalse(driver, newCD.managePwdDialog.isAppExistingInManagePassword(appName),
                CommonMessage.APP_IS_REMOVED_OUT_OF_MANAGE_PSW_MSG);
        newCD.managePwdDialog.closeMangePasswordsDlg();
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyTrue(driver, newCD.inputCredDlg.waitDlgLoad(), CommonMessage.INPUT_CRED_DLG_IS_DISPLAYED_MSG);
        newCD.inputCredDlg.closeInputCredDlg();

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testSignOnDlg(WebDriver driver)
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

        NewBESharedStep.addDefaultAppFromCatalog(driver, windowHdls, appName);
        newCD.passPKPopups(TestAccountManager.getPrivacyKey(), windowHdls, true);

        Verification.logStep("Go to Manage Password to count number of learned apps");
        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, newCD.managePwdDialog.isAppExistingInManagePassword(appName),
                        CommonMessage.APP_IS_DISPLAYED_IN_MANAGE_PSW_MSG, false);
        int countLearnedApp = newCD.managePwdDialog.countElementsInManagePassword();
        Verification.logStep("   Number of learned apps: " + (countLearnedApp - 3));
        newCD.managePwdDialog.closeMangePasswordsDlg();

        Verification.logStep("Re-login to start testing");
        newCD.logout();
        newCD.loginCDP(dockURL, username, pass);

        // TEST: SIGN ON DIALOG IS NOT DISPLAYED AFTER LOGIN 3 MINUTES
        Verification.logStep("SIG ON DIALOG IS DISPLAYED WHEN OPENING MANAGE PASSWORD AFTER LOGIN 3 MINUTES");
        Verification.logSubStep("Waiting 3 minutes");
        SelUtil.wait(180, "Test timeout");
        newCD.clickSettingMenu();
        newCD.clickMenu(NewCloudDesktopPage.MenuName.MANAGE_PASSWORDS.getMenu());
        Verification.verifyFalse(driver, newCD.waitReAuthenDlgLoad(), CommonMessage.SIGNON_DLG_NOT_INVOKED_MSG, false);
        Verification.verifyTrue(driver, newCD.confirmPKDlg.waitDlgLoad(),
                "Verification: Confirm Privacy Key dialog is displayed", false);
        newCD.confirmPKDlg.fillPKAndConfirm(TestAccountManager.getPrivacyKey());

        // TEST: SIGN ON DIALOG IS NOT DISPLAYED WHEN CLICK SAVE PRIVACY KEY
        // AFTER LOGIN 3 MINUTES
        Verification.logStep("SIGN ON DIALOG IS NOT DISPLAYED WHEN CLICK SAVE PRIVACY KEY AFTER LOGIN 3 MINUTES");
        Verification.logSubStep("Waiting 3 minutes");
        SelUtil.wait(180, "Test timeout");

        Verification.logSubStep("Saving privacy key");
        newCD.managePwdDialog.updatePrivacyKeyAndSave(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, newCD.waitForUpdatePKSuccessMsgLoad(), CommonMessage.PK_IS_UPDATED_MSG);

        Verification.verifyFalse(driver, newCD.waitReAuthenDlgLoad(), CommonMessage.SIGNON_DLG_NOT_INVOKED_MSG, false);

        // TEST: SIGN ON DIALOG IS NOT DISPLAYED WHEN CLICK SAVE CREDENTIAL
        // AFTER LOGIN 3 MINUTES
        Verification.logStep("SIGN ON DIALOG IS NOT DISPLAYED WHEN CLICK SAVE CREDENTIAL AFTER LOGIN 3 MINUTES");
        Verification.logSubStep("Saving credential of " + appName);
        newCD.managePwdDialog.updateAppSecret(appName, "new@new.com", "new12345");
        Verification.verifyFalse(driver, newCD.waitReAuthenDlgLoad(),
                CommonMessage.SIGNON_DLG_NOT_INVOKED_MSG, false);

        newCD.managePwdDialog.closeMangePasswordsDlg();

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testConfirmPrivacyKeyDialog(WebDriver driver)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        // ------------------------------------------------------------

        String dockURL = TestAccountManager.getCDPUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        ArrayList<String> windowHdls = new ArrayList<>();

        newCD.loginCDP(dockURL, username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        NewBESharedStep.preSetupTest(driver, windowHdls);

        // Step: Open manage password dialog -> confirm PK dialog appear
        newCD.clickSettingMenu();
        newCD.clickMenu(NewCloudDesktopPage.MenuName.MANAGE_PASSWORDS.getMenu());
        Verification.verifyTrue(driver, newCD.confirmPKDlg.waitDlgLoad(),
                "Verification: Confirm Privacy Key dialog is displayed", false);
        Verification.takeScreenshot(driver, "Confirm Privacy Key dialog");

        newCD.confirmPKDlg.closeConfirmPKDlg();
        Verification.verifyTrue(driver, newCD.confirmPKDlg.waitDlgClose(), CommonMessage.CONFIRM_PK_CLOSED_MSG, false);

        // Step: Re-open manage password dialog -> confirm PK dialog appear
        newCD.clickSettingMenu();
        newCD.clickMenu(NewCloudDesktopPage.MenuName.MANAGE_PASSWORDS.getMenu());
        newCD.confirmPKDlg.clickCancel();
        Verification.verifyTrue(driver, newCD.confirmPKDlg.waitDlgClose(), CommonMessage.CONFIRM_PK_CLOSED_MSG, false);

        // Step: Fill invalid privacy key
        Verification.logStep("Fill invalid privacy key");
        newCD.goToManagePasswords("WrongPKey");
        Verification.verifyTrue(driver, newCD.confirmPKDlg.waitDlgLoad(),
                "Verification: Confirm Privacy Key dialog is still displayed", false);
        Verification.verifyTrue(driver, newCD.confirmPKDlg.isInvalidErrorDisplay(), "Verification: Try again error is displayed");
        Verification.takeScreenshot(driver, "Confirm Privacy Key dialog with Error message");

        // Step: Fill valid privacy key
        Verification.logStep("Fill valid privacy key");
        newCD.confirmPKDlg.fillPKAndConfirm(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, newCD.confirmPKDlg.waitDlgClose(), CommonMessage.CONFIRM_PK_CLOSED_MSG, false);
        Verification.verifyTrue(driver, newCD.managePwdDialog.waitManagePasswordDlgLoad(), CommonMessage.MANAGE_PSW_DISPLAYED_MSG, false);
        Verification.takeScreenshot(driver, CommonMessage.MANAGE_PSW_DISPLAYED_MSG);

        Verification.logStep("Close Manage Password");
        newCD.managePwdDialog.closeMangePasswordsDlg();

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testInputCredDlg(WebDriver driver)
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
        newCD.managePwdDialog.removeAppSecret(appName);

        // Step: Fill credential and click Cancel
        Verification.logStep("Fill credential and Cancel on " + appName);
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyTrue(driver, newCD.inputCredDlg.waitDlgLoad(), CommonMessage.INPUT_CRED_DLG_IS_DISPLAYED_MSG);
        Verification.takeScreenshot(driver, "Input Credential dialog");
        newCD.inputCredDlg.inputCredentialsAndCancel(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
        Verification.verifyFalse(driver, newCD.inputCredDlg.waitDlgLoad(), CommonMessage.INPUT_CRED_DLG_IS_CLOSED_MSG);
        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, newCD.managePwdDialog.waitManagePasswordDlgLoad(), CommonMessage.MANAGE_PSW_DISPLAYED_MSG);
        Verification.verifyFalse(driver, newCD.managePwdDialog.isAppExistingInManagePassword(appName),
                CommonMessage.APP_IS_NOT_DISPLAYED_IN_MANAGE_PSW_MSG);
        newCD.managePwdDialog.closeMangePasswordsDlg();

        Verification.logStep("Verify Input Credential dialog");
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyTrue(driver, newCD.inputCredDlg.waitDlgLoad(), CommonMessage.INPUT_CRED_DLG_IS_DISPLAYED_MSG);
        Verification.verifyTrue(driver, newCD.inputCredDlg.isDisableSaveButtonDisplay(), CommonMessage.SAVE_BTN_DISABLED_MSG);

        Verification.logSubStep("Step: Click on disabled Save button");
        newCD.managePwdDialog.clickDisabledSaveCred();
        Verification.verifyTrue(driver, newCD.inputCredDlg.waitDlgLoad(), CommonMessage.INPUT_CRED_DLG_IS_DISPLAYED_MSG);

        Verification.logSubStep("Step: Fill username only");
        newCD.inputCredDlg.fillUserName(Setup.SAMPLE_USERNAME);
        Verification.verifyTrue(driver, newCD.inputCredDlg.isDisableSaveButtonDisplay(), CommonMessage.SAVE_BTN_DISABLED_MSG);
        newCD.managePwdDialog.clickDisabledSaveCred();
        Verification.verifyTrue(driver, newCD.inputCredDlg.waitDlgLoad(), CommonMessage.INPUT_CRED_DLG_IS_DISPLAYED_MSG);

        Verification.logSubStep("Step: Fill password only");
        newCD.inputCredDlg.closeInputCredDlg();
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        newCD.inputCredDlg.fillPassword(Setup.SAMPLE_PSW);
        Verification.verifyTrue(driver, newCD.inputCredDlg.isDisableSaveButtonDisplay(), CommonMessage.SAVE_BTN_DISABLED_MSG);
        newCD.managePwdDialog.clickDisabledSaveCred();
        Verification.verifyTrue(driver, newCD.inputCredDlg.waitDlgLoad(), CommonMessage.INPUT_CRED_DLG_IS_DISPLAYED_MSG);

        Verification.logSubStep("Step: Fill username and password");
        newCD.inputCredDlg.closeInputCredDlg();
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        newCD.inputCredDlg.fillUserName(Setup.SAMPLE_USERNAME);
        newCD.inputCredDlg.fillPassword(Setup.SAMPLE_PSW);
        Verification.verifyTrue(driver, newCD.inputCredDlg.isEnableSaveButtonDisplay(), CommonMessage.SAVE_BTN_ENABLED_MSG);
        newCD.inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
        Verification.verifyFalse(driver, newCD.inputCredDlg.waitDlgLoad(), CommonMessage.INPUT_CRED_DLG_IS_CLOSED_MSG);

        SelWindowUtil.closeCurrentWindowAndSwitchTo(driver,windowHdls, 0);
        
        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testUpdatePKDlg(WebDriver driver)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        // ------------------------------------------------------------

        String privacyKey = "PingOne1";
        String appName = Setup.getDefaultApp();
        String dockURL = TestAccountManager.getCDPUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        ArrayList<String> windowHdls = new ArrayList<>();

        newCD.loginCDP(dockURL, username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        NewBESharedStep.preSetupTest(driver, windowHdls);
        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        newCD.managePwdDialog.removeAppSecret(appName);

        // Step: Verify Manage Password dialog
        Verification.logStep("Verify Manage Password dialog");
        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, newCD.managePwdDialog.waitManagePasswordDlgLoad(),
                CommonMessage.MANAGE_PSW_DISPLAYED_MSG);
        Verification.verifyTrue(driver, newCD.managePwdDialog.getContentHeader().contentEquals("Why You're Here"),
                "OVERVIEW tab is displayed");

        // Step: Verify Update PK dialog
        Verification.logStep("Verify Update PK dialog");
        newCD.managePwdDialog.gotoPrivacyKey();
        Verification.verifyTrue(driver, newCD.managePwdDialog.getContentHeader().contentEquals("Privacy Key"),
                " - Verification: PRIVACY KEY tab is displayed");
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableChangeBtnDisplay(),
                CommonMessage.CHANGE_BTN_DISABLED_MSG);

        // Step: Save with main key only
        Verification.logSubStep("Step: Save with main key only");
        newCD.managePwdDialog.fillChangePK(privacyKey);
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableChangeBtnDisplay(),
                CommonMessage.CHANGE_BTN_DISABLED_MSG, true);

        // Step: Save with confirmation key only
        Verification.logSubStep("Step: Save confirmation key only");
        newCD.managePwdDialog.gotoOverview();
        newCD.managePwdDialog.gotoPrivacyKey();
        newCD.managePwdDialog.fillConfirmNewPK(privacyKey);
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableChangeBtnDisplay(),
                CommonMessage.CHANGE_BTN_DISABLED_MSG, true);

        // Step: Save with mismatch key
        Verification.logSubStep("Step: Save with mismatch key");
        newCD.managePwdDialog.fillChangePK(privacyKey);
        newCD.managePwdDialog.fillConfirmNewPK("PingOne2");
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableChangeBtnDisplay(),
                CommonMessage.CHANGE_BTN_DISABLED_MSG, true);

        // Step: Save with key less than 8 charaters
        Verification.logSubStep("Step: Save with key less than 8 charaters");
        newCD.managePwdDialog.updatePrivacyKey("PingOn1");
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableChangeBtnDisplay(),
                CommonMessage.CHANGE_BTN_DISABLED_MSG, true);

        // Step: Save with key without lower-case letter
        Verification.logSubStep("Step: Save with key without lower-case letter");
        newCD.managePwdDialog.updatePrivacyKey("PINGONE1");
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableChangeBtnDisplay(),
                CommonMessage.CHANGE_BTN_DISABLED_MSG, true);

        // Step: Save with key without upper-case letter
        Verification.logSubStep("Step: Save with key without upper-case letter");
        newCD.managePwdDialog.updatePrivacyKey("pingone1");
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableChangeBtnDisplay(),
                CommonMessage.CHANGE_BTN_DISABLED_MSG, true);

        // Step: Save with key without digit
        Verification.logSubStep("Step: Save with key without digit");
        newCD.managePwdDialog.updatePrivacyKey("PingOneA");
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableChangeBtnDisplay(),
                CommonMessage.CHANGE_BTN_DISABLED_MSG, true);

        // Step: Save with valid PK
        Verification.logSubStep("Step: Update and save privacy key");
        newCD.managePwdDialog.updatePrivacyKey(privacyKey);
        Verification.verifyTrue(driver, newCD.managePwdDialog.isEnableChangeBtnDisplay(),
                CommonMessage.CHANGE_BTN_ENABLED_MSG, true);

        newCD.managePwdDialog.closeMangePasswordsDlg();

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testUpdateAppCredDlg(WebDriver driver)
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
        newCD.managePwdDialog.removeAppSecret(appName);

        // Step: Verify Update App Credetial dialog
        Verification.logStep("Verify Update App Credential dialog");
        newCD.goToManagePasswords(TestAccountManager.getPrivacyKey());
        newCD.managePwdDialog.selectAppInManagePsw(appName);
        Verification.verifyTrue(driver, newCD.managePwdDialog.getContentHeader().contentEquals(appName),
                " - Verification: " + appName + " is displayed");
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableSaveButtonDisplay(),
                CommonMessage.SAVE_BTN_DISABLED_MSG, false);

        Verification.logSubStep("Clear Username and change Password");
        newCD.managePwdDialog.clearUserName();
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableSaveButtonDisplay(),
                CommonMessage.SAVE_BTN_DISABLED_MSG, false);

        Verification.logSubStep("Change Username and clear Password");
        newCD.managePwdDialog.gotoPrivacyKey();
        newCD.managePwdDialog.selectAppInManagePsw(appName);
        newCD.managePwdDialog.clearPassword();
        Verification.verifyTrue(driver, newCD.managePwdDialog.isDisableSaveButtonDisplay(),
                CommonMessage.SAVE_BTN_DISABLED_MSG, false);

        String newUserName = generateRandomUsername();
        String newPsw = generateRandomPsw();

        Verification.logSubStep("Change Username only");
        newCD.managePwdDialog.gotoPrivacyKey();
        newCD.managePwdDialog.selectAppInManagePsw(appName);
        newCD.managePwdDialog.fillUserName(newUserName);
        Verification.verifyTrue(driver, newCD.managePwdDialog.isEnableSaveButtonDisplay(),
                CommonMessage.SAVE_BTN_ENABLED_MSG, false);

        Verification.logSubStep("Change Password only");
        newCD.managePwdDialog.gotoPrivacyKey();
        newCD.managePwdDialog.selectAppInManagePsw(appName);
        newCD.managePwdDialog.fillPassword(newPsw);
        Verification.verifyTrue(driver, newCD.managePwdDialog.isEnableSaveButtonDisplay(),
                CommonMessage.SAVE_BTN_ENABLED_MSG, false);

        Verification.logSubStep("Change Username and Password of " + appName);
        newCD.managePwdDialog.gotoPrivacyKey();
        newCD.managePwdDialog.selectAppInManagePsw(appName);
        newCD.managePwdDialog.fillUserName(newUserName);
        newCD.managePwdDialog.fillPassword(newPsw);
        Verification.verifyTrue(driver, newCD.managePwdDialog.isEnableSaveButtonDisplay(),
                CommonMessage.SAVE_BTN_ENABLED_MSG, false);

        // Step: Verify user Remove app credential and click Cancel
        newCD.managePwdDialog.clickRemoveAppSecretLink(appName);
        Verification.verifyTrue(driver, newCD.managePwdDialog.waitRemoveConfirmDlgLoad(),
                " - Verification: Confirm dialog is displayed");
        Verification.takeScreenshot(driver, "Confirm Remove Credential dialog");
        newCD.managePwdDialog.clickCancelLink();
        Verification.verifyTrue(driver, newCD.managePwdDialog.waitRemoveConfirmDlgClose(),
                " - Verification: Confirm dialog is closed");
        Verification.verifyTrue(driver, newCD.managePwdDialog.waitManagePasswordDlgLoad(),
                CommonMessage.MANAGE_PSW_DISPLAYED_MSG);
        Verification.verifyTrue(driver, newCD.managePwdDialog.isAppExistingInManagePassword(appName),
                        CommonMessage.APP_IS_DISPLAYED_IN_MANAGE_PSW_MSG);

        newCD.managePwdDialog.closeMangePasswordsDlg();

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

}
