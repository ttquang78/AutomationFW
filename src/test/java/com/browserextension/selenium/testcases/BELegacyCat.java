package com.browserextension.selenium.testcases;

import java.util.ArrayList;

import com.uid.cloudportal.pages.PVLegacy;
import com.uid.common.base.CommonMessage;
import com.uid.common.utils.SelUtil;
import com.uid.common.utils.SelWindowUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.uid.cloudportal.pages.CloudDesktopPage;
import com.uid.common.utils.ApplicationData;
import com.uid.common.config.Setup;
import com.uid.common.config.TestAccountManager;
import com.uid.common.config.Verification;
import com.uid.webportal.pages.WebPortalPage;

public class BELegacyCat
{
    private BELegacyCat() {}

    public static void testLearnCredentialAndNoSave(WebDriver driver,
            ApplicationData appData, boolean... isPersonal)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        CloudDesktopPage cloudDesktopPage = PageFactory.initElements(driver, CloudDesktopPage.class);
        PVLegacy pvLegacy = PageFactory.initElements(driver, PVLegacy.class);
        // ------------------------------------------------------------
        String appName = Setup.getDefaultApp();

        cloudDesktopPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
        cloudDesktopPage.beDialog.passPKeyOverlay(TestAccountManager.getPrivacyKey());
        cloudDesktopPage.goToView(isPersonal[0]);
        cloudDesktopPage.gotoManageAppPasswords(TestAccountManager.getPrivacyKey());
        cloudDesktopPage.managePwdDialog.removeAppSecret(appName);
        cloudDesktopPage.managePwdDialog.closeDialog();
        cloudDesktopPage.waitSettingIconDisplay();

        // Step: LEARN CREDENTIAL AND QUIT
        Verification.logStep("Launch basicSSO app and close tab after login page is loaded");
        cloudDesktopPage.selectApp(appName);
        SelWindowUtil.switchBackMainTab(driver);
        cloudDesktopPage.waitSettingIconDisplay();
        cloudDesktopPage.selectApp(appName);
        Verification.verifyTrue(driver, pvLegacy.isPVPopupLoad("Sign on like you normally do."),
                "Verification: Overlay is displayed", false);

        // Step: LEARN CREDENTIAL AND CANCEL
        Verification.logStep("Launch basicSSO app and cancel after fill credential");
        Verification.logSubStep("Submit login credentials");
        pvLegacy.appLoginLegacy(appData);
        Verification.logSubStep("Opt to NOT save credentials");
        pvLegacy.verifyPVPopupButtons(Setup.ButtonName.SAVE);
        pvLegacy.closePVPopup();
        // Verify app is logged in normally
        Verification.verifyTrue(driver, cloudDesktopPage.appPage.isBEPlay(appData.getLoginUrl()),
                "--- Verification: " + appName + " can continue login process.", false);
        SelWindowUtil.switchBackMainTab(driver);
        // Step: Verify app credentials are not saved
        cloudDesktopPage.gotoManageAppPasswords(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, cloudDesktopPage.managePwdDialog.findApp(appName) == null,
                CommonMessage.APP_IS_NOT_DISPLAYED_IN_MANAGE_PSW_MSG);

        cloudDesktopPage.managePwdDialog.closeDialog();
        cloudDesktopPage.waitSettingIconDisplay();

        cloudDesktopPage.logout();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testUpdateInvalidPK(WebDriver driver, boolean... isPersonal)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        CloudDesktopPage cloudDesktopPage = PageFactory.initElements(driver, CloudDesktopPage.class);
        // ------------------------------------------------------------

        cloudDesktopPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
        cloudDesktopPage.beDialog.passPKeyOverlay(TestAccountManager.getPrivacyKey());
        cloudDesktopPage.goToView(isPersonal[0]);

        cloudDesktopPage.gotoManageAppPasswords(TestAccountManager.getPrivacyKey());

        // TEST: Update PK by invalid data
        cloudDesktopPage.managePwdDialog.selectItemInManagePassword("Privacy Key");
        Verification.logStep("Update Privacy key: Leave blank for both PK and confirm PK");
        Verification.verifyFalse(driver, cloudDesktopPage.managePwdDialog.isSaveButtonIsDisabled(), CommonMessage.SAVE_BTN_DISABLED_MSG);

