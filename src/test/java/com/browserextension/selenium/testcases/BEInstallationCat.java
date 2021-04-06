package com.browserextension.selenium.testcases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.uid.common.base.CommonMessage;
import com.uid.common.base.TestSuiteBase;
import com.uid.common.utils.SelUtil;
import com.uid.common.utils.SelWindowUtil;
import com.uid.common.utils.SikuliUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.uid.cloudportal.pages.NewCloudDesktopPage;
import com.uid.common.config.Setup;
import com.uid.common.config.TestAccountManager;
import com.uid.common.config.Verification;
import com.uid.common.config.WebdriverManager;

public class BEInstallationCat extends TestSuiteBase
{
    private static final String PERSONAL_APP = "Tripit";
    private static final String SCREENSHOT_PATH = ".\\sikuli_screenshots\\";
    private static final String CHROME_SCREENSHOT_PATH = SCREENSHOT_PATH + "chrome\\";
    private static final String FIREFOX_SCREENSHOT_PATH = SCREENSHOT_PATH + "firefox\\";
    private static final String IE_SCREENSHOT_PATH = SCREENSHOT_PATH + "ie\\";
    
    private static final String INSTALL_BE_CANCEL_BTN = "InstallBE_CancelBtn.png";
    private static final String CONFIRM_ADD_EXTENSION = "ConfirmAddExt.png";
    private static final String ADD_EXT_BTN = "AddExtBtn.png";
    private static final String DISABLE_BE_ICON = "DisableBEIcon.png";
    private static final String FIRST_BE_POPUP = "FirstBEPopup.png";
    private static final String SECOND_BE_POPUP = "SecondBEPopup.png";
    private static final String GO_TO_PINGONE_BTN = "GoToPingOneBtn.png";
    private static final String SIGN_OFF_LNK = "SignOffLnk.png";
    private static final String ENABLE_BE_ICON = "EnableBEIcon.png";
    private static final String SIGN_ON_BTN = "SignOnBtn.png";

    private static final String HELLO_POPUP_MSG = "Hello popup is displayed.";
    private static final String HELLO_USER_POPUP_MSG = "Hello <useraccount> popup is displayed.";
    private static final String SIGN_OFF_LINK_DISPLAY_MSG = "Click Sign Off link is displayed.";
    private static final String SIGN_ON_PAGE_DISPLAY_MSG = "Sign On page is displayed.";
    private static final String SIGN_ON_LINK_DISPLAY_MSG = "Click Sign On link is displayed.";
    private static final String GO_TO_PINGONE_MSG = "Click Go To PingOne button.";
    private static final String BE_INSTALL_DISPLAY_MSG = "BE Installation popup is displayed.";
    private static final String CLICK_INACTIVE_ICON_MSG = "Click inactive BE icon";
    private static final String CLICK_ACTIVE_ICON_MSG = "Click active BE icon";
    private static final String CLICK_ADD_EXT_MSG = "Click [Add extension]";
    private static final String INSTALL_BE_FROM_SEARCH_ADD = "Verify BE Install by Search and add Tripit on Search box";
    private static final String INSTALL_BE_FROM_SEARCH_OPEN = "Verify BE Install by Search and open app";

    private BEInstallationCat()
    {
        addExtension = true;
        useTestAccount = true;
        Setup.setMode("new");
        Setup.setMaxRetryCount(0);
    }

    public static void testInstallBEOnChrome(WebDriver driver)
    {
        //Initialize class needed:
        //------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        //------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        newCD.loginPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
        SelWindowUtil.storeWindowHandler(driver, windowHdls);
        Verification.verifyFalse(driver, newCD.installDlg.waitDlgLoad(),
                CommonMessage.BE_INSTALLATION_IS_NOT_DISPLAYED_MSG, false);

        //Cancel Disable popup
        boolean result = SikuliUtils.waitClickElement(CHROME_SCREENSHOT_PATH + INSTALL_BE_CANCEL_BTN);
        Verification.verifyTrue(driver, result, CommonMessage.CANCEL_INSTALL_BE_POPUP_MSG);
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);

