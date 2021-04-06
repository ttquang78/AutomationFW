package com.uid.cloudportal.pages;

import java.util.List;

import com.uid.common.config.Setup;
import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import com.uid.common.utils.SelWindowUtil;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.uid.common.config.Verification;
import com.uid.common.config.WebdriverManager;

public class NewManagePasswordsDialog
{
    private static Logger log = Logger.getLogger(NewManagePasswordsDialog.class.getName());

    final WebDriver driver;

    public NewManagePasswordsDialog(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //                    WEB ELEMENTS OF MANAGE passWord DLG         	   //
    //*********************************************************************//

    //[Manage Password header content]
    @FindBy(xpath = "//div[@class='manage-passwords-modal']//div[@class='managepasswords-content']/h1")
    WebElement lblManagePasswordsContentHeader;

    //[Close X button]
    @FindBy(css = "div.manage-passwords-modal span.close-modal")
    WebElement btnClose;

    //[Tab Overview]
    @FindBy(xpath = "//div[@class='manage-passwords-modal']//div[@class='item'][text()='Overview']")
    WebElement mnuOverview;

    //[Tab Privacy Key]
    @FindBy(xpath = "//div[@class='manage-passwords-modal']//div[@class='item'][text()='Privacy Key']")
    WebElement mnuPrivacyKey;

    //[Change Privacy Key text field]
    @FindBy(css = "div.manage-passwords-modal input[data-id='privacyKey-input'][type='password']")
    WebElement txtChangePrivacyKey;

    //[Confirm Privacy Key text field]
    @FindBy(css = "div.manage-passwords-modal input[data-id='privacyKeyConfirm-input'][type='password']")
    WebElement txtConfirmPrivacyKey;

    //[Change Privacy Key button disabled]
    @FindBy(xpath = "//div[@class='manage-passwords-modal']//button[@class='ellipsis-loader-button disabled primary']")
    WebElement btnChangeDisabled;

    //[Change Privacy Key button enabled]
    @FindBy(xpath = "//div[@class='manage-passwords-modal']//button[@class='ellipsis-loader-button primary']")
    WebElement btnChangeEnabled;

    //[Credential - Username]
    @FindBy(css = "div.managepasswords-content input[data-id='form-text-field-input'][type='text']")
    WebElement txtCredUsername;

    //[Credential - Password]
    @FindBy(css = "div.managepasswords-content input[data-id='form-text-field-input'][type='password']")
    WebElement txtCredPassword;

    //[Remove application]
    @FindBy(xpath = "//div[@class='manage-passwords-modal']//a[@class='details-target'][text()='Remove Application']")
    WebElement lnkRemoveApp;

    //[Save credential button disabled]
    @FindBy(xpath = "//div[@class='manage-passwords-modal']//button[@class='ellipsis-loader-button disabled primary'][@disabled='']")
    WebElement btnSaveCredDisabled;

    //[Save credential button enabled]
    @FindBy(xpath = "//div[@class='manage-passwords-modal']//button[@class='ellipsis-loader-button primary']")
    WebElement btnSaveCredEnabled;

    //[Confirm Remove dialog]
    @FindBy(xpath = "//div[@class='manage-passwords-modal']//span[@class='details-tooltip show top right alert change-password']//div[@class='details-content']")
    WebElement dlgRemoveAppConfirm;

    //[Remove confirm button]
    @FindBy(xpath = "//div[@class='manage-passwords-modal']//span[@class='details-tooltip show top right alert change-password']//div[@class='details-content']//input[@value='Remove']")
    WebElement btnRemoveAppConfirm;

    //[Cancel confirm link]
    @FindBy(xpath = "//div[@class='manage-passwords-modal']//span[@class='details-tooltip show top right alert change-password']//div[@class='details-content']//a[text()='Cancel']")
    WebElement lnkCancelAppConfirm;

    //[Cancel confirm link]
    @FindBy(xpath = "//div[@class='managepasswords-menu']/ul/li")
    List<WebElement> listApp;

    //*********************************************************************//
    //                             METHODS                                 //
    //*********************************************************************//

    public boolean checkCred(String userName, String passWord)
    {
        boolean isTrue = true;

        SelCheckUtil.isElementVisibility(driver, txtCredUsername);
        String usernameValue = SelUtil.getAttribute(txtCredUsername, "value");
        String passwordValue = SelUtil.getAttribute(txtCredPassword, "value");

        if (!usernameValue.equals(userName) || !passwordValue.equals(passWord))
        {
            isTrue = false;
        }

        return isTrue;
    }

    public void clearPassword()
    {
        SelCheckUtil.isElementVisibility(driver, txtCredPassword);
        txtCredPassword.clear();
    }

    public void clearUserName()
    {
        SelCheckUtil.isElementVisibility(driver, txtCredUsername);
        txtCredUsername.clear();
    }

    public void clickCancelLink()
    {
        SelUtil.waitElementClickableAndClick(driver, lnkCancelAppConfirm);
    }

    public void clickDisabledSaveCred()
    {
        SelUtil.waitElementClickableAndClick(driver, btnSaveCredDisabled);
    }

    public void clickRemoveAppSecretLink(String appName)
    {
        Verification.logSubStep("Remove credential of " + appName);

        selectAppInManagePsw(appName);
        SelUtil.waitElementClickableAndClick(driver, lnkRemoveApp);
    }

    public void closeMangePasswordsDlg()
    {
        Verification.logSubStep("Close Manage Password");
        SelUtil.waitElementClickableAndClick(driver, btnClose);
    }

    public int countElementsInManagePassword()
    {
        return listApp.size();
    }

    public void fillChangePK(String value)
    {
        SelUtil.sendKeyToElement(driver, txtChangePrivacyKey, value);
    }

    public void fillConfirmNewPK(String value)
    {
        SelUtil.sendKeyToElement(driver, txtConfirmPrivacyKey, value);
    }

    public void fillPassword(String value)
    {
        SelUtil.sendKeyToElement(driver, txtCredPassword, value);
    }

    public void fillUserName(String value)
    {
        SelUtil.sendKeyToElement(driver, txtCredUsername, value);
    }

    private WebElement findApp(String appName)
    {
        String xpathStr = "//div[@class='modal show']//div[@class='item'][text()='" + appName + "']";
        WebdriverManager.setErrorExpectedTrue();
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, Setup.DEFAULT_TIME_OUT);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathStr)));
            WebElement app = driver.findElement(By.xpath(xpathStr));
            WebdriverManager.setErrorExpectedFalse();
            return app;
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            WebdriverManager.setErrorExpectedFalse();
            return null;
        }
    }

    public String getContentHeader()
    {
        return SelUtil.getText(lblManagePasswordsContentHeader);
    }

    public void gotoOverview()
    {
        SelUtil.waitElementClickableAndClick(driver, mnuOverview);
    }

    public void gotoPrivacyKey()
    {
        Verification.logSubStep("Step: Click on PRIVACY KEY");
        SelUtil.waitElementClickableAndClick(driver, mnuPrivacyKey);
    }

    public boolean isAppExistingInManagePassword(String appName)
    {
        boolean isDisplay = true;

        WebElement element = findApp(appName);
        if (element == null)
        {
            isDisplay = false;
        }

        return isDisplay;
    }

    public boolean isDisableChangeBtnDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, btnChangeDisabled);
    }

    public boolean isDisableSaveButtonDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, btnSaveCredDisabled, 3);
    }

    public boolean isEnableChangeBtnDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, btnChangeEnabled, 3);
    }

    public boolean isEnableSaveButtonDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, btnSaveCredEnabled, 3);
    }

    public void removeAppSecret(String appName)
    {
        if (isAppExistingInManagePassword(appName))
        {
            Verification.logSubStep("Remove " + appName);

            selectAppInManagePsw(appName);
            SelUtil.waitElementClickableAndClick(driver, lnkRemoveApp);
            SelUtil.waitElementClickableAndClick(driver, btnRemoveAppConfirm);

            WebElement app = findApp(appName);
            SelCheckUtil.waitForElementChangeToInvisible(driver, app, 20);
        }
    }

    public void selectAppInManagePsw(String appName)
    {
        Verification.logSubStep("Click on " + appName);

        WebElement app = findApp(appName);

        SelWindowUtil.scrollToElement(driver, app);
        SelUtil.waitElementClickableAndClick(driver, app);
    }

    public void updateAppSecret(String appName, String userName, String passWord)
    {
        Verification.logSubStep("Update Username and Password of " + appName);

        selectAppInManagePsw(appName);
        if (!userName.isEmpty())
        {
            SelUtil.sendKeyToElement(driver, txtCredUsername, userName);
        }

        if (!passWord.isEmpty())
        {
            SelUtil.sendKeyToElement(driver, txtCredPassword, passWord);
        }

        SelUtil.waitElementClickableAndClick(driver, btnSaveCredEnabled);
    }

    public void updatePrivacyKey(String privacyKey)
    {
        SelUtil.sendKeyToElement(driver, txtChangePrivacyKey, privacyKey);
        SelUtil.sendKeyToElement(driver, txtConfirmPrivacyKey, privacyKey);
    }

    public void updatePrivacyKeyAndSave(String privacyKey)
    {
        gotoPrivacyKey();
        Verification.logSubStep("Fill PK and click Change");
        SelUtil.sendKeyToElement(driver, txtChangePrivacyKey, privacyKey);
        SelUtil.sendKeyToElement(driver, txtConfirmPrivacyKey, privacyKey);

        SelUtil.waitElementClickableAndClick(driver, btnChangeEnabled);
    }

    public boolean waitManagePasswordDlgLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, mnuOverview, 5);
    }

    public boolean waitRemoveConfirmDlgClose()
    {
        return SelCheckUtil.waitForElementChangeToInvisible(driver, dlgRemoveAppConfirm, 10);
    }

    public boolean waitRemoveConfirmDlgLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, dlgRemoveAppConfirm, 3);
    }

}