        Verification.logStep("Update Privacy key: Input Confirm PK, leave blank main PK");
        cloudDesktopPage.managePwdDialog.inputPK("");
        cloudDesktopPage.managePwdDialog.inputConfirmPK(TestAccountManager.getPrivacyKey());
        Verification.verifyFalse(driver, cloudDesktopPage.managePwdDialog.isSaveButtonIsDisabled(), CommonMessage.SAVE_BTN_DISABLED_MSG);

        Verification.logStep("Update Privacy key: Input main PK, leave blank Confirm PK");
        cloudDesktopPage.managePwdDialog.inputPK(TestAccountManager.getPrivacyKey());
        cloudDesktopPage.managePwdDialog.inputConfirmPK("");
        Verification.verifyFalse(driver, cloudDesktopPage.managePwdDialog.isSaveButtonIsDisabled(), CommonMessage.SAVE_BTN_DISABLED_MSG);

        String[] testNameList = {"Update Privacy key: Input less than 8 characters",
                "Update Privacy key: Input without any digits",
                "Update Privacy key: Input without lower-case letter",
                "Update Privacy key: Input without upper-case letter"};
        String[] testDataList = {"PingOn1", "PingOnee", "PINGONE1", "pingone1"};
        for (int i = 0; i < testNameList.length; i++)
        {
            Verification.logStep(testNameList[i]);
            cloudDesktopPage.managePwdDialog.updatePrivacyKey(testDataList[i]);
            boolean checkPoint = cloudDesktopPage.managePwdDialog.getPrivacyKeyStatus().contentEquals(CommonMessage.LEGACY_PK_REQ_MSG);
            Verification.verifyTrue(driver, checkPoint, CommonMessage.LEGACY_PK_REQ_MSG);
        }

        Verification.logStep("Update Privacy key: Main PK and Confirm PK are not same");
        cloudDesktopPage.managePwdDialog.inputPK(TestAccountManager.getPrivacyKey());
        cloudDesktopPage.managePwdDialog.inputConfirmPK("PingOne2");
        cloudDesktopPage.managePwdDialog.clickSavePK();
        Verification.verifyTrue(driver,
                cloudDesktopPage.managePwdDialog.getPrivacyKeyStatus().contentEquals("The privacy key and confirmation entered don't match."),
                CommonMessage.LEGACY_CORRECT_ERROR_MSG);

        cloudDesktopPage.managePwdDialog.closeDialog();
        cloudDesktopPage.waitSettingIconDisplay();

        cloudDesktopPage.logout();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testLearnReplayUpdateDeleteCredential(WebDriver driver,
            ApplicationData appData, boolean... isPersonal)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        CloudDesktopPage cloudDesktopPage = PageFactory.initElements(driver, CloudDesktopPage.class);
        PVLegacy pvLegacy = PageFactory.initElements(driver, PVLegacy.class);
        // ------------------------------------------------------------
        String appName = appData.getAppName();

        String usernameValue = appData.getAppName() + "@pingidentity.com";
        String passwordValue = "Password123" + appData.getAppName();

        Verification.logStep("<font color=\"blue\">PRE-SETUP</font>");
        cloudDesktopPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
        cloudDesktopPage.beDialog.passPKeyOverlay(TestAccountManager.getPrivacyKey());
        cloudDesktopPage.goToView(isPersonal[0]);
        cloudDesktopPage.removeAppCred(appName);

        // Step: LEARN CREDENTIAL AND SAVE
        Verification.logStep("Launch basicSSO app and save after fill credential");
        cloudDesktopPage.learnAndSaveCred(appData);
        Verification.verifyTrue(driver, cloudDesktopPage.appPage.isBEPlay(appData.getLoginUrl()),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);
        SelWindowUtil.switchBackMainTab(driver);
        // Step: Go back to cloud desktop tab and verify new secret was saved correctly
        cloudDesktopPage.findAppInManagePsw(appName);
        Verification.verifyTrue(driver,
                cloudDesktopPage.managePwdDialog.getAppUsername().contentEquals(usernameValue),
                "Verification: Username saved correctly as " + usernameValue);
        cloudDesktopPage.managePwdDialog.showPassword();
        Verification.verifyTrue(driver,
                cloudDesktopPage.managePwdDialog.getAppPassword().contentEquals(passwordValue),
                "Verification: Password saved correctly as " + passwordValue);
        cloudDesktopPage.managePwdDialog.closeDialog();

