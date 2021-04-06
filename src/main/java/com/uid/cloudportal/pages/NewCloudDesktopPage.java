package com.uid.cloudportal.pages;

import java.util.*;

import com.uid.common.base.CommonMessage;
import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import com.uid.common.utils.SelWindowUtil;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.uid.common.config.Setup;
import com.uid.common.config.Verification;

public class NewCloudDesktopPage
{
    private static Logger log = Logger.getLogger(NewCloudDesktopPage.class.getName());

    public enum Category
    {
        OTHER("Other"), BENEFITS("Benefits"), ALL_APPLICATIONS("All Applications"), PERSONAL("Personal");

        private String cat;

        Category(String name)
        {
            this.cat = name;
        }

        public String getCat()
        {
            return this.cat;
        }
    }

    public enum MenuName
    {
        ADMIN("Admin"), MANAGE_PASSWORDS("Manage Passwords"), INSTALL_EXTENSION("Install Extension"),
        SIGN_OFF("Sign Off");

        private String menu;

        MenuName(String name)
        {
            this.menu = name;
        }

        public String getMenu()
        {
            return this.menu;
        }
    }

    // Apps use alert dialog
    private List<String> alertAppList =
            Arrays.asList("AccuConference", "ADP - FlexDirect", "Adobe Marketing Cloud", "Boundary",
                    "app.keysurvey.com", "Averiware", "Blackboard Connect", "eFolder Mobile",
                    "eFolder PC", "Eloqua", "EloquaUniversity",
                    "ExchangeDefender Compliance Archive", "ExpenseWire", "Famous Software", "IMEC",
                    "Intelex", "HRN Online", "Mangrove V5", "My Marketwire Account", "Tactile CRM",
                    "TharpeRobbins P360", "Trivaeo", "TrueShare", "Veeva Customer Support",
                    "WorldApp Extreme Form", "WorldApp KeySurvey", "YARDI Voyager");

    private List<String> alertApps = new LinkedList<>(alertAppList);

    final WebDriver driver;

    // Sub-pages
    public final LoginPage loginPage;
    public final NewBEInstallDlg installDlg;
    public final NewPKDlg privacyKeyDlg;
    public final NewCreatePKDlg createPrivacyKeyDlg;
    public final NewInputCredDialog inputCredDlg;
    public final NewConfirmPKDialog confirmPKDlg;
    public final NewManagePasswordsDialog managePwdDialog;
    public final CustomerPage appPages;

    public NewCloudDesktopPage(WebDriver driver)
    {
        this.driver = driver;

        // Initialize Sub-pages
        loginPage = PageFactory.initElements(driver, LoginPage.class);
        managePwdDialog = PageFactory.initElements(driver, NewManagePasswordsDialog.class);
        appPages = PageFactory.initElements(driver, CustomerPage.class);
        installDlg = PageFactory.initElements(driver, NewBEInstallDlg.class);
        privacyKeyDlg = PageFactory.initElements(driver, NewPKDlg.class);
        createPrivacyKeyDlg = PageFactory.initElements(driver, NewCreatePKDlg.class);
        inputCredDlg = PageFactory.initElements(driver, NewInputCredDialog.class);
        confirmPKDlg = PageFactory.initElements(driver, NewConfirmPKDialog.class);
    }

    // *********************************************************************//
    // WEB ELEMENTS OF TOP MENU //
    // *********************************************************************//

    // [Setting Icon]
    @FindBy(xpath = "//div[@class='settings-container']//a")
    WebElement lnkSettingIcon;

    // [Manage Passwords Menu Item]
    @FindBy(xpath = "//div[@class='settings-container']//div[@data-id='details-content']//a[node()='Manage Passwords']")
    WebElement mnuManagePasswords;

    // [Install Plugin Menu Item]
    @FindBy(xpath = "//div[@class='settings-container']//div[@data-id='details-content']//a[node()='Install Extension']")
    WebElement mnuInstallPlugin;

    // [Logout Menu Item]
    @FindBy(css = "div.settings-container  div[data-id='details-content'] a.logout")
    WebElement mnuLogout;

    // [Application Search box]
    @FindBy(xpath = "//label[@data-id='app-search']/span/input") WebElement txtAppSearch;

    // [Update PK Success message]
    @FindBy(xpath = "//div[@id='pingone-dock-top']//span[text()='You successfully changed your privacy key.']")
    WebElement lblUpdatePKSuccessMsg;

    // [Update credential message]
    @FindBy(xpath = "//div[@id='pingone-dock-top']//span[text()='You successfully changed your credentials.']")
    WebElement lblUpdateCredSuccessMsg;

