package com.uid.cloudportal.pages;

import com.uid.common.config.TestAccountManager;
import com.uid.common.utils.ApplicationData;
import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import com.uid.common.utils.SelWindowUtil;
import com.uid.common.config.Verification;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.uid.webportal.pages.WebPortalPage;

public class CloudDesktopPage
{

    final WebDriver driver;

    // Sub-pages
    public final LoginPage loginPage;
    public final ManagePasswordsDialog managePwdDialog;
    public final BEDialog beDialog;
    public final CustomerPage appPage;

    public CloudDesktopPage(WebDriver driver)
    {
        this.driver = driver;

        // Initialize Sub-pages
        loginPage = PageFactory.initElements(driver, LoginPage.class);
        managePwdDialog = PageFactory.initElements(driver, ManagePasswordsDialog.class);
        beDialog = PageFactory.initElements(driver, BEDialog.class);
        appPage = PageFactory.initElements(driver, CustomerPage.class);
    }

    // *********************************************************************//
    // WEB ELEMENTS / METHODS //
    // *********************************************************************//

    @FindBy(xpath = "//div[@class='icon-settings']")
    WebElement mnuMain;

    @FindBy(xpath = "//div[@class='apps-toggle-btn icon-organization']")
    WebElement btnOrganizationIcon;

    @FindBy(xpath = "//div[@class='apps-toggle-btn icon-personal']")
    WebElement btnPersonalIcon;

    @FindBy(xpath = "//li[@id='nav-download-plugin-link']")
    WebElement mnuInstallBrowserExt;

    @FindBy(id = "nav-manage-password-vault")
    WebElement mnuManageAppPasswords;

    @FindBy(xpath = "//li[@id='nav-logout-link']")
    WebElement mnuLogout;

    @FindBy(id = "pingone-button-confirm")
    WebElement btnSignOn;

    @FindBy(id = "ping-organization-apps-customize")
    WebElement pnlLegacyDock;

    @FindBy(xpath = "//div[@id='pingone-dialog-credentials']")
    WebElement pnlPistachioPmDialog;

    private String generateAppXpath(String appName)
    {
        return "//div[label[text()='" + appName + "']]/a";
    }

    // *********************************************************************//
    // ACTIONS
    // *********************************************************************//

    public void clickSettingIcon()
    {
        Verification.logSubStep("Open dock menu");
        SelUtil.waitElementClickableAndClick(driver, mnuMain);
    }

    public void goToView(boolean isPersonalView)
    {
        if (isPersonalView)
        {
            if (SelCheckUtil.isElementVisibility(driver, btnOrganizationIcon, 2))
            {
                Verification.logStep("OPTION: Switch to Personal view");
                SelUtil.waitElementClickableAndClick(driver, btnOrganizationIcon);
            }
        }
        else
        {
            if (SelCheckUtil.isElementVisibility(driver, btnPersonalIcon, 2))
            {
                Verification.logStep("OPTION: Switch to Corporation view");
                SelUtil.waitElementClickableAndClick(driver, btnPersonalIcon);
            }
        }
    }

    public void waitSettingIconDisplay()
    {
        SelCheckUtil.isElementVisibility(driver, mnuMain);
    }

    public void clickManageAppPasswords()
    {
        Verification.logSubStep("Click Manage Password menu");
        SelUtil.waitElementClickableAndClick(driver, mnuMain);
        SelUtil.waitElementClickableAndClick(driver, mnuManageAppPasswords);
    }

    public void gotoManageAppPasswords(String pkey)
    {
        Verification.logSubStep("Open Manage Password");
        clickManageAppPasswords();
        beDialog.fillConfirmPrivacyKeyInput(pkey);
        beDialog.clickConfirmPrivacyKeyButton();
    }

    public boolean isInstallBrowserExtMenuDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, mnuInstallBrowserExt);
    }

    public void findAppInManagePsw(String appName)
    {
        gotoManageAppPasswords(TestAccountManager.getPrivacyKey());
        WebElement app = managePwdDialog.findApp(appName);
        if (app == null)
        {
            Verification.exceptionError(driver, "Cannot find Test-App in ManagePasswords", "Not Saved");
        }
        else
        {
            SelUtil.clickElement(driver, app);
        }
    }

    public void updateAppCred(String appName, String appUsername, String appPsw)
    {
        findAppInManagePsw(appName);
        managePwdDialog.updateAppSecret(appUsername, appPsw);
        managePwdDialog.closeDialog();
    }

    public void removeAppCred(String appName)
    {
        Verification.logSubStep("Remove " + appName + " credential out of Manage Password");
        gotoManageAppPasswords(TestAccountManager.getPrivacyKey());
        managePwdDialog.removeAppSecret(appName);
        managePwdDialog.closeDialog();
        SelCheckUtil.isElementVisibility(driver, mnuMain);
    }

    public void learnAndSaveCred(ApplicationData appData)
    {
        selectApp(appData.getAppName());
        PVLegacy pvLegacy = PageFactory.initElements(driver, PVLegacy.class);
        pvLegacy.appTrainingLegacy(appData);
    }

    public boolean isReAuthenticationPopupDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, btnSignOn, 3);
    }

    public void reSignOn()
    {
        if (isReAuthenticationPopupDisplay())
        {
            Verification.logSubStep("OPTION: Re-sign on dock");
            SelUtil.waitElementClickableAndClick(driver, btnSignOn);
            loginPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());
            isBEActively();
        }
    }

    /**
     * [LOGOUT:] /* -Will select the logout item from the main menu drop-down
     * /*---------------------------------------------------
     */
    public void logout()
    {
        Verification.logSubStep("Sign Out");
        SelUtil.waitElementClickableAndClick(driver, mnuMain);
        SelUtil.waitElementClickableAndClick(driver, mnuLogout);

        // wait until fully logged out
        SelCheckUtil.isElementVisibility(driver, loginPage.btnSignIn);
    }

    /**
     * [SELECTAPP:] /* -Will click on the specified app icon
     * /*---------------------------------------------------
     */
    public void selectApp(String appName)
    {
        Verification.logSubStep("Click " + appName);

        String xpathStr = generateAppXpath(appName);
        SelUtil.waitElementClickableAndClick(driver, By.xpath(xpathStr));
        SelWindowUtil.switchToNewTab(driver);

        SelCheckUtil.waitForCondition(driver, driver1 -> !driver1.getCurrentUrl().contains("pingone"));

        driver.manage().window().maximize();

        if (SelCheckUtil.isElementVisibility(driver, beDialog.lnkSkipInstall))
        {
            driver.navigate().refresh();
        }

        appPage.passKMSSecurity();
    }

    public boolean isLegacyDock()
    {
        return SelCheckUtil.isElementVisibility(driver, pnlLegacyDock);
    }

    public boolean isLegacyPM()
    {
        return SelCheckUtil.isElementVisibility(driver, pnlPistachioPmDialog);
    }

    public void switchToLegacyMode()
    {
        if (!isLegacyDock())
        {
            Verification.logStep("Switch test account to Legacy mode.");

            NewCloudDesktopPage newCD =
                    PageFactory.initElements(driver, NewCloudDesktopPage.class);
            WebPortalPage wp = PageFactory.initElements(driver, WebPortalPage.class);

            newCD.goToAdmin();
            SelWindowUtil.switchToNewTab(driver);

            wp.goToMenu(WebPortalPage.WPMenu.SETUP, WebPortalPage.WPMenu.DOCK);
            wp.setupDockPage.switchToLegacy();
            SelWindowUtil.switchBackMainTab(driver);
            driver.navigate().refresh();
        }
    }

    public void login(String userName, String password)
    {
        Verification.logSubStep("Login to CDP");
        NewCloudDesktopPage newCD = PageFactory.initElements(driver, NewCloudDesktopPage.class);

        int tryTime = 0;

        do
        {
            if (loginPage.waitForPageLoad())
            {
                loginPage.login(userName, password);
            }
            else
            {
                driver.get(TestAccountManager.getCDPUrl());
            }
            tryTime++;
        }
        while (!(isLegacyDock() || newCD.waitForNewDockLoad()) && tryTime < 3);

        beDialog.reloadPage();
    }

    public boolean isBEActively()
    {
        boolean flag = false;
        int count = 0;
        do
        {
            if (!beDialog.waitBEPopupLoad())
            {
                flag = true;
                break;
            }

            driver.navigate().refresh();
            count++;
        }
        while (count < 3);

        return flag;
    }

    // *********
    // END CLASS
}