        // Step: RE-PLAY APP
        Verification.logStep("Play the application");
        cloudDesktopPage.waitSettingIconDisplay();
        cloudDesktopPage.selectApp(appName);
        Verification.verifyFalse(driver, pvLegacy.isPVPopupLoad(), CommonMessage.LEGACY_OVERLAY_IS_NOT_DISPLAYED_MSG,
                false);
        Verification.verifyTrue(driver, cloudDesktopPage.appPage.isBEPlay(appData.getLoginUrl()),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);
        SelWindowUtil.switchBackMainTab(driver);

        // Step: UPDATE APP CREDENTIALS AND RE-PLAY
        Verification.logStep("Update App credentials and replay");
        String fakeUser = "new@new.com";
        String fakePass = "new123PWD";
        cloudDesktopPage.updateAppCred(appName, fakeUser, fakePass);
        // Step: Verify new credentials were saved correctly
        cloudDesktopPage.findAppInManagePsw(appName);
        Verification.verifyTrue(driver,
                cloudDesktopPage.managePwdDialog.getAppUsername().contentEquals(fakeUser),
                "Verification: Username saved correctly as " + fakeUser);
        cloudDesktopPage.managePwdDialog.showPassword();
        Verification.verifyTrue(driver, cloudDesktopPage.managePwdDialog.getAppPassword().contentEquals(fakePass),
                "Verification: Password saved correctly as " + fakePass);
        cloudDesktopPage.managePwdDialog.closeDialog();
        // Re-play the app
        Verification.logStep("Re-play the application");
        cloudDesktopPage.selectApp(appName);
        Verification.verifyFalse(driver, pvLegacy.isPVPopupLoad(), CommonMessage.LEGACY_OVERLAY_IS_NOT_DISPLAYED_MSG,
                false);
        Verification.verifyTrue(driver, cloudDesktopPage.appPage.isBEPlay(appData.getLoginUrl()),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);
        SelWindowUtil.switchBackMainTab(driver);

        // Step: REMOVE APP CREDENTIAL
        // Step: Remove credentials - Cancel button
        Verification.logStep("Cancel removing credentials");
        cloudDesktopPage.findAppInManagePsw(appName);
        cloudDesktopPage.managePwdDialog.cancelRemoveAppSecret(appName);
        Verification.verifyTrue(driver, cloudDesktopPage.managePwdDialog.findApp(appName) != null,
                "Verification: App is still displayed in manage applications passwords section");

        // Step: Clear secret
        Verification.logStep("Clear Credentials");
        cloudDesktopPage.managePwdDialog.removeAppSecret(appName);
        cloudDesktopPage.managePwdDialog.closeDialog();
        // Step: Verify app is not displayed in manage passwords anymore
        cloudDesktopPage.gotoManageAppPasswords(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, cloudDesktopPage.managePwdDialog.findApp(appName) == null,
                "Verification: App is not displayed in manage applications passwords section");
        cloudDesktopPage.managePwdDialog.closeDialog();

        // Step: Launch basic SSO app and verify learning process is reset
        Verification.logStep("Launch basicSSO app and verify learning process is reset");
        cloudDesktopPage.selectApp(appName);
        pvLegacy.isPVPopupLoad();
        Verification.verifyTrue(driver, pvLegacy.getContentOfPVPopup().contains("Sign on"),
                "Verification: Overlay content contains 'Sign on...'");
        SelWindowUtil.switchBackMainTab(driver);

        // #END#
        // Step: Logout
        cloudDesktopPage.logout();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testLearnReplayUpdatePK(WebDriver driver, ApplicationData appData,
            boolean... isPersonal)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        CloudDesktopPage cloudDesktopPage = PageFactory.initElements(driver, CloudDesktopPage.class);
        PVLegacy pvLegacy = PageFactory.initElements(driver, PVLegacy.class);
        // ------------------------------------------------------------
        String appName = appData.getAppName();

        String newPK = "Pi#$%^&**()_+<scritpts>1";