    // [Sign On Button]
    @FindBy(css = "div[data-id='reauthenticate-modal'] button[data-id='btn-reauthenticate']")
    WebElement btnSignOn;

    // [Authen]
    @FindBy(xpath = "//div[@class='cdp-interstitial']")
    WebElement lblAuthMsg;

    private String generateAppXPath(String catName, String appName)
    {
        return "//div[div[text()='" + catName + "']]//span[@title=\"" + appName + "\"]";
    }

    private String generateCategoryXPath(String catName)
    {
        return "//div[text()='" + catName + "'][@data-id='category-name']";
    }

    private String generateMenuXPath(String menuName)
    {
        return "//div[@class='settings-container']//div[@data-id='details-content']//a[.='" + menuName + "']";
    }

    private String generateAppFromSearchXPath(String appName)
    {
        return "//label[@data-id='app-search']/ul//li[span[text()=\"" + appName + "\"]]";
    }

    private String generateAddAppXPath(String appName)
    {
        return generateAppFromSearchXPath(appName) + "//span[text()='Add']";
    }

    private String generateOpenAppXPath(String appName)
    {
        return "//label[@data-id='app-search']/ul//li[span[text()=\"" + appName + "\"]]//span[text()='Open']";
    }

    private String generateDeleteAppXPath(String catName, String appName)
    {
        return generateAppXPath(catName, appName) + "/span[@class='delete-button']";
    }

    private String generateAddAppSuccessMsgXPath(String appName)
    {
        return "//div[@id='pingone-dock-top']//span[text()=\"You have added " + appName + " to your applications.\"]";
    }

    // *********************************************************************//
    // ACTIONS                                                              //
    // *********************************************************************//

    public void bypassLearnOrReplay(List windowHdls, boolean saveCred)
    {
        if (inputCredDlg.waitDlgLoad())
        {
            if (saveCred)
            {
                inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
                SelWindowUtil.closeNewWindow(driver, windowHdls);
            }
            else
            {
                inputCredDlg.closeInputCredDlg();
            }
        }
        else
        {
            SelWindowUtil.closeNewWindow(driver, windowHdls);
        }
    }

    public void clickMenu(String menuName)
    {
        String xpathStr = generateMenuXPath(menuName);
        SelUtil.waitElementClickableAndClick(driver, By.xpath(xpathStr));
    }

    public void clickSettingMenu()
    {
        Verification.logSubStep("Open Setting menu");
        SelUtil.waitElementClickableAndClick(driver, lnkSettingIcon);
    }

    public void goToAdmin()
    {
        Verification.logSubStep("Launch Web Portal");
        clickSettingMenu();
        clickMenu(MenuName.ADMIN.getMenu());
    }

    public void goToManagePasswords(String pKey)
    {
        Verification.logSubStep("Open Manage Password");

        clickSettingMenu();
        clickMenu(MenuName.MANAGE_PASSWORDS.getMenu());
        confirmPKDlg.fillPKAndConfirm(pKey);
        managePwdDialog.waitManagePasswordDlgLoad();
    }

    public void logout()
    {
        Verification.logStep("Log out dock");

        clickSettingMenu();
        SelUtil.waitElementClickableAndClick(driver, mnuLogout);

        // wait until fully logged out
        SelCheckUtil.isElementVisibility(driver, loginPage.btnSignIn);
    }

    public boolean addAppFromSearchBox(String appName)
    {
        Verification.logStep("Adding app from Search box...");
        boolean isAddedApp = false;

        if (appName.equals(Setup.getDefaultApp()) || waitForAppDisplayInCat(Category.PERSONAL.getCat(), appName))
        {
            isAddedApp = true;
        }
        else
        {
            String xpathStr = generateAppFromSearchXPath(appName);
            fillSearch(appName);

            //Check Add link
            if (SelCheckUtil.isElementVisibility(driver, By.xpath(xpathStr), 5))
            {
                xpathStr = generateAddAppXPath(appName);
                if (SelCheckUtil.isElementClickable(driver, By.xpath(xpathStr)))
                {
                    SelUtil.waitElementClickableAndClick(driver, By.xpath(xpathStr));
                    waitForAddAppSuccessMsgLoad(appName);
                    log.info("Added app from Search box...");
                }
                isAddedApp = true;
                Verification.verifyTrue(driver, waitForAppDisplayInCat(Category.PERSONAL.getCat(), appName),
                        CommonMessage.APP_IS_ADDED_MSG, false);
            }
        }

        return isAddedApp;
    }

