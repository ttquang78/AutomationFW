package com.browserextension.selenium.testcases;

import java.util.ArrayList;

import com.uid.cloudportal.pages.NewPVTraining;
import com.uid.common.base.CommonMessage;
import com.uid.common.utils.SelWindowUtil;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.uid.cloudportal.pages.NewCloudDesktopPage;
import com.uid.common.utils.ApplicationData;
import com.uid.common.config.Setup;
import com.uid.common.config.TestAccountManager;
import com.uid.common.config.Verification;
import com.uid.webportal.pages.WebPortalPage;

public class NewBETrainingCat
{
    private NewBETrainingCat() {}

    public static void testTrainingAppByFD(WebDriver driver, ApplicationData appData)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewPVTraining newPVTraining = PageFactory.initElements(driver, NewPVTraining.class);
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        String appName = appData.getAppName();
        String adminURL = TestAccountManager.getAdminUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        Verification.logStep("Training " + appName + " by FD");

        driver.get(adminURL);
        webPortalPage.loginPage.login(username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        webPortalPage.waitForPageLoad();

        webPortalPage.goToMenu(WebPortalPage.WPMenu.APPLICATIONS, WebPortalPage.WPMenu.MY_APPLICATIONS);
        webPortalPage.waitForMyAppMenuLoad();
        webPortalPage.myApplicationsSAMLPage.removeAppAndStartBasicSSOTraining(appName);
        newPVTraining.checkGoToApplicationStep();

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP1_TITLE_MSG, Setup.ButtonName.NEXT, appData.getLoginUrl());
        newPVTraining.checkGoToSignOnStep();

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP2_TITLE_MSG, Setup.ButtonName.NEXT);
        newPVTraining.checkVerifySignOnControls1Step(appData);

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP3_TITLE_MSG, Setup.ButtonName.ACCEPT);
        Verification.verifyTrue(driver, webPortalPage.myApplicationsSAMLPage.waitForPageLoad(), CommonMessage.BACK_TO_WEBPORTAL_MSG);
        Verification.verifyTrue(driver, webPortalPage.myApplicationsSAMLPage.checkTrainedData(appData.getLoginUrl()), CommonMessage.TRAINED_DATA_CORRECT_MSG);

        webPortalPage.myApplicationsSAMLPage.saveTrainedData(NewCloudDesktopPage.Category.OTHER, appName, appData.getLoginUrl());
        Verification.verifyTrue(driver, webPortalPage.myApplicationsSAMLPage.isAppExistingInTrainingList(appName), CommonMessage.APP_IS_TRAINED_MSG);

        webPortalPage.removeApp(appName);

        webPortalPage.logout();
        windowHdls.clear();
        Verification.endCollapseHtml();
        Verification.checkForVerificationErrors();
    }

    public static void testTrainingApp2FieldsByManual(WebDriver driver, ApplicationData appData)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewPVTraining newPVTraining = PageFactory.initElements(driver, NewPVTraining.class);
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        String appName = appData.getAppName();
        String adminURL = TestAccountManager.getAdminUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        Verification.logStep("Training " + appName + " by manual");

        driver.get(adminURL);
        webPortalPage.loginPage.login(username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        webPortalPage.waitForPageLoad();

        webPortalPage.goToMenu(WebPortalPage.WPMenu.APPLICATIONS, WebPortalPage.WPMenu.MY_APPLICATIONS);
        webPortalPage.waitForMyAppMenuLoad();
        webPortalPage.myApplicationsSAMLPage.removeAppAndStartBasicSSOTraining(appName);
        newPVTraining.checkGoToApplicationStep();

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP1_TITLE_MSG, Setup.ButtonName.NEXT, appData.getLoginUrl());
        newPVTraining.checkGoToSignOnStep();

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP2_TITLE_MSG, Setup.ButtonName.NEXT);
        newPVTraining.checkVerifySignOnControls1Step(appData);

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP3_TITLE_MSG, Setup.ButtonName.SELECT_MANUALLY);
        newPVTraining.checkVerifyUsernameFieldStep(appData);

        newPVTraining.trainElement(appData, "username");
        newPVTraining.checkVerifyPasswordFieldStep(appData);

        newPVTraining.trainElement(appData, "password");
        newPVTraining.checkVerifyThirdFieldStep(appData);

        newPVTraining.clickButtonOnPVPopup(Setup.ButtonName.SKIP_THIS_STEP);
        newPVTraining.checkVerifySubmitButtonStep(appData);

        newPVTraining.trainElement(appData, "submit");
        newPVTraining.checkVerifySignOnControls3Step(appData);

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP3_TITLE_MSG, Setup.ButtonName.ACCEPT);
        Verification.verifyTrue(driver, webPortalPage.myApplicationsSAMLPage.waitForPageLoad(),CommonMessage.BACK_TO_WEBPORTAL_MSG);
        Verification.verifyTrue(driver, webPortalPage.myApplicationsSAMLPage.checkTrainedData(appData.getLoginUrl()),CommonMessage.TRAINED_DATA_CORRECT_MSG);

        webPortalPage.myApplicationsSAMLPage.saveTrainedData(NewCloudDesktopPage.Category.OTHER, appName, appData.getLoginUrl());
        Verification.verifyTrue(driver, webPortalPage.myApplicationsSAMLPage.isAppExistingInTrainingList(appName), CommonMessage.APP_IS_TRAINED_MSG);

        webPortalPage.removeApp(appName);

        webPortalPage.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testTrainingAppWhenFailedFD(WebDriver driver, ApplicationData appData)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewPVTraining newPVTraining = PageFactory.initElements(driver, NewPVTraining.class);
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        String adminURL = TestAccountManager.getAdminUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        driver.get(adminURL);
        webPortalPage.loginPage.login(username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        webPortalPage.waitForPageLoad();

        webPortalPage.goToMenu(WebPortalPage.WPMenu.APPLICATIONS, WebPortalPage.WPMenu.MY_APPLICATIONS);
        webPortalPage.waitForMyAppMenuLoad();
        webPortalPage.myApplicationsSAMLPage.startBasicSSOTrainingWizard();
        newPVTraining.checkGoToApplicationStep();

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP1_TITLE_MSG, Setup.ButtonName.NEXT, appData.getLoginUrl());
        newPVTraining.checkGoToSignOnStep();

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP2_TITLE_MSG, Setup.ButtonName.NEXT);
        newPVTraining.checkVerifySignOnControls2Step(appData);

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP3_TITLE_MSG, Setup.ButtonName.SELECT_MANUALLY);
        newPVTraining.checkVerifyUsernameFieldStep(appData);

        newPVTraining.cancelTrainingSession();

        webPortalPage.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testReTrainingAppAfterManualTrain(WebDriver driver, ApplicationData appData)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewPVTraining newPVTraining = PageFactory.initElements(driver, NewPVTraining.class);
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        String adminURL = TestAccountManager.getAdminUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        driver.get(adminURL);
        webPortalPage.loginPage.login(username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        webPortalPage.waitForPageLoad();

        webPortalPage.goToMenu(WebPortalPage.WPMenu.APPLICATIONS, WebPortalPage.WPMenu.MY_APPLICATIONS);
        webPortalPage.waitForMyAppMenuLoad();
        webPortalPage.myApplicationsSAMLPage.startBasicSSOTrainingWizard();
        newPVTraining.checkGoToApplicationStep();

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP1_TITLE_MSG, Setup.ButtonName.NEXT, appData.getLoginUrl());
        newPVTraining.checkGoToSignOnStep();

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP2_TITLE_MSG, Setup.ButtonName.NEXT);
        newPVTraining.checkVerifySignOnControls1Step(appData);

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP3_TITLE_MSG, Setup.ButtonName.SELECT_MANUALLY);
        newPVTraining.checkVerifyUsernameFieldStep(appData);

        newPVTraining.trainElement(appData, "username");
        newPVTraining.checkVerifyPasswordFieldStep(appData);

        newPVTraining.trainElement(appData, "password");
        newPVTraining.checkVerifySubmitButtonStep(appData);

        newPVTraining.trainElement(appData, "submit");
        newPVTraining.checkVerifySignOnControls3Step(appData);

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP3_TITLE_MSG, Setup.ButtonName.TRY_AGAIN);
        newPVTraining.checkVerifyUsernameFieldStep(appData);

        newPVTraining.cancelTrainingSession();

        webPortalPage.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testAccessHelpPopupAndRefreshPageAndCancelTraining(WebDriver driver, ApplicationData appData)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewPVTraining newPVTraining = PageFactory.initElements(driver, NewPVTraining.class);
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        String appName = appData.getAppName();
        String adminURL = TestAccountManager.getAdminUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        driver.get(adminURL);
        webPortalPage.loginPage.login(username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        webPortalPage.waitForPageLoad();

        webPortalPage.goToMenu(WebPortalPage.WPMenu.APPLICATIONS, WebPortalPage.WPMenu.MY_APPLICATIONS);
        webPortalPage.waitForMyAppMenuLoad();
        webPortalPage.myApplicationsSAMLPage.removeAppAndStartBasicSSOTraining(appName);
        newPVTraining.checkGoToApplicationStep();

        Verification.logStep("TEST - ACCESS HELP POPUP");
        newPVTraining.trainingHelpCloseIconTest();
        newPVTraining.checkGoToApplicationStep();
        newPVTraining.trainingHelpOKButtonTest();
        newPVTraining.checkGoToApplicationStep();

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP1_TITLE_MSG, Setup.ButtonName.NEXT, appData.getLoginUrl());
        newPVTraining.checkGoToSignOnStep();

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP2_TITLE_MSG, Setup.ButtonName.NEXT);
        newPVTraining.checkVerifySignOnControls1Step(appData);

        Verification.logStep("TEST - REFRESH PAGE");
        driver.navigate().refresh();
        newPVTraining.checkGoToSignOnStep();

        Verification.logStep("TEST - CANCEL TRAINING WIZARD");
        newPVTraining.closePVPopup();
        newPVTraining.checkCancelStep();

        newPVTraining.clickButtonOnPVPopup(Setup.ButtonName.NEXT);
        Verification.verifyTrue(driver, webPortalPage.myApplicationsSAMLPage.waitForPageLoad(), CommonMessage.BACK_TO_WEBPORTAL_MSG);
        Verification.verifyFalse(driver, webPortalPage.myApplicationsSAMLPage.isAppExistingInTrainingList(appName), CommonMessage.APP_IS_NOT_ADDED_MSG);

        webPortalPage.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testTrainingExistingApp(WebDriver driver, ApplicationData appData)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewPVTraining newPVTraining = PageFactory.initElements(driver, NewPVTraining.class);
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        String adminURL = TestAccountManager.getAdminUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        driver.get(adminURL);
        webPortalPage.loginPage.login(username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        webPortalPage.waitForPageLoad();

        webPortalPage.trainAppAndAddToCDP(appData);

        webPortalPage.goToMenu(WebPortalPage.WPMenu.APPLICATIONS, WebPortalPage.WPMenu.MY_APPLICATIONS);
        webPortalPage.myApplicationsSAMLPage.startBasicSSOTrainingWizard();
        newPVTraining.trainingApp(appData);
        newPVTraining.checkExistingAppStep();

        newPVTraining.cancelTrainingSession();

        webPortalPage.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testDragRibbonWhileTraining(WebDriver driver, ApplicationData appData)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewPVTraining newPVTraining = PageFactory.initElements(driver, NewPVTraining.class);
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        String adminURL = TestAccountManager.getAdminUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        driver.get(adminURL);
        webPortalPage.loginPage.login(username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        webPortalPage.waitForPageLoad();

        webPortalPage.goToMenu(WebPortalPage.WPMenu.APPLICATIONS, WebPortalPage.WPMenu.MY_APPLICATIONS);
        webPortalPage.startBasicSSOTraining();
        newPVTraining.checkGoToApplicationStep();

        Point originalPostion = newPVTraining.getPVPopupLocation();

        Point offset = new Point(20, 20);
        Point newPostion = new Point(originalPostion.x + offset.x, originalPostion.y + offset.y);

        //==========================================================================================
        Verification.logStep("Verify Training popup when page is reloaded while training");

        Verification.logSubStep(CommonMessage.MOVE_DLG_TO_NEW_POSITION_MSG);
        newPVTraining.dragPVPopup(offset.x, offset.y);
        Verification.verifyTrue(driver, newPVTraining.isPVPopupDisplayAtPosition(newPostion),
                CommonMessage.TRAINING_DLG_MOVED_TO_NEW_POSITION_MSG, false);

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP1_TITLE_MSG, Setup.ButtonName.NEXT, appData.getLoginUrl());
        newPVTraining.checkGoToSignOnStep();
        Verification.verifyTrue(driver, newPVTraining.isPVPopupDisplayAtPosition(originalPostion),
                        CommonMessage.TRAINING_DLG_MOVED_BACK_ORIGINAL_POSITION_MSG, false);

        //==========================================================================================
        Verification.logStep("Verify Training popup when page is not reloaded while training");

        Verification.logSubStep(CommonMessage.MOVE_DLG_TO_NEW_POSITION_MSG);
        newPVTraining.dragPVPopup(offset.x, offset.y);
        Verification.verifyTrue(driver, newPVTraining.isPVPopupDisplayAtPosition(newPostion),
                CommonMessage.TRAINING_DLG_MOVED_TO_NEW_POSITION_MSG, false);

        newPVTraining.processTrainingWizard(CommonMessage.TRAINING_STEP2_TITLE_MSG, Setup.ButtonName.NEXT);
        newPVTraining.checkVerifySignOnControls1Step(appData);
        Verification.verifyTrue(driver, newPVTraining.isPVPopupDisplayAtPosition(newPostion),
                CommonMessage.TRAINING_DLG_STILL_IN_NEW_POSITION_MSG, false);

        //==========================================================================================
        Verification.logStep("Verify Training popup when page is reloaded when user refresh browser");

        Verification.logSubStep(CommonMessage.MOVE_DLG_TO_NEW_POSITION_MSG);
        Verification.verifyTrue(driver, newPVTraining.isPVPopupDisplayAtPosition(newPostion),
                CommonMessage.TRAINING_DLG_MOVED_TO_NEW_POSITION_MSG, false);

        driver.navigate().refresh();
        newPVTraining.checkGoToSignOnStep();
        Verification.verifyTrue(driver, newPVTraining.isPVPopupDisplayAtPosition(originalPostion),
                CommonMessage.TRAINING_DLG_MOVED_BACK_ORIGINAL_POSITION_MSG, false);

        newPVTraining.cancelTrainingSession();

        webPortalPage.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testVerifyTrainingConfiguration(WebDriver driver, ApplicationData appData)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        WebPortalPage webPortalPage = PageFactory.initElements(driver, WebPortalPage.class);
        // ------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        String appName = appData.getAppName();
        String dockURL = TestAccountManager.getCDPUrl();
        String username = TestAccountManager.getUserName();
        String pass = TestAccountManager.getPassword();

        newCD.loginCDP(dockURL, username, pass);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        NewBESharedStep.preSetupTest(driver, windowHdls);

        newCD.goToAdmin();
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        webPortalPage.waitForPageLoad();
        webPortalPage.trainAppAndAddToCDP(appData);
        webPortalPage.updateTrainingData(appData);

        Verification.logSubStep("Close Web Portal and re-play app");
        SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);
        newCD.refreshCDP();
        Verification.verifyTrue(driver, newCD.waitForBEActive(), CommonMessage.BE_IS_ACTIVE_MSG, false);

        newCD.replayApp(NewCloudDesktopPage.Category.OTHER.getCat(), appName);
        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        Verification.verifyTrue(driver, newCD.appPages.isBEPlay(appData.getLoginUrl()),
                CommonMessage.APP_IS_REPLAY_BY_FD_MSG, false);

        SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

}