        Verification.logStep("<font color=\"blue\">PRE-SETUP</font>");
        cloudDesktopPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
        cloudDesktopPage.beDialog.passPKeyOverlay(TestAccountManager.getPrivacyKey());
        cloudDesktopPage.goToView(isPersonal[0]);
        cloudDesktopPage.removeAppCred(appName);

        Verification.logStep("<font color=\"blue\">TEST - Learn app credential</font>");
        cloudDesktopPage.selectApp(appName);
        pvLegacy.appTrainingLegacy(appData);
        Verification.verifyTrue(driver, cloudDesktopPage.appPage.isBEPlay(appData.getLoginUrl()),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);
        SelWindowUtil.switchBackMainTab(driver);

        Verification.logStep("<font color=\"blue\">TEST - Update PK with special letters</font>");
        cloudDesktopPage.gotoManageAppPasswords(TestAccountManager.getPrivacyKey());
        cloudDesktopPage.managePwdDialog.updatePrivacyKey(newPK);
        Verification.verifyTrue(driver, cloudDesktopPage.managePwdDialog.isPKSavingDisplay(),
                "Verification: Status message displays 'Saving new privacy key...'");
        Verification.verifyTrue(driver, cloudDesktopPage.managePwdDialog.isPKSavedDisplay(),
                "Verification: Status message displays 'New privacy key saved!'");
        Verification.verifyTrue(driver, cloudDesktopPage.managePwdDialog.isPKStatusInvisibility(),
                "Verification: Status message is hided");
        cloudDesktopPage.managePwdDialog.closeDialog();

        // Step: Play application
        cloudDesktopPage.selectApp(appName);
        Boolean overlayVisible = pvLegacy.isPVPopupLoad();
        Verification.verifyFalse(driver, overlayVisible, CommonMessage.LEGACY_OVERLAY_IS_NOT_DISPLAYED_MSG, false);
        Verification.verifyTrue(driver, cloudDesktopPage.appPage.isBEPlay(appData.getLoginUrl()),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);
        SelWindowUtil.switchBackMainTab(driver);

        Verification.logStep("<font color=\"blue\">TEST - Update PK, App Credential</font>");
        cloudDesktopPage.gotoManageAppPasswords(newPK);
        cloudDesktopPage.managePwdDialog.updatePrivacyKey(TestAccountManager.getPrivacyKey());
        Verification.verifyTrue(driver, cloudDesktopPage.managePwdDialog.isPKSavingDisplay(),
                "Verification: Status message displays 'Saving new privacy key...'");
        Verification.verifyTrue(driver, cloudDesktopPage.managePwdDialog.isPKSavedDisplay(),
                "Verification: Status message displays 'New privacy key saved!'");
        Verification.verifyTrue(driver, cloudDesktopPage.managePwdDialog.isPKStatusInvisibility(),
                "Verification: Status message is hided");

        // Step: Update App credentials
        WebElement app = cloudDesktopPage.managePwdDialog.findApp(appName);
        Verification.verifyTrue(driver, app != null,
                "Verification: " + appName + " is displayed in Manage Password", false);
        SelUtil.clickElement(driver, app);
        String fakeUser = "new2@new.com";
        String fakePass = "new456PWD";
        cloudDesktopPage.managePwdDialog.updateAppSecret(fakeUser, fakePass);
        Verification.verifyTrue(driver, cloudDesktopPage.managePwdDialog.isPKFormDisplay(),
                "Privacy Key panel is displayed", false);
        cloudDesktopPage.managePwdDialog.closeDialog();

        // Re-play the app
        cloudDesktopPage.waitSettingIconDisplay();
        cloudDesktopPage.selectApp(appName);
        overlayVisible = pvLegacy.isPVPopupLoad();
        Verification.verifyFalse(driver, overlayVisible, CommonMessage.LEGACY_OVERLAY_IS_NOT_DISPLAYED_MSG, false);
        Verification.verifyTrue(driver, cloudDesktopPage.appPage.isBEPlay(appData.getLoginUrl()),
                CommonMessage.APP_IS_REPLAY_BY_BE_MSG, false);
        SelWindowUtil.switchBackMainTab(driver);