    private void fillSearch(String appName)
    {
        do
        {
            SelUtil.sendKeyToElement(driver, txtAppSearch, appName);
        }
        while (!SelUtil.getAttribute(txtAppSearch, "value").equals(appName));
    }

    public String getAppLink(String catName, String appName)
    {
        goToCategory(catName);
        String xpathStr = generateAppXPath(catName, appName) + "/parent::a";
        WebElement element = SelUtil.waitForElementVisibility(driver, By.xpath(xpathStr));

        return SelUtil.getAttribute(element, "href");
    }

    private void goToCategory(String catName)
    {
        String xpathStr = generateCategoryXPath(catName);

        if (waitForCategoryLoad(catName))
        {
            SelUtil.waitElementClickableAndClick(driver, By.xpath(xpathStr));
        }
    }

    public void goToLoginPage()
    {
        int count = 0;
        while (count < 3 && !loginPage.waitForPageLoad())
        {
            if (waitForNewDockLoad())
            {
                logout();
                break;
            }
            driver.navigate().refresh();
            count++;
        }
    }

    public void loginCDP(String loginURL, String username, String password)
    {
        if (!waitForNewDockLoad())
        {
            Verification.logStep("Login CDP");
            driver.navigate().to(loginURL);
            goToLoginPage();
            loginPage.login(username, password);
            waitForNewDockLoad();
            waitForBEActive();
            Setup.setBEVersion(appPages.getBEVersion());
        }
    }

    public void openAppFromCategory(String catName, String appName)
    {
        Verification.logStep("Open " + appName + " from " + catName);
        goToCategory(catName);
        String xpathStr = generateAppXPath(catName, appName);

        do
        {
            SelUtil.waitElementClickableAndClick(driver, By.xpath(xpathStr));
        }
        while (!SelCheckUtil.isElementVisibility(driver, By.xpath(xpathStr)));

    }

    public boolean openAppFromSearchBox(String appName)
    {
        Verification.logSubStep("Open app from search box");

        String openAppLink = generateOpenAppXPath(appName);
        do
        {
            SelUtil.sendKeyToElement(driver, txtAppSearch, appName);
        }
        while (!SelUtil.getAttribute(txtAppSearch, "value").equals(appName));
        //Check Open link
        if (SelCheckUtil.isElementVisibility(driver, By.xpath(openAppLink)))
        {
            SelUtil.waitElementClickableAndClick(driver, By.xpath(openAppLink));
            return true;
        }
        else
        {
            return false;
        }
    }

    public void passPKeyDlg(String key)
    {
        if (privacyKeyDlg.waitPKDlgLoad())
        {
            privacyKeyDlg.fillPKAndSignOn(key);
            if (privacyKeyDlg.isInvalidErrorDisplay())
            {
                privacyKeyDlg.clearAllPasswords();
                createPrivacyKeyDlg.createPrivacyKey(key);
            }
        }
        else if (createPrivacyKeyDlg.waitDlgLoad())
        { // Key never been created
            createPrivacyKeyDlg.createPrivacyKey(key);
        }
    }

    public void passPKPopups(String key, List windowHdls, boolean saveCred)
    {
        Verification.logStep("OPTION: Pass PK dialogs");
        clickSettingMenu();
        boolean needInputPK = waitForManagePswMenuLoad();
        openAppFromCategory(Category.ALL_APPLICATIONS.getCat(), Setup.getDefaultApp());
        if (!needInputPK)
        {
            passPKeyDlg(key);
        }
        bypassLearnOrReplay(windowHdls, saveCred);
    }

    public void removeAppCred(String privacyKey, String appName)
    {
        Verification.logStep("OPTION: Remove app credential");

        goToManagePasswords(privacyKey);
        managePwdDialog.removeAppSecret(appName);
        managePwdDialog.closeMangePasswordsDlg();
    }

    public void removePersonalAppFromDock(String catName, String appName)
    {
        goToCategory(catName);

        String xpathStr = generateAppXPath(catName, appName);

        if (SelCheckUtil.isElementVisibility(driver, By.xpath(xpathStr)))
        {
            WebElement app = driver.findElement(By.xpath(xpathStr));

            xpathStr = generateDeleteAppXPath(catName, appName);

            while (SelCheckUtil.isElementPresence(driver, By.xpath(xpathStr), 5))
            {
                goToCategory(catName);
                Verification.logSubStep("Remove " + appName);
                WebElement element = SelUtil.waitForElementPresence(driver, By.xpath(xpathStr));
                SelUtil.waitElementClickableAndClick(driver, element);
                SelCheckUtil.waitForElementChangeToInvisible(driver, app, 20);
                goToCategory(Category.ALL_APPLICATIONS.getCat());
            }
        }
    }