        //Step: Check Install Plugin menu in Setting
        Verification.logStep(CommonMessage.VERIFY_ON_SETTING_MENU_MSG);
        newCD.clickSettingMenu();
        Verification.verifyTrue(driver, newCD.waitForInstallBEMenuLoad(),
                CommonMessage.INSTALL_PLUGIN_MENU_IS_HIDED_MSG, false);
        Verification.logSubStep(CommonMessage.CLICK_INSTALL_MSG);
        newCD.clickMenu(NewCloudDesktopPage.MenuName.INSTALL_EXTENSION.getMenu());
        result = SikuliUtils.waitElementDisplay(
                CHROME_SCREENSHOT_PATH + CONFIRM_ADD_EXTENSION, 20);
        Verification.verifyTrue(driver, result,
                CommonMessage.CONFIRM_INSTALL_BE_MSG, false);

        result = SikuliUtils.waitClickElement(CHROME_SCREENSHOT_PATH + INSTALL_BE_CANCEL_BTN);
        Verification.verifyTrue(driver, result, CommonMessage.CANCEL_INSTALL_BE_POPUP_MSG, false);

        //Step: Check Install Plugin dialog when opening an app
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), Setup.getDefaultApp());
        Verification.verifyTrue(driver, newCD.installDlg.waitDlgLoad(),
                CommonMessage.BE_INSTALLATION_IS_DISPLAYED_MSG, false);
        newCD.installDlg.closeDlg();
        Verification.verifyFalse(driver, newCD.installDlg.waitDlgLoad(),
                CommonMessage.BE_INSTALLATION_IS_CLOSED_MSG, false);

        Verification.logStep(INSTALL_BE_FROM_SEARCH_ADD);
        //Remove personal app
        newCD.removePersonalAppFromDock(NewCloudDesktopPage.Category.PERSONAL.getCat(), PERSONAL_APP);
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);
        newCD.addAppFromSearchBox(PERSONAL_APP);
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);
        Verification.verifyTrue(driver, newCD.waitForAppDisplayInCat(NewCloudDesktopPage.Category.PERSONAL.getCat(),
                PERSONAL_APP),
                CommonMessage.APP_IS_ADDED_MSG, false);
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.PERSONAL.getCat(), PERSONAL_APP);
        Verification.verifyTrue(driver, newCD.installDlg.waitDlgLoad(),
                CommonMessage.BE_INSTALLATION_IS_DISPLAYED_MSG, false);
        newCD.installDlg.closeDlg();
        newCD.removePersonalAppFromDock(NewCloudDesktopPage.Category.PERSONAL.getCat(), PERSONAL_APP);

        //Step: Check Install Plugin dialog when opening an app from Search result
        Verification.logStep(INSTALL_BE_FROM_SEARCH_OPEN);
        newCD.openAppFromSearchBox(Setup.getDefaultApp());
        Verification.verifyTrue(driver, newCD.installDlg.waitDlgLoad(),
                CommonMessage.BE_INSTALLATION_IS_DISPLAYED_MSG, false);
        newCD.installDlg.addBE();

        result = SikuliUtils.waitElementDisplay(CHROME_SCREENSHOT_PATH + CONFIRM_ADD_EXTENSION, 20);
        Verification.verifyTrue(driver, result, CommonMessage.CONFIRM_INSTALL_BE_MSG, false);
        
        result = SikuliUtils.waitClickElement(CHROME_SCREENSHOT_PATH + ADD_EXT_BTN);
        Verification.verifyTrue(driver, result, CLICK_ADD_EXT_MSG, false);

        result = SikuliUtils.waitElementDisplay(CHROME_SCREENSHOT_PATH + "ConfirmAddedExt.png");
        Verification.verifyTrue(driver, result,
                "[Confirm added extension] popup is displayed.", false);


        result = SikuliUtils.waitClickElement(CHROME_SCREENSHOT_PATH + DISABLE_BE_ICON);
        Verification.verifyTrue(driver, result, CLICK_INACTIVE_ICON_MSG, false);

        result = SikuliUtils.waitClickElement(CHROME_SCREENSHOT_PATH + FIRST_BE_POPUP);
        Verification.verifyTrue(driver, result, HELLO_POPUP_MSG, false);

        driver.navigate().refresh();
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);

        result = SikuliUtils.waitClickElement(CHROME_SCREENSHOT_PATH + SECOND_BE_POPUP);
        Verification.verifyTrue(driver, result, HELLO_USER_POPUP_MSG, false);

        result = SikuliUtils.waitElementDisplay(CHROME_SCREENSHOT_PATH + GO_TO_PINGONE_BTN);
        Verification.verifyTrue(driver, result, "Go To PingOne button is displayed.", false);

        result = SikuliUtils.waitElementDisplay(CHROME_SCREENSHOT_PATH + SIGN_OFF_LNK);
        Verification.verifyTrue(driver, result, "Sign Off link is displayed.", false);

        NewBESharedStep.preSetupTest(driver, windowHdls);

        driver.navigate().refresh();
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);


        result = SikuliUtils.waitClickElement(CHROME_SCREENSHOT_PATH + ENABLE_BE_ICON);
        Verification.verifyTrue(driver, result, CLICK_ACTIVE_ICON_MSG, false);

        result = SikuliUtils.waitClickElement(CHROME_SCREENSHOT_PATH + SIGN_OFF_LNK);
        Verification.verifyTrue(driver, result, SIGN_OFF_LINK_DISPLAY_MSG, false);
        Verification.verifyTrue(driver, newCD.loginPage.waitForPageLoad(), SIGN_ON_PAGE_DISPLAY_MSG, false);
        SelUtil.wait(3, CommonMessage.SIKULI_WAIT_MSG);

        result = SikuliUtils.waitClickElement(CHROME_SCREENSHOT_PATH + DISABLE_BE_ICON);
        Verification.verifyTrue(driver, result, CLICK_INACTIVE_ICON_MSG, false);

        result = SikuliUtils.waitClickElement(CHROME_SCREENSHOT_PATH + SIGN_ON_BTN);
        Verification.verifyTrue(driver, result, SIGN_ON_LINK_DISPLAY_MSG, false);
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);
        Verification.verifyTrue(driver, newCD.loginPage.waitForPageLoad(), SIGN_ON_PAGE_DISPLAY_MSG, false);

        newCD.loginPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());


        result = SikuliUtils.waitClickElement(CHROME_SCREENSHOT_PATH + ENABLE_BE_ICON);
        Verification.verifyTrue(driver, result, CLICK_ACTIVE_ICON_MSG, false);

        result = SikuliUtils.waitClickElement(CHROME_SCREENSHOT_PATH + GO_TO_PINGONE_BTN);
        Verification.verifyTrue(driver, result, GO_TO_PINGONE_MSG, false);
        Verification.verifyTrue(driver, newCD.waitForNewDockLoad(), CommonMessage.CDP_IS_LOADED_MSG, false);

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testInstallBEOnFF(WebDriver driver)
    {
        //Initialize class needed:
        //------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        //------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        newCD.loginPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
        SelWindowUtil.storeWindowHandler(driver, windowHdls);
        Verification.verifyFalse(driver, newCD.installDlg.waitDlgLoad(),
                CommonMessage.BE_INSTALLATION_IS_NOT_DISPLAYED_MSG, false);

        //Step: Check Install Plugin menu in Setting
        Verification.logStep(CommonMessage.VERIFY_ON_SETTING_MENU_MSG);
        Verification.logSubStep("Open Setting menu");
        newCD.clickSettingMenu();
        Verification.verifyTrue(driver, newCD.waitForInstallBEMenuLoad(),
                " - - Install Plugin menu is displayed.", false);
        Verification.logSubStep(CommonMessage.CLICK_INSTALL_MSG);
        newCD.clickMenu(NewCloudDesktopPage.MenuName.INSTALL_EXTENSION.getMenu());
        boolean result = SikuliUtils.waitElementDisplay(
                FIREFOX_SCREENSHOT_PATH + CONFIRM_ADD_EXTENSION, 30);
        Verification.verifyTrue(driver, result,
                " - - [Confirm install extension] popup is displayed.", false);

        result = SikuliUtils.waitClickElement(
                FIREFOX_SCREENSHOT_PATH + INSTALL_BE_CANCEL_BTN);
        Verification.verifyTrue(driver, result, CommonMessage.CANCEL_INSTALL_BE_POPUP_MSG, false);

        //Step: Check Install Plugin dialog when opening an app
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), Setup.getDefaultApp());
        Verification.verifyTrue(driver, newCD.installDlg.waitDlgLoad(),
                BE_INSTALL_DISPLAY_MSG, false);
        newCD.installDlg.closeDlg();
        Verification.verifyFalse(driver, newCD.installDlg.waitDlgLoad(),
                "BE Installation popup is closed.", false);

        Verification.logStep(INSTALL_BE_FROM_SEARCH_ADD);
        //Remove personal app
        if (newCD.waitForAppDisplayInCat(NewCloudDesktopPage.Category.PERSONAL.getCat(), PERSONAL_APP))
        {
            newCD.removePersonalAppFromDock(NewCloudDesktopPage.Category.PERSONAL.getCat(), PERSONAL_APP);
        }
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);
        Verification.logSubStep("Add Tripit from Search box");
        newCD.addAppFromSearchBox(PERSONAL_APP);
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);
        Verification.verifyTrue(driver, newCD.waitForAppDisplayInCat(NewCloudDesktopPage.Category.PERSONAL.getCat(), PERSONAL_APP),
                " - - Tripit is added to Personal", false);
        Verification.logSubStep("Click Tripit");
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.PERSONAL.getCat(), PERSONAL_APP);
        Verification.verifyTrue(driver, newCD.installDlg.waitDlgLoad(), BE_INSTALL_DISPLAY_MSG, false);
        newCD.installDlg.closeDlg();
        Verification.logSubStep("Remove Tripit");
        newCD.removePersonalAppFromDock(NewCloudDesktopPage.Category.PERSONAL.getCat(), PERSONAL_APP);

        //Step: Check Install Plugin dialog when opening an app from Search result
        Verification.logStep(INSTALL_BE_FROM_SEARCH_OPEN);
        newCD.openAppFromSearchBox(Setup.getDefaultApp());
        Verification.verifyTrue(driver, newCD.installDlg.waitDlgLoad(), BE_INSTALL_DISPLAY_MSG, false);
        Verification.logSubStep("Click Add");
        newCD.installDlg.addBE();

        result = SikuliUtils.waitElementDisplay(FIREFOX_SCREENSHOT_PATH + CONFIRM_ADD_EXTENSION);
        Verification.verifyTrue(driver, result,
                CommonMessage.CONFIRM_INSTALL_BE_MSG, false);


        result = SikuliUtils.waitClickElement(FIREFOX_SCREENSHOT_PATH + ADD_EXT_BTN);
        Verification.verifyTrue(driver, result, CLICK_ADD_EXT_MSG, false);

        result = SikuliUtils.waitElementDisplay(
                FIREFOX_SCREENSHOT_PATH + "ConfirmAddedExt.png");
        Verification.verifyTrue(driver, result,
                "[Confirm added extension] popup is displayed.", false);
        
        result = SikuliUtils.waitClickElement(FIREFOX_SCREENSHOT_PATH + DISABLE_BE_ICON);
        Verification.verifyTrue(driver, result, CLICK_INACTIVE_ICON_MSG, false);

        result = SikuliUtils.waitClickElement(FIREFOX_SCREENSHOT_PATH + FIRST_BE_POPUP);
        Verification.verifyTrue(driver, result, HELLO_POPUP_MSG, false);

        driver.navigate().refresh();
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);

        result = SikuliUtils.waitClickElement(FIREFOX_SCREENSHOT_PATH + SECOND_BE_POPUP,
                10);
        Verification.verifyTrue(driver, result, HELLO_USER_POPUP_MSG,
                        false);

        NewBESharedStep.preSetupTest(driver, windowHdls);

        driver.navigate().refresh();
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);


        result = SikuliUtils
                .waitClickElement(FIREFOX_SCREENSHOT_PATH + ENABLE_BE_ICON);
        Verification.verifyTrue(driver, result, CLICK_ACTIVE_ICON_MSG, false);

        result = SikuliUtils.waitClickElement(FIREFOX_SCREENSHOT_PATH + SIGN_OFF_LNK);
        Verification.verifyTrue(driver, result, SIGN_OFF_LINK_DISPLAY_MSG,
                false);
        Verification.verifyTrue(driver, newCD.loginPage.waitForPageLoad(),
                SIGN_ON_PAGE_DISPLAY_MSG, false);
        SelUtil.wait(3, CommonMessage.SIKULI_WAIT_MSG);

        result = SikuliUtils.waitClickElement(FIREFOX_SCREENSHOT_PATH + SIGN_ON_BTN);
        Verification.verifyTrue(driver, result, SIGN_ON_LINK_DISPLAY_MSG,
                false);
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);
        Verification.verifyTrue(driver, newCD.loginPage.waitForPageLoad(),
                SIGN_ON_PAGE_DISPLAY_MSG, false);

        newCD.loginPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());

        result = SikuliUtils.waitClickElement(FIREFOX_SCREENSHOT_PATH + GO_TO_PINGONE_BTN,
                10);
        Verification.verifyTrue(driver, result, GO_TO_PINGONE_MSG, false);
        Verification.verifyTrue(driver, newCD.waitForNewDockLoad(),
                "New dock page is displayed.", false);

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testInstallBEOnIE(WebDriver driver) throws IOException
    {
        //Initialize class needed:
        //------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        //------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        newCD.loginPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
        SelWindowUtil.storeWindowHandler(driver, windowHdls);
        Verification.verifyFalse(driver, newCD.installDlg.waitDlgLoad(),
                CommonMessage.BE_INSTALLATION_IS_NOT_DISPLAYED_MSG, false);

        //Step: Check Install Plugin menu in Setting
        Verification.logStep(CommonMessage.VERIFY_ON_SETTING_MENU_MSG);
        newCD.clickSettingMenu();
        Verification.verifyTrue(driver, newCD.waitForInstallBEMenuLoad(),
                " - - Install Plugin menu is displayed.", false);
        Verification.logSubStep(CommonMessage.CLICK_INSTALL_MSG);
        newCD.clickMenu(NewCloudDesktopPage.MenuName.INSTALL_EXTENSION.getMenu());
        boolean result = SikuliUtils.waitElementDisplay(IE_SCREENSHOT_PATH + CONFIRM_ADD_EXTENSION,
                30);
        Verification.verifyTrue(driver, result,
                CommonMessage.CONFIRM_INSTALL_BE_MSG, false);

        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + INSTALL_BE_CANCEL_BTN,
                3);
        Verification.verifyTrue(driver, result, CommonMessage.CANCEL_INSTALL_BE_POPUP_MSG, false);

        //Step: Check Install Plugin dialog when opening an app
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), Setup.getDefaultApp());
        Verification.verifyTrue(driver, newCD.installDlg.waitDlgLoad(),
                BE_INSTALL_DISPLAY_MSG, false);
        newCD.installDlg.closeDlg();
        Verification.verifyFalse(driver, newCD.installDlg.waitDlgLoad(),
                " - - BE Installation popup is closed.", false);

        Verification.logStep(INSTALL_BE_FROM_SEARCH_ADD);
        //Remove personal app
        if (!newCD.waitForAppDisplayInCat(NewCloudDesktopPage.Category.PERSONAL.getCat(), PERSONAL_APP))
        {
            SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);
            Verification.logSubStep("Add Tripit from Search box");
            newCD.addAppFromSearchBox(PERSONAL_APP);
        }
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);
        Verification.verifyTrue(driver, newCD.waitForAppDisplayInCat(NewCloudDesktopPage.Category.PERSONAL.getCat(), PERSONAL_APP),
                " - - Tripit is added to Personal", false);
        Verification.logSubStep("Click Tripit");
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.PERSONAL.getCat(), PERSONAL_APP);
        Verification.verifyTrue(driver, newCD.installDlg.waitDlgLoad(), BE_INSTALL_DISPLAY_MSG, false);
        newCD.installDlg.closeDlg();

        //Step: Check Install Plugin dialog when opening an app from Search result
        Verification.logStep(INSTALL_BE_FROM_SEARCH_OPEN);
        newCD.openAppFromSearchBox(Setup.getDefaultApp());
        Verification.verifyTrue(driver, newCD.installDlg.waitDlgLoad(), BE_INSTALL_DISPLAY_MSG, false);
        Verification.logSubStep("Click Add");
        newCD.installDlg.addBE();

        result = SikuliUtils.waitElementDisplay(IE_SCREENSHOT_PATH + CONFIRM_ADD_EXTENSION);
        Verification.verifyTrue(driver, result, CommonMessage.CONFIRM_INSTALL_BE_MSG, false);

        Verification.logStep("Install BE");
        Verification.logSubStep("Click Add extension");
        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + ADD_EXT_BTN);
        Verification.verifyTrue(driver, result, CLICK_ADD_EXT_MSG, false);

        result = SikuliUtils.waitElementDisplay(IE_SCREENSHOT_PATH + "WelcomeScreen.png", 90);
        Verification.verifyTrue(driver, result, "[Welcome screen] window is displayed.", false);

        Verification.logSubStep("Click Next");
        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + "NextBtn.png");
        Verification.verifyTrue(driver, result, "Click Next button", false);

        Verification.logSubStep("Click Install");
        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + "InstallBtn.png");
        Verification.verifyTrue(driver, result, "Click Install button", false);

        result = SikuliUtils.waitElementDisplay(IE_SCREENSHOT_PATH + "FilesInUseScreen.png", 30);
        Verification.verifyTrue(driver, result, "[Files in use] window is displayed.", false);

        //Close browser
        Verification.logSubStep("Close browser");
        driver.quit();
        windowHdls.clear();

        Verification.logSubStep("Click OK");
        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + "OKBtn.png");
        Verification.verifyTrue(driver, result, "Click OK button", false);

        Verification.logSubStep("Click Finish");
        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + "FinishBtn.png", 30);
        Verification.verifyTrue(driver, result, "Click Finish button", false);

        //Reopen browser
        Verification.logSubStep("Launch browser");
        WebdriverManager.setBrowserDriver(Setup.getBrowserType(), false);
        driver = WebdriverManager.get();
        driver.get(TestAccountManager.getCDPUrl());
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        Verification.logSubStep("Click Enable");
        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + "EnableBtn.png", 30);
        Verification.verifyTrue(driver, result, "Click Enable button", false);

        //Close and reopen browser
        Verification.logSubStep("Relaunch browser");
        driver.quit();
        windowHdls.clear();
        WebdriverManager.setBrowserDriver(Setup.getBrowserType(), false);
        driver = WebdriverManager.get();
        driver.get(TestAccountManager.getCDPUrl());
        SelWindowUtil.storeWindowHandler(driver, windowHdls);
        newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);

        Verification.logSubStep(CLICK_INACTIVE_ICON_MSG);
        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + DISABLE_BE_ICON);
        Verification.verifyTrue(driver, result, CLICK_INACTIVE_ICON_MSG, false);

        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + FIRST_BE_POPUP);
        Verification.verifyTrue(driver, result, HELLO_POPUP_MSG, false);

        newCD.loginPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
        SelUtil.wait(10, CommonMessage.SIKULI_WAIT_MSG);

        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + ENABLE_BE_ICON);
        Verification.verifyTrue(driver, result, CLICK_ACTIVE_ICON_MSG, false);

        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + SECOND_BE_POPUP);
        Verification.verifyTrue(driver, result, HELLO_USER_POPUP_MSG, false);

        NewBESharedStep.preSetupTest(driver, windowHdls);

        driver.navigate().refresh();
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);


        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + ENABLE_BE_ICON);
        Verification.verifyTrue(driver, result, CLICK_ACTIVE_ICON_MSG, false);
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);

        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + SIGN_OFF_LNK);
        Verification.verifyTrue(driver, result, SIGN_OFF_LINK_DISPLAY_MSG, false);
        Verification.verifyTrue(driver, newCD.loginPage.waitForPageLoad(), SIGN_ON_PAGE_DISPLAY_MSG, false);
        SelUtil.wait(3, CommonMessage.SIKULI_WAIT_MSG);
        
        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + ENABLE_BE_ICON);
        Verification.verifyTrue(driver, result, "Click inactivr BE icon", false);

        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + SIGN_ON_BTN);
        Verification.verifyTrue(driver, result, SIGN_ON_LINK_DISPLAY_MSG,
                false);
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);
        Verification.verifyTrue(driver, newCD.loginPage.waitForPageLoad(), SIGN_ON_PAGE_DISPLAY_MSG, false);

        newCD.loginPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());

        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + ENABLE_BE_ICON);
        Verification.verifyTrue(driver, result, CLICK_ACTIVE_ICON_MSG, false);

        result = SikuliUtils.waitClickElement(IE_SCREENSHOT_PATH + GO_TO_PINGONE_BTN);
        Verification.verifyTrue(driver, result, GO_TO_PINGONE_MSG, false);
        SelUtil.wait(3, CommonMessage.SIKULI_WAIT_MSG);
        Verification.verifyTrue(driver, newCD.waitForNewDockLoad(),
                "New dock page is displayed.", false);

        //#END#
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    public static void testReplayOpenLinkInNewTab(WebDriver driver)
    {
        //Initialize class needed:
        //------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        //------------------------------------------------------------

        ArrayList<String> windowHdls = new ArrayList<>();

        String appName = Setup.getDefaultApp();

        newCD.loginPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG); //waiting for BE icon load (in red)
        SelWindowUtil.storeWindowHandler(driver, windowHdls);

        NewBESharedStep.preSetupTest(driver, windowHdls);

        SelWindowUtil.switchToNewWindow(driver, windowHdls);
        SelWindowUtil.closeCurrentWindow(driver, windowHdls);
        SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);

        //Open app by "Open new tab"
        Verification.logStep("Right click and click Open link in new tab");
        boolean result = SikuliUtils.rightClickElement(
                SCREENSHOT_PATH + Setup.getBrowserType() + "\\EvernoteAppDock.png");
        Verification.verifyTrue(driver, result, "Right click " + appName, false);
        result = SikuliUtils.waitClickElement(
                SCREENSHOT_PATH + Setup.getBrowserType() + "\\OpenLinkInNewTab.png");
        Verification.verifyTrue(driver, result, "Click menu 'Open link in new tab'", false);

        checkAppReplay(driver, windowHdls, newCD, appName);

        //Open app by "Open new window"
        Verification.logStep("Right click and click Open link in new window");
        result = SikuliUtils.rightClickElement(
                SCREENSHOT_PATH + Setup.getBrowserType() + "\\EvernoteAppDock.png");
        Verification.verifyTrue(driver, result, "Right click " + appName, false);
        SelUtil.wait(5, CommonMessage.SIKULI_WAIT_MSG);
        result = SikuliUtils.waitClickElement(
                SCREENSHOT_PATH + Setup.getBrowserType() + "\\OpenLinkInNewWindow.png");
        Verification.verifyTrue(driver, result, "Click menu 'Open link in new window'", false);

        checkAppReplay(driver, windowHdls, newCD, appName);

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }

    private static void checkAppReplay(WebDriver driver, List<String> windowHdls, NewCloudDesktopPage newCD, String appName)
    {
        if (!Setup.getBrowserType().equalsIgnoreCase("firefox"))
        {
            SelWindowUtil.switchToNewWindow(driver, windowHdls);
            Verification.verifyTrue(driver, newCD.appPages.isBEPlayByTraining(),
                    "--- BE can replay " + appName + ".", false);
            SelWindowUtil.closeCurrentWindowAndSwitchTo(driver, windowHdls, 0);
        }
        else
        {
            SelUtil.wait(10, CommonMessage.SIKULI_WAIT_MSG);
            driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "\t");
            Verification.takeScreenshot(driver, appName + " screen");
            driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.SHIFT + "\t");
        }
    }

    public static void testBEInstallationPopupWithInstalledBE(WebDriver driver)
    {
        // Initialize class needed:
        // ------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);
        // ------------------------------------------------------------

        String appName = Setup.getDefaultApp();
        String dockURL = TestAccountManager.getCDPUrl();
        String username = TestAccountManager.getUserName();
        String password = TestAccountManager.getPassword();

        ArrayList<String> windowHdls = new ArrayList<>();

        newCD.loginCDP(dockURL, username, password);
        SelWindowUtil.storeWindowHandler(driver, windowHdls);
        Verification.verifyTrue(driver, newCD.waitForNewDockLoad(),
                CommonMessage.CDP_IS_LOADED_MSG, false);
        Verification.verifyTrue(driver, newCD.waitForBEActive(),
                CommonMessage.BE_IS_ACTIVE_MSG, false);
        Verification.verifyFalse(driver, newCD.installDlg.waitDlgLoad(),
                CommonMessage.BE_INSTALLATION_IS_NOT_DISPLAYED_MSG, false);

        NewBESharedStep.addDefaultAppFromCatalog(driver, windowHdls, appName);

        // Step: Check Install Plugin dialog when opening an app
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyFalse(driver, newCD.installDlg.waitDlgLoad(),
                CommonMessage.BE_INSTALLATION_IS_NOT_DISPLAYED_MSG, false);

        newCD.passPKeyDlg(TestAccountManager.getPrivacyKey());
        newCD.bypassLearnOrReplay(windowHdls, false);

        // Step: Check Install Plugin menu in Setting
        newCD.clickSettingMenu();
        Verification.verifyFalse(driver, newCD.waitForInstallBEMenuLoad(),
                CommonMessage.INSTALL_PLUGIN_MENU_IS_HIDED_MSG, false);
        Verification.verifyTrue(driver, newCD.waitForManagePswMenuLoad(),
                CommonMessage.MANAGE_PASS_MENU_IS_DISPLAYED_MSG, false);

        // Step: Check refresh page
        Verification.logStep("Refresh page");
        driver.navigate().refresh();
        Verification.verifyFalse(driver, newCD.installDlg.waitDlgLoad(),
                CommonMessage.BE_INSTALLATION_IS_NOT_DISPLAYED_MSG, false);
        Verification.verifyTrue(driver, newCD.waitForBEActive(),
                CommonMessage.BE_IS_ACTIVE_MSG, false);
        newCD.openAppFromCategory(NewCloudDesktopPage.Category.ALL_APPLICATIONS.getCat(), appName);
        Verification.verifyFalse(driver, newCD.installDlg.waitDlgLoad(),
                CommonMessage.BE_INSTALLATION_IS_NOT_DISPLAYED_MSG, false);
        newCD.bypassLearnOrReplay(windowHdls, false);

        newCD.openAppFromSearchBox(appName);
        Verification.verifyFalse(driver, newCD.installDlg.waitDlgLoad(),
                CommonMessage.BE_INSTALLATION_IS_NOT_DISPLAYED_MSG, false);
        newCD.bypassLearnOrReplay(windowHdls, false);

        newCD.logout();
        windowHdls.clear();
        Verification.endCollapseHtml(); // End steps logging formatting
        Verification.checkForVerificationErrors();
    }
}