        cloudDesktopPage.logout();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testBEInstallation(WebDriver driver)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        CloudDesktopPage cloudDesktopPage = PageFactory.initElements(driver, CloudDesktopPage.class);
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        // ------------------------------------------------------------

        // Step: Login
        cloudDesktopPage.loginPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
        Verification.verifyTrue(driver, cloudDesktopPage.beDialog.waitBEPopupLoad(), CommonMessage.BE_INSTALLATION_IS_DISPLAYED_MSG);

        Verification.logSubStep(CommonMessage.LEGACY_SKIP_INSTALL_AND_CLOSE_MSG);
        cloudDesktopPage.beDialog.clickSkipInstall();
        Verification.verifyTrue(driver, cloudDesktopPage.beDialog.waitBEPopupUnLoad(), CommonMessage.BE_INSTALLATION_IS_CLOSED_MSG);

        cloudDesktopPage.clickSettingIcon();
        Verification.verifyTrue(driver, cloudDesktopPage.isInstallBrowserExtMenuDisplay(), "Verification: 'Install browser extension' menu is displayed.");

        Verification.logStep("Move to Organization view and Refresh page");
        cloudDesktopPage.clickSettingIcon();
        cloudDesktopPage.goToView(false);
        driver.navigate().refresh();
        Verification.verifyTrue(driver, cloudDesktopPage.beDialog.waitBEPopupLoad(), CommonMessage.BE_INSTALLATION_IS_DISPLAYED_MSG);
        Verification.logSubStep(CommonMessage.LEGACY_SKIP_INSTALL_AND_CLOSE_MSG);
        cloudDesktopPage.beDialog.clickSkipInstall();
        Verification.verifyTrue(driver, cloudDesktopPage.beDialog.waitBEPopupUnLoad(), CommonMessage.BE_INSTALLATION_IS_CLOSED_MSG);
        
        cloudDesktopPage.selectApp(Setup.getDefaultApp());
        Verification.verifyTrue(driver, cloudDesktopPage.beDialog.waitBEPopupLoad(), CommonMessage.BE_INSTALLATION_IS_DISPLAYED_MSG);
        Verification.logSubStep(CommonMessage.LEGACY_SKIP_INSTALL_AND_CLOSE_MSG);
        cloudDesktopPage.beDialog.clickSkipInstall();
        Verification.verifyTrue(driver, cloudDesktopPage.beDialog.waitBEPopupUnLoad(), CommonMessage.BE_INSTALLATION_IS_CLOSED_MSG);
        SelWindowUtil.switchBackMainTab(driver);

        Verification.logStep("Move to Personal view and Refresh page");
        cloudDesktopPage.goToView(true);
        driver.navigate().refresh();
        Verification.verifyTrue(driver, cloudDesktopPage.beDialog.waitBEPopupLoad(), CommonMessage.BE_INSTALLATION_IS_DISPLAYED_MSG);
        Verification.logSubStep(CommonMessage.LEGACY_SKIP_INSTALL_AND_CLOSE_MSG);
        cloudDesktopPage.beDialog.clickSkipInstall();
        Verification.verifyTrue(driver, cloudDesktopPage.beDialog.waitBEPopupUnLoad(), CommonMessage.BE_INSTALLATION_IS_CLOSED_MSG);

        Verification.logStep("Move to Web Portal");
        cloudDesktopPage.goToView(false);
        cloudDesktopPage.selectApp("PingOne Web Portal");
        webPortalPage.goToMenu(WebPortalPage.WPMenu.APPLICATIONS, WebPortalPage.WPMenu.MY_APPLICATIONS);
        webPortalPage.myApplicationsSAMLPage.clickAddApplication();
        webPortalPage.myApplicationsSAMLPage.clickNewBasicSSOApp();
        Verification.verifyTrue(driver, webPortalPage.myApplicationsSAMLPage.isRequestInstallBEMsgDisplay(),
                "Verification: Displaying Install BE message.");
        SelWindowUtil.switchBackMainTab(driver);