    public void replayApp(String cat, String appName)
    {
        openAppFromCategory(cat, appName);
        if (inputCredDlg.waitDlgLoad())
        {
            inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
        }
    }

    private void waitForAddAppSuccessMsgLoad(String appName)
    {
        String xpathStr = generateAddAppSuccessMsgXPath(appName);
        SelCheckUtil.isElementVisibility(driver, By.xpath(xpathStr), Setup.DEFAULT_TIME_OUT);
    }

    private boolean waitForCategoryLoad(String catName)
    {
        String xpathStr = generateCategoryXPath(catName);
        return SelCheckUtil.isElementVisibility(driver, By.xpath(xpathStr), 20);
    }

    public boolean waitForAppDisplayInCat(String catName, String appName)
    {
        goToCategory(catName);
        String xpathStr = generateAppXPath(catName, appName);

        return SelCheckUtil.isElementVisibility(driver, By.xpath(xpathStr), 5);
    }

    public boolean waitForBEActive()
    {
        boolean flag = true;
        int count = 0;
        do
        {
            SelUtil.waitElementClickableAndClick(driver, lnkSettingIcon);
            if (SelCheckUtil.isElementVisibility(driver, mnuInstallPlugin, 5)
                    && !SelCheckUtil.waitForElementChangeToInvisible(driver, mnuInstallPlugin, 20))
            {
                flag = false;
                driver.navigate().refresh();
                count++;
            }
            else
            {
                SelUtil.waitElementClickableAndClick(driver, lnkSettingIcon);
                break;
            }
        }
        while (count < 3);

        return flag;
    }

    public boolean waitForInstallBEMenuLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, mnuInstallPlugin, 3);
    }

    public boolean waitForManagePswMenuLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, mnuManagePasswords, 3);
    }

    public boolean waitForNewDockLoad()
    {
        if (!waitForCategoryLoad(Category.ALL_APPLICATIONS.getCat()))
        {
            driver.navigate().refresh();
        }
        return waitForCategoryLoad(Category.ALL_APPLICATIONS.getCat());
    }

    public boolean waitForUpdateCredSuccessMsgLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, lblUpdateCredSuccessMsg, 30);
    }

    public void waitForUpdateCredSuccessMsgInvisible()
    {
        SelCheckUtil.waitForElementChangeToInvisible(driver, lblUpdateCredSuccessMsg, 30);
    }

    public boolean waitForUpdatePKSuccessMsgLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, lblUpdatePKSuccessMsg, 30);
    }

    public void waitForUpdatePKSuccessMsgInvisible()
    {
        SelCheckUtil.waitForElementChangeToInvisible(driver, lblUpdatePKSuccessMsg, 30);
    }

    public boolean waitReAuthenDlgLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, btnSignOn);
    }

    public boolean isAuthMsgDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, lblAuthMsg, 5);
    }

    public void waitAuthMsgDisappear()
    {
        if (isAuthMsgDisplay())
        {
            SelCheckUtil.waitForElementChangeToInvisible(driver, lblAuthMsg);
        }
    }

    public void playAppFullStep(String appName, int frameID, List windowHdls, String privacyKey)
    {
        openAppFromSearchBox(appName);
        if (!inputCredDlg.waitDlgLoad(3) && !SelWindowUtil.isNewWindowDisplay(driver, windowHdls))
        {
            passPKeyDlg(privacyKey);
        }
        inputCredDlg.inputCredentialsAndSave(Setup.SAMPLE_USERNAME, Setup.SAMPLE_PSW);
        SelWindowUtil.switchToNewWindow(driver, windowHdls);

        if (alertApps.contains(appName))
        {
            SelWindowUtil.closeAlert(driver, 30);
        }

        if (appPages.passKMSSecurity())
        {
            SelWindowUtil.closeCurrentWindow(driver, windowHdls);
            SelWindowUtil.switchWindowByIndex(driver, 0, windowHdls);

            openAppFromSearchBox(appName);
            SelWindowUtil.switchToNewWindow(driver, windowHdls);
        }

        if (alertApps.contains(appName))
        {
            SelWindowUtil.closeAlert(driver, 10);
        }
        SelWindowUtil.moveToFrameByIndex(driver, frameID);
    }

    public void refreshCDP()
    {
        Verification.logSubStep("Refresh dock");
        driver.navigate().refresh();
        waitForNewDockLoad();
        waitForBEActive();
    }
}