        cloudDesktopPage.logout();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testSignOnDlg(WebDriver driver)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        CloudDesktopPage cloudDesktopPage = PageFactory.initElements(driver, CloudDesktopPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        // Step: Login
        cloudDesktopPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        // OPTION step: fill privacy key
        cloudDesktopPage.beDialog.passPKeyOverlay(TestAccountManager.getPrivacyKey());

        // TEST: SIG ON DIALOG DOES NOT DISPLAY AFTER LOGIN 3 MINUTES
        Verification.logStep("SIG ON DIALOG IS NOT DISPLAYED WHEN OPENING MANAGE passWord AFTER LOGIN 3 MINUTES");
        Verification.logSubStep("Waiting 3 minutes");
        SelUtil.wait(180, "Test timeout");

        cloudDesktopPage.clickManageAppPasswords();
        Verification.verifyFalse(driver, cloudDesktopPage.isReAuthenticationPopupDisplay(),
                "   Verification: Sign On dialog is not invoked", false);
        Verification.verifyTrue(driver, cloudDesktopPage.beDialog.isConFirmPrivacyKeyDisplay(),
                CommonMessage.LEGACY_PK_IS_DISPLAYED_MSG, false);
        cloudDesktopPage.beDialog.fillConfirmPrivacyKeyInput(TestAccountManager.getPrivacyKey());
        cloudDesktopPage.beDialog.clickConfirmPrivacyKeyButton();
        Verification.verifyTrue(driver, cloudDesktopPage.managePwdDialog.isManagePasswordDisplay(),
                CommonMessage.MANAGE_PSW_DISPLAYED_MSG, false);

        cloudDesktopPage.managePwdDialog.closeDialog();

        cloudDesktopPage.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testConfirmPrivacyKeyDialog(WebDriver driver)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        CloudDesktopPage cloudDesktopPage = PageFactory.initElements(driver, CloudDesktopPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        // Step: Login
        cloudDesktopPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        // OPTION step: fill privacy key
        cloudDesktopPage.beDialog.passPKeyOverlay(TestAccountManager.getPrivacyKey());

        // Step: go to manage password dialog -> confirm PK dialog appear
        cloudDesktopPage.clickManageAppPasswords();
        Verification.verifyTrue(driver, cloudDesktopPage.beDialog.isConFirmPrivacyKeyDisplay(),
                CommonMessage.LEGACY_PK_IS_DISPLAYED_MSG, false);
        Verification.takeScreenshot(driver, "Confirm Privacy Key dialog");

        // Step: Close confirm PK dialog
        Verification.logStep("Close Confirm Privacy Key dialog");
        cloudDesktopPage.beDialog.closeConfirmPrivacyKeyByClickOnCloseIcon();

        // Step: Re-open manage password dialog -> confirm PK dialog appear
        cloudDesktopPage.clickManageAppPasswords();
        Verification.verifyTrue(driver, cloudDesktopPage.beDialog.isConFirmPrivacyKeyDisplay(),
                CommonMessage.LEGACY_PK_IS_DISPLAYED_MSG, false);
        // Step: Fill invalid privacy key
        Verification.logStep("Fill invalid privacy key");
        cloudDesktopPage.beDialog.fillConfirmPrivacyKeyInput("WrongPKey");
        cloudDesktopPage.beDialog.clickConfirmPrivacyKeyButton();
        Verification.verifyTrue(driver, cloudDesktopPage.beDialog.isConFirmPrivacyKeyDisplay(),
                "Verification: Confirm Privacy Key dialog is displayed again", false);
        Verification.verifyTrue(driver, cloudDesktopPage.beDialog.isInvalidErrorDisplay(),
                "Verification: Try again error is displayed", true);
        Verification.takeScreenshot(driver, "Confirm Privacy Key dialog with error message");

        // Step: Fill valid privacy key
        Verification.logStep("Fill valid privacy key");
        cloudDesktopPage.beDialog.fillConfirmPrivacyKeyInput(TestAccountManager.getPrivacyKey());
        cloudDesktopPage.beDialog.clickConfirmPrivacyKeyButton();
        Verification.verifyTrue(driver, cloudDesktopPage.managePwdDialog.isManagePasswordDisplay(),
                "Verification: Manage Password dialog is displayed", false);
        Verification.takeScreenshot(driver, "Manage Password dialog is displayed");

        // Step: Close manage password dialog
        Verification.logStep("Close Manage Password");
        cloudDesktopPage.managePwdDialog.closeDialog();

        cloudDesktopPage.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

}